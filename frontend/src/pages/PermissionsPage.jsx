import React, { useState, useEffect } from 'react';
import api from '../api';
import { useToast } from '../components/ToastContext';
import { Save, Loader2, Info, Shield, Store, Tags, Inbox, Box, Users, Receipt, CheckCircle, HelpCircle, ChevronDown } from 'lucide-react';

export default function PermissionsPage() {
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(null); // stores the role currently saving
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
    setSaving(role);
    const data = permissions.find(p => p.role === role);
    try {
      await api.put('/users/permissions', data);
      toast.success(`Políticas de seguridad de ${role} actualizadas correctamente`);
    } catch (err) {
      toast.error('Error al guardar cambios');
    } finally {
      setSaving(null);
    }
  };

  if (loading) return (
    <div className="flex flex-col items-center justify-center py-20 text-slate-500 text-sm">
      <Loader2 className="animate-spin text-indigo-650 mb-3" size={32} />
      <span className="font-semibold">Cargando consola de seguridad...</span>
    </div>
  );

  const permissionGroups = [
    { 
      title: 'Punto de Venta (POS)', 
      icon: Receipt,
      fields: [
        { key: 'ventas_ver', label: 'Acceder a la Terminal de POS / Ventas', desc: 'Permite abrir la pantalla de ventas y facturación.' },
        { key: 'ventas_crear', label: 'Procesar y Emitir Ventas', desc: 'Permite registrar transacciones y cobros.' },
        { key: 'ventas_editar', label: 'Modificar Registros de Ventas', desc: 'Permite editar transacciones emitidas.' },
        { key: 'ventas_eliminar', label: 'Anular / Eliminar Transacciones', desc: 'Permite borrar facturas del historial.' }
      ] 
    },
    { 
      title: 'Catálogo Central (Productos)', 
      icon: Tags,
      fields: [
        { key: 'catalogo_ver', label: 'Visualización Global de Productos', desc: 'Permite navegar por el catálogo de artículos.' },
        { key: 'catalogo_crear', label: 'Añadir Nuevos Productos', desc: 'Permite registrar nuevos códigos y marcas.' },
        { key: 'catalogo_editar', label: 'Modificar Precios y Artículos', desc: 'Permite editar descripciones y precios.' },
        { key: 'catalogo_eliminar', label: 'Eliminar Artículos del Catálogo', desc: 'Permite dar de baja artículos permanentemente.' }
      ] 
    },
    { 
      title: 'Gestión de Proveedores', 
      icon: Users,
      fields: [
        { key: 'proveedores_ver', label: 'Consultar Directorio de Proveedores', desc: 'Permite ver la lista de proveedores asociados.' },
        { key: 'proveedores_crear', label: 'Registrar Nuevos Proveedores', desc: 'Permite registrar NITs y razones sociales libres.' },
        { key: 'proveedores_editar', label: 'Editar Datos de Proveedores', desc: 'Permite cambiar correos e información comercial.' },
        { key: 'proveedores_eliminar', label: 'Dar de Baja Proveedores', desc: 'Permite remover proveedores que no tengan relación activa.' }
      ] 
    },
    { 
      title: 'Sourcing (Recepción de Stock)', 
      icon: Inbox,
      fields: [
        { key: 'sourcing_ver', label: 'Inspeccionar Lotes de Compra', desc: 'Permite ver el historial de entradas al almacén.' },
        { key: 'sourcing_crear', label: 'Registrar Nuevos Ingresos', desc: 'Permite sumar unidades físicas de stock.' },
        { key: 'sourcing_editar', label: 'Editar Lotes Existentes', desc: 'Permite modificar cantidades capturadas por error.' },
        { key: 'sourcing_eliminar', label: 'Anular Lotes Ingresados', desc: 'Permite revertir y restar las unidades ingresadas.' }
      ] 
    },
    { 
      title: 'Ajustes de Inventario', 
      icon: Box,
      fields: [
        { key: 'inventario_ver', label: 'Visualización de Auditorías', desc: 'Permite auditar el registro analítico de pérdidas.' },
        { key: 'inventario_crear', label: 'Registrar Actas de Ajuste', desc: 'Permite reportar robos, pérdidas o caducidades.' },
        { key: 'inventario_editar', label: 'Modificar Incidencias', desc: 'Permite modificar actas ya guardadas.' },
        { key: 'inventario_eliminar', label: 'Eliminar Historial de Pérdidas', desc: 'Permite remover reportes de la base de datos.' }
      ] 
    },
    { 
      title: 'Recursos Humanos', 
      icon: Shield,
      fields: [
        { key: 'usuarios_ver', label: 'Consultar Organigrama Interno', desc: 'Permite ver la lista de empleados contratados.' },
        { key: 'usuarios_crear', label: 'Contratar Personal', desc: 'Permite crear nuevos usuarios con roles asignados.' },
        { key: 'usuarios_editar', label: 'Editar Datos de Personal', desc: 'Permite restablecer contraseñas y editar roles.' },
        { key: 'usuarios_eliminar', label: 'Desvincular Personal', desc: 'Permite dar de baja usuarios del sistema.' }
      ] 
    },
    { 
      title: 'Sucursales Físicas', 
      icon: Store,
      fields: [
        { key: 'sucursales_ver', label: 'Consultar Directorio Geográfico', desc: 'Permite ver las sucursales habilitadas.' },
        { key: 'sucursales_crear', label: 'Registrar Nuevas Sucursales', desc: 'Permite registrar locales comerciales nuevos.' },
        { key: 'sucursales_editar', label: 'Editar Datos de Sucursales', desc: 'Permite cambiar direcciones o teléfonos.' },
        { key: 'sucursales_eliminar', label: 'Dar de Baja Sucursales', desc: 'Permite desactivar sucursales.' }
      ] 
    }
  ];

  const renderRoleColumn = (roleName) => {
    const roleData = permissions.find(p => p.role === roleName);
    if (!roleData) return null;

    const isRoleSaving = saving === roleName;

    return (
      <div className="bg-transparent border border-slate-200/40 rounded-3xl overflow-hidden flex flex-col relative group">
        
        {/* Header monocromático minimalista */}
        <div className="p-6 border-b border-slate-200/40 flex items-center justify-between">
          <div>
            <div className="flex items-center gap-2">
              <div className="p-2 rounded-xl border border-slate-200/40 text-slate-500">
                <Shield size={18} strokeWidth={2} />
              </div>
              <div>
                <span className="block font-black text-slate-800 text-lg tracking-tight uppercase">{roleName}</span>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest block">Políticas de Acceso</span>
              </div>
            </div>
          </div>
          <button
            onClick={() => handleSave(roleName)}
            disabled={isRoleSaving}
            className="py-2 px-5 rounded-xl text-xs font-bold flex items-center gap-2 transition-all border border-slate-300 bg-white hover:bg-slate-50 text-slate-700 disabled:opacity-50 disabled:cursor-not-allowed shadow-xs"
          >
            {isRoleSaving ? (
              <Loader2 size={14} className="animate-spin" />
            ) : (
              <Save size={14} />
            )}
            Guardar Cambios
          </button>
        </div>

        {/* Listado de Grupos de Permisos */}
        <div className="p-6 space-y-3 flex-1 overflow-y-auto max-h-[600px] custom-scrollbar">
          {permissionGroups.map((group) => {
            const Icon = group.icon;
            return (
              <details key={group.title} className="group bg-transparent border border-slate-200/40 rounded-xl transition-all duration-200 hover:border-slate-300 overflow-hidden">
                <summary className="flex items-center justify-between p-4 cursor-pointer list-none outline-none select-none">
                  <div className="flex items-center gap-3">
                    <div className="p-1.5 rounded border border-slate-200/40 text-slate-500">
                      <Icon size={16} strokeWidth={2} />
                    </div>
                    <h4 className="text-sm font-bold text-slate-700 tracking-wide m-0">
                      {group.title}
                    </h4>
                  </div>
                  <div className="p-1 text-slate-400 group-open:-rotate-180 transition-transform duration-300">
                    <ChevronDown size={16} strokeWidth={2} />
                  </div>
                </summary>
                
                <div className="space-y-1 px-4 pb-4 pt-1 border-t border-slate-200/40 bg-transparent">
                  {group.fields.map((field) => (
                    <div key={field.key} className="flex items-center justify-between gap-4 p-3 rounded-xl hover:bg-slate-50 transition-colors duration-150">
                      <div className="space-y-0.5">
                        <span className="block text-xs font-bold text-slate-800 leading-none">
                          {field.label}
                        </span>
                        <span className="text-[10px] text-slate-400 font-medium leading-relaxed block">
                          {field.desc}
                        </span>
                      </div>
                      
                      <label className="relative inline-flex items-center cursor-pointer flex-shrink-0">
                        <input
                          type="checkbox"
                          className="sr-only peer"
                          checked={!!roleData[field.key]}
                          onChange={() => handleToggle(roleName, field.key)}
                        />
                        <div className={`w-10 h-5 bg-slate-200 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all shadow-inner ${
                          roleName === 'SUPERVISOR' ? 'peer-checked:bg-indigo-600' : 'peer-checked:bg-teal-600'
                        }`}></div>
                      </label>
                    </div>
                  ))}
                </div>
              </details>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className="full-width-container animate-fadein space-y-6">
      
      {/* Page Header */}
      <div className="page-header-bar">
        <div>
          <h1>Políticas de Acceso (ACL)</h1>
          <p>Configura y actualiza en caliente los privilegios de tu equipo de trabajo.</p>
        </div>
      </div>



      {/* Dos Columnas de Privilegios */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
        {renderRoleColumn('SUPERVISOR')}
        {renderRoleColumn('VENDEDOR')}
      </div>
    </div>
  );
}
