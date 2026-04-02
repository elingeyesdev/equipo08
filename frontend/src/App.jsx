import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import ProvidersPage from './pages/ProvidersPage';
import SourcingPage from './pages/SourcingPage';
import StockPage from './pages/StockPage';
import ProductsPage from './pages/ProductsPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import { ToastProvider } from './components/ToastContext';
import { Settings, Users, Package, ShoppingCart, LogOut, Tag, Archive } from 'lucide-react';
import './index.css';

function Sidebar({ setTenantId }) {
  const location = useLocation();
  const tenantName = localStorage.getItem('tenant_name') || 'Mi Tienda';

  const handleLogout = () => {
    localStorage.removeItem('tenant_id');
    localStorage.removeItem('tenant_name');
    setTenantId(null);
  };

  return (
    <div className="sidebar" style={{ display: 'flex', flexDirection: 'column', padding: '1.5rem', boxSizing: 'border-box' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2.5rem' }}>
        <div style={{ width: '40px', height: '40px', borderRadius: '8px', overflow: 'hidden', display: 'flex', justifyContent: 'center', alignItems: 'center', backgroundColor: 'white', border: '1px solid var(--border-color)' }}>
          <img src="/logo.png" alt="BolClick Logo" style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
        </div>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.2rem', color: 'var(--primary-color)' }}>BolClick</h2>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', fontWeight: '600', textTransform: 'uppercase' }}>{tenantName}</span>
        </div>
      </div>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem', flex: 1 }}>
        <Link to="/providers" className={`nav-link ${location.pathname === '/providers' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Users size={18} /> Proveedores
        </Link>
        <Link to="/products" className={`nav-link ${location.pathname === '/products' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Tag size={18} /> Catálogo
        </Link>
        <Link to="/sourcing" className={`nav-link ${location.pathname === '/sourcing' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <ShoppingCart size={18} /> Lotes (Sourcing)
        </Link>
        <Link to="/stock" className={`nav-link ${location.pathname === '/stock' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Archive size={18} /> Inventario
        </Link>
      </div>

      <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1rem', marginTop: 'auto' }}>
        <button 
          onClick={handleLogout}
          style={{ 
            backgroundColor: 'transparent', 
            color: 'var(--danger-color)', 
            width: '100%', 
            display: 'flex', 
            alignItems: 'center', 
            gap: '0.75rem',
            padding: '0.75rem 1rem',
            textAlign: 'left'
          }}
          onMouseOver={e => e.currentTarget.style.backgroundColor = '#fee2e2'}
          onMouseOut={e => e.currentTarget.style.backgroundColor = 'transparent'}
        >
          <LogOut size={18} /> Cerrar Sesión
        </button>
      </div>
    </div>
  );
}

// Protected Route Component
const ProtectedRoute = ({ children, tenantId }) => {
  if (!tenantId) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  const [tenantId, setTenantId] = useState(localStorage.getItem('tenant_id'));

  return (
    <ToastProvider>
      <Router>
        {!tenantId ? (
          <Routes>
            <Route path="/login" element={<LoginPage setTenantId={setTenantId} />} />
            <Route path="/register" element={<RegisterPage setTenantId={setTenantId} />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        ) : (
          <div className="app-layout">
            <Sidebar setTenantId={setTenantId} />
            <div className="main-content">
              
              <Routes>
                <Route path="/" element={
                  <div className="glass-container" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <Package size={48} color="var(--accent-blue)" style={{ marginBottom: '1rem' }} />
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Resumen de Operaciones</h2>
                    <p style={{ color: 'var(--text-secondary)', maxWidth: '500px', margin: '0 auto' }}>
                      Bienvenido al sistema de administración de tu tienda. Utiliza el menú lateral para gestionar tus proveedores, registrar nuevos ingresos de mercancía y consultar el nivel de inventario en tiempo real.
                    </p>
                  </div>
                } />
                <Route path="/providers" element={<ProvidersPage key={tenantId} />} />
                <Route path="/products" element={<ProductsPage key={tenantId} />} />
                <Route path="/sourcing" element={<SourcingPage key={tenantId} />} />
                <Route path="/stock" element={<StockPage key={tenantId} />} />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>

            </div>
          </div>
        )}
      </Router>
    </ToastProvider>
  );
}

export default App;
