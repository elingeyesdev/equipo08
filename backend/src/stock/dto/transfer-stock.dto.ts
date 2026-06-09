import { IsNotEmpty, IsUUID, IsNumber, Min } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class TransferStockDto {
  @ApiProperty()
  @IsUUID()
  @IsNotEmpty()
  from_sucursal_id: string;

  @ApiProperty()
  @IsUUID()
  @IsNotEmpty()
  to_sucursal_id: string;

  @ApiProperty()
  @IsUUID()
  @IsNotEmpty()
  producto_id: string;

  @ApiProperty()
  @IsNumber()
  @Min(1)
  @IsNotEmpty()
  cantidad: number;
}
