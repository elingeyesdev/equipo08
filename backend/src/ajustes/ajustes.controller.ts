import { Controller, Get, Post, Body } from '@nestjs/common';
import { AjustesService } from './ajustes.service';
import { TenantId } from '../tenant/tenant-id.decorator';
import { RequirePermission } from '../auth/decorators/permissions.decorator';

@Controller('ajustes')
export class AjustesController {
  constructor(private readonly ajustesService: AjustesService) {}

  @Get()
  @RequirePermission('inventario.ver')
  findAll(@TenantId() tenant_id: string) {
    return this.ajustesService.findAll(tenant_id);
  }
}
