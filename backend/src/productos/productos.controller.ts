import { Controller, Get, Post, Body, Put, Param, Delete } from '@nestjs/common';
import { ProductosService } from './productos.service';
import { CreateProductoDto } from './dto/create-producto.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags, ApiHeader } from '@nestjs/swagger';

@ApiTags('Productos')
@ApiHeader({ name: 'x-tenant-id', required: true })
@Controller('productos')
export class ProductosController {
  constructor(private readonly productosService: ProductosService) {}

  @Post()
  create(
    @TenantId() tenantId: string,
    @Body() dto: CreateProductoDto,
  ) {
    return this.productosService.create(tenantId, dto);
  }

  @Get()
  findAll(@TenantId() tenantId: string) {
    return this.productosService.findAll(tenantId);
  }

  @Put(':id')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateProductoDto>,
  ) {
    return this.productosService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.productosService.remove(tenantId, id);
  }
}
