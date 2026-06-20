import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
  Index,
  OneToMany,
} from 'typeorm';
import { Venta } from '../ventas/venta.entity';

@Entity('clientes')
@Index(['tenant_id', 'documento'])
export class Cliente {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ nullable: true })
  tenant_id: string;

  @Column()
  nombre: string;

  @Column({ nullable: true })
  documento: string;

  @Column({ nullable: true })
  email: string;

  @Column({ nullable: true })
  telefono: string;

  @Column({ default: true })
  isActive: boolean;


  @OneToMany(() => Venta, (venta) => venta.cliente)
  ventas: Venta[];

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
