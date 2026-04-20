import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { useToast } from '../components/ToastContext';

export default function RegisterPage() {
  const [name, setName] = useState('');
  const [domain, setDomain] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.post('/auth/register', { name, domain, email, password });
      
      toast.success('Cuenta comercial creada con éxito. Ya puedes iniciar sesión.');
      navigate('/login');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al registrar la tienda.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card" style={{ maxWidth: '500px' }}>
        <h2 className="auth-title">Registra tu Tienda</h2>
        <p className="auth-subtitle">Crea un espacio de trabajo para tu tienda</p>
        
        <form onSubmit={handleRegister}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div>
              <label>Nombre Fiscal o Fantasía</label>
              <input 
                type="text" 
                value={name} 
                onChange={e => setName(e.target.value)} 
                required 
                placeholder="Ej. Tienda Alpha"
                pattern="^[A-Za-záéíóúÁÉÍÓÚñÑ\s]+$"
                title="El nombre no puede contener números ni símbolos"
              />
            </div>
            <div>
              <label>Identificador Único (Dominio)</label>
              <input 
                type="text" 
                value={domain} 
                onChange={e => setDomain(e.target.value.toLowerCase().replace(/[^a-z0-9]/g, ''))} 
                required 
                placeholder="tiendaalpha"
              />
            </div>
          </div>
          
          <label>Correo Electrónico (Admin)</label>
          <input 
            type="email" 
            value={email} 
            onChange={e => setEmail(e.target.value)} 
            required 
            placeholder="admin@tiendaalpha.com"
          />
          
          <label>Contraseña Maestra</label>
          <input 
            type="password" 
            value={password} 
            onChange={e => setPassword(e.target.value)} 
            required 
            placeholder="••••••••"
            minLength="6"
          />
          
          <button type="submit" disabled={loading} style={{ width: '100%', marginTop: '1.5rem', backgroundColor: 'var(--accent-blue)' }}>
            {loading ? 'Creando tienda...' : 'Comenzar a usar BolClick'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', textAlign: 'center', fontSize: '0.9rem' }}>
          ¿Ya tienes cuenta? <span className="auth-link" onClick={() => navigate('/login')}>Inicia sesión</span>
        </div>
      </div>
    </div>
  );
}
