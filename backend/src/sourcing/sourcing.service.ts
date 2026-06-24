import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { LoteIngreso } from './lote-ingreso.entity';
import { CreateLoteIngresoDto } from './dto/create-lote.dto';
import { Stock } from '../stock/stock.entity';
import { StockService } from '../stock/stock.service';
import { Sucursal } from '../sucursales/sucursal.entity';
import { Producto } from '../productos/producto.entity';
import { ProductoVariacion } from '../productos/producto-variacion.entity';

@Injectable()
export class SourcingService {
  constructor(
    @InjectRepository(LoteIngreso)
    private readonly loteRep: Repository<LoteIngreso>,
    private readonly dataSource: DataSource,
    private readonly stockService: StockService,
  ) {}

  async registrarIngreso(
    tenant_id: string,
    dto: CreateLoteIngresoDto,
    usuario_id?: string,
  ): Promise<any> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const producto = await queryRunner.manager.findOne(Producto, {
        where: { id: dto.producto_id, tenant_id },
      });
      if (!producto) throw new NotFoundException('Producto no encontrado');

      const sucursal = await queryRunner.manager.findOne(Sucursal, {
        where: { id: dto.sucursal_id, tenant_id },
      });
      if (!sucursal) throw new NotFoundException('Sucursal no encontrada');
      if (!sucursal.isActive) {
        throw new BadRequestException(
          `La sucursal "${sucursal.name}" esta inactiva o clausurada. No se pueden registrar ingresos en ella.`,
        );
      }

      // Prioridad: costo del DTO > precioCosto del catálogo
      const costoUnitario = dto.costoUnitario != null && dto.costoUnitario > 0
        ? Number(dto.costoUnitario)
        : Number(producto.precioCosto || 0);

      // Buscar o crear la variante por defecto en la base de datos para este producto
      let variant = await queryRunner.manager.findOne(ProductoVariacion, {
        where: { producto: { id: dto.producto_id, tenant_id } },
        order: { createdAt: 'ASC' },
      });
      if (!variant) {
        // En caso extremo que no exista, la creamos al vuelo para mantener consistencia
        variant = queryRunner.manager.create(ProductoVariacion, {
          producto_id: dto.producto_id,
          sku: `SKU-${dto.producto_id.split('-')[0]}`,
          precioCosto: costoUnitario,
          precioVenta: Number(producto.precioVenta || 0),
          opciones: {},
        });
        variant = await queryRunner.manager.save(variant);
      }

      // Buscar o crear stock_id utilizando producto_variacion_id
      let stock = await queryRunner.manager.findOne(Stock, {
        where: { tenant_id, sucursal_id: dto.sucursal_id, producto_variacion_id: variant.id },
      });
      if (!stock) {
        stock = queryRunner.manager.create(Stock, {
          tenant_id,
          sucursal_id: dto.sucursal_id,
          producto_id: dto.producto_id,
          producto_variacion_id: variant.id,
          cantidadActual: 0,
          costoPromedio: 0,
        });
        stock = await queryRunner.manager.save(stock);
      }

      const lote = queryRunner.manager.create(LoteIngreso, {
        tenant_id,
        stock_id: stock.id,
        cantidad: dto.cantidad,
        costoUnitario,
        fechaElaboracion: dto.fechaElaboracion,
        fechaVencimiento: dto.fechaVencimiento,
        usuario_id,
      });
      const loteGuardado = await queryRunner.manager.save(lote);

      await this.stockService.applyStockDelta(
        queryRunner.manager,
        tenant_id,
        dto.sucursal_id,
        dto.producto_id,
        Number(dto.cantidad || 0),
        Number(dto.cantidad || 0) * costoUnitario,
        'INGRESO',
        `Lote de ingreso ${loteGuardado.id}`,
        undefined,
        usuario_id,
        'COMPRA',
        loteGuardado.id,
        variant.id,
      );

      await queryRunner.commitTransaction();
      
      // Mapear para compatibilidad con el frontend
      return {
        ...loteGuardado,
        producto_id: dto.producto_id,
        sucursal_id: dto.sucursal_id,
        producto,
        sucursal,
      };
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  async getHistorial(tenant_id: string): Promise<any[]> {
    const lotes = await this.loteRep.find({
      where: { tenant_id },
      relations: ['stock', 'stock.producto', 'stock.producto.proveedor', 'stock.sucursal'],
      order: { fechaIngreso: 'DESC' },
    });

    // Mapear para compatibilidad 100% con el frontend actual
    return lotes.map(l => ({
      ...l,
      producto_id: l.stock?.producto_id,
      sucursal_id: l.stock?.sucursal_id,
      producto: l.stock?.producto,
      sucursal: l.stock?.sucursal,
    }));
  }

  async update(
    tenant_id: string,
    id: string,
    dto: Partial<CreateLoteIngresoDto>,
  ): Promise<any> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const lote = await queryRunner.manager.findOne(LoteIngreso, {
        where: { id, tenant_id },
        relations: ['stock'],
      });
      if (!lote) throw new NotFoundException('Lote no encontrado');

      const currentProductoId = dto.producto_id || lote.stock?.producto_id;
      const currentSucursalId = dto.sucursal_id || lote.stock?.sucursal_id;

      const previous = {
        sucursal_id: lote.stock?.sucursal_id,
        producto_id: lote.stock?.producto_id,
        cantidad: Number(lote.cantidad || 0),
        valor: Number(lote.cantidad || 0) * Number(lote.costoUnitario || 0),
      };

      const producto = await queryRunner.manager.findOne(Producto, {
        where: { id: currentProductoId, tenant_id },
      });
      if (!producto) throw new NotFoundException('Producto no encontrado');

      const sucursal = await queryRunner.manager.findOne(Sucursal, {
        where: { id: currentSucursalId, tenant_id },
      });
      if (!sucursal) throw new NotFoundException('Sucursal no encontrada');
      if (!sucursal.isActive) {
        throw new BadRequestException(
          `La sucursal "${sucursal.name}" esta inactiva o clausurada. No se pueden registrar ingresos en ella.`,
        );
      }

      // Buscar o crear la variante por defecto
      let variant = await queryRunner.manager.findOne(ProductoVariacion, {
        where: { producto: { id: currentProductoId, tenant_id } },
        order: { createdAt: 'ASC' },
      });
      if (!variant) {
        variant = queryRunner.manager.create(ProductoVariacion, {
          producto_id: currentProductoId,
          sku: `SKU-${currentProductoId.split('-')[0]}`,
          precioCosto: Number(producto.precioCosto || 0),
          precioVenta: Number(producto.precioVenta || 0),
          opciones: {},
        });
        variant = await queryRunner.manager.save(variant);
      }

      // Buscar o crear stock
      let stock = await queryRunner.manager.findOne(Stock, {
        where: { tenant_id, sucursal_id: currentSucursalId, producto_variacion_id: variant.id },
      });
      if (!stock) {
        stock = queryRunner.manager.create(Stock, {
          tenant_id,
          sucursal_id: currentSucursalId,
          producto_id: currentProductoId,
          producto_variacion_id: variant.id,
          cantidadActual: 0,
          costoPromedio: 0,
        });
        stock = await queryRunner.manager.save(stock);
      }

      lote.stock_id = stock.id;
      if (dto.cantidad !== undefined) lote.cantidad = dto.cantidad;
      if (dto.fechaElaboracion !== undefined) lote.fechaElaboracion = dto.fechaElaboracion;
      if (dto.fechaVencimiento !== undefined) lote.fechaVencimiento = dto.fechaVencimiento;
      lote.costoUnitario = Number(producto.precioCosto || 0);

      const checkElaboracion = lote.fechaElaboracion;
      const checkVencimiento = lote.fechaVencimiento;
      if (checkElaboracion && checkVencimiento) {
        const elaboracion = new Date(checkElaboracion);
        const vencimiento = new Date(checkVencimiento);
        if (vencimiento < elaboracion) {
          throw new BadRequestException('La fecha de vencimiento no puede ser anterior a la fecha de elaboración.');
        }
      }

      const loteGuardado = await queryRunner.manager.save(lote);
      const currentValue =
        Number(loteGuardado.cantidad || 0) *
        Number(loteGuardado.costoUnitario || 0);

      if (
        previous.sucursal_id === currentSucursalId &&
        previous.producto_id === currentProductoId
      ) {
        await this.stockService.applyStockDelta(
          queryRunner.manager,
          tenant_id,
          currentSucursalId,
          currentProductoId,
          Number(loteGuardado.cantidad || 0) - previous.cantidad,
          currentValue - previous.valor,
          'INGRESO',
          `Modificación lote de ingreso ${loteGuardado.id}`,
          undefined,
          undefined,
          'COMPRA',
          loteGuardado.id,
          variant.id,
        );
      } else {
        // Obtener el anterior variant_id del stock asociado al lote
        const previousVariantId = lote.stock?.producto_variacion_id;

        // Desasociar del anterior
        await this.stockService.applyStockDelta(
          queryRunner.manager,
          tenant_id,
          previous.sucursal_id,
          previous.producto_id,
          -previous.cantidad,
          -previous.valor,
          'EGRESO',
          `Modificación lote de ingreso (desasociación) ${loteGuardado.id}`,
          undefined,
          undefined,
          'COMPRA',
          loteGuardado.id,
          previousVariantId,
        );

        // Asociar al nuevo
        await this.stockService.applyStockDelta(
          queryRunner.manager,
          tenant_id,
          currentSucursalId,
          currentProductoId,
          Number(loteGuardado.cantidad || 0),
          currentValue,
          'INGRESO',
          `Modificación lote de ingreso (asociación) ${loteGuardado.id}`,
          undefined,
          undefined,
          'COMPRA',
          loteGuardado.id,
          variant.id,
        );
      }

      await queryRunner.commitTransaction();
      
      return {
        ...loteGuardado,
        producto_id: currentProductoId,
        sucursal_id: currentSucursalId,
        producto,
        sucursal,
      };
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  async remove(tenant_id: string, id: string): Promise<void> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const lote = await queryRunner.manager.findOne(LoteIngreso, {
        where: { id, tenant_id },
        relations: ['stock'],
      });
      if (!lote) throw new NotFoundException('Lote no encontrado');

      const valorLote =
        Number(lote.cantidad || 0) * Number(lote.costoUnitario || 0);
      
      const sucursalId = lote.stock?.sucursal_id;
      const productoId = lote.stock?.producto_id;
      const variacionId = lote.stock?.producto_variacion_id;

      await queryRunner.manager.remove(lote);

      if (sucursalId && productoId) {
        await this.stockService.applyStockDelta(
          queryRunner.manager,
          tenant_id,
          sucursalId,
          productoId,
          -Number(lote.cantidad || 0),
          -valorLote,
          'EGRESO',
          `Eliminación de lote de ingreso ${lote.id}`,
          undefined,
          undefined,
          'COMPRA',
          lote.id,
          variacionId,
        );
      }

      await queryRunner.commitTransaction();
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }
}
