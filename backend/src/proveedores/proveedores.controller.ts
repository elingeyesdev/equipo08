import { Controller, Get, Post, Body, Put, Param, Delete } from '@nestjs/common';
import { ProveedoresService } from './proveedores.service';
import { CreateProveedorDto } from './dto/create-proveedor.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags, ApiHeader } from '@nestjs/swagger';

@ApiTags('Proveedores')
@ApiHeader({ name: 'x-tenant-id', required: true, description: 'ID del tenant' })
@Controller('proveedores')
export class ProveedoresController {
  constructor(private readonly proveedoresService: ProveedoresService) {}

  @Post()
  create(
    @TenantId() tenantId: string,
    @Body() createProveedorDto: CreateProveedorDto,
  ) {
    return this.proveedoresService.create(tenantId, createProveedorDto);
  }

  @Get('global/:nit')
  findByGlobalNit(@Param('nit') nit: string) {
    return this.proveedoresService.findByGlobalNit(nit);
  }

  @Get()
  findAll(@TenantId() tenantId: string) {
    return this.proveedoresService.findAll(tenantId);
  }

  @Put(':id')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateProveedorDto>,
  ) {
    return this.proveedoresService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.proveedoresService.remove(tenantId, id);
  }
}
