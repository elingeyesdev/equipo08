import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { useToast } from '../components/ToastContext';
import { motion } from 'framer-motion';
import { Mail, Lock, ArrowRight, MousePointerClick } from 'lucide-react';

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
      
      // Guardar todo en sessionStorage
      sessionStorage.setItem('access_token', access_token);
      sessionStorage.setItem('user_id', user.id);
      sessionStorage.setItem('user_name', user.name);
      sessionStorage.setItem('user_role', user.role);
      sessionStorage.setItem('tenant_id', user.tenant_id || '');
      sessionStorage.setItem('tenant_name', user.tenant_name || '');
      sessionStorage.setItem('tenant_domain', user.tenant_domain || '');
      sessionStorage.setItem('tenant_logo', user.tenant_logoUrl || '');
      sessionStorage.setItem('user_sucursal_id', user.sucursal_id || '');
      sessionStorage.setItem('user_sucursal_name', user.sucursal_name || '');
      sessionStorage.setItem('permissions', JSON.stringify(user.permissions || {}));
      
      setAuthToken(access_token);
      
      toast.success(`Bienvenido, ${user.name} (${user.tenant_name})`);
      navigate('/');
    } catch (err) {
      const msg = err.response?.data?.message;
      if (msg === 'PENDING_APPROVAL') {
        toast.error('Tu tienda está en proceso de revisión. Te notificaremos cuando sea aprobada.', { duration: 5000 });
      } else {
        toast.error(msg || 'Error al iniciar sesión');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4 font-sans" style={{ colorScheme: 'light', background: '#f8fafc', color: '#0f172a' }}>
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, ease: 'easeOut' }}
        className="w-full max-w-md bg-white border border-slate-200 rounded-2xl shadow-xl p-8 md:p-10 z-10"
      >
        <div className="flex justify-center mb-8">
          <span className="text-3xl font-black text-slate-900 tracking-tight flex items-center">
            BolCl
            <MousePointerClick size={28} className="text-slate-700 mx-0.5" strokeWidth={2.5} />
            ck
          </span>
        </div>
        
        <div className="text-center mb-8">
          <h2 className="text-2xl font-bold text-slate-900 mb-2">Bienvenido</h2>
          <p className="text-slate-500 text-sm font-medium">Ingresa a tu panel de control</p>
        </div>
        
        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wide mb-1.5">Correo Electrónico</label>
            <div className="relative">
              <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
              <input 
                type="email" 
                value={email} 
                onChange={e => setEmail(e.target.value)} 
                required 
                placeholder="admin@mitienda.com"
                className="w-full bg-white border border-slate-300 rounded-lg py-2.5 pl-10 pr-4 text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors font-medium"
              />
            </div>
          </div>
          
          <div>
            <label className="block text-xs font-semibold text-slate-600 uppercase tracking-wide mb-1.5">Contraseña</label>
            <div className="relative">
              <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
              <input 
                type="password" 
                value={password} 
                onChange={e => setPassword(e.target.value)} 
                required 
                placeholder="••••••••"
                className="w-full bg-white border border-slate-300 rounded-lg py-2.5 pl-10 pr-4 text-slate-900 placeholder:text-slate-400 focus:outline-none focus:border-blue-600 focus:ring-1 focus:ring-blue-600 transition-colors font-medium"
              />
            </div>
          </div>
          
          <button 
            type="submit" 
            disabled={loading} 
            className="w-full bg-slate-900 hover:bg-slate-800 dark:bg-slate-100 dark:hover:bg-white text-white dark:text-slate-900 font-semibold py-3 rounded-lg flex items-center justify-center gap-2 transition-colors disabled:opacity-60 mt-2 shadow-sm"
          >
            {loading ? 'Verificando...' : 'Ingresar al Sistema'} 
            {!loading && <ArrowRight size={18} />}
          </button>
        </form>

        <div className="mt-8 text-center text-sm text-slate-500 font-medium">
          ¿No tienes una cuenta?{' '}
          <span className="text-blue-600 font-semibold cursor-pointer hover:text-blue-700 hover:underline transition-colors" onClick={() => navigate('/register')}>
            Registra tu tienda
          </span>
        </div>
      </motion.div>
    </div>
  );
}
