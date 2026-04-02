import React, { useState, useEffect } from 'react';
import api from '../api';
import { Archive } from 'lucide-react';

export default function StockPage() {
  const [stock, setStock] = useState([]);

  useEffect(() => {
    api.get('/stock').then(res => setStock(res.data)).catch(console.error);
  }, []);

  return (
    <div className="glass-container">
      <h3 style={{ marginTop: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <Archive size={24} color="var(--primary-color)" /> Inventario Transversal Unificado
      </h3>
      <p style={{ color: 'var(--text-secondary)' }}>Este inventario se sincroniza dinámicamente cada vez que registras decesos o ingresos en sourcing, operando bajo tenencia aislada.</p>
      
      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>Nombre del Producto</th>
            <th>Stock Actual (Físico)</th>
            <th>Última Actualización</th>
          </tr>
        </thead>
        <tbody>
          {stock.length === 0 ? <tr><td colSpan="4" style={{ textAlign: 'center' }}>Sin stock registrado</td></tr> : 
           stock.map(s => (
             <tr key={s.id}>
               <td>{s.producto?.sku}</td>
               <td>{s.producto?.name}</td>
               <td><span className="badge success" style={{ padding: '0.4rem 0.8rem', fontSize: '0.9rem' }}>{s.cantidadTotal} U</span></td>
               <td style={{ fontSize: '0.9rem', color: 'var(--text-secondary)'}}>{new Date(s.ultimaActualizacion).toLocaleString()}</td>
             </tr>
           ))
          }
        </tbody>
      </table>
    </div>
  );
}
