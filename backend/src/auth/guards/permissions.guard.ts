import { Injectable, CanActivate, ExecutionContext } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { PERMISSION_KEY } from '../decorators/permissions.decorator';
import { UsersService } from '../../users/users.service';
import { UserRole } from '../../users/user.entity';

@Injectable()
export class PermissionsGuard implements CanActivate {
  constructor(
    private reflector: Reflector,
    private usersService: UsersService,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const requiredPermission = this.reflector.getAllAndOverride<string>(
      PERMISSION_KEY,
      [context.getHandler(), context.getClass()],
    );

    if (!requiredPermission) {
      return true;
    }

    const { user } = context.switchToHttp().getRequest();

    
    if (user.role === UserRole.OWNER) return true;

    
    const allPermissions = await this.usersService.getPermissions(
      user.tenantId,
    );
    const rolePerm = allPermissions.find((p) => p.role === user.role);

    if (!rolePerm) return false;

    
    const entityField = requiredPermission.replace('.', '_');

    
    if (
      (requiredPermission === 'sucursales.ver' ||
        requiredPermission === 'inventario.ver') &&
      (rolePerm.ventas_ver || rolePerm.ventas_crear)
    ) {
      return true;
    }

    return !!(rolePerm as any)[entityField];
  }
}
