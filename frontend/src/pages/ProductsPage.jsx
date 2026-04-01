import React, { useState, useEffect } from 'react';
import api from '../api';
import { PackageSearch, Plus, X, Loader2, Edit2, Trash2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({ name: '', sku: '', proveedor_id: '' });
  const toast = useToast();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [prodRes, provRes] = await Promise.all([
        api.get('/productos'),
        api.get('/proveedores')
      ]);
      setProducts(prodRes.data);
      setProviders(provRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (p) => {
    setEditingId(p.id);
    setFormData({ name: p.name, sku: p.sku, proveedor_id: p.proveedor_id || '' });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Seguro que deseas eliminar este producto permanentemente?')) return;
    try {
      await api.delete(`/productos/${id}`);
      toast.success('Producto eliminado del catálogo');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await api.put(`/productos/${editingId}`, formData);
        toast.success('Producto actualizado correctamente');
      } else {
        await api.post('/productos', formData);
        toast.success('Producto ingresado al catálogo');
      }
      resetForm();
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    }
  };

  const resetForm = () => {
    setFormData({ name: '', sku: '', proveedor_id: '' });
    setEditingId(null);
    setShowForm(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      
      {/* Header and Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0 }}>Catálogo de Productos</h2>
          <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Define los artículos que venderás y almacenarás.</p>
        </div>
        <button 
          onClick={showForm ? resetForm : () => setShowForm(true)} 
          style={{ 
            display: 'flex', alignItems: 'center', gap: '0.5rem', 
            backgroundColor: showForm ? 'var(--text-secondary)' : 'var(--accent-blue)' 
          }}
        >
          {showForm ? <><X size={18} /> Cancelar</> : <><Plus size={18} /> Añadir al Catálogo</>}
        </button>
      </div>

      {/* Expandable Form Section */}
      {showForm && (
        <div className="glass-container" style={{ animation: 'fadeIn 0.3s ease' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem' }}>
            {editingId ? 'Editar Artículo' : 'Nuevo Artículo'}
          </h3>
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>Nombre del Producto *</label>
                <input type="text" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required placeholder="Ej. Zapatillas Nike" />
              </div>
              <div className="form-group">
                <label>SKU (Código Interno) *</label>
                <input type="text" value={formData.sku} onChange={e => setFormData({...formData, sku: e.target.value.toUpperCase()})} required placeholder="ZAP-NK-01" />
              </div>
              <div className="form-group">
                <label>Proveedor Recomendado *</label>
                <select required value={formData.proveedor_id} onChange={e => setFormData({...formData, proveedor_id: e.target.value})}>
                  <option value="">-- Seleccione proveedor --</option>
                  {providers.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
              </div>
            </div>
            <div className="form-actions">
              <button type="submit">
                {editingId ? 'Guardar Cambios' : 'Guardar Producto'}
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
                <th>Código (SKU)</th>
                <th>Nombre del Producto</th>
                <th>Proveedor Habitual</th>
                <th style={{ textAlign: 'right', width: '100px' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                    <PackageSearch size={32} style={{ opacity: 0.5, marginBottom: '0.5rem' }} /><br/>
                    Aún no hay productos en el catálogo.
                  </td>
                </tr>
              ) : products.map(p => (
                <tr key={p.id}>
                  <td style={{ fontWeight: '600', color: 'var(--primary-color)' }}><span style={{ backgroundColor: '#e2e8f0', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem' }}>{p.sku}</span></td>
                  <td>{p.name}</td>
                  <td style={{ color: p.proveedor ? 'inherit' : 'var(--text-secondary)' }}>{p.proveedor?.name || 'Huérfano'}</td>
                  <td style={{ textAlign: 'right' }}>
                    <button onClick={() => handleEdit(p)} style={{ padding: '0.25rem', background: 'none', color: 'var(--accent-blue)' }} title="Editar">
                      <Edit2 size={16} />
                    </button>
                    <button onClick={() => handleDelete(p.id)} style={{ padding: '0.25rem', background: 'none', color: 'var(--danger-color)', marginLeft: '0.5rem' }} title="Eliminar">
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

    </div>
  );
}
