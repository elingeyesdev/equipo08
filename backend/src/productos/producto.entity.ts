import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, UpdateDateColumn, Index, ManyToOne, JoinColumn } from 'typeorm';
import { Proveedor } from '../proveedores/proveedor.entity';

@Entity('productos')
@Index(['tenant_id', 'id'])
@Index(['tenant_id', 'sku'], { unique: true })
export class Producto {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  tenant_id: string;

  @Column()
  name: string;

  @Column()
  sku: string;
  
  @Column({ nullable: true })
  description: string;

  @Column()
  proveedor_id: string;

  @ManyToOne(() => Proveedor)
  @JoinColumn({ name: 'proveedor_id' })
  proveedor: Proveedor;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
