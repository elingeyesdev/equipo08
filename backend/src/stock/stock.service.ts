import {
  Injectable,
  BadRequestException,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource, EntityManager } from 'typeorm';
import { Stock } from './stock.entity';
import { MovimientoInventario } from './movimiento-inventario.entity';

@Injectable()
export class StockService {
  constructor(
    @InjectRepository(Stock)
    private readonly stockRep: Repository<Stock>,
    private readonly dataSource: DataSource,
  ) {}

  async getStockRow(
    tenant_id: string,
    sucursal_id: string,
    producto_id: string,
  ): Promise<Stock | null> {
    return this.stockRep.findOne({
      where: { tenant_id, sucursal_id, producto_id },
    });
  }

  async sumStock(
    tenant_id: string,
    sucursal_id: string,
    producto_id: string,
    cantidad: number,
    valorAdquisicionDelta: number = 0,
    tipo: string = 'AJUSTE',
    motivo?: string,
    usuario_id?: string,
    referencia_tipo?: string,
    referencia_id?: string,
  ): Promise<Stock> {
    return this.applyStockDelta(
      this.stockRep.manager,
      tenant_id,
      sucursal_id,
      producto_id,
      cantidad,
      valorAdquisicionDelta,
      tipo,
      motivo,
      undefined,
      usuario_id,
      referencia_tipo,
      referencia_id,
    );
  }

  async applyStockDelta(
    manager: EntityManager,
    tenant_id: string,
    sucursal_id: string,
    producto_id: string,
    cantidad: number,
    valorAdquisicionDelta: number = 0,
    tipo: string = 'AJUSTE',
    motivo?: string,
    existingStock?: Stock,
    usuario_id?: string,
    referencia_tipo?: string,
    referencia_id?: string,
  ): Promise<Stock> {
    let stock = existingStock || await manager.findOne(Stock, {
      where: { tenant_id, sucursal_id, producto_id },
    });

    const costBefore = stock && stock.cantidadActual > 0
      ? Number(stock.valorAdquisicion || 0) / stock.cantidadActual
      : 0;

    const stockAnterior = stock ? Number(stock.cantidadActual || 0) : 0;

    if (!stock) {
      stock = manager.create(Stock, {
        tenant_id,
        sucursal_id,
        producto_id,
        cantidadActual: Number(cantidad || 0),
        valorAdquisicion: Number(valorAdquisicionDelta || 0),
      });
    } else {
      stock.cantidadActual =
        Number(stock.cantidadActual || 0) + Number(cantidad || 0);
      stock.valorAdquisicion =
        Number(stock.valorAdquisicion || 0) +
        Number(valorAdquisicionDelta || 0);
    }
    const savedStock = await manager.save(Stock, stock);
    const stockResultante = savedStock ? savedStock.cantidadActual : stock.cantidadActual;

    // Registrar movimiento de inventario
    const unitCost = costBefore || (cantidad !== 0 ? Math.abs(Number(valorAdquisicionDelta)) / Math.abs(cantidad) : 0);
    const movimiento = manager.create(MovimientoInventario, {
      tenant_id,
      stock_id: savedStock?.id || stock.id || 'mock-stock-id',
      tipo,
      cantidad,
      stockAnterior,
      stockResultante,
      costoUnitario: unitCost,
      motivo,
      usuario_id,
      referenciaTipo: referencia_tipo,
      referenciaId: referencia_id,
    });
    await manager.save(MovimientoInventario, movimiento);

    return savedStock || stock;
  }

  async getStockByTenant(tenant_id: string): Promise<Stock[]> {
    return this.stockRep.find({
      where: { tenant_id },
      relations: ['producto', 'sucursal'],
      order: {
        sucursal_id: 'ASC',
      },
    });
  }

  async transferStock(
    tenant_id: string,
    from_sucursal_id: string,
    to_sucursal_id: string,
    producto_id: string,
    cantidad: number,
  ): Promise<void> {
    if (from_sucursal_id === to_sucursal_id) {
      throw new BadRequestException(
        'No se puede transferir a la misma sucursal',
      );
    }
    if (cantidad <= 0) {
      throw new BadRequestException('La cantidad debe ser mayor a cero');
    }

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      // Bloquear registro origen
      const sourceStock = await queryRunner.manager.findOne(Stock, {
        where: { tenant_id, sucursal_id: from_sucursal_id, producto_id },
        lock: { mode: 'pessimistic_write' },
      });

      if (!sourceStock || sourceStock.cantidadActual < cantidad) {
        throw new BadRequestException(
          `Stock insuficiente en sucursal origen. Disponible: ${sourceStock ? sourceStock.cantidadActual : 0}`,
        );
      }

      // Calcular valor de la porción transferida
      const avgCost =
        sourceStock.cantidadActual > 0
          ? Number(sourceStock.valorAdquisicion || 0) /
            sourceStock.cantidadActual
          : 0;
      const transferredValue = avgCost * cantidad;

      // Descontar de origen utilizando applyStockDelta para auditoría
      await this.applyStockDelta(
        queryRunner.manager,
        tenant_id,
        from_sucursal_id,
        producto_id,
        -cantidad,
        -transferredValue,
        'TRANSFERENCIA',
        `Transferencia salida a sucursal ${to_sucursal_id}`,
        sourceStock,
      );

      // Bloquear/Crear registro destino
      const targetStock = await queryRunner.manager.findOne(Stock, {
        where: { tenant_id, sucursal_id: to_sucursal_id, producto_id },
        lock: { mode: 'pessimistic_write' },
      });

      // Incrementar en destino utilizando applyStockDelta para auditoría
      await this.applyStockDelta(
        queryRunner.manager,
        tenant_id,
        to_sucursal_id,
        producto_id,
        cantidad,
        transferredValue,
        'TRANSFERENCIA',
        `Transferencia entrada desde sucursal ${from_sucursal_id}`,
        targetStock || undefined,
      );

      await queryRunner.commitTransaction();
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }
}
