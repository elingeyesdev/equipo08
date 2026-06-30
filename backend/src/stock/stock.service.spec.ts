import { BadRequestException } from '@nestjs/common';
import { StockService } from './stock.service';
import { Stock } from './stock.entity';


describe('StockService - Pruebas de Producción', () => {
  let service: StockService;
  let mockStockRep: any;
  let mockDataSource: any;

  
  const createMockManager = () => ({
    findOne: jest.fn((entity: any, options: any) => {
      
      if (entity.name === 'ProductoVariacion') {
        return Promise.resolve({ id: 'generated-id' });
      }
      return Promise.resolve(null);
    }),
    create: jest.fn((entity: any, data: any) => ({ ...data })),
    save: jest.fn((entity: any, data: any) => Promise.resolve({ id: 'generated-id', ...data })),
  });

  beforeEach(() => {
    mockStockRep = {
      findOne: jest.fn((options: any) => {
        return Promise.resolve(null);
      }),
      manager: createMockManager(),
    };
    mockDataSource = {
      createQueryRunner: jest.fn().mockReturnValue({
        connect: jest.fn(),
        startTransaction: jest.fn(),
        commitTransaction: jest.fn(),
        rollbackTransaction: jest.fn(),
        release: jest.fn(),
        manager: createMockManager(),
      }),
    };

    service = new StockService(mockStockRep, mockDataSource);
  });

  
  
  
  describe('Transacciones y Consistencia', () => {

    it('11. [Transacción] applyStockDelta - Debe crear stock Y movimiento en la misma transacción', async () => {
      const manager = createMockManager();
      manager.findOne.mockResolvedValue(null); 

      const result = await service.applyStockDelta(
        manager, 'tenant-1', 'suc-1', 'prod-1',
        10, 500, 'INGRESO', 'Lote inicial',
      );

      
      expect(manager.save).toHaveBeenCalledTimes(3); 
      expect(result.cantidadActual).toBe(10);
      expect(result.costoPromedio).toBe(50); 
    });

    it('12. [Transacción] applyStockDelta - Debe registrar movimiento con stock_anterior y stock_resultante correctos', async () => {
      const manager = createMockManager();
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 20, costoPromedio: 50,
      };

      await service.applyStockDelta(
        manager, 'tenant-1', 'suc-1', 'prod-1',
        -5, -250, 'EGRESO', 'Venta CPB-000001',
        stockExistente as any,
      );

      
      const movimientoCall = manager.save.mock.calls[1]; 
      const movimientoData = movimientoCall[1];
      expect(movimientoData.stockAnterior).toBe(20);
      expect(movimientoData.stockResultante).toBe(15);
      expect(movimientoData.cantidadDelta).toBe(-5);
      expect(movimientoData.tipo).toBe('EGRESO');
    });

    it('13. [Transacción] applyStockDelta - Debe calcular WAC correctamente en entradas', async () => {
      const manager = createMockManager();
      
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 10, costoPromedio: 50,
      };

      await service.applyStockDelta(
        manager, 'tenant-1', 'suc-1', 'prod-1',
        5, 400, 'INGRESO', 'Nuevo lote', 
        stockExistente as any,
      );

      
      expect(stockExistente.costoPromedio).toBe(60);
      expect(stockExistente.cantidadActual).toBe(15);
    });

    it('14. [Transacción] applyStockDelta - En salidas el costoPromedio se mantiene', async () => {
      const manager = createMockManager();
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 20, costoPromedio: 75,
      };

      await service.applyStockDelta(
        manager, 'tenant-1', 'suc-1', 'prod-1',
        -8, -600, 'EGRESO', 'Venta',
        stockExistente as any,
      );

      
      expect(stockExistente.costoPromedio).toBe(75);
      expect(stockExistente.cantidadActual).toBe(12);
    });
  });

  
  
  
  describe('Bloqueo de Stock - Anti-Sobreventa', () => {

    it('15. [Bloqueo] applyStockDelta - Debe rechazar si el stock quedaría negativo', async () => {
      const manager = createMockManager();
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 3, costoPromedio: 50,
      };

      await expect(
        service.applyStockDelta(
          manager, 'tenant-1', 'suc-1', 'prod-1',
          -5, -250, 'EGRESO', 'Venta que excede stock',
          stockExistente as any,
        ),
      ).rejects.toThrow(BadRequestException);
    });

    it('16. [Bloqueo] applyStockDelta - Debe permitir venta exacta al stock disponible (stock=0 final)', async () => {
      const manager = createMockManager();
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 5, costoPromedio: 50,
      };

      await service.applyStockDelta(
        manager, 'tenant-1', 'suc-1', 'prod-1',
        -5, -250, 'EGRESO', 'Venta exacta',
        stockExistente as any,
      );

      expect(stockExistente.cantidadActual).toBe(0);
    });

    it('17. [Bloqueo] applyStockDelta - Debe rechazar -1 cuando stock es 0', async () => {
      const manager = createMockManager();
      const stockExistente = {
        id: 'stock-1', tenant_id: 'tenant-1', sucursal_id: 'suc-1',
        producto_id: 'prod-1', cantidadActual: 0, costoPromedio: 50,
      };

      await expect(
        service.applyStockDelta(
          manager, 'tenant-1', 'suc-1', 'prod-1',
          -1, -50, 'EGRESO', 'Venta imposible',
          stockExistente as any,
        ),
      ).rejects.toThrow('El stock no puede quedar negativo');
    });
  });

  
  
  
  describe('Rollback en Transacciones', () => {

    it('18. [Rollback] transferStock - Debe hacer rollback si sucursal origen no tiene stock suficiente', async () => {
      const runner = mockDataSource.createQueryRunner();
      runner.manager.findOne.mockResolvedValue({
        id: 'stock-1', cantidadActual: 2, costoPromedio: 50,
      });

      await expect(
        service.transferStock('tenant-1', 'suc-origen', 'suc-destino', 'prod-1', 5),
      ).rejects.toThrow(BadRequestException);

      expect(runner.rollbackTransaction).toHaveBeenCalled();
      expect(runner.release).toHaveBeenCalled();
      expect(runner.commitTransaction).not.toHaveBeenCalled();
    });

    it('19. [Rollback] transferStock - Debe rechazar si origen y destino son la misma sucursal (sin abrir transacción)', async () => {
      const runner = mockDataSource.createQueryRunner();

      await expect(
        service.transferStock('tenant-1', 'suc-1', 'suc-1', 'prod-1', 5),
      ).rejects.toThrow('No se puede transferir a la misma sucursal');

      
      expect(runner.startTransaction).not.toHaveBeenCalled();
      expect(runner.commitTransaction).not.toHaveBeenCalled();
    });

    it('20. [Rollback] transferStock - Debe rechazar cantidad 0 o negativa (sin abrir transacción)', async () => {
      const runner = mockDataSource.createQueryRunner();

      await expect(
        service.transferStock('tenant-1', 'suc-1', 'suc-2', 'prod-1', 0),
      ).rejects.toThrow('La cantidad debe ser mayor a cero');

      
      expect(runner.startTransaction).not.toHaveBeenCalled();
    });

    it('21. [Rollback] transferStock - Debe hacer rollback si no existe stock en origen', async () => {
      const runner = mockDataSource.createQueryRunner();
      runner.manager.findOne.mockResolvedValue(null);

      await expect(
        service.transferStock('tenant-1', 'suc-1', 'suc-2', 'prod-1', 5),
      ).rejects.toThrow(BadRequestException);

      expect(runner.rollbackTransaction).toHaveBeenCalled();
      expect(runner.commitTransaction).not.toHaveBeenCalled();
    });
  });

  
  
  
  describe('Aislamiento Multi-Tenant', () => {

    it('22. [Multi-Tenant] getStockRow - Debe filtrar siempre por tenant_id', async () => {
      mockStockRep.findOne.mockResolvedValue(null);

      await service.getStockRow('tenant-A', 'suc-1', 'prod-1');

      expect(mockStockRep.findOne).toHaveBeenCalledWith({
        where: { tenant_id: 'tenant-A', sucursal_id: 'suc-1', producto_variacion_id: 'generated-id' },
      });
    });

    it('23. [Multi-Tenant] getStockByTenant - Debe devolver solo stock del tenant solicitado', async () => {
      mockStockRep.find = jest.fn().mockResolvedValue([]);

      await service.getStockByTenant('tenant-B');

      expect(mockStockRep.find).toHaveBeenCalledWith(
        expect.objectContaining({
          where: { tenant_id: 'tenant-B' },
        }),
      );
    });

    it('24. [Multi-Tenant] applyStockDelta - Debe crear stock con tenant_id correcto', async () => {
      const manager = createMockManager();

      await service.applyStockDelta(
        manager, 'tenant-X', 'suc-1', 'prod-1',
        10, 500, 'INGRESO', 'Primer lote',
      );

      
      const stockCreateCall = manager.create.mock.calls[0];
      const stockData = stockCreateCall[1];
      expect(stockData.tenant_id).toBe('tenant-X');

      
      const movCreateCall = manager.create.mock.calls[1];
      const movData = movCreateCall[1];
      expect(movData.tenant_id).toBe('tenant-X');
    });

    it('25. [Multi-Tenant] applyStockDelta - Debe buscar stock filtrando por tenant_id', async () => {
      const manager = createMockManager();

      await service.applyStockDelta(
        manager, 'tenant-Z', 'suc-1', 'prod-1',
        5, 100, 'INGRESO', 'Test',
      );

      expect(manager.findOne).toHaveBeenCalledWith(Stock, {
        where: { tenant_id: 'tenant-Z', sucursal_id: 'suc-1', producto_variacion_id: 'generated-id' },
      });
    });

    it('26. [Multi-Tenant] transferStock - Debe respetar tenant_id en origen y destino', async () => {
      const runner = mockDataSource.createQueryRunner();
      const mockSourceStock = {
        id: 'stock-1', cantidadActual: 20, costoPromedio: 50,
      };
      
      
      runner.manager.findOne
        .mockResolvedValueOnce({ id: 'generated-id' }) 
        .mockResolvedValueOnce(mockSourceStock)       
        .mockResolvedValueOnce(null);                 

      
      jest.spyOn(service, 'applyStockDelta').mockResolvedValue(mockSourceStock as any);

      await service.transferStock('tenant-AISLADO', 'suc-1', 'suc-2', 'prod-1', 5);

      
      expect(runner.manager.findOne).toHaveBeenNthCalledWith(2, Stock, {
        where: { tenant_id: 'tenant-AISLADO', sucursal_id: 'suc-1', producto_variacion_id: 'generated-id' },
        lock: { mode: 'pessimistic_write' },
      });

      expect(runner.commitTransaction).toHaveBeenCalled();
    });
  });
});
