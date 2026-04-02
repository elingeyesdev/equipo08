import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, Not } from 'typeorm';
import { Proveedor } from './proveedor.entity';
import { CreateProveedorDto } from './dto/create-proveedor.dto';

@Injectable()
export class ProveedoresService {
  constructor(
    @InjectRepository(Proveedor)
    private readonly proveRep: Repository<Proveedor>,
  ) {}

  private async validateProveedor(tenant_id: string, dto: Partial<CreateProveedorDto>, excludeId?: string) {
    if (dto.name && !/^[A-Za-záéíóúÁÉÍÓÚñÑ\s]+$/.test(dto.name)) {
      throw new BadRequestException('El nombre del proveedor no debe incluir números ni símbolos.');
    }

    if (dto.taxId) {
      if (!/^\d{8,12}$/.test(dto.taxId)) {
        throw new BadRequestException('El NIT o RUT debe contener únicamente entre 8 y 12 números, sin letras ni símbolos.');
      }
      
      const existingTax = await this.proveRep.findOne({
        where: { tenant_id, taxId: dto.taxId, ...(excludeId ? { id: Not(excludeId) } : {}) }
      });
      if (existingTax) throw new BadRequestException(`El NIT/RUT '${dto.taxId}' ya está asignado a otro proveedor.`);
    }

    if (dto.contactEmail) {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(dto.contactEmail)) {
        throw new BadRequestException('El formato del correo electrónico u omite un símbolo @ o un dominio válido.');
      }

      const existingEmail = await this.proveRep.findOne({
        where: { tenant_id, contactEmail: dto.contactEmail, ...(excludeId ? { id: Not(excludeId) } : {}) }
      });
      if (existingEmail) throw new BadRequestException(`El correo '${dto.contactEmail}' ya está registrado en tu directorio.`);
    }
  }

  async create(tenant_id: string, dto: CreateProveedorDto): Promise<Proveedor> {
    await this.validateProveedor(tenant_id, dto);
    const proveedor = this.proveRep.create({ ...dto, tenant_id });
    return this.proveRep.save(proveedor);
  }

  async findAll(tenant_id: string): Promise<Proveedor[]> {
    return this.proveRep.find({ where: { tenant_id } });
  }

  async update(tenant_id: string, id: string, dto: Partial<CreateProveedorDto>): Promise<Proveedor> {
    const proveedor = await this.proveRep.findOne({ where: { id, tenant_id } });
    if (!proveedor) throw new NotFoundException('Proveedor no encontrado');
    
    await this.validateProveedor(tenant_id, dto, id);

    // Copy the updated properties
    Object.assign(proveedor, dto);
    return this.proveRep.save(proveedor);
  }

  async remove(tenant_id: string, id: string): Promise<void> {
    const proveedor = await this.proveRep.findOne({ where: { id, tenant_id } });
    if (!proveedor) throw new NotFoundException('Proveedor no encontrado');

    try {
      await this.proveRep.remove(proveedor);
    } catch (e) {
      if (e.code === '23503') { // Foreign key constraint
        throw new NotFoundException('No se puede eliminar el proveedor. Está asociado a productos o lotes de compra en tu catálogo.');
      }
      throw e;
    }
  }
}
