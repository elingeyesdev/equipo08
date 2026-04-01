import { IsString, IsNumber, Min } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateLoteIngresoDto {
  @ApiProperty()
  @IsString()
  producto_id: string;

  @ApiProperty()
  @IsString()
  proveedor_id: string;

  @ApiProperty()
  @IsNumber()
  @Min(1)
  cantidad: number;

  @ApiProperty({ description: 'Costo de adquisición. Obligatorio > 0' })
  @IsNumber()
  @Min(0.01, { message: 'No se puede registrar stock sin costo de adquisición' })
  costoAdquisicion: number;
}
