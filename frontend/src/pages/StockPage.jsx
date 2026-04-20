import React, { useState, useEffect } from 'react';
import api from '../api';
import { Archive, MapPin } from 'lucide-react';

export default function StockPage() {
  const [stock, setStock] = useState([]);
  const [sucursales, setSucursales] = useState([]);
  const [selectedBranch, setSelectedBranch] = useState('ALL');

  useEffect(() => {
    Promise.all([
      api.get('/stock'),
      api.get('/sucursales')
    ]).then(([resStock, resSuc]) => {
      setStock(resStock.data);
      setSucursales(resSuc.data);
    }).catch(console.error);
  }, []);

  const filteredStock = selectedBranch === 'ALL' 
    ? stock 
    : stock.filter(s => s.sucursal_id === selectedBranch);

  const totalValuation = filteredStock.reduce((acc, curr) => acc + Number(curr.valorAdquisicion || 0), 0);

  return (
    <div className="glass-container" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h3 style={{ marginTop: 0, margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Archive size={24} color="var(--primary-color)" /> Inventario y Valuación
          </h3>
          <p style={{ margin: 0, color: 'var(--text-secondary)' }}>Operando bajo centros de costos segregados físicamente.</p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MapPin size={18} color="var(--accent-blue)" />
          <select 
            value={selectedBranch} 
            onChange={e => setSelectedBranch(e.target.value)}
            style={{ minWidth: '200px', backgroundColor: 'var(--bg-color)', border: '1px solid var(--border-color)', borderRadius: '6px', padding: '0.5rem' }}
          >
            <option value="ALL">Consolidado Total (Todas las Sucursales)</option>
            {sucursales.map(s => <option key={s.id} value={s.id}>Sucursal: {s.name}</option>)}
          </select>
        </div>
      </div>
      
      {/* Resumen */}
      <div style={{ backgroundColor: '#f1f5f9', padding: '1rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ color: 'var(--text-secondary)', fontWeight: '500' }}>Inversión Física Total (A Costo):</span>
        <span style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'var(--primary-color)' }}>
          Bs. {totalValuation.toFixed(2)}
        </span>
      </div>

      <table style={{ margin: 0 }}>
        <thead>
          <tr>
            <th>SKU</th>
            <th>Nombre del Producto</th>
            <th>Ubicación Física</th>
            <th style={{ textAlign: 'center' }}>Stock Total</th>
            <th style={{ textAlign: 'center' }}>Costo Prom. (U.)</th>
            <th style={{ textAlign: 'right' }}>Valuación Histórica</th>
          </tr>
        </thead>
        <tbody>
          {filteredStock.length === 0 ? <tr><td colSpan="6" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>Sin inventario físico en esta selección.</td></tr> : 
           filteredStock.map(s => {
             const valuation = Number(s.valorAdquisicion || 0);
             const costoPromedio = s.cantidadTotal > 0 ? (valuation / s.cantidadTotal) : 0;
             return (
               <tr key={s.id}>
                 <td style={{ fontWeight: '500' }}><span style={{ backgroundColor: '#e2e8f0', padding: '0.2rem 0.4rem', borderRadius: '4px', fontSize: '0.8rem' }}>{s.producto?.sku}</span></td>
                 <td>{s.producto?.name}</td>
                 <td style={{ color: 'var(--text-secondary)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                       <MapPin size={14} /> {s.sucursal?.name || 'Huérfana'}
                    </div>
                 </td>
                 <td style={{ textAlign: 'center' }}><strong style={{ color: '#16a34a' }}>{s.cantidadTotal} U</strong></td>
                 <td style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>Bs {costoPromedio.toFixed(2)}</td>
                 <td style={{ textAlign: 'right', fontWeight: 'bold', color: 'var(--primary-color)' }}>Bs {valuation.toFixed(2)}</td>
               </tr>
             );
           })
          }
        </tbody>
      </table>
    </div>
  );
}
