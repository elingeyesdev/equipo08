import { Controller, Get } from '@nestjs/common';
import { Public } from '../auth/decorators/public.decorator';
import { VentasService } from './ventas.service';

@Controller('test-ventas')
export class TestVentasController {
  constructor(private readonly ventasService: VentasService) {}

  @Public()
  @Get()
  async test() {
    return this.ventasService.findAll('554d1fcc-ab90-45ea-b070-c7eefc44623c');
  }
}
