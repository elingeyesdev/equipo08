import { Injectable, BadRequestException, UnauthorizedException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcrypt';
import { Tenant } from '../tenant/tenant.entity';
import { RegisterDto } from './dto/register.dto';
import { LoginDto } from './dto/login.dto';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(Tenant)
    private readonly tenantRep: Repository<Tenant>,
  ) {}

  async register(dto: RegisterDto): Promise<{ message: string; tenant_id: string }> {
    const existing = await this.tenantRep.findOne({
      where: [{ email: dto.email }, { domain: dto.domain }]
    });

    if (existing) {
      throw new BadRequestException('El correo o dominio ya está registrado para otra tienda.');
    }

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(dto.password, salt);

    const newTenant = this.tenantRep.create({
      name: dto.name,
      domain: dto.domain,
      email: dto.email,
      password: hashedPassword,
    });

    const saved = await this.tenantRep.save(newTenant);
    return {
      message: 'Tienda registrada correctamente',
      tenant_id: saved.id,
    };
  }

  async login(dto: LoginDto): Promise<{ message: string; tenant_id: string; name: string }> {
    const tenant = await this.tenantRep.findOne({ where: { email: dto.email } });

    if (!tenant) {
      throw new UnauthorizedException('Credenciales inválidas');
    }

    const isMatch = await bcrypt.compare(dto.password, tenant.password);

    if (!isMatch) {
      throw new UnauthorizedException('Credenciales inválidas');
    }

    if (!tenant.isActive) {
      throw new UnauthorizedException('La cuenta de la tienda está inactiva');
    }

    return {
      message: 'Inicio de sesión exitoso',
      tenant_id: tenant.id,
      name: tenant.name,
    };
  }
}
