import { createParamDecorator, ExecutionContext, BadRequestException } from '@nestjs/common';

export const TenantId = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request = ctx.switchToHttp().getRequest();
    const tenantId = request.headers['x-tenant-id'];
    
    // Si no es el endpoint /api/docs y no tiene el header
    if (!tenantId) {
      throw new BadRequestException('Falta el header x-tenant-id en la petición HTTP.');
    }
    
    return tenantId;
  },
);
