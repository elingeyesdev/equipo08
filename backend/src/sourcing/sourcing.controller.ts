import { Controller, Get, Post, Body, Param, Put, Delete } from '@nestjs/common';
import { SourcingService } from './sourcing.service';
import { CreateLoteIngresoDto } from './dto/create-lote.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags, ApiHeader } from '@nestjs/swagger';

@ApiTags('Sourcing')
@ApiHeader({ name: 'x-tenant-id', required: true })
@Controller('sourcing')
export class SourcingController {
  constructor(private readonly sourcingService: SourcingService) {}

  @Post()
  registrarIngreso(
    @TenantId() tenantId: string,
    @Body() dto: CreateLoteIngresoDto,
  ) {
    return this.sourcingService.registrarIngreso(tenantId, dto);
  }

  @Get()
  getHistorial(@TenantId() tenantId: string) {
    return this.sourcingService.getHistorial(tenantId);
  }

  @Put(':id')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateLoteIngresoDto>,
  ) {
    return this.sourcingService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.sourcingService.remove(tenantId, id);
  }
}
