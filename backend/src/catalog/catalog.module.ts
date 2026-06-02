import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CatalogController } from './catalog.controller';
import { CatalogService } from './catalog.service';
import { Tenant } from '../tenant/tenant.entity';
import { Producto } from '../productos/producto.entity';
import { Stock } from '../stock/stock.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Tenant, Producto, Stock])],
  controllers: [CatalogController],
  providers: [CatalogService],
})
export class CatalogModule {}
