import { Controller, Get, Post, Body, Put, Param, Delete } from '@nestjs/common';
import { SucursalesService } from './sucursales.service';
import { CreateSucursalDto } from './dto/create-sucursal.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags } from '@nestjs/swagger';
import { RequirePermission } from '../auth/decorators/permissions.decorator';

@ApiTags('Sucursales')
@Controller('sucursales')
export class SucursalesController {
  constructor(private readonly sucursalesService: SucursalesService) {}

  @Post()
  @RequirePermission('sucursales.gestionar')
  create(
    @TenantId() tenantId: string,
    @Body() createSucursalDto: CreateSucursalDto,
  ) {
    return this.sucursalesService.create(tenantId, createSucursalDto);
  }

  @Get()
  @RequirePermission('sucursales.ver')
  findAll(@TenantId() tenantId: string) {
    return this.sucursalesService.findAll(tenantId);
  }

  @Put(':id')
  @RequirePermission('sucursales.gestionar')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateSucursalDto>,
  ) {
    return this.sucursalesService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  @RequirePermission('sucursales.gestionar')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.sucursalesService.remove(tenantId, id);
  }
}
