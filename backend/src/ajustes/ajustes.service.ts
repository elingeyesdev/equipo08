import { Injectable, BadRequestException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { AjusteInventario } from './ajuste.entity';
import { CreateAjusteDto } from './dto/create-ajuste.dto';
import { StockService } from '../stock/stock.service';

@Injectable()
export class AjustesService {
  constructor(
    @InjectRepository(AjusteInventario)
    private readonly ajusteRep: Repository<AjusteInventario>,
    private readonly stockService: StockService,
  ) {}

  async create(tenant_id: string, usuario_id: string, dto: CreateAjusteDto): Promise<AjusteInventario> {
    // 1. Guardar el Acta de Ajuste (Auditoría Lineal)
    const nuevoAjuste = this.ajusteRep.create({
      tenant_id,
      usuario_id,
      ...dto,
    });
    const guardado = await this.ajusteRep.save(nuevoAjuste);

    // 2. Sincronizador Transversal: Forzar la actualización del Stock Físico a la cantidad reportada.
    // Usamos el StockService para hacer un delta: cantidad fisica - cantidad sistema = ajuste necesario para sumar/restar.
    const diferencia = dto.cantidad_fisica - dto.cantidad_sistema;
    
    // El método sumStock del StockService acepta valores negativos, sumando/restando hasta cuadrar con la `cantidad_fisica`.
    await this.stockService.sumStock(tenant_id, dto.sucursal_id, dto.producto_id, diferencia);

    return guardado;
  }

  async findAll(tenant_id: string): Promise<AjusteInventario[]> {
    return this.ajusteRep.find({
      where: { tenant_id },
      relations: ['sucursal', 'producto', 'usuario'],
      order: { fecha: 'DESC' }
    });
  }
}
