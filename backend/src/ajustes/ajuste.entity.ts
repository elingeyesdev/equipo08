import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
} from 'typeorm';
import { User } from '../users/user.entity';
import { Stock } from '../stock/stock.entity';

export enum MotivoAjuste {
  ROBO_O_PERDIDA = 'ROBO_O_PERDIDA',
  DANO_MERMA = 'DANO_MERMA',
  ERROR_REGISTRO = 'ERROR_REGISTRO',
  CADUCIDAD = 'CADUCIDAD',
}

@Entity('ajustes_inventario')
export class AjusteInventario {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ nullable: true })
  tenant_id: string;

  @Column()
  usuario_id: string;

  @Column()
  stock_id: string;

  @Column('int')
  cantidad_sistema: number;

  @Column('int')
  cantidad_fisica: number;

  @Column({
    type: 'enum',
    enum: MotivoAjuste,
    nullable: false,
  })
  motivo: MotivoAjuste;

  @Column('text', { nullable: true })
  observaciones: string;

  @Column('decimal', { precision: 10, scale: 2, default: 0 })
  valor_perdido: number;

  @CreateDateColumn()
  fecha: Date;

  @ManyToOne(() => Stock, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'stock_id' })
  stock: Stock;

  @ManyToOne(() => User, { nullable: true, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'usuario_id' })
  usuario: User;
}
