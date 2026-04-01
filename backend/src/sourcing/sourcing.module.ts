import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SourcingService } from './sourcing.service';
import { SourcingController } from './sourcing.controller';
import { LoteIngreso } from './lote-ingreso.entity';
import { StockModule } from '../stock/stock.module';

@Module({
  imports: [TypeOrmModule.forFeature([LoteIngreso]), StockModule],
  controllers: [SourcingController],
  providers: [SourcingService],
})
export class SourcingModule {}
