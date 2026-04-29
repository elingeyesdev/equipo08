import { Controller, Get } from '@nestjs/common';
import { StockService } from './stock.service';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags } from '@nestjs/swagger';
import { RequirePermission } from '../auth/decorators/permissions.decorator';

@ApiTags('Stock')
@Controller('stock')
export class StockController {
  constructor(private readonly stockService: StockService) {}

  @Get()
  @RequirePermission('inventario.ver')
  getStock(@TenantId() tenantId: string) {
    return this.stockService.getStockByTenant(tenantId);
  }
}
