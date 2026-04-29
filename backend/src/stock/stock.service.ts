import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Stock } from './stock.entity';

@Injectable()
export class StockService {
  constructor(
    @InjectRepository(Stock)
    private readonly stockRep: Repository<Stock>,
  ) {}

  async getStockRow(tenant_id: string, sucursal_id: string, producto_id: string): Promise<Stock | null> {
    return this.stockRep.findOne({ where: { tenant_id, sucursal_id, producto_id } });
  }

  async sumStock(tenant_id: string, sucursal_id: string, producto_id: string, cantidad: number, valorAdquisicionDelta: number = 0): Promise<Stock> {
    let stock = await this.stockRep.findOne({ where: { tenant_id, sucursal_id, producto_id } });
    if (!stock) {
      stock = this.stockRep.create({ tenant_id, sucursal_id, producto_id, cantidadTotal: Number(cantidad || 0), valorAdquisicion: valorAdquisicionDelta > 0 ? valorAdquisicionDelta : 0 });
    } else {
      stock.cantidadTotal = Number(stock.cantidadTotal || 0) + Number(cantidad || 0);
      stock.valorAdquisicion = Number(stock.valorAdquisicion || 0) + Number(valorAdquisicionDelta || 0);
    }
    return this.stockRep.save(stock);
  }

  async getStockByTenant(tenant_id: string): Promise<Stock[]> {
    return this.stockRep.find({ 
      where: { tenant_id },
      relations: ['producto', 'sucursal'],
      order: {
         sucursal_id: 'ASC'
      }
    });
  }
}
