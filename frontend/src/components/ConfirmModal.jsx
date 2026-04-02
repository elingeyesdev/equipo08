import React from 'react';
import { AlertTriangle } from 'lucide-react';

export default function ConfirmModal({ isOpen, title, message, onConfirm, onCancel }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
          <div style={{ backgroundColor: '#fee2e2', padding: '0.5rem', borderRadius: '50%' }}>
            <AlertTriangle color="var(--danger-color)" size={24} />
          </div>
          <h3 style={{ margin: 0, color: 'var(--primary-color)' }}>{title}</h3>
        </div>
        
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', lineHeight: '1.5' }}>
          {message}
        </p>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
          <button 
            onClick={onCancel} 
            style={{ 
              backgroundColor: 'white', 
              color: 'var(--text-secondary)', 
              border: '1px solid var(--border-color)',
            }}
          >
            Cancelar
          </button>
          <button 
            onClick={onConfirm} 
            style={{ 
              backgroundColor: 'var(--danger-color)'
            }}
          >
             Eliminar
          </button>
        </div>
      </div>
    </div>
  );
}
