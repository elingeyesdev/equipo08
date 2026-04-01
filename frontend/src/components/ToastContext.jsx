import React, { createContext, useContext, useState, useCallback } from 'react';
import { XCircle, CheckCircle, Info } from 'lucide-react';

const ToastContext = createContext();

export const useToast = () => useContext(ToastContext);

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);

    setTimeout(() => {
      removeToast(id);
    }, 5000); // Auto vanish after 5s
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const toast = {
    error: (msg) => addToast(msg, 'error'),
    success: (msg) => addToast(msg, 'success'),
    info: (msg) => addToast(msg, 'info'),
  };

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="toast-container">
        {toasts.map((t) => (
          <div key={t.id} className={`toast-card toast-${t.type}`}>
            <div className="toast-icon">
              {t.type === 'error' && <XCircle size={24} color="#ef4444" />}
              {t.type === 'success' && <CheckCircle size={24} color="#10b981" />}
              {t.type === 'info' && <Info size={24} color="#3b82f6" />}
            </div>
            <div className="toast-content">
              <strong>{t.type === 'error' ? 'Operación Denegada' : t.type === 'success' ? 'Éxito' : 'Información'}</strong>
              <p>{t.message}</p>
            </div>
            <button className="toast-close" onClick={() => removeToast(t.id)}>×</button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};
