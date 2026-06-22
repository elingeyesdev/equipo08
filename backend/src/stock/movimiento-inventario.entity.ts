import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
  Index,
} from 'typeorm';
import { Stock } from './stock.entity';
import { User } from '../users/user.entity';

@Entity('movimientos_inventario')
@Index(['tenant_id', 'stock_id'])
export class MovimientoInventario {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ name: 'tenant_id', type: 'uuid', nullable: true })
  tenant_id: string;

  @Column({ name: 'stock_id' })
  stock_id: string;

  @Column({ type: 'varchar' })
  tipo: string; // INGRESO, EGRESO, AJUSTE, DEVOLUCION, ANULACION, TRANSFERENCIA

  @Column('int', { name: 'cantidad_delta' })
  cantidadDelta: number;

  @Column('int', { name: 'stock_anterior', default: 0 })
  stockAnterior: number;

  @Column('int', { name: 'stock_resultante', default: 0 })
  stockResultante: number;

  @Column('decimal', { precision: 10, scale: 2, name: 'costo_unitario', default: 0 })
  costoUnitario: number;

  @Column({ nullable: true })
  motivo: string;

  @Column({ name: 'usuario_id', type: 'uuid', nullable: true })
  usuario_id: string;

  @Column({ name: 'referencia_tipo', type: 'varchar', nullable: true })
  referenciaTipo: string; // VENTA, COMPRA, AJUSTE, TRANSFERENCIA, ANULACION

  @Column({ name: 'referencia_id', type: 'uuid', nullable: true })
  referenciaId: string;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @ManyToOne(() => Stock, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'stock_id' })
  stock: Stock;

  @ManyToOne(() => User, { createForeignKeyConstraints: false, nullable: true })
  @JoinColumn({ name: 'usuario_id' })
  usuario: User;
}
