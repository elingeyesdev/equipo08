import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { Injectable } from '@nestjs/common';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: 'super-secret-key-123', // Debe coincidir con la de AuthModule
    });
  }

  async validate(payload: any) {
    // Lo que sea que devolvamos aquí se inyectará en request.user
    return { 
      userId: payload.sub, 
      tenantId: payload.tenantId, 
      role: payload.role,
      tenantName: payload.tenantName 
    };
  }
}
