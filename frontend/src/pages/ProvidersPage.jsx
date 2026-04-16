import React, { useState, useEffect } from 'react';
import api from '../api';
import { Plus, X, Loader2, Edit2, Trash2, Search, Building2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';
import ConfirmModal from '../components/ConfirmModal';

export default function ProvidersPage() {
  const [providers, setProviders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [formData, setFormData] = useState({ name: '', taxId: '', contactEmail: '' });
  const [searchingNit, setSearchingNit] = useState(false);
  const [isFound, setIsFound] = useState(false);
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

  const handleNitSearch = async () => {
    if (!formData.taxId) {
      toast.error('Por favor, ingresa un NIT para buscar.');
      return;
    }
    setSearchingNit(true);
    try {
      const { data } = await api.get(`/proveedores/global/${formData.taxId}`);
      // Auto-fill and lock
      setFormData({
        ...formData,
        name: data.name,
        contactEmail: data.contactEmail,
      });
      setIsFound(true);
      toast.success('Proveedor Maestro encontrado y autocompletado.');
    } catch (err) {
      setIsFound(false);
      setFormData({ ...formData, name: '', contactEmail: '' });
      toast.error(err.response?.data?.message || 'Proveedor no encontrado. Contacta al administrador.');
    } finally {
      setSearchingNit(false);
    }
  };

  const handleDelete = (id) => {
    setConfirmDelete(id);
  };

  const proceedDelete = async () => {
    if (!confirmDelete) return;
    try {
      await api.delete(`/proveedores/${confirmDelete}`);
      toast.success('Proveedor quitado de tu catálogo local');
      fetchProviders();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    } finally {
      setConfirmDelete(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!isFound) {
        toast.error('Debes buscar un NIT válido y registrado primero.');
        return;
      }
      if (!formData.name) return;
      
      await api.post('/proveedores', formData);
      toast.success('Proveedor anexado a tu directorio');
      
      resetForm();
      fetchProviders();
    } catch (err) {
      toast.error(err.response?.data?.message || err.message);
    }
  };

  const resetForm = () => {
    setFormData({ name: '', taxId: '', contactEmail: '' });
    setIsFound(false);
    setShowForm(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Building2 size={24} color="var(--primary-color)" /> Mi Directorio de Proveedores
          </h2>
          <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Añade proveedores de manera local sincronizándolos mediante NIT.
          </p>
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

      {showForm && (
        <div className="glass-container" style={{ animation: 'fadeIn 0.3s ease' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem' }}>
            Importar Proveedor Maestro
          </h3>
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              
              <div className="form-group" style={{ position: 'relative' }}>
                <label>Buscador Maestro de NIT / RUT</label>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input 
                    type="text" 
                    value={formData.taxId} 
                    onChange={e => setFormData({...formData, taxId: e.target.value})} 
                    pattern="^\d{8,12}$" 
                    title="Debe contener entre 8 y 12 números sin espacios ni símbolos" 
                    placeholder="Escribe el NIT y pulsa buscar..." 
                    style={{ flex: 1 }}
                  />
                  {!isFound && (
                    <button 
                      type="button" 
                      onClick={handleNitSearch} 
                      disabled={searchingNit}
                      style={{ padding: '0.5rem 1rem', display: 'flex', alignItems: 'center', gap: '0.4rem', backgroundColor: 'var(--accent-blue)' }}
                    >
                      {searchingNit ? <Loader2 size={16} className="animate-spin" /> : <Search size={16} />} 
                      Buscar
                    </button>
                  )}
                </div>
                <p style={{ margin: '0.4rem 0 0', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  Ejemplo: 10002000 (Sugerido por Admin Central)
                </p>
              </div>

              <div className="form-group">
                <label>Razón Social / Nombre Oficial *</label>
                <input 
                  type="text" 
                  value={formData.name} 
                  required 
                  readOnly 
                  placeholder="Esperando NIT válido..." 
                  style={{ backgroundColor: '#f1f5f9', cursor: 'not-allowed', color: '#64748b' }}
                />
              </div>

              <div className="form-group">
                <label>Email de Contacto Comercial</label>
                <input 
                  type="email" 
                  value={formData.contactEmail} 
                  readOnly 
                  placeholder="Se autonombra desde el NIT" 
                  style={{ backgroundColor: '#f1f5f9', cursor: 'not-allowed', color: '#64748b' }}
                />
              </div>

            </div>
            <div className="form-actions">
              <button type="submit" disabled={!isFound} style={!isFound ? { opacity: 0.5, cursor: 'not-allowed' } : { backgroundColor: 'var(--primary-color)' }}>
                Anexar Proveedor a mi Tienda
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="glass-container" style={{ padding: '0', overflow: 'hidden' }}>
        {loading ? <div style={{ padding: '3rem', textAlign: 'center' }}><Loader2 className="animate-spin" size={32} color="var(--accent-blue)" style={{ margin: '0 auto' }} /></div> : (
          <table>
            <thead>
              <tr>
                <th>Razón Social Local</th>
                <th>NIT / RUT</th>
                <th>Correo de Contacto</th>
                <th style={{ textAlign: 'right', width: '100px' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {providers.length === 0 ? (
                <tr><td colSpan="4" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>Aún no tienes proveedores en tu Empresa. Agrega uno mediante NIT.</td></tr>
              ) : providers.map(p => (
                <tr key={p.id}>
                  <td style={{ fontWeight: '500' }}>{p.name}</td>
                  <td style={{ color: 'var(--text-secondary)', fontFamily: 'monospace' }}>{p.taxId || '-'}</td>
                  <td>{p.contactEmail || '-'}</td>
                  <td style={{ textAlign: 'right' }}>
                    <button onClick={() => handleDelete(p.id)} style={{ padding: '0.25rem', background: 'none', color: 'var(--danger-color)' }} title="Eliminar">
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <ConfirmModal 
        isOpen={!!confirmDelete}
        title="Quitar Proveedor Local"
        message="¿Estás seguro de que deseas quitar a este proveedor de la vista local de tu empresa? Solo se eliminará de TU catálogo."
        onConfirm={proceedDelete}
        onCancel={() => setConfirmDelete(null)}
      />
    </div>
  );
}
