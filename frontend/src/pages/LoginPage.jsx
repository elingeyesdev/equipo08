import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { useToast } from '../components/ToastContext';

export default function LoginPage({ setAuthToken }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { email, password });
      const { access_token, user } = response.data;
      
      // Guardar todo en localStorage
      localStorage.setItem('access_token', access_token);
      localStorage.setItem('user_id', user.id);
      localStorage.setItem('user_name', user.name);
      localStorage.setItem('user_role', user.role);
      localStorage.setItem('tenant_id', user.tenant_id);
      localStorage.setItem('tenant_name', user.tenant_name);
      localStorage.setItem('permissions', JSON.stringify(user.permissions || {}));
      
      setAuthToken(access_token);
      
      toast.success(`Bienvenido, ${user.name} (${user.tenant_name})`);
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2 className="auth-title">Iniciar Sesión</h2>
        <p className="auth-subtitle">Accede al panel de control de tu tienda</p>
        
        <form onSubmit={handleLogin}>
          <label>Correo Electrónico</label>
          <input 
            type="email" 
            value={email} 
            onChange={e => setEmail(e.target.value)} 
            required 
            placeholder="admin@mitienda.com"
          />
          
          <label>Contraseña</label>
          <input 
            type="password" 
            value={password} 
            onChange={e => setPassword(e.target.value)} 
            required 
            placeholder="••••••••"
          />
          
          <button type="submit" disabled={loading} style={{ width: '100%', marginTop: '1rem' }}>
            {loading ? 'Ingresando...' : 'Ingresar al Sistema'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem' }}>
          ¿No tienes una cuenta? <span className="auth-link" onClick={() => navigate('/register')}>Registrar mi tienda</span>
        </div>
      </div>
    </div>
  );
}
