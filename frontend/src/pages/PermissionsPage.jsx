import React, { useState, useEffect } from 'react';
import api from '../api';
import { useToast } from '../components/ToastContext';
import { Save, Loader2, Info, Shield, Store, Tags, Inbox, Box, Users } from 'lucide-react';

export default function PermissionsPage() {
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const toast = useToast();

  useEffect(() => {
    fetchPermissions();
  }, []);

  const fetchPermissions = async () => {
    try {
      const res = await api.get('/users/permissions');
      setPermissions(res.data);
    } catch (err) {
      toast.error('Error al cargar la configuración de permisos');
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = (role, field) => {
    setPermissions(prev => prev.map(p => {
      if (p.role === role) {
        return { ...p, [field]: !p[field] };
      }
      return p;
    }));
  };

  const handleSave = async (role) => {
    setSaving(true);
    const data = permissions.find(p => p.role === role);
    try {
      await api.put('/users/permissions', data);
      toast.success(`Políticas de seguridad de ${role} actualizadas correctamente`);
    } catch (err) {
      toast.error('Error al guardar cambios');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="loading-state"><Loader2 className="spinner" /> Sincronizando políticas de seguridad...</div>;

  const permissionGroups = [
    { 
      title: 'Sucursales Físicas', 
      icon: Store,
      fields: [
        { key: 'sucursales_ver', label: 'Consultar Directorio Geográfico' },
        { key: 'sucursales_gestionar', label: 'Control de Alta, Edición y Cierre' }
      ] 
    },
    { 
      title: 'Catálogo Central', 
      icon: Tags,
      fields: [
        { key: 'catalogo_ver', label: 'Visualización Global de Productos' },
        { key: 'catalogo_gestionar', label: 'Creación y Alteración de Precios' }
      ] 
    },
    { 
      title: 'Auditoría Sourcing', 
      icon: Inbox,
      fields: [
        { key: 'sourcing_ver', label: 'Inspeccionar Historial de Accesos' },
        { key: 'sourcing_gestionar', label: 'Registrar Nuevos Lotes y Costos' }
      ] 
    },
    { 
      title: 'Niveles de Inventario', 
      icon: Box,
      fields: [
        { key: 'inventario_ver', label: 'Visualización de Cantidades por Sucursal' }
      ] 
    },
    { 
      title: 'Recursos Humanos', 
      icon: Users,
      fields: [
        { key: 'usuarios_ver', label: 'Consultar Organigrama Interno' },
        { key: 'usuarios_gestionar', label: 'Contratar y Desvincular Personal' }
      ] 
    },
  ];

  const renderRoleColumn = (roleName) => {
    const roleData = permissions.find(p => p.role === roleName);
    if (!roleData) return null;

    return (
      <div className="glass-container permission-card">
        <div className="permission-card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Shield size={24} color="var(--accent-blue)" />
            <h3 style={{ margin: 0, textTransform: 'uppercase', letterSpacing: '0.05em' }}>{roleName}</h3>
          </div>
          <button className="btn-small" onClick={() => handleSave(roleName)} disabled={saving} style={{ backgroundColor: 'var(--accent-blue)' }}>
            <Save size={16} /> Aplicar Perfil
          </button>
        </div>
        
        <div className="permission-groups">
          {permissionGroups.map(group => {
            const Icon = group.icon;
            return (
              <div key={group.title} className="permission-group" style={{ marginBottom: '2rem' }}>
                <h4 className="group-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--primary-color)', fontSize: '0.9rem', paddingBottom: '0.5rem', borderBottom: '1px solid var(--border-color)' }}>
                  <Icon size={18} /> {group.title}
                </h4>
                {group.fields.map(field => (
                  <div key={field.key} className="permission-row" style={{ padding: '0.85rem 0' }}>
                    <span style={{ fontSize: '0.85rem', fontWeight: '500', color: 'var(--text-secondary)' }}>{field.label}</span>
                    <label className="switch">
                      <input 
                        type="checkbox" 
                        checked={roleData[field.key] || false} 
                        onChange={() => handleToggle(roleName, field.key)} 
                      />
                      <span className="slider round"></span>
                    </label>
                  </div>
                ))}
              </div>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h1 className="page-title">Políticas de Control de Acceso (ACL)</h1>
          <p className="page-subtitle">Modula las directivas de seguridad para jerarquías administrativas y operacionales.</p>
        </div>
      </div>

      <div className="alert-info" style={{ marginBottom: '2rem', display: 'flex', gap: '1rem', alignItems: 'center', padding: '1rem', borderRadius: '12px', backgroundColor: 'var(--bg-card)', color: 'var(--text-secondary)', borderLeft: '4px solid var(--accent-blue)' }}>
        <Info size={24} color="var(--accent-blue)" />
        <p style={{ margin: 0, fontSize: '0.9rem', lineHeight: '1.4' }}>
          <strong>Modo de Riesgo Corporativo:</strong> El perfil <strong>OWNER</strong> asume responsabilidades completas sobre el Root. Las directivas a continuación son exclusivas para el personal delegado. Cualquier modificación entra en rigor al renovar las firmas de los tokens.
        </p>
      </div>

      <div className="grid-2">
        {renderRoleColumn('SUPERVISOR')}
        {renderRoleColumn('VENDEDOR')}
      </div>
    </div>
  );
}
