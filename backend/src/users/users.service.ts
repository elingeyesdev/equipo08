import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcrypt';
import { User, UserRole } from './user.entity';
import { RolePermissions } from './role-permissions.entity';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdatePermissionsDto } from './dto/update-permissions.dto';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User)
    private readonly userRep: Repository<User>,
    @InjectRepository(RolePermissions)
    private readonly permissionsRep: Repository<RolePermissions>,
  ) {}

  async create(tenant_id: string, dto: CreateUserDto): Promise<User> {
    const existing = await this.userRep.findOne({ where: { email: dto.email } });
    if (existing) {
      throw new BadRequestException('El correo electrónico ya está registrado.');
    }

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(dto.password, salt);

    const newUser = this.userRep.create({
      ...dto,
      password: hashedPassword,
      tenant_id,
    });

    return this.userRep.save(newUser);
  }

  async findAll(tenant_id: string): Promise<User[]> {
    return this.userRep.find({
      where: { tenant_id },
      relations: ['sucursal'],
      select: ['id', 'name', 'email', 'role', 'isActive', 'createdAt', 'sucursal_id'],
    });
  }

  async findOne(tenant_id: string, id: string): Promise<User> {
    const user = await this.userRep.findOne({ where: { id, tenant_id } });
    if (!user) throw new NotFoundException('Usuario no encontrado');
    return user;
  }

  async remove(tenant_id: string, id: string): Promise<void> {
    const user = await this.findOne(tenant_id, id);
    if (user.role === UserRole.OWNER) {
      throw new BadRequestException('No se puede eliminar al dueño de la tienda.');
    }
    await this.userRep.remove(user);
  }

  async update(tenant_id: string, id: string, dto: Partial<CreateUserDto>): Promise<User> {
    const user = await this.findOne(tenant_id, id);

    // Proteccion: No se puede cambiar el rol del Owner original
    if (user.role === UserRole.OWNER && dto.role && dto.role !== UserRole.OWNER) {
      throw new BadRequestException('No se puede degradar el perfil del Dueño de la tienda.');
    }

    if (dto.password && dto.password.trim() !== '') {
      const salt = await bcrypt.genSalt();
      dto.password = await bcrypt.hash(dto.password, salt);
    } else {
      delete dto.password; // Evitar sobreescribir con vacio/null
    }

    Object.assign(user, dto);
    return this.userRep.save(user);
  }

  // --- Permissions Logic ---

  async getPermissions(tenant_id: string): Promise<RolePermissions[]> {
    return this.permissionsRep.find({ where: { tenant_id } });
  }

  async updatePermissions(tenant_id: string, dto: UpdatePermissionsDto): Promise<RolePermissions> {
    let perm = await this.permissionsRep.findOne({ where: { tenant_id, role: dto.role } });
    if (!perm) {
      perm = this.permissionsRep.create({ tenant_id, role: dto.role });
    }
    Object.assign(perm, dto);
    return this.permissionsRep.save(perm);
  }

  async seedDefaultPermissions(tenant_id: string): Promise<void> {
    const supervisor = this.permissionsRep.create({
      tenant_id,
      role: UserRole.SUPERVISOR,
      sucursales_ver: true,
      sucursales_gestionar: false,
      catalogo_ver: true,
      catalogo_gestionar: true,
      sourcing_ver: true,
      sourcing_gestionar: true,
      inventario_ver: true,
      usuarios_ver: true,
      usuarios_gestionar: false,
    });

    const vendedor = this.permissionsRep.create({
      tenant_id,
      role: UserRole.VENDEDOR,
      sucursales_ver: true,
      sucursales_gestionar: false,
      catalogo_ver: true,
      catalogo_gestionar: false,
      sourcing_ver: true,
      sourcing_gestionar: false,
      inventario_ver: true,
      usuarios_ver: false,
      usuarios_gestionar: false,
    });

    await this.permissionsRep.save([supervisor, vendedor]);
  }
}
