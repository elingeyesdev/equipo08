import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Index,
  ManyToOne,
  JoinColumn,
  Check,
} from 'typeorm';
import { Producto } from '../productos/producto.entity';
import { Proveedor } from '../proveedores/proveedor.entity';
import { Sucursal } from '../sucursales/sucursal.entity';

import { User } from '../users/user.entity';

@Entity('lotes_ingreso')
@Index(['tenant_id', 'id'])
@Check('cantidad >= 0')
@Check('costo_unitario >= 0')
export class LoteIngreso {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  tenant_id: string;

  @Column({ nullable: true })
  sucursal_id: string;

  @Column()
  producto_id: string;

  @Column()
  proveedor_id: string;

  @Column('int')
  cantidad: number;

  @Column('decimal', { name: 'costo_unitario', precision: 10, scale: 2, default: 0 })
  costoUnitario: number;

  @Column({ name: 'fecha_elaboracion', type: 'date', nullable: true })
  fechaElaboracion: string;

  @Column({ name: 'fecha_vencimiento', type: 'date', nullable: true })
  fechaVencimiento: string;

  @Column({ name: 'usuario_id', type: 'uuid', nullable: true })
  usuario_id: string;

  @CreateDateColumn({ name: 'fecha_ingreso' })
  fechaIngreso: Date;

  @ManyToOne(() => Producto, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'producto_id' })
  producto: Producto;

  @ManyToOne(() => Proveedor, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'proveedor_id' })
  proveedor: Proveedor;

  @ManyToOne(() => Sucursal, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'sucursal_id' })
  sucursal: Sucursal;

  @ManyToOne(() => User, { createForeignKeyConstraints: false, nullable: true })
  @JoinColumn({ name: 'usuario_id' })
  usuario: User;
}
