import { IsBoolean, IsEnum, IsNotEmpty } from 'class-validator';
import { UserRole } from '../user.entity';

export class UpdatePermissionsDto {
  @IsEnum([UserRole.SUPERVISOR, UserRole.VENDEDOR], { message: 'Solo se pueden configurar permisos para SUPERVISOR o VENDEDOR' })
  @IsNotEmpty()
  role: string;

  @IsBoolean()
  sucursales_ver: boolean;

  @IsBoolean()
  sucursales_gestionar: boolean;

  @IsBoolean()
  catalogo_ver: boolean;

  @IsBoolean()
  catalogo_gestionar: boolean;

  @IsBoolean()
  sourcing_ver: boolean;

  @IsBoolean()
  sourcing_gestionar: boolean;

  @IsBoolean()
  inventario_ver: boolean;

  @IsBoolean()
  usuarios_ver: boolean;

  @IsBoolean()
  usuarios_gestionar: boolean;
}
