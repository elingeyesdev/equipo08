import React, { useState, useEffect } from 'react';
import api from '../api';
import { 
  Building2, CheckCircle2, XCircle, 
  Clock, ShieldAlert, BarChart3, Activity, Eye
} from 'lucide-react';
import { useToast } from '../components/ToastContext';

export default function AdminConsolePage() {
  const [metrics, setMetrics] = useState({ total: 0, pending: 0, approved: 0 });
  const [tenants, setTenants] = useState([]);
  const [selectedTenant, setSelectedTenant] = useState(null);
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [mRes, tRes] = await Promise.all([
        api.get('/admin/metrics'),
        api.get('/admin/tenants')
      ]);
      setMetrics(mRes.data);
      setTenants(tRes.data);
    } catch (err) {
      toast.error('Error cargando consola global');
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id, status) => {
    try {
      await api.patch(`/admin/tenants/${id}/status`, { status });
      toast.success(`Tienda marcada como ${status}`);
      loadData();
    } catch (err) {
      toast.error('Error actualizando estado');
    }
  };

  if (loading) return <div className="p-8 text-slate-400">Cargando consola...</div>;

  return (
    <div className="max-w-6xl mx-auto pb-12">
      <div className="mb-8">
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          Supervisión Global del Mall
        </h1>
        <p className="text-slate-500 font-medium mt-2">Control maestro de todas las tiendas registradas en el sistema.</p>
      </div>

      {}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        {[
          { label: 'Total Tiendas', value: metrics.total, color: 'text-slate-800 dark:text-slate-200' },
          { label: 'Pendientes', value: metrics.pending, color: 'text-amber-600 dark:text-amber-400' },
          { label: 'Aprobadas', value: metrics.approved, color: 'text-emerald-600 dark:text-emerald-400' },
          { label: 'Sistema', value: 'Operativo', color: 'text-blue-600 dark:text-blue-400' },
        ].map((m, i) => (
          <div key={i} className="bg-white border border-slate-200 dark:border-slate-800 p-5 rounded-2xl flex flex-col justify-center min-h-[90px] shadow-sm">
            <div className="text-xs font-bold text-slate-500 uppercase tracking-wider">{m.label}</div>
            <div className={`text-2xl font-black mt-1 ${m.color}`}>{m.value}</div>
          </div>
        ))}
      </div>

      {}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="px-6 py-5 border-b border-slate-100 flex justify-between items-center bg-slate-50">
          <h2 className="text-base font-bold text-slate-800">
            Directorio de Tiendas
          </h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider">
              <tr>
                <th className="px-6 py-4">Tienda / Dominio</th>
                <th className="px-6 py-4">Contacto (Admin)</th>
                <th className="px-6 py-4">Fecha Registro</th>
                <th className="px-6 py-4">Estado</th>
                <th className="px-6 py-4 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {tenants.map(t => (
                <tr key={t.id} className="hover:bg-slate-50/50 transition-colors">
                  <td className="px-6 py-4">
                    <div className="font-bold text-slate-900">{t.name}</div>
                    <div className="text-xs text-slate-500 font-mono mt-1">{t.domain}.bolclick.app</div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-slate-700">{t.email}</div>
                    {t.phone && <div className="text-xs text-slate-500 mt-1">{t.phone}</div>}
                  </td>
                  <td className="px-6 py-4 text-slate-500">
                    {new Date(t.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                      t.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-700' :
                      t.status === 'PENDING' ? 'bg-amber-100 text-amber-700' :
                      t.status === 'SUSPENDED' ? 'bg-rose-100 text-rose-700' :
                      'bg-slate-100 text-slate-700'
                    }`}>
                      {t.status === 'APPROVED' ? 'APROBADA' :
                       t.status === 'PENDING' ? 'PENDIENTE' :
                       t.status === 'SUSPENDED' ? 'SUSPENDIDA' : 'RECHAZADA'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex justify-end gap-2 items-center">
                      <button 
                        onClick={() => setSelectedTenant(t)} 
                        className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors flex items-center justify-center" 
                        title="Revisar Información"
                      >
                        <Eye size={18} />
                      </button>

                      {t.status === 'PENDING' && (
                        <>
                          <button onClick={() => updateStatus(t.id, 'APPROVED')} className="p-2 text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors flex items-center justify-center" title="Aprobar">
                            <CheckCircle2 size={18} />
                          </button>
                          <button onClick={() => updateStatus(t.id, 'REJECTED')} className="p-2 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors flex items-center justify-center" title="Rechazar">
                            <XCircle size={18} />
                          </button>
                        </>
                      )}
                      {t.status === 'APPROVED' && (
                        <button onClick={() => updateStatus(t.id, 'SUSPENDED')} className="text-xs font-bold text-rose-600 hover:bg-rose-50 px-3 py-1.5 rounded-lg transition-colors">
                          Suspender
                        </button>
                      )}
                      {t.status === 'SUSPENDED' && (
                        <button onClick={() => updateStatus(t.id, 'APPROVED')} className="text-xs font-bold text-emerald-600 hover:bg-emerald-50 px-3 py-1.5 rounded-lg transition-colors">
                          Activar
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {tenants.length === 0 && (
                <tr><td colSpan="5" className="px-6 py-8 text-center text-slate-400">No hay tiendas registradas.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {}
      {selectedTenant && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-2xl w-full max-w-md overflow-hidden flex flex-col border border-slate-200 dark:border-slate-700 animate-fadeIn">
            <div className="px-6 py-5 border-b border-slate-105 dark:border-slate-800 flex justify-between items-center bg-slate-50 dark:bg-slate-800">
              <h3 className="text-base font-bold text-slate-900 dark:text-white">Información de la Empresa</h3>
              <button 
                onClick={() => setSelectedTenant(null)} 
                className="text-slate-500 hover:text-slate-700 dark:hover:text-slate-350 text-lg font-bold"
              >
                ✕
              </button>
            </div>
            <div className="p-6 flex flex-col gap-4 text-left">
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Nombre Comercial</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.name}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Razón Social</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.razonSocial || 'No registrada'}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">NIT</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.nit || 'No registrado'}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Dirección / Ubicación</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.ubicacion || 'No registrada'}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Teléfono de Contacto</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.phone || 'No registrado'}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Correo Electrónico</div>
                <div className="text-sm font-bold text-slate-800 dark:text-slate-200">{selectedTenant.email}</div>
              </div>
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Dominio</div>
                <div className="text-sm font-bold text-slate-850 dark:text-slate-200">{selectedTenant.domain}.bolclick.app</div>
              </div>
            </div>
            <div className="p-6 border-t border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/50 flex justify-end">
              <button 
                onClick={() => setSelectedTenant(null)} 
                className="px-5 py-2 bg-slate-900 dark:bg-white text-white dark:text-slate-900 rounded-xl font-bold text-xs hover:opacity-90 transition-opacity"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
