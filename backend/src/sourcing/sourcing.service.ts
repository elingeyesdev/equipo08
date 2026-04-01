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
    if (dto.costoAdquisicion <= 0) {
      throw new BadRequestException('No se puede registrar stock sin costo de adquisición válido (mayor a 0).');
    }

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      // Validar que el proveedor coincida con el proveedor asignado al producto
      const producto = await queryRunner.manager.findOne(Producto, { where: { id: dto.producto_id, tenant_id } });
      if (!producto) throw new NotFoundException('Producto no encontrado');
      if (producto.proveedor_id !== dto.proveedor_id) {
         throw new BadRequestException('No puedes ingresar un lote con un proveedor distinto al oficial del producto. Por favor actualiza el proveedor en el Catálogo si es necesario.');
      }

      // 1. Crear el lote (Historial de costos)
      const lote = queryRunner.manager.create(LoteIngreso, { ...dto, tenant_id });
      const loteGuardado = await queryRunner.manager.save(lote);

      // 2. Transversal: Aumentar el stock principal
      const stockRep = queryRunner.manager.getRepository(Stock);
      let stock = await stockRep.findOne({ where: { tenant_id, producto_id: dto.producto_id } });
      if (!stock) {
         stock = stockRep.create({ tenant_id, producto_id: dto.producto_id, cantidadTotal: dto.cantidad });
      } else {
         stock.cantidadTotal += Number(dto.cantidad);
      }
      await stockRep.save(stock);

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

      // Si cambian la cantidad, ajustar el stock físico
      if (dto.cantidad !== undefined && dto.cantidad !== lote.cantidad) {
        const diferencia = dto.cantidad - lote.cantidad;
        const stockRep = queryRunner.manager.getRepository(Stock);
        let stock = await stockRep.findOne({ where: { tenant_id, producto_id: lote.producto_id } });
        if (stock) {
          stock.cantidadTotal += diferencia;
          if (stock.cantidadTotal < 0) stock.cantidadTotal = 0;
          await stockRep.save(stock);
        }
      }

      Object.assign(lote, dto);
      const loteGuardado = await queryRunner.manager.save(lote);
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

      // Restar del Stock
      const stockRep = queryRunner.manager.getRepository(Stock);
      let stock = await stockRep.findOne({ where: { tenant_id, producto_id: lote.producto_id } });
      if (stock) {
        stock.cantidadTotal -= lote.cantidad;
        if (stock.cantidadTotal < 0) stock.cantidadTotal = 0; // Prevent negative physical stock technically
        await stockRep.save(stock);
      }

      await queryRunner.manager.remove(lote);
      await queryRunner.commitTransaction();
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }
}
