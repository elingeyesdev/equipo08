import React, { useState, useEffect } from 'react';
import api from '../api';
import { ShoppingCart, Plus, X, Loader2, Edit2, Trash2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';
import ConfirmModal from '../components/ConfirmModal';

export default function SourcingPage() {
  const [providers, setProviders] = useState([]);
  const [products, setProducts] = useState([]);
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [loteForm, setLoteForm] = useState({ producto_id: '', proveedor_id: '', cantidad: 1, precioUnitario: '' });
  const toast = useToast();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [provRes, prodRes, histRes] = await Promise.all([
        api.get('/proveedores'),
        api.get('/productos'),
        api.get('/sourcing')
      ]);
      setProviders(provRes.data);
      setProducts(prodRes.data);
      setHistorial(histRes.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setLoteForm({ producto_id: '', proveedor_id: '', cantidad: 1, precioUnitario: '' });
    setEditingId(null);
    setShowForm(false);
  };

  const handleEdit = (h) => {
    setEditingId(h.id);
    const precioU = h.cantidad > 0 ? (Number(h.costoAdquisicion) / h.cantidad).toFixed(2) : '';
    setLoteForm({
      producto_id: h.producto_id,
      proveedor_id: h.proveedor_id,
      cantidad: h.cantidad,
      precioUnitario: precioU
    });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    setConfirmDelete(id);
  };

  const proceedDelete = async () => {
    if (!confirmDelete) return;
    try {
      await api.delete(`/sourcing/${confirmDelete}`);
      toast.success('Ingreso anulado y stock descontado');
      loadData();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    } finally {
      setConfirmDelete(null);
    }
  };

  const handleCreateLote = async (e) => {
    e.preventDefault();
    const inversionTotal = (parseFloat(loteForm.precioUnitario) * parseInt(loteForm.cantidad)).toFixed(2);
    if (inversionTotal <= 0) {
      return toast.error('El costo total de adquisición debe ser mayor a Bs 0');
    }
    
    // Validacion cruzada frontend
    const selectedProd = products.find(p => p.id === loteForm.producto_id);
    if (selectedProd && loteForm.proveedor_id !== selectedProd.proveedor_id) {
       return toast.error('El proveedor seleccionado no coincide con el proveedor oficial del producto en el catálogo.');
    }

    try {
      const payload = {
        ...loteForm,
        cantidad: parseInt(loteForm.cantidad),
        costoAdquisicion: parseFloat(inversionTotal)
      };

      if (editingId) {
        await api.put(`/sourcing/${editingId}`, payload);
        toast.success('Lote actualizado exitosamente');
      } else {
        await api.post('/sourcing', payload);
        toast.success('Ingreso de mercancía registrado con éxito');
      }
      resetForm();
      loadData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error registrando ingreso de mercancía');
    }
  };

  const isProviderLocked = !!loteForm.producto_id; // Si hay producto, se bloquea el proveedor

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      
      {/* Header and Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0 }}>Entradas y Lotes (Sourcing)</h2>
          <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Registra compras a proveedores. Esto actualizará el stock físico.</p>
        </div>
        <button 
          onClick={showForm ? resetForm : () => setShowForm(true)} 
          style={{ 
            display: 'flex', alignItems: 'center', gap: '0.5rem', 
            backgroundColor: showForm ? 'var(--text-secondary)' : 'var(--accent-blue)' 
          }}
        >
          {showForm ? <><X size={18} /> Cancelar</> : <><Plus size={18} /> Registrar Nuevo Ingreso</>}
        </button>
      </div>

      {/* Expandable Form Section */}
      {showForm && (
        <div className="glass-container" style={{ animation: 'fadeIn 0.3s ease', borderLeft: '4px solid #16a34a' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
             {editingId ? 'Editar Ingreso de Mercancía' : 'Procesar Ingreso de Mercancía'}
          </h3>
          <form onSubmit={handleCreateLote}>
            <div className="form-grid">
              <div className="form-group">
                <label>Producto Entrante *</label>
                <select required value={loteForm.producto_id} onChange={e => {
                  const selectedProd = products.find(p => p.id === e.target.value);
                  setLoteForm({...loteForm, producto_id: e.target.value, proveedor_id: selectedProd ? selectedProd.proveedor_id : ''});
                }} disabled={editingId ? true : false}>
                  <option value="">Seleccione artículo...</option>
                  {products.map(p => <option key={p.id} value={p.id}>{p.name} ({p.sku})</option>)}
                </select>
              </div>

              <div className="form-group">
                <label>Proveedor (Factura Origen) *</label>
                <select required value={loteForm.proveedor_id} onChange={e => setLoteForm({...loteForm, proveedor_id: e.target.value})} style={{ backgroundColor: isProviderLocked ? '#f1f5f9' : 'white' }} disabled={isProviderLocked}>
                  <option value="">Seleccione proveedor...</option>
                  {providers.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
                {isProviderLocked && <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block', marginTop: '0.25rem' }}>Automático según el catálogo</span>}
              </div>

              <div className="form-group">
                <label>Unidades *</label>
                <input type="number" min="1" required value={loteForm.cantidad} onChange={e => setLoteForm({...loteForm, cantidad: e.target.value})} />
              </div>

              <div className="form-group">
                <label>Precio Unitario (Bs) *</label>
                <input type="number" step="0.01" min="0.01" required value={loteForm.precioUnitario} onChange={e => setLoteForm({...loteForm, precioUnitario: e.target.value})} placeholder="Eg. 10.50" />
              </div>

              <div className="form-group">
                <label>Inversión Estimada (Bs)</label>
                <div style={{ 
                  backgroundColor: '#f1f5f9', 
                  padding: '0.75rem 1rem', 
                  borderRadius: '6px', 
                  border: '1px solid var(--border-color)',
                  fontWeight: '600',
                  color: 'var(--primary-color)',
                  display: 'flex',
                  alignItems: 'center',
                  height: '42px',
                  boxSizing: 'border-box'
                }}>
                  Bs {loteForm.precioUnitario && loteForm.cantidad ? (parseFloat(loteForm.precioUnitario) * parseInt(loteForm.cantidad)).toFixed(2) : '0.00'}
                </div>
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" style={{ backgroundColor: 'var(--accent-blue)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShoppingCart size={18} /> {editingId ? 'Guardar Cambios' : 'Confirmar Ingreso'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Table Section */}
      <div className="glass-container" style={{ padding: '0', overflow: 'hidden' }}>
        {loading ? <div style={{ padding: '3rem', textAlign: 'center' }}><Loader2 className="animate-spin" size={32} color="var(--accent-blue)" style={{ margin: '0 auto' }} /></div> : (
          <table>
            <thead>
              <tr>
                <th>Lote TRx</th>
                <th>Fecha Ingreso</th>
                <th>Producto (SKU)</th>
                <th>Proveedor Origen</th>
                <th style={{ textAlign: 'center' }}>Unidades</th>
                <th style={{ textAlign: 'center' }}>Precio U.</th>
                <th style={{ textAlign: 'right' }}>Inversión Total</th>
                <th style={{ textAlign: 'right', width: '80px' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {historial.length === 0 ? (
                <tr>
                  <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                     Aún no hay ingresos registrados en el sistema de la tienda.
                  </td>
                </tr>
              ) : historial.map(h => {
                const precioU = h.cantidad > 0 ? (Number(h.costoAdquisicion) / h.cantidad) : 0;
                return (
                 <tr key={h.id}>
                   <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontFamily: 'monospace' }}>#{h.id.split('-')[0]}</td>
                   <td>{new Date(h.fechaIngreso).toLocaleDateString()} {new Date(h.fechaIngreso).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</td>
                   <td style={{ fontWeight: '500' }}>
                     {h.producto?.name || '---'} 
                     <br/><span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{h.producto?.sku}</span>
                   </td>
                   <td>{h.proveedor?.name || '---'}</td>
                   <td style={{ textAlign: 'center' }}>
                     <span className="badge success" style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>{h.cantidad} U</span>
                   </td>
                   <td style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>
                     Bs {precioU.toFixed(2)}
                   </td>
                   <td style={{ textAlign: 'right', color: 'var(--primary-color)', fontWeight: '600' }}>
                     Bs {Number(h.costoAdquisicion).toFixed(2)}
                   </td>
                   <td style={{ textAlign: 'right' }}>
                    <button onClick={() => handleEdit(h)} style={{ padding: '0.25rem', background: 'none', color: 'var(--accent-blue)' }} title="Editar">
                      <Edit2 size={16} />
                    </button>
                    <button onClick={() => handleDelete(h.id)} style={{ padding: '0.25rem', background: 'none', color: 'var(--danger-color)', marginLeft: '0.5rem' }} title="Eliminar">
                      <Trash2 size={16} />
                    </button>
                  </td>
                 </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>

      <ConfirmModal 
        isOpen={!!confirmDelete}
        title="Anular Ingreso de Mercancía"
        message="¿Seguro que deseas anular este recibo de ingreso? Esta acción descontará irremediablemente las cantidades añadidas de tu inventario físico operativo."
        onConfirm={proceedDelete}
        onCancel={() => setConfirmDelete(null)}
      />
    </div>
  );
}
