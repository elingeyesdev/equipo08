import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import api from './api';
import ProvidersPage from './pages/ProvidersPage';
import SourcingPage from './pages/SourcingPage';
import StockPage from './pages/StockPage';
import ProductsPage from './pages/ProductsPage';
import SucursalesPage from './pages/SucursalesPage';
import UsersPage from './pages/UsersPage';
import PermissionsPage from './pages/PermissionsPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import { ToastProvider } from './components/ToastContext';
import { Settings, Users, Package, ShoppingCart, LogOut, Tag, Archive, Store, ShieldCheck, UserPlus } from 'lucide-react';
import './index.css';

function Sidebar({ setAuthToken, permissions }) {
  const location = useLocation();
  const tenantName = localStorage.getItem('tenant_name') || 'Mi Tienda';
  const userName = localStorage.getItem('user_name') || 'Usuario';
  const userRole = localStorage.getItem('user_role') || 'VENDEDOR';

  const handleLogout = () => {
    localStorage.clear();
    setAuthToken(null);
  };

  // Helper to check if a permission exists in our list
  const hasPerm = (key) => {
    if (userRole === 'OWNER') return true;
    if (!permissions) return false;
    const p = permissions.find(p => p.role === userRole);
    return p ? p[key.replace('.', '_')] : false;
  };

  return (
    <div className="sidebar" style={{ display: 'flex', flexDirection: 'column', padding: '1.5rem', boxSizing: 'border-box' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2rem' }}>
        <div style={{ width: '40px', height: '40px', borderRadius: '8px', overflow: 'hidden', display: 'flex', justifyContent: 'center', alignItems: 'center', backgroundColor: 'white', border: '1px solid var(--border-color)' }}>
          <img src="/logo.png" alt="BolClick Logo" style={{ width: '100%', height: '100%', objectFit: 'contain' }} />
        </div>
        <div>
          <h2 style={{ margin: 0, fontSize: '1.2rem', color: 'var(--primary-color)' }}>BolClick</h2>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', fontWeight: '600', textTransform: 'uppercase' }}>{tenantName}</span>
        </div>
      </div>

      <div style={{ padding: '0.75rem', backgroundColor: 'rgba(255,255,255,0.5)', borderRadius: '12px', marginBottom: '1.5rem', border: '1px solid var(--border-color)' }}>
        <div style={{ fontSize: '0.85rem', fontWeight: 'bold' }}>{userName}</div>
        <div style={{ fontSize: '0.7rem', color: 'var(--primary-color)', fontWeight: 'bold', textTransform: 'uppercase' }}>{userRole}</div>
      </div>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem', flex: 1 }}>
        {hasPerm('catalogo.ver') && (
          <Link to="/providers" className={`nav-link ${location.pathname === '/providers' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Users size={18} /> Proveedores
          </Link>
        )}
        
        {hasPerm('sucursales.ver') && (
          <Link to="/sucursales" className={`nav-link ${location.pathname === '/sucursales' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Store size={18} /> Sucursales
          </Link>
        )}

        {hasPerm('catalogo.ver') && (
          <Link to="/products" className={`nav-link ${location.pathname === '/products' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Tag size={18} /> Catálogo
          </Link>
        )}

        {hasPerm('sourcing.ver') && (
          <Link to="/sourcing" className={`nav-link ${location.pathname === '/sourcing' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <ShoppingCart size={18} /> Lotes (Sourcing)
          </Link>
        )}

        {hasPerm('inventario.ver') && (
          <Link to="/stock" className={`nav-link ${location.pathname === '/stock' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Archive size={18} /> Inventario
          </Link>
        )}

        {userRole === 'OWNER' && (
          <>
            <div style={{ height: '1px', backgroundColor: 'var(--border-color)', margin: '1rem 0' }} />
            <span style={{ fontSize: '0.7rem', color: 'var(--text-secondary)', padding: '0 0.75rem', marginBottom: '0.5rem', fontWeight: 'bold' }}>ADMINISTRACIÓN</span>
            
            <Link to="/users" className={`nav-link ${location.pathname === '/users' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <UserPlus size={18} /> Empleados
            </Link>

            <Link to="/permissions" className={`nav-link ${location.pathname === '/permissions' ? 'active' : ''}`} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <ShieldCheck size={18} /> Permisos
            </Link>
          </>
        )}
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

function App() {
  const [authToken, setAuthToken] = useState(localStorage.getItem('access_token'));
  const [permissions, setPermissions] = useState(null);

  useEffect(() => {
    if (authToken && localStorage.getItem('user_role') !== 'OWNER') {
      fetchPermissions();
    }
  }, [authToken]);

  const fetchPermissions = async () => {
    try {
      const res = await api.get('/users/permissions');
      setPermissions(res.data);
    } catch (err) {
      console.error("Error fetching permissions", err);
    }
  };

  return (
    <ToastProvider>
      <Router>
        {!authToken ? (
          <Routes>
            <Route path="/login" element={<LoginPage setAuthToken={setAuthToken} />} />
            <Route path="/register" element={<RegisterPage setAuthToken={setAuthToken} />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        ) : (
          <div className="app-layout">
            <Sidebar setAuthToken={setAuthToken} permissions={permissions} />
            <div className="main-content">
              
              <Routes>
                <Route path="/" element={
                  <div className="glass-container" style={{ textAlign: 'center', padding: '4rem 2rem' }}>
                    <Package size={48} color="var(--accent-blue)" style={{ marginBottom: '1rem' }} />
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Resumen de Operaciones</h2>
                    <p style={{ color: 'var(--text-secondary)', maxWidth: '500px', margin: '0 auto' }}>
                      Bienvenido al sistema, {localStorage.getItem('user_name')}. Tienes el rol de {localStorage.getItem('user_role')}.
                    </p>
                  </div>
                } />
                
                <Route path="/providers" element={<ProvidersPage key={authToken} />} />
                <Route path="/sucursales" element={<SucursalesPage key={authToken} />} />
                <Route path="/products" element={<ProductsPage key={authToken} />} />
                <Route path="/sourcing" element={<SourcingPage key={authToken} />} />
                <Route path="/stock" element={<StockPage key={authToken} />} />
                
                {/* Admin Routes */}
                <Route path="/users" element={<UsersPage key={authToken} />} />
                <Route path="/permissions" element={<PermissionsPage key={authToken} />} />

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
