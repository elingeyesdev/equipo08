import React, { useState, useEffect } from 'react';
import api from '../api';
import { Plus, X, Loader2, Edit2, Trash2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';

export default function ProvidersPage() {
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({ name: '', taxId: '', contactEmail: '' });
  const toast = useToast();

  useEffect(() => {
    fetchProviders();
  }, []);

  const fetchProviders = async () => {
    try {
      const { data } = await api.get('/proveedores');
      setProviders(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (p) => {
    setEditingId(p.id);
    setFormData({ name: p.name, taxId: p.taxId || '', contactEmail: p.contactEmail || '' });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('¿Estás seguro de que deseas eliminar este proveedor?')) return;
    try {
      await api.delete(`/proveedores/${id}`);
      toast.success('Proveedor eliminado correctamente');
      fetchProviders();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!formData.name) return;
      if (editingId) {
        await api.put(`/proveedores/${editingId}`, formData);
        toast.success('Proveedor actualizado con éxito');
      } else {
        await api.post('/proveedores', formData);
        toast.success('Proveedor creado con éxito');
      }
      resetForm();
      fetchProviders();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    }
  };

  const resetForm = () => {
    setFormData({ name: '', taxId: '', contactEmail: '' });
    setEditingId(null);
    setShowForm(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      
      {/* Header and Actions */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0 }}>Directorio de Proveedores</h2>
          <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Gestiona las empresas que abastecen tu tienda.</p>
        </div>
        <button 
          onClick={showForm ? resetForm : () => setShowForm(true)} 
          style={{ 
            display: 'flex', alignItems: 'center', gap: '0.5rem', 
            backgroundColor: showForm ? 'var(--text-secondary)' : 'var(--accent-blue)' 
          }}
        >
          {showForm ? <><X size={18} /> Cancelar</> : <><Plus size={18} /> Nuevo Proveedor</>}
        </button>
      </div>

      {/* Expandable Form Section */}
      {showForm && (
        <div className="glass-container" style={{ animation: 'fadeIn 0.3s ease' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem' }}>
            {editingId ? 'Editar Proveedor' : 'Registrar Nuevo Proveedor'}
          </h3>
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>Razón Social / Nombre *</label>
                <input type="text" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required pattern="^[^0-9]+$" title="El nombre del proveedor no debe contener números" placeholder="Ej. Comercializadora ABC" />
              </div>
              <div className="form-group">
                <label>NIT / RUT Fiscal</label>
                <input type="text" value={formData.taxId} onChange={e => setFormData({...formData, taxId: e.target.value})} placeholder="Opcional" />
              </div>
              <div className="form-group">
                <label>Email de Contacto</label>
                <input type="email" value={formData.contactEmail} onChange={e => setFormData({...formData, contactEmail: e.target.value})} placeholder="contacto@abc.com" />
              </div>
            </div>
            <div className="form-actions">
              <button type="submit">
                {editingId ? 'Guardar Cambios' : 'Guardar Proveedor'}
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
                <th>Razón Social</th>
                <th>NIT / RUT</th>
                <th>Correo de Contacto</th>
                <th style={{ textAlign: 'right', width: '100px' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {providers.length === 0 ? (
                <tr><td colSpan="4" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>No hay proveedores registrados todavía. Haz clic en "Nuevo Proveedor" para comenzar.</td></tr>
              ) : providers.map(p => (
                <tr key={p.id}>
                  <td style={{ fontWeight: '500' }}>{p.name}</td>
                  <td style={{ color: 'var(--text-secondary)' }}>{p.taxId || '-'}</td>
                  <td>{p.contactEmail || '-'}</td>
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
