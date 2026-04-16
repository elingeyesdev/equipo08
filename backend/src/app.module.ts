import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { TenantModule } from './tenant/tenant.module';
import { AuthModule } from './auth/auth.module';
import { ProveedoresModule } from './proveedores/proveedores.module';
import { SourcingModule } from './sourcing/sourcing.module';
import { StockModule } from './stock/stock.module';
import { ProductosModule } from './productos/productos.module';
import { SucursalesModule } from './sucursales/sucursales.module';
import { Tenant } from './tenant/tenant.entity';
import { Proveedor } from './proveedores/proveedor.entity';
import { Producto } from './productos/producto.entity';
import { Sucursal } from './sucursales/sucursal.entity';
import { LoteIngreso } from './sourcing/lote-ingreso.entity';
import { Stock } from './stock/stock.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: 'localhost',
      port: 5432,
      username: 'postgres',
      password: '1234',
      database: 'mall_db',
      entities: [Tenant, Proveedor, Producto, LoteIngreso, Stock, Sucursal],
      synchronize: true,
    }),
    AuthModule,
    TenantModule,
    ProveedoresModule,
    SourcingModule,
    StockModule,
    ProductosModule,
    SucursalesModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
