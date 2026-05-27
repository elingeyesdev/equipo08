import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Venta } from './venta.entity';
import { CreateVentaDto } from './dto/create-venta.dto';
import { StockService } from '../stock/stock.service';
import { Producto } from '../productos/producto.entity';
import PDFDocument = require('pdfkit');
import * as fs from 'fs';
import * as path from 'path';

@Injectable()
export class VentasService {
  constructor(
    @InjectRepository(Venta)
    private readonly ventaRep: Repository<Venta>,
    @InjectRepository(Producto)
    private readonly productoRep: Repository<Producto>,
    private readonly stockService: StockService,
  ) {
    // Ensure temp directory exists
    const tempDir = path.join(process.cwd(), 'temp', 'comprobantes');
    if (!fs.existsSync(tempDir)) {
      fs.mkdirSync(tempDir, { recursive: true });
    }
  }

  async create(dto: CreateVentaDto, tenant_id: string): Promise<Venta> {
    const detalle = [];
    let total = 0;

    // Validate and process each item
    for (const item of dto.items) {
      const producto = await this.productoRep.findOne({ where: { id: item.producto_id, tenant_id } });
      if (!producto) throw new NotFoundException(`Producto ${item.producto_id} no encontrado`);

      const stock = await this.stockService.getStockRow(tenant_id, dto.sucursal_id, producto.id);
      const stockDisponible = stock ? stock.cantidadTotal : 0;
      
      if (stockDisponible < item.cantidad) {
        throw new BadRequestException(`Stock insuficiente para ${producto.name}. Disponible: ${stockDisponible}`);
      }

      const precioUnitario = Number(producto.precioVenta);
      const subtotal = precioUnitario * item.cantidad;
      total += subtotal;

      // Calculate cost proportion to reduce inventory value accurately
      const avgCost = stockDisponible > 0 ? (Number(stock?.valorAdquisicion || 0) / stockDisponible) : 0;
      const proportionalCost = avgCost * item.cantidad;

      // Reduce stock
      await this.stockService.sumStock(tenant_id, dto.sucursal_id, producto.id, -item.cantidad, -proportionalCost);

      detalle.push({
        producto_id: producto.id,
        sku: producto.sku,
        name: producto.name,
        cantidad: item.cantidad,
        precioUnitario,
        subtotal
      });
    }

    const numeroComprobante = `FAC-${Date.now()}`;

    const venta = this.ventaRep.create({
      tenant_id,
      sucursal_id: dto.sucursal_id,
      numeroComprobante,
      clienteNombre: dto.clienteNombre,
      clienteDocumento: dto.clienteDocumento,
      detalle,
      total
    });

    const savedVenta = await this.ventaRep.save(venta);
    
    // Generate PDF asynchronously
    this.generatePdf(savedVenta.id, tenant_id).catch(err => console.error('Error al generar PDF', err));

    return savedVenta;
  }

  async findAll(tenant_id: string): Promise<Venta[]> {
    return this.ventaRep.find({ 
      where: { tenant_id },
      order: { fecha: 'DESC' },
      relations: ['sucursal']
    });
  }

  async findOne(id: string, tenant_id: string): Promise<Venta> {
    const venta = await this.ventaRep.findOne({ where: { id, tenant_id }, relations: ['sucursal'] });
    if (!venta) throw new NotFoundException('Venta no encontrada');
    return venta;
  }

  async generatePdf(id: string, tenant_id: string): Promise<string> {
    const venta = await this.findOne(id, tenant_id);
    const pdfPath = path.join(process.cwd(), 'temp', 'comprobantes', `${venta.id}.pdf`);
    
    return new Promise((resolve, reject) => {
      const doc = new PDFDocument({ margin: 50 });
      const writeStream = fs.createWriteStream(pdfPath);
      doc.pipe(writeStream);

      // Header
      doc.fontSize(20).fillColor('#10b981').text('COMPROBANTE DE VENTA', { align: 'center' });
      doc.moveDown();
      
      doc.fontSize(10).fillColor('#4b5563');
      doc.text(`Comprobante Nro: ${venta.numeroComprobante}`);
      doc.text(`Fecha: ${new Date(venta.fecha).toLocaleString()}`);
      doc.text(`Sucursal: ${venta.sucursal ? venta.sucursal.name : 'Principal'}`);
      doc.moveDown();

      // Client Data
      doc.fontSize(12).fillColor('#111827').text('Datos del Cliente');
      doc.fontSize(10).fillColor('#4b5563');
      doc.text(`Nombre/Razón Social: ${venta.clienteNombre}`);
      if (venta.clienteDocumento) doc.text(`NIT/CI: ${venta.clienteDocumento}`);
      doc.moveDown();

      // Table Header
      const tableTop = doc.y;
      doc.font('Helvetica-Bold');
      doc.text('Cant.', 50, tableTop);
      doc.text('Descripción', 100, tableTop);
      doc.text('P. Unit (Bs)', 350, tableTop, { width: 90, align: 'right' });
      doc.text('Subtotal (Bs)', 450, tableTop, { width: 90, align: 'right' });
      
      doc.moveTo(50, tableTop + 15).lineTo(540, tableTop + 15).strokeColor('#d1d5db').stroke();

      // Table Rows
      doc.font('Helvetica');
      let currentY = tableTop + 25;
      
      venta.detalle.forEach(item => {
        doc.text(item.cantidad.toString(), 50, currentY);
        doc.text(`[${item.sku}] ${item.name}`, 100, currentY, { width: 240 });
        doc.text(item.precioUnitario.toFixed(2), 350, currentY, { width: 90, align: 'right' });
        doc.text(item.subtotal.toFixed(2), 450, currentY, { width: 90, align: 'right' });
        currentY += 20;
      });

      doc.moveTo(50, currentY).lineTo(540, currentY).stroke();
      currentY += 10;

      // Totals
      doc.font('Helvetica-Bold').fontSize(14).fillColor('#111827');
      doc.text('TOTAL:', 350, currentY, { width: 90, align: 'right' });
      doc.fillColor('#10b981').text(`Bs. ${Number(venta.total).toFixed(2)}`, 450, currentY, { width: 90, align: 'right' });

      // Footer
      doc.moveDown(3);
      doc.fontSize(10).fillColor('#6b7280').text('¡Gracias por su compra!', { align: 'center' });
      doc.text('Este documento es una representación digital de la venta.', { align: 'center' });

      doc.end();

      writeStream.on('finish', () => resolve(pdfPath));
      writeStream.on('error', reject);
    });
  }

  async getPdfPath(id: string, tenant_id: string): Promise<string> {
    const pdfPath = path.join(process.cwd(), 'temp', 'comprobantes', `${id}.pdf`);
    if (!fs.existsSync(pdfPath)) {
      await this.generatePdf(id, tenant_id);
    }
    return pdfPath;
  }
}
