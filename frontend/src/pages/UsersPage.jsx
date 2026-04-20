import React, { useState, useEffect } from 'react';
import api from '../api';
import { useToast } from '../components/ToastContext';
import { Trash2, Mail, Shield, User as UserIcon, Loader2, UserPlus, X, Store, Filter, Edit2, Save } from 'lucide-react';

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [sucursales, setSucursales] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  
  // Show/Hide Inline Form (matching SourcingPage pattern)
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [userForm, setUserForm] = useState({
    name: '',
    email: '',
    password: '',
    role: 'VENDEDOR',
    sucursal_id: ''
  });

  // Filters State
  const [filterRole, setFilterRole] = useState('ALL');
  const [filterSucursal, setFilterSucursal] = useState('ALL');
  
  const toast = useToast();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [usersRes, sucursalesRes] = await Promise.all([
        api.get('/users'),
        api.get('/sucursales')
      ]);
      setUsers(usersRes.data);
      setSucursales(sucursalesRes.data);
    } catch (err) {
      toast.error('Error al cargar datos del personal');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (user) => {
    setEditingId(user.id);
    setUserForm({
      name: user.name,
      email: user.email,
      password: '', // Password stays empty unless intentionally reset
      role: user.role,
      sucursal_id: user.sucursal_id || ''
    });
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingId(null);
    setUserForm({ name: '', email: '', password: '', role: 'VENDEDOR', sucursal_id: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = { ...userForm };
      if (!payload.sucursal_id) delete payload.sucursal_id;
      if (editingId && !payload.password) delete payload.password; // Don't send empty pass on edit

      if (editingId) {
        await api.put(`/users/${editingId}`, payload);
        toast.success('Perfil de empleado actualizado exitosamente');
      } else {
        await api.post('/users', payload);
        toast.success('Empleado registrado correctamente en el sistema');
      }
      
      handleCancel();
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al procesar usuario');
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteUser = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar a este empleado? Perderá acceso inmediato.')) return;
    try {
      await api.delete(`/users/${id}`);
      toast.success('Empleado desvinculado con éxito');
      fetchData();
    } catch (err) {
      toast.error('Error al eliminar usuario');
    }
  };

  const filteredUsers = users.filter(u => {
    if (filterRole !== 'ALL' && u.role !== filterRole) return false;
    if (filterSucursal !== 'ALL' && u.sucursal_id !== filterSucursal) return false;
    return true;
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      
      {/* Header Section */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ margin: 0 }}>Gestión de Nómina y Personal</h2>
          <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Administra el organigrama de tu tienda y asigna sucursales operacionales.</p>
        </div>
        <button 
          onClick={showForm ? handleCancel : () => setShowForm(true)} 
          style={{ 
            display: 'flex', alignItems: 'center', gap: '0.5rem', 
            backgroundColor: showForm ? 'var(--text-secondary)' : 'var(--accent-blue)' 
          }}
        >
          {showForm ? <><X size={18} /> Cancelar Operación</> : <><UserPlus size={18} /> Registrar Nuevo Empleado</>}
        </button>
      </div>

      {/* Inline Form */}
      {showForm && (
        <div className="glass-container" style={{ animation: 'fadeIn 0.3s ease', borderLeft: `4px solid ${editingId ? 'var(--accent-blue)' : '#2563eb'}` }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
             {editingId ? `Editando Perfil: ${userForm.name}` : 'Procesar Alta de Nuevo Empleado'}
          </h3>
          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              <div className="form-group">
                <label>Nombre Completo *</label>
                <div className="input-with-icon">
                  <UserIcon size={18} style={{ color: 'var(--text-secondary)' }} />
                  <input 
                    type="text" 
                    value={userForm.name} 
                    onChange={e => setUserForm({...userForm, name: e.target.value})} 
                    placeholder="Ej: Juan Pérez"
                    pattern="^[A-Za-záéíóúÁÉÍÓÚñÑ\s]+$"
                    title="El nombre no puede contener números ni símbolos"
                    required 
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Correo Electrónico (Acceso) *</label>
                <div className="input-with-icon">
                  <Mail size={18} style={{ color: 'var(--text-secondary)' }} />
                  <input 
                    type="email" 
                    value={userForm.email} 
                    onChange={e => setUserForm({...userForm, email: e.target.value})} 
                    placeholder="juan@mitienda.com"
                    required 
                  />
                </div>
              </div>

              <div className="form-group">
                <label>{editingId ? 'Nueva Contraseña (Opcional)' : 'Contraseña Provisoria *'}</label>
                <input 
                  type="password" 
                  value={userForm.password} 
                  onChange={e => setUserForm({...userForm, password: e.target.value})} 
                  placeholder={editingId ? "Dejar vacío para no cambiar" : "Mínimo 6 caracteres"}
                  required={!editingId}
                  minLength={6}
                />
                {editingId && <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>Sólo llena este campo si deseas resetear el acceso del empleado.</span>}
              </div>

              <div className="form-group">
                <label>Rol de Sistema *</label>
                <div className="input-with-icon">
                  <Shield size={18} style={{ color: 'var(--text-secondary)' }} />
                  <select 
                    value={userForm.role} 
                    onChange={e => setUserForm({...userForm, role: e.target.value})}
                    required
                  >
                    <option value="VENDEDOR">Vendedor operativo</option>
                    <option value="SUPERVISOR">Supervisor administrativo</option>
                    {userForm.role === 'OWNER' && <option value="OWNER">Dueño (Original)</option>}
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>Sucursal / Centro de Trabajo</label>
                <div className="input-with-icon">
                  <Store size={18} style={{ color: 'var(--text-secondary)' }} />
                  <select 
                    value={userForm.sucursal_id} 
                    onChange={e => setUserForm({...userForm, sucursal_id: e.target.value})}
                  >
                    <option value="">- Asignación Global (Dueño/HQ) -</option>
                    {sucursales.map(s => (
                      <option key={s.id} value={s.id}>{s.name}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div className="form-actions" style={{ marginTop: '1.5rem' }}>
              <button type="submit" disabled={saving} style={{ backgroundColor: 'var(--accent-blue)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                {editingId ? <><Save size={18} /> Guardar Cambios</> : <><UserPlus size={18} /> Confirmar Alta de Personal</>}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Filter Bar */}
      <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: 1, minWidth: '200px' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', whiteSpace: 'nowrap' }}><Filter size={14} /> Rol:</label>
          <select value={filterRole} onChange={e => setFilterRole(e.target.value)} style={{ flex: 1, padding: '0.4rem 0.6rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)' }}>
            <option value="ALL">Todos los roles</option>
            <option value="OWNER">Owner</option>
            <option value="SUPERVISOR">Supervisor</option>
            <option value="VENDEDOR">Vendedor</option>
          </select>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flex: 1, minWidth: '200px' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', whiteSpace: 'nowrap' }}><Store size={14} /> Sucursal:</label>
          <select value={filterSucursal} onChange={e => setFilterSucursal(e.target.value)} style={{ flex: 1, padding: '0.4rem 0.6rem', borderRadius: '6px', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)' }}>
            <option value="ALL">Todas las sucursales</option>
            {sucursales.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>
      </div>

      {/* Table Section */}
      <div className="glass-container" style={{ padding: '0', overflow: 'hidden' }}>
        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center' }}><Loader2 className="spinner" size={32} color="var(--accent-blue)" style={{ margin: '0 auto' }} /></div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Nombre del Usuario</th>
                <th>Acceso (Email)</th>
                <th>Privilegios (Rol)</th>
                <th>Sucursal Destino</th>
                <th style={{ textAlign: 'center' }}>Estado</th>
                <th style={{ textAlign: 'right', width: '90px' }}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                    No se encontraron empleados bajo estos criterios de búsqueda.
                  </td>
                </tr>
              ) : (
                filteredUsers.map(user => (
                  <tr key={user.id}>
                    <td>
                      <div className="user-info-cell">
                        <div className="user-avatar">{user.name.charAt(0)}</div>
                        <span style={{ fontWeight: '500' }}>{user.name}</span>
                      </div>
                    </td>
                    <td>{user.email}</td>
                    <td>
                      <span className={`badge badge-${user.role.toLowerCase()}`}>
                        {user.role}
                      </span>
                    </td>
                    <td>
                      {user.sucursal ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', color: 'var(--text-primary)' }}>
                          <Store size={14} color="var(--text-secondary)" />
                          {user.sucursal.name}
                        </div>
                      ) : (
                        <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontStyle: 'italic' }}>Administración Global</span>
                      )}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.4rem' }}>
                        <span className={`status-dot ${user.isActive ? 'active' : 'inactive'}`}></span>
                        <span style={{ fontSize: '0.85rem' }}>{user.isActive ? 'Activo' : 'Inactivo'}</span>
                      </div>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
                        <button onClick={() => handleEdit(user)} style={{ background: 'none', color: 'var(--accent-blue)', padding: '0.25rem' }} title="Editar / Reset Password">
                          <Edit2 size={16} />
                        </button>
                        {user.role !== 'OWNER' && (
                          <button 
                            onClick={() => handleDeleteUser(user.id)} 
                            style={{ background: 'none', color: 'var(--danger-color)', padding: '0.25rem' }}
                            title="Dar de baja definitiva"
                          >
                            <Trash2 size={16} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
