import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { AjusteInventario } from './ajuste.entity';

@Injectable()
export class AjustesService {
  constructor(
    @InjectRepository(AjusteInventario)
    private readonly ajusteRep: Repository<AjusteInventario>,
  ) {}

  async findAll(tenant_id: string): Promise<AjusteInventario[]> {
    return this.ajusteRep.find({
      where: { tenant_id },
      relations: ['sucursal', 'producto', 'usuario'],
      order: { fecha: 'DESC' }
    });
  }
}
