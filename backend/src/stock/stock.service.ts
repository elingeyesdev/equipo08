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

  async sumStock(tenant_id: string, producto_id: string, cantidad: number): Promise<Stock> {
    let stock = await this.stockRep.findOne({ where: { tenant_id, producto_id } });
    if (!stock) {
      stock = this.stockRep.create({ tenant_id, producto_id, cantidadTotal: cantidad });
    } else {
      stock.cantidadTotal += Number(cantidad);
    }
    return this.stockRep.save(stock);
  }

  async getStockByTenant(tenant_id: string): Promise<Stock[]> {
    return this.stockRep.find({ 
      where: { tenant_id },
      relations: ['producto']
    });
  }
}
