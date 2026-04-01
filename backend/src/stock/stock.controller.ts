import { Controller, Get } from '@nestjs/common';
import { StockService } from './stock.service';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags, ApiHeader } from '@nestjs/swagger';

@ApiTags('Stock')
@ApiHeader({ name: 'x-tenant-id', required: true })
@Controller('stock')
export class StockController {
  constructor(private readonly stockService: StockService) {}

  @Get()
  getStock(@TenantId() tenantId: string) {
    return this.stockService.getStockByTenant(tenantId);
  }
}
