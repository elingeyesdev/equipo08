import { IsString, IsNumber, Min, IsOptional } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateLoteIngresoDto {
  @ApiProperty()
  @IsString()
  sucursal_id: string;

  @ApiProperty()
  @IsString()
  producto_id: string;

  @ApiProperty()
  @IsNumber()
  @Min(1)
  cantidad: number;

  @ApiProperty({ required: false, description: 'Costo unitario de compra. Si no se envía, se usa el precioCosto del catálogo.' })
  @IsNumber()
  @Min(0)
  @IsOptional()
  costoUnitario?: number;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  fechaElaboracion?: string;

  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  fechaVencimiento?: string;
}
