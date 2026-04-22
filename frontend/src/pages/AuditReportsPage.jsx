import React, { useState, useEffect } from 'react';
import { ClipboardList, Filter, MapPin } from 'lucide-react';
import api from '../api';
import { useToast } from '../components/ToastContext';

export default function AuditReportsPage() {
  const [ajustes, setAjustes] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Advanced Reporting Filters
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [selectedUser, setSelectedUser] = useState('ALL');
  const [selectedMotivo, setSelectedMotivo] = useState('ALL');

  const toast = useToast();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [resAj, resUsr] = await Promise.all([
        api.get('/ajustes'),
        api.get('/users').catch(() => ({ data: [] }))
      ]);
      setAjustes(resAj.data);
      setUsuarios(resUsr.data);
    } catch (err) {
      console.error(err);
      toast.error('Error al descargar el registro analítico');
    } finally {
      setLoading(false);
    }
  };

  const formatMotivo = (motivo) => {
    switch(motivo) {
      case 'ERROR_REGISTRO': return 'Error de Registro';
      case 'DANO_MERMA': return 'Artículo Dañado / Extraviado';
      case 'ROBO_O_PERDIDA': return 'Robo / No Habido';
      case 'CADUCIDAD': return 'Vencido';
      default: return motivo;
    }
  };

  const filteredAjustes = ajustes.filter(a => {
    // 1. Date Range
    let validDate = true;
    if (startDate && endDate) {
       const rowDate = new Date(a.fecha);
       const start = new Date(startDate);
       const end = new Date(endDate);
       end.setHours(23, 59, 59, 999);
       validDate = rowDate >= start && rowDate <= end;
    }
    // 2. User
    let validUser = true;
    if (selectedUser !== 'ALL') {
       validUser = a.usuario_id === selectedUser;
    }
    // 3. Motivo
    let validMotivo = true;
    if (selectedMotivo !== 'ALL') {
       validMotivo = a.motivo === selectedMotivo;
    }

    return validDate && validUser && validMotivo;
  });

  const totalFilteredLoss = filteredAjustes.reduce((acc, a) => acc + Number(a.valor_perdido || 0), 0);

  return (
    <div className="glass-container" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', animation: 'fadeIn 0.3s ease' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h3 style={{ marginTop: 0, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <ClipboardList size={24} color="var(--primary-color)" /> Registro Analítico de Auditorías
          </h3>
          <p style={{ margin: 0, color: 'var(--text-secondary)' }}>Dashboard unificado para el análisis histórico y seguimiento del déficit de inventario.</p>
        </div>
      </div>

      {/* KPI Section */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1rem' }}>
         <div style={{ backgroundColor: '#fef2f2', padding: '1.25rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid #fecaca' }}>
            <div>
              <span style={{ color: '#991b1b', fontWeight: '500', display: 'block', marginBottom: '0.25rem' }}>Valuación de Déficit (En Base a Filtros):</span>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '1.8rem', fontWeight: 'bold', color: '#b91c1c' }}>
                  Bs. {totalFilteredLoss.toFixed(2)}
                </span>
              </div>
            </div>
         </div>

         <div style={{ backgroundColor: '#f1f5f9', padding: '1.25rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid #cbd5e1' }}>
            <div>
              <span style={{ color: 'var(--text-secondary)', fontWeight: '500', display: 'block', marginBottom: '0.25rem' }}>Incidencias Detectadas:</span>
              <span style={{ fontSize: '1.8rem', fontWeight: 'bold', color: 'var(--primary-color)' }}>
                {filteredAjustes.length} Registros
              </span>
            </div>
         </div>
      </div>

      {/* Advanced Filters */}
      <div style={{ padding: '1rem', backgroundColor: '#fff', borderRadius: '8px', border: '1px solid var(--border-color)', display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '1rem' }}>
         <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Filter size={18} color="var(--text-secondary)" />
            <strong style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Filtros:</strong>
         </div>
         
         <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <label style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Desde:</label>
            <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} style={{ padding: '0.3rem', borderRadius: '4px', border: '1px solid #cbd5e1' }} />
         </div>

         <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <label style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Hasta:</label>
            <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} style={{ padding: '0.3rem', borderRadius: '4px', border: '1px solid #cbd5e1' }} />
         </div>

         <select value={selectedUser} onChange={e => setSelectedUser(e.target.value)} style={{ padding: '0.4rem', borderRadius: '4px', border: '1px solid #cbd5e1' }}>
            <option value="ALL">Cualquier Operador</option>
            {usuarios.map(u => <option key={u.id} value={u.id}>{u.name}</option>)}
         </select>

         <select value={selectedMotivo} onChange={e => setSelectedMotivo(e.target.value)} style={{ padding: '0.4rem', borderRadius: '4px', border: '1px solid #cbd5e1' }}>
            <option value="ALL">Cualquier Categoría</option>
            <option value="ERROR_REGISTRO">Error de Registro</option>
            <option value="DANO_MERMA">Artículo Dañado / Extraviado</option>
            <option value="ROBO_O_PERDIDA">Robo / No Habido</option>
            <option value="CADUCIDAD">Vencido</option>
         </select>

         <button 
           onClick={() => { setStartDate(''); setEndDate(''); setSelectedUser('ALL'); setSelectedMotivo('ALL'); }}
           style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem', background: 'transparent', border: '1px solid #cbd5e1', color: 'var(--text-secondary)' }}
         >
            Limpiar
         </button>
      </div>

      {/* Main Report Table */}
      <div style={{ overflowX: 'auto', borderRadius: '8px', border: '1px solid var(--border-color)', backgroundColor: '#fff' }}>
        <table style={{ margin: 0 }}>
          <thead>
            <tr>
              <th>Fecha y Hora</th>
              <th>Producto Afectado</th>
              <th>Ubicación</th>
              <th>Operador Técnico</th>
              <th style={{ textAlign: 'center' }}>Diferencia Fija</th>
              <th>Categoría Judicial</th>
              <th style={{ textAlign: 'right' }}>Déficit Financiero</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="7" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>Sincronizando auditorías...</td></tr>
            ) : filteredAjustes.length === 0 ? (
              <tr><td colSpan="7" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>No existen incidencias que coincidan con estos filtros.</td></tr>
            ) : (
                 filteredAjustes.slice(0).reverse().map(a => {
                    const deltaVal = a.cantidad_fisica - a.cantidad_sistema;
                    return (
                      <tr key={a.id}>
                        <td style={{ color: 'var(--text-secondary)' }}>{new Date(a.fecha).toLocaleString()}</td>
                        <td style={{ fontWeight: '500' }}>{a.producto?.name || 'SKU D/C'}</td>
                        <td style={{ color: 'var(--text-secondary)' }}><MapPin size={12}/> {a.sucursal?.name}</td>
                        <td>{a.usuario?.name || 'Operador'}</td>
                        <td style={{ textAlign: 'center' }}>
                           <span style={{ padding: '0.2rem 0.5rem', borderRadius: '4px', backgroundColor: deltaVal < 0 ? '#fee2e2' : '#dcfce3', color: deltaVal < 0 ? '#991b1b' : '#166534', fontWeight: 'bold' }}>
                              {deltaVal === 0 ? 'Sin Cambios' : (deltaVal > 0 ? `+${deltaVal}` : deltaVal)} U
                           </span>
                        </td>
                        <td><span style={{ fontSize: '0.8rem', backgroundColor: '#f1f5f9', padding: '0.2rem 0.5rem', borderRadius: '4px' }}>{formatMotivo(a.motivo)}</span></td>
                        <td style={{ textAlign: 'right', fontWeight: 'bold', color: '#b91c1c' }}>Bs. {Number(a.valor_perdido || 0).toFixed(2)}</td>
                      </tr>
                    );
                 })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
