import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { LoteIngreso } from './lote-ingreso.entity';
import { CreateLoteIngresoDto } from './dto/create-lote.dto';
import { Stock } from '../stock/stock.entity';
import { Producto } from '../productos/producto.entity';

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
      if (producto.proveedor_id !== dto.proveedor_id) {
         throw new BadRequestException('No puedes ingresar un lote con un proveedor distinto al oficial del producto. Por favor actualiza el proveedor en el Catálogo si es necesario.');
      }

   
      const lote = queryRunner.manager.create(LoteIngreso, { ...dto, tenant_id });
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

    const lotes = await loteRep.find({ where: { tenant_id, sucursal_id, producto_id } });
    let totalUnidades = 0;

    for (const lote of lotes) {
      totalUnidades += Number(lote.cantidad);
    }

    let stock = await stockRep.findOne({ where: { tenant_id, sucursal_id, producto_id } });
    if (!stock) {
      stock = stockRep.create({ tenant_id, sucursal_id, producto_id });
    }
    
    stock.cantidadTotal = totalUnidades;
    // CPP properties have been intentionally deprecated as pricing migrated directly to Producto Schema
    await stockRep.save(stock);
  }
}
