import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
  ManyToOne,
  JoinColumn,
  Index,
  Check,
} from 'typeorm';
import { Producto } from './producto.entity';

@Entity('producto_variaciones')
@Index(['sku'], { unique: true })
@Check('precio_venta >= 0')
@Check('precio_costo >= 0')
export class ProductoVariacion {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  producto_id: string;

  @Column()
  sku: string;

  @Column('decimal', { name: 'precio_costo', precision: 10, scale: 2, default: 0 })
  precioCosto: number;

  @Column('decimal', { name: 'precio_venta', precision: 10, scale: 2, default: 0 })
  precioVenta: number;

  @Column({ type: 'jsonb', default: {} })
  opciones: Record<string, any>;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date;

  @ManyToOne(() => Producto, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'producto_id' })
  producto: Producto;
}
