import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { LoteIngreso } from './lote-ingreso.entity';
import { CreateLoteIngresoDto } from './dto/create-lote.dto';
import { Stock } from '../stock/stock.entity';
import { Producto } from '../productos/producto.entity';
import { Sucursal } from '../sucursales/sucursal.entity';
import { AjusteInventario } from '../ajustes/ajuste.entity';

@Injectable()
export class SourcingService {
  constructor(
    @InjectRepository(LoteIngreso)
    private readonly loteRep: Repository<LoteIngreso>,
    private readonly dataSource: DataSource,
  ) {}

  async registrarIngreso(tenant_id: string, dto: CreateLoteIngresoDto): Promise<LoteIngreso> {

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
     
      const producto = await queryRunner.manager.findOne(Producto, { where: { id: dto.producto_id, tenant_id } });
      if (!producto) throw new NotFoundException('Producto no encontrado');

      const sucursal = await queryRunner.manager.findOne(Sucursal, { where: { id: dto.sucursal_id, tenant_id } });
      if (!sucursal) throw new NotFoundException('Sucursal no encontrada');
      if (!sucursal.isActive) throw new BadRequestException(`La sucursal "${sucursal.name}" está inactiva o clausurada. No se pueden registrar ingresos en ella.`);

      if (producto.proveedor_id !== dto.proveedor_id) {
         throw new BadRequestException('No puedes ingresar un lote con un proveedor distinto al oficial del producto. Por favor actualiza el proveedor en el Catálogo si es necesario.');
      }

   
      const lote = queryRunner.manager.create(LoteIngreso, { 
        ...dto, 
        tenant_id,
        costoUnitarioSnapshot: producto.precioCosto 
      });
      const loteGuardado = await queryRunner.manager.save(lote);

      await this.recalculateStock(queryRunner, tenant_id, dto.sucursal_id, dto.producto_id);

      await queryRunner.commitTransaction();
      return loteGuardado;
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  async getHistorial(tenant_id: string): Promise<LoteIngreso[]> {
    return this.loteRep.find({ 
      where: { tenant_id },
      relations: ['producto', 'proveedor'],
      order: { fechaIngreso: 'DESC' }
    });
  }

  async update(tenant_id: string, id: string, dto: Partial<CreateLoteIngresoDto>): Promise<LoteIngreso> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const lote = await queryRunner.manager.findOne(LoteIngreso, { where: { id, tenant_id } });
      if (!lote) throw new NotFoundException('Lote no encontrado');

      Object.assign(lote, dto);
      const loteGuardado = await queryRunner.manager.save(lote);

      await this.recalculateStock(queryRunner, tenant_id, lote.sucursal_id, lote.producto_id);

      await queryRunner.commitTransaction();
      return loteGuardado;
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
      const lote = await queryRunner.manager.findOne(LoteIngreso, { where: { id, tenant_id } });
      if (!lote) throw new NotFoundException('Lote no encontrado');
      
      const producto_id = lote.producto_id;
      const sucursal_id = lote.sucursal_id;
      await queryRunner.manager.remove(lote);

      await this.recalculateStock(queryRunner, tenant_id, sucursal_id, producto_id);

      await queryRunner.commitTransaction();
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  private async recalculateStock(queryRunner: any, tenant_id: string, sucursal_id: string, producto_id: string) {
    const stockRep = queryRunner.manager.getRepository(Stock);
    const loteRep = queryRunner.manager.getRepository(LoteIngreso);
    const ajusteRep = queryRunner.manager.getRepository(AjusteInventario);

    const lotes = await loteRep.find({ where: { tenant_id, sucursal_id, producto_id } });
    const ajustes = await ajusteRep.find({ where: { tenant_id, sucursal_id, producto_id } });

    let totalUnidades = 0;
    let totalValorAdquisicion = 0;

    for (const lote of lotes) {
      totalUnidades += Number(lote.cantidad);
      totalValorAdquisicion += Number(lote.cantidad) * Number(lote.costoUnitarioSnapshot || 0);
    }

    // Restar todas las mermas o ajustes físicos (Ajustes Anómalos)
    for (const ajuste of ajustes) {
       const gap = ajuste.cantidad_sistema - ajuste.cantidad_fisica;
       if (gap > 0) {
           totalUnidades -= gap;
           totalValorAdquisicion -= Number(ajuste.valor_perdido || 0);
       }
    }

    let stock = await stockRep.findOne({ where: { tenant_id, sucursal_id, producto_id } });
    if (!stock) {
      stock = stockRep.create({ tenant_id, sucursal_id, producto_id });
    }
    
    stock.cantidadTotal = totalUnidades;
    stock.valorAdquisicion = totalValorAdquisicion;
    await stockRep.save(stock);
  }
}
