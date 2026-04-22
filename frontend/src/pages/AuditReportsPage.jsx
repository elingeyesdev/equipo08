import React, { useState, useEffect } from 'react';
import { ClipboardList, Filter, MapPin } from 'lucide-react';
import api from '../api';
import { useToast } from '../components/ToastContext';

export default function AuditReportsPage() {
  const [ajustes, setAjustes] = useState([]);
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    fetchAjustes();
  }, []);

  const fetchAjustes = async () => {
    setLoading(true);
    try {
      const res = await api.get('/ajustes');
      setAjustes(res.data);
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

      {/* KPI Section Placeholder */}
      <div style={{ padding: '1.5rem', backgroundColor: '#f1f5f9', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
        [Estructura KPIs / Resumen de Reporte en construcción]
      </div>

      {/* Filter Section Placeholder */}
      <div style={{ padding: '1rem', backgroundColor: '#fff', borderRadius: '8px', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '1rem' }}>
         <Filter size={18} color="var(--text-secondary)" />
         <span style={{ color: 'var(--text-secondary)' }}>[Herramientas de filtrado por diseñarse]</span>
      </div>

      {/* Main Report Table Placeholder */}
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
            ) : ajustes.length === 0 ? (
              <tr><td colSpan="7" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>No existen incidencias perjudiciales registradas.</td></tr>
            ) : (
                 ajustes.slice(0).reverse().map(a => {
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
