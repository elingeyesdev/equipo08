import {
  Injectable,
  BadRequestException,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource, EntityManager } from 'typeorm';
import { Stock } from './stock.entity';

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
  ): Promise<Stock> {
    return this.applyStockDelta(
      this.stockRep.manager,
      tenant_id,
      sucursal_id,
      producto_id,
      cantidad,
      valorAdquisicionDelta,
    );
  }

  async applyStockDelta(
    manager: EntityManager,
    tenant_id: string,
    sucursal_id: string,
    producto_id: string,
    cantidad: number,
    valorAdquisicionDelta: number = 0,
  ): Promise<Stock> {
    const stockRep = manager.getRepository(Stock);
    let stock = await stockRep.findOne({
      where: { tenant_id, sucursal_id, producto_id },
    });
    if (!stock) {
      stock = stockRep.create({
        tenant_id,
        sucursal_id,
        producto_id,
        cantidadTotal: Number(cantidad || 0),
        valorAdquisicion: Number(valorAdquisicionDelta || 0),
      });
    } else {
      stock.cantidadTotal =
        Number(stock.cantidadTotal || 0) + Number(cantidad || 0);
      stock.valorAdquisicion =
        Number(stock.valorAdquisicion || 0) +
        Number(valorAdquisicionDelta || 0);
    }
    return stockRep.save(stock);
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

      if (!sourceStock || sourceStock.cantidadTotal < cantidad) {
        throw new BadRequestException(
          `Stock insuficiente en sucursal origen. Disponible: ${sourceStock ? sourceStock.cantidadTotal : 0}`,
        );
      }

      // Calcular valor de la porción transferida
      const avgCost =
        sourceStock.cantidadTotal > 0
          ? Number(sourceStock.valorAdquisicion || 0) /
            sourceStock.cantidadTotal
          : 0;
      const transferredValue = avgCost * cantidad;

      // Descontar de origen
      sourceStock.cantidadTotal = Number(sourceStock.cantidadTotal) - cantidad;
      sourceStock.valorAdquisicion =
        Number(sourceStock.valorAdquisicion) - transferredValue;
      await queryRunner.manager.save(Stock, sourceStock);

      // Bloquear/Crear registro destino
      let targetStock = await queryRunner.manager.findOne(Stock, {
        where: { tenant_id, sucursal_id: to_sucursal_id, producto_id },
        lock: { mode: 'pessimistic_write' },
      });

      if (!targetStock) {
        targetStock = queryRunner.manager.create(Stock, {
          tenant_id,
          sucursal_id: to_sucursal_id,
          producto_id,
          cantidadTotal: cantidad,
          valorAdquisicion: transferredValue,
        });
      } else {
        targetStock.cantidadTotal =
          Number(targetStock.cantidadTotal || 0) + cantidad;
        targetStock.valorAdquisicion =
          Number(targetStock.valorAdquisicion || 0) + transferredValue;
      }
      await queryRunner.manager.save(Stock, targetStock);

      await queryRunner.commitTransaction();
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }
}
