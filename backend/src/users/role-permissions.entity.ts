import { Entity, Column, PrimaryGeneratedColumn, Index, ManyToOne, JoinColumn } from 'typeorm';
import { Tenant } from '../tenant/tenant.entity';

@Entity('role_permissions')
@Index(['tenant_id', 'role'], { unique: true })
export class RolePermissions {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  tenant_id: string;

  @Column()
  role: string; // SUPERVISOR | VENDEDOR

  @Column({ default: true })
  sucursales_ver: boolean;

  @Column({ default: false })
  sucursales_gestionar: boolean;

  @Column({ default: true })
  catalogo_ver: boolean;

  @Column({ default: false })
  catalogo_gestionar: boolean;

  @Column({ default: true })
  sourcing_ver: boolean;

  @Column({ default: false })
  sourcing_gestionar: boolean;

  @Column({ default: true })
  inventario_ver: boolean;

  @Column({ default: true })
  usuarios_ver: boolean;

  @Column({ default: false })
  usuarios_gestionar: boolean;

  @ManyToOne(() => Tenant)
  @JoinColumn({ name: 'tenant_id' })
  tenant: Tenant;
}
