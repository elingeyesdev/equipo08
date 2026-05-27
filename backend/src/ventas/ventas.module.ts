import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Venta } from './venta.entity';
import { VentasController } from './ventas.controller';
import { VentasService } from './ventas.service';
import { StockModule } from '../stock/stock.module';
import { Producto } from '../productos/producto.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Venta, Producto]), StockModule],
  controllers: [VentasController],
  providers: [VentasService],
  exports: [VentasService],
})
export class VentasModule {}
