import { Entity, Column, PrimaryGeneratedColumn, UpdateDateColumn, Index, ManyToOne, JoinColumn } from 'typeorm';
import { Producto } from '../productos/producto.entity';

@Entity('stock')
@Index(['tenant_id', 'producto_id'], { unique: true })
export class Stock {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  tenant_id: string;

  @Column()
  producto_id: string;

  @Column('int', { default: 0 })
  cantidadTotal: number;

  @UpdateDateColumn()
  ultimaActualizacion: Date;

  @ManyToOne(() => Producto)
  @JoinColumn({ name: 'producto_id' })
  producto: Producto;
}
