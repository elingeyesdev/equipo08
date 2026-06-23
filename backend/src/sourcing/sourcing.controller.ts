import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Put,
  Delete,
  Req,
} from '@nestjs/common';
import { SourcingService } from './sourcing.service';
import { CreateLoteIngresoDto } from './dto/create-lote.dto';
import { TenantId } from '../tenant/tenant-id.decorator';
import { ApiTags } from '@nestjs/swagger';
import { RequirePermission } from '../auth/decorators/permissions.decorator';

@ApiTags('Sourcing')
@Controller('sourcing')
export class SourcingController {
  constructor(private readonly sourcingService: SourcingService) {}

  @Post()
  @RequirePermission('sourcing.crear')
  registrarIngreso(
    @TenantId() tenantId: string,
    @Body() dto: CreateLoteIngresoDto,
    @Req() req: any,
  ) {
    return this.sourcingService.registrarIngreso(tenantId, dto, req.user?.userId);
  }

  @Get()
  @RequirePermission('sourcing.ver')
  getHistorial(@TenantId() tenantId: string) {
    return this.sourcingService.getHistorial(tenantId);
  }

  @Put(':id')
  @RequirePermission('sourcing.editar')
  update(
    @TenantId() tenantId: string,
    @Param('id') id: string,
    @Body() updateDto: Partial<CreateLoteIngresoDto>,
  ) {
    return this.sourcingService.update(tenantId, id, updateDto);
  }

  @Delete(':id')
  @RequirePermission('sourcing.eliminar')
  remove(@TenantId() tenantId: string, @Param('id') id: string) {
    return this.sourcingService.remove(tenantId, id);
  }
}
