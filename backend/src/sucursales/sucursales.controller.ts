import { Controller, Get, Post, Body, Put, Param, Delete } from '@nestjs/common';
import { SucursalesService } from './sucursales.service';
import { CreateSucursalDto } from './dto/create-sucursal.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags, ApiHeader } from '@nestjs/swagger';

@ApiTags('Sucursales')
@ApiHeader({ name: 'x-tenant-id', required: true, description: 'ID del tenant' })
@Controller('sucursales')
export class SucursalesController {
  constructor(private readonly sucursalesService: SucursalesService) {}

  @Post()
  create(
    @TenantId() tenantId: string,
    @Body() createSucursalDto: CreateSucursalDto,
  ) {
    return this.sucursalesService.create(tenantId, createSucursalDto);
  }

  @Get()
  findAll(@TenantId() tenantId: string) {
    return this.sucursalesService.findAll(tenantId);
  }

  @Put(':id')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateSucursalDto>,
  ) {
    return this.sucursalesService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.sucursalesService.remove(tenantId, id);
  }
}
