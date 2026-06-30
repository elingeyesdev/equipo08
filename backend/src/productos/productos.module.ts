import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ProductosService } from './productos.service';
import { ProductosController } from './productos.controller';
import { Producto } from './producto.entity';
import { Categoria } from './categoria.entity';
import { ProductoVariacion } from './producto-variacion.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Producto, Categoria, ProductoVariacion])],
  controllers: [ProductosController],
  providers: [ProductosService],
})
export class ProductosModule {}
