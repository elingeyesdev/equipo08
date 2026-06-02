import React, { useState, useEffect } from 'react';
import api from '../api';
import { Settings, Store, Phone, Image as ImageIcon, Save, Loader2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';

export default function SettingsPage() {
  const [profile, setProfile] = useState({ name: '', phone: '', logoUrl: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const toast = useToast();

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const res = await api.get('/tenant/profile');
      setProfile({
        name: res.data.name || '',
        phone: res.data.phone || '',
        logoUrl: res.data.logoUrl || ''
      });
    } catch (err) {
      toast.error('Error cargando perfil');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.patch('/tenant/profile', profile);
      sessionStorage.setItem('tenant_logo', profile.logoUrl || '');
      window.dispatchEvent(new Event('tenant_logo_updated'));
      toast.success('Configuración guardada correctamente');
    } catch (err) {
      toast.error('Error al guardar configuración');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-20">
        <Loader2 className="animate-spin text-[var(--txt-muted)] mb-4" size={32} />
        <span className="text-[var(--txt-secondary)] font-medium">Cargando ajustes...</span>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto pb-12">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[var(--txt-primary)] tracking-tight flex items-center gap-3">
          <Settings className="text-[var(--txt-secondary)]" size={32} />
          Ajustes del Negocio
        </h1>
        <p className="text-[var(--txt-secondary)] font-medium mt-2">Personaliza la información principal de tu tienda.</p>
      </div>

      <div className="bg-[var(--bg-card)] rounded-2xl border border-[var(--border)] shadow-sm overflow-hidden">
        <div className="px-6 py-5 border-b border-[var(--border)] bg-[var(--bg)]">
          <h2 className="text-base font-bold text-[var(--txt-primary)] flex items-center gap-2">
            <Store size={18} className="text-[var(--txt-secondary)]" /> Perfil Comercial
          </h2>
        </div>
        
        <form onSubmit={handleSave} className="p-6 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

            <div>
              <label className="block text-xs font-bold text-[var(--txt-secondary)] uppercase tracking-wider mb-2">
                Teléfono de Contacto
              </label>
              <input 
                type="text" 
                value={profile.phone}
                onChange={e => setProfile({...profile, phone: e.target.value})}
                placeholder="+591 70000000"
                className="w-full bg-[var(--bg)] border border-[var(--border)] rounded-xl px-4 py-3 text-sm font-medium text-[var(--txt-primary)] focus:ring-1 focus:ring-[var(--txt-primary)] focus:border-[var(--txt-primary)] outline-none transition-all"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-xs font-bold text-[var(--txt-secondary)] uppercase tracking-wider mb-2">
                URL del Logo
              </label>
              <input 
                type="url" 
                value={profile.logoUrl}
                onChange={e => setProfile({...profile, logoUrl: e.target.value})}
                placeholder="https://ejemplo.com/logo.png"
                className="w-full bg-[var(--bg)] border border-[var(--border)] rounded-xl px-4 py-3 text-sm font-medium text-[var(--txt-primary)] focus:ring-1 focus:ring-[var(--txt-primary)] focus:border-[var(--txt-primary)] outline-none transition-all"
              />
            </div>
          </div>

          <div className="pt-6 border-t border-[var(--border)] flex justify-end">
            <button 
              type="submit" 
              disabled={saving}
              className="flex items-center gap-2 bg-[var(--txt-primary)] hover:opacity-90 text-[var(--bg-card)] px-6 py-3 rounded-xl font-bold text-sm transition-opacity disabled:opacity-50"
            >
              <Save size={16} /> {saving ? 'Guardando...' : 'Guardar Cambios'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
