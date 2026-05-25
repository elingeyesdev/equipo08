import React, { useState, useEffect, useMemo } from 'react';
import api from '../api';
import { useToast } from '../components/ToastContext';
import { ShoppingCart, User, MapPin, Printer, Plus, Trash2, Download, Receipt } from 'lucide-react';

export default function SalesPage() {
  const [sucursales, setSucursales] = useState([]);
  const [selectedBranch, setSelectedBranch] = useState('');
  
  const [stockInfo, setStockInfo] = useState([]);
  const [searchProduct, setSearchProduct] = useState('');
  
  const [cart, setCart] = useState([]);
  const [clienteNombre, setClienteNombre] = useState('Cliente Casual');
  const [clienteDocumento, setClienteDocumento] = useState('');
  
  const [salesHistory, setSalesHistory] = useState([]);
  const [saving, setSaving] = useState(false);
  const [downloading, setDownloading] = useState(null);

  const toast = useToast();

  useEffect(() => {
    fetchSucursales();
    fetchSalesHistory();
  }, []);

  useEffect(() => {
    if (selectedBranch) {
      fetchStock();
    } else {
      setStockInfo([]);
    }
  }, [selectedBranch]);

  const fetchSucursales = async () => {
    try {
      const { data } = await api.get('/sucursales');
      const activos = data.filter(s => s.isActive);
      setSucursales(activos);
      if (activos.length > 0) setSelectedBranch(activos[0].id);
    } catch (err) {
      toast.error('Error al cargar sucursales');
    }
  };

  const fetchStock = async () => {
    try {
      const { data } = await api.get('/stock');
      // Filter stock by selected branch
      setStockInfo(data.filter(s => s.sucursal_id === selectedBranch && s.cantidadTotal > 0));
    } catch (err) {
      toast.error('Error al cargar inventario');
    }
  };

  const fetchSalesHistory = async () => {
    try {
      const { data } = await api.get('/ventas');
      setSalesHistory(data);
    } catch (err) {
      toast.error('Error al cargar historial de comprobantes');
    }
  };

  const filteredStock = useMemo(() => {
    return stockInfo.filter(s => {
      const term = searchProduct.toLowerCase();
      return s.producto?.name?.toLowerCase().includes(term) || s.producto?.sku?.toLowerCase().includes(term);
    });
  }, [stockInfo, searchProduct]);

  const addToCart = (stockItem) => {
    setCart(prev => {
      const existing = prev.find(item => item.producto_id === stockItem.producto_id);
      if (existing) {
        if (existing.cantidad >= stockItem.cantidadTotal) {
          toast.error('No hay suficiente stock físico');
          return prev;
        }
        return prev.map(item => item.producto_id === stockItem.producto_id ? { ...item, cantidad: item.cantidad + 1 } : item);
      }
      return [...prev, {
        producto_id: stockItem.producto_id,
        name: stockItem.producto.name,
        sku: stockItem.producto.sku,
        precioUnitario: Number(stockItem.producto.precioVenta || 0),
        cantidad: 1,
        maxStock: stockItem.cantidadTotal
      }];
    });
  };

  const updateCartQty = (producto_id, cantidad) => {
    setCart(prev => prev.map(item => {
      if (item.producto_id === producto_id) {
        if (cantidad > item.maxStock) {
          toast.error(`Solo hay ${item.maxStock} unidades disponibles`);
          return { ...item, cantidad: item.maxStock };
        }
        return { ...item, cantidad: Math.max(1, cantidad) };
      }
      return item;
    }));
  };

  const removeFromCart = (producto_id) => {
    setCart(prev => prev.filter(item => item.producto_id !== producto_id));
  };

  const cartTotal = cart.reduce((acc, item) => acc + (item.cantidad * item.precioUnitario), 0);

  const handleRegisterSale = async () => {
    if (!selectedBranch) return toast.error('Selecciona una sucursal');
    if (cart.length === 0) return toast.error('El carrito está vacío');
    if (!clienteNombre.trim()) return toast.error('Ingresa el nombre del cliente');

    setSaving(true);
    try {
      const payload = {
        sucursal_id: selectedBranch,
        clienteNombre,
        clienteDocumento,
        items: cart.map(item => ({ producto_id: item.producto_id, cantidad: item.cantidad }))
      };

      const { data } = await api.post('/ventas', payload);
      
      toast.success('¡Venta registrada exitosamente!');
      setCart([]);
      setClienteNombre('Cliente Casual');
      setClienteDocumento('');
      
      fetchStock();
      fetchSalesHistory();
      
      // Auto-download the PDF
      downloadPdf(data.id, data.numeroComprobante);
      
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al registrar la venta');
    } finally {
      setSaving(false);
    }
  };

  const downloadPdf = async (id, numeroComprobante) => {
    setDownloading(id);
    try {
      const response = await api.get(`/ventas/${id}/pdf`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${numeroComprobante || 'comprobante'}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      toast.error('Error al descargar el comprobante');
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', alignItems: 'start' }}>
      
      {/* Left Column: POS / Terminal */}
      <div className="glass-container" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', minHeight: '600px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--primary-color)' }}>
            <ShoppingCart size={24} /> Terminal de Ventas
          </h3>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <MapPin size={18} color="var(--accent-blue)" />
            <select 
              value={selectedBranch} 
              onChange={e => { setSelectedBranch(e.target.value); setCart([]); }}
              style={{ backgroundColor: 'var(--bg-color)', border: '1px solid var(--border-color)', borderRadius: '6px', padding: '0.4rem' }}
            >
              <option value="" disabled>Seleccione Sucursal...</option>
              {sucursales.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
        </div>

        <div>
          <input 
            type="text" 
            placeholder="🔍 Buscar producto en inventario por nombre o SKU..." 
            value={searchProduct}
            onChange={e => setSearchProduct(e.target.value)}
            style={{ width: '100%', padding: '0.75rem', borderRadius: '8px', border: '1px solid var(--border-color)' }}
            disabled={!selectedBranch}
          />
        </div>

        <div style={{ flex: 1, overflowY: 'auto', border: '1px solid var(--border-color)', borderRadius: '8px', padding: '0.5rem', backgroundColor: '#f8fafc' }}>
          {filteredStock.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>
              {selectedBranch ? 'No hay productos en inventario' : 'Selecciona una sucursal primero'}
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '0.5rem' }}>
              {filteredStock.map(s => (
                <div 
                  key={s.id} 
                  onClick={() => addToCart(s)}
                  style={{ backgroundColor: '#fff', padding: '0.75rem', borderRadius: '6px', border: '1px solid #e2e8f0', cursor: 'pointer', transition: 'all 0.2s ease', display: 'flex', flexDirection: 'column', gap: '0.25rem' }}
                  className="hover-scale"
                >
                  <div style={{ fontSize: '0.75rem', color: '#64748b', fontFamily: 'monospace' }}>{s.producto?.sku}</div>
                  <strong style={{ fontSize: '0.9rem', lineHeight: '1.2' }}>{s.producto?.name}</strong>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem' }}>
                    <span style={{ color: '#10b981', fontWeight: 'bold' }}>Bs {Number(s.producto?.precioVenta || 0).toFixed(2)}</span>
                    <span style={{ fontSize: '0.8rem', backgroundColor: '#e2e8f0', padding: '0.1rem 0.4rem', borderRadius: '10px' }}>{s.cantidadTotal} U.</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Right Column: Cart & Billing & History */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
        
        {/* Cart Form */}
        <div className="glass-container" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <h4 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Receipt size={20} /> Detalle de Venta
          </h4>

          {/* Client Details */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div>
              <label style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem', marginBottom: '0.2rem', color: 'var(--text-secondary)' }}><User size={14} /> Cliente / Razón Social</label>
              <input type="text" value={clienteNombre} onChange={e => setClienteNombre(e.target.value)} style={{ width: '100%', padding: '0.6rem', borderRadius: '6px', border: '1px solid var(--border-color)' }} />
            </div>
            <div>
              <label style={{ fontSize: '0.85rem', marginBottom: '0.2rem', display: 'block', color: 'var(--text-secondary)' }}>NIT / CI (Opcional)</label>
              <input type="text" value={clienteDocumento} onChange={e => setClienteDocumento(e.target.value)} placeholder="Ej. 1234567" style={{ width: '100%', padding: '0.6rem', borderRadius: '6px', border: '1px solid var(--border-color)' }} />
            </div>
          </div>

          <hr style={{ border: 'none', borderTop: '1px dashed #cbd5e1', margin: 0 }} />

          {/* Cart Items */}
          <div style={{ minHeight: '150px', maxHeight: '250px', overflowY: 'auto' }}>
            {cart.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-secondary)' }}>Agrega productos al carrito</div>
            ) : (
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid #e2e8f0', color: 'var(--text-secondary)' }}>
                    <th style={{ textAlign: 'left', paddingBottom: '0.5rem' }}>Producto</th>
                    <th style={{ textAlign: 'center', paddingBottom: '0.5rem' }}>Cant.</th>
                    <th style={{ textAlign: 'right', paddingBottom: '0.5rem' }}>Subtotal</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {cart.map(item => (
                    <tr key={item.producto_id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                      <td style={{ padding: '0.5rem 0' }}>
                        <div style={{ fontWeight: '500' }}>{item.name}</div>
                        <div style={{ fontSize: '0.75rem', color: '#64748b' }}>Bs {item.precioUnitario.toFixed(2)} c/u</div>
                      </td>
                      <td style={{ textAlign: 'center', padding: '0.5rem 0' }}>
                        <input 
                          type="number" 
                          min="1" 
                          max={item.maxStock} 
                          value={item.cantidad} 
                          onChange={e => updateCartQty(item.producto_id, parseInt(e.target.value) || 1)}
                          style={{ width: '60px', textAlign: 'center', padding: '0.3rem', borderRadius: '4px', border: '1px solid #cbd5e1' }}
                        />
                      </td>
                      <td style={{ textAlign: 'right', fontWeight: 'bold', padding: '0.5rem 0' }}>
                        Bs {(item.cantidad * item.precioUnitario).toFixed(2)}
                      </td>
                      <td style={{ textAlign: 'right', padding: '0.5rem 0' }}>
                        <button onClick={() => removeFromCart(item.producto_id)} style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', padding: '0.2rem' }}>
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', backgroundColor: '#f8fafc', borderRadius: '8px' }}>
            <span style={{ fontSize: '1.2rem', color: 'var(--text-secondary)' }}>Total:</span>
            <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--primary-color)' }}>Bs {cartTotal.toFixed(2)}</span>
          </div>

          <button 
            onClick={handleRegisterSale} 
            disabled={saving || cart.length === 0}
            style={{ width: '100%', padding: '1rem', backgroundColor: 'var(--primary-color)', color: 'white', borderRadius: '8px', border: 'none', fontSize: '1.1rem', fontWeight: 'bold', cursor: cart.length > 0 ? 'pointer' : 'not-allowed', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem', opacity: cart.length > 0 ? 1 : 0.6 }}
          >
            <Printer size={20} /> {saving ? 'Procesando...' : 'Cobrar y Emitir Comprobante'}
          </button>
        </div>

        {/* History List */}
        <div className="glass-container" style={{ flex: 1 }}>
          <h4 style={{ margin: '0 0 1rem 0', display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)' }}>
             Historial Reciente
          </h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', maxHeight: '200px', overflowY: 'auto' }}>
            {salesHistory.length === 0 ? <p style={{ fontSize: '0.9rem', color: '#64748b' }}>No hay ventas registradas.</p> : 
             salesHistory.map(sale => (
               <div key={sale.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem', border: '1px solid #e2e8f0', borderRadius: '6px', backgroundColor: '#fff' }}>
                 <div>
                   <div style={{ fontWeight: 'bold', color: 'var(--primary-color)' }}>{sale.numeroComprobante}</div>
                   <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{new Date(sale.fecha).toLocaleString()} | {sale.clienteNombre}</div>
                 </div>
                 <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                   <span style={{ fontWeight: 'bold' }}>Bs {Number(sale.total).toFixed(2)}</span>
                   <button 
                     onClick={() => downloadPdf(sale.id, sale.numeroComprobante)}
                     disabled={downloading === sale.id}
                     title="Descargar PDF"
                     style={{ background: '#f1f5f9', border: 'none', padding: '0.5rem', borderRadius: '50%', cursor: 'pointer', color: '#334155' }}
                   >
                     <Download size={16} />
                   </button>
                 </div>
               </div>
             ))
            }
          </div>
        </div>

      </div>
    </div>
  );
}
