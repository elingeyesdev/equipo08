import { Controller, Get, Post, Body, Put, Param, Delete } from '@nestjs/common';
import { ProveedoresService } from './proveedores.service';
import { CreateProveedorDto } from './dto/create-proveedor.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags } from '@nestjs/swagger';
import { RequirePermission } from '../auth/decorators/permissions.decorator';

@ApiTags('Proveedores')
@Controller('proveedores')
export class ProveedoresController {
  constructor(private readonly proveedoresService: ProveedoresService) {}

  @Post()
  @RequirePermission('catalogo.gestionar')
  create(
    @TenantId() tenantId: string,
    @Body() createProveedorDto: CreateProveedorDto,
  ) {
    return this.proveedoresService.create(tenantId, createProveedorDto);
  }

  @Get('global/:nit')
  @RequirePermission('catalogo.ver')
  findByGlobalNit(@Param('nit') nit: string) {
    return this.proveedoresService.findByGlobalNit(nit);
  }

  @Get()
  @RequirePermission('catalogo.ver')
  findAll(@TenantId() tenantId: string) {
    return this.proveedoresService.findAll(tenantId);
  }

  @Put(':id')
  @RequirePermission('catalogo.gestionar')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateProveedorDto>,
  ) {
    return this.proveedoresService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  @RequirePermission('catalogo.gestionar')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.proveedoresService.remove(tenantId, id);
  }
}
