import React, { useState, useEffect } from 'react';
import api from '../api';
import { Plus, X, Loader2, Trash2, Edit2, Search, Tag, AlertTriangle } from 'lucide-react';
import { useToast } from '../components/ToastContext';
import ConfirmModal from '../components/ConfirmModal';

export default function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [formData, setFormData] = useState({ nombre: '' });
  const [searchQuery, setSearchQuery] = useState('');
  const toast = useToast();

  const userRole = sessionStorage.getItem('user_role');
  const userPermissions = JSON.parse(sessionStorage.getItem('permissions') || '{}');

  const hasPermission = (key) => {
    if (userRole === 'OWNER') return true;
    return !!userPermissions[key];
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const { data } = await api.get('/productos/categorias');
      setCategories(data);
    } catch (err) {
      console.error(err);
      toast.error('Error al cargar las categorías');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (cat) => {
    setEditingId(cat.id);
    setFormData({ nombre: cat.nombre });
    setShowForm(true);
  };

  const handleDelete = (id) => {
    setConfirmDelete(id);
  };

  const proceedDelete = async () => {
    if (!confirmDelete) return;
    try {
      await api.delete(`/productos/categorias/${confirmDelete}`);
      toast.success('Categoría eliminada con éxito');
      fetchCategories();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al eliminar la categoría');
    } finally {
      setConfirmDelete(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.nombre.trim()) {
      toast.error('El nombre es requerido.');
      return;
    }

    try {
      if (editingId) {
        await api.put(`/productos/categorias/${editingId}`, formData);
        toast.success('Categoría actualizada con éxito');
      } else {
        await api.post('/productos/categorias', formData);
        toast.success('Categoría creada con éxito');
      }
      resetForm();
      fetchCategories();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Error al procesar la categoría');
    }
  };

  const resetForm = () => {
    setFormData({ nombre: '' });
    setEditingId(null);
    setShowForm(false);
  };

  const filteredCategories = categories.filter(c => 
    c.nombre.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="full-width-container animate-fadein space-y-6">
      
      {/* Header and Actions */}
      <div className="page-header-bar">
        <div>
          <h1>Categorías de Productos</h1>
          <p>Organiza tu catálogo y define las categorías de productos específicas para tu local.</p>
        </div>
        {hasPermission('catalogo_crear') && (
          <button 
            onClick={showForm ? resetForm : () => setShowForm(true)} 
            className={`py-2 px-4 rounded-xl text-xs font-semibold flex items-center gap-2 transition-all shadow-sm ${
              showForm ? 'bg-white/20 hover:bg-white/30 text-white' : 'bg-white text-[#184e77] hover:bg-slate-50'
            }`}
          >
            {showForm ? <><X size={14} /> Cancelar</> : <><Plus size={14} /> Nueva Categoría</>}
          </button>
        )}
      </div>

      {showForm && (
        <div className="bg-white border border-slate-200/60 rounded-2xl p-6 shadow-sm animate-fadeIn relative">
          <div className="flex justify-between items-center pb-3 border-b border-slate-100 mb-6">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider m-0">
              {editingId ? 'Editar Categoría' : 'Nueva Categoría'}
            </h3>
            <button 
              type="button" 
              onClick={resetForm} 
              className="text-slate-400 hover:text-slate-650 hover:bg-slate-50 p-1.5 rounded-lg transition-colors"
              title="Cerrar Formulario"
            >
              <X size={16} />
            </button>
          </div>
          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="form-grid">
              <div className="form-group w-full max-w-md">
                <label htmlFor="cat-nombre">Nombre de la Categoría *</label>
                <input 
                  id="cat-nombre"
                  type="text" 
                  value={formData.nombre} 
                  onChange={e => setFormData({ nombre: e.target.value })} 
                  required 
                  placeholder="Ej. Lácteos, Accesorios de Limpieza, etc." 
                  className="w-full h-[42px] px-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-semibold text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/10"
                />
              </div>
            </div>
            
            <div className="flex gap-2 justify-end pt-3">
              <button 
                type="button" 
                onClick={resetForm} 
                className="bg-transparent border border-slate-200 hover:bg-slate-50 text-slate-600 h-10 px-5 rounded-xl text-xs font-bold transition-all"
              >
                Volver
              </button>
              <button 
                type="submit" 
                className="bg-indigo-600 hover:bg-indigo-700 text-white h-10 px-5 rounded-xl text-xs font-bold transition-all shadow-sm shadow-indigo-600/10"
              >
                {editingId ? 'Guardar Cambios' : 'Registrar'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Filter and Search Section */}
      <div className="bg-white border border-slate-200/60 rounded-2xl p-5 shadow-sm">
        <div className="flex flex-wrap gap-4 items-center">
          <div className="flex-1 min-w-[240px] relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input 
              type="text" 
              placeholder="Buscar categoría por nombre..." 
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="w-full h-11 pl-10 pr-4 bg-slate-50 border border-slate-200 rounded-xl text-sm font-medium text-slate-700 focus:bg-white focus:outline-none transition-colors"
            />
          </div>
        </div>
      </div>

      {/* Categories List */}
      <div className="bg-white border border-slate-200/60 rounded-2xl shadow-sm overflow-hidden">
        {loading ? (
          <div className="flex flex-col items-center justify-center py-20 gap-3 text-slate-400">
            <Loader2 className="animate-spin text-indigo-500" size={32} />
            <span className="font-semibold text-sm">Cargando categorías...</span>
          </div>
        ) : filteredCategories.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 gap-3 text-slate-400">
            <div className="bg-slate-50 p-4 rounded-full border border-slate-100">
              <Tag size={32} className="text-slate-350" />
            </div>
            <p className="font-semibold text-sm text-slate-500">No se encontraron categorías</p>
            <p className="text-xs text-slate-400">Las categorías ayudan a clasificar y filtrar los artículos de tu local.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr>
                  <th>Nombre de la Categoría</th>
                  <th className="w-24 text-center">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-150">
                {filteredCategories.map(cat => (
                  <tr key={cat.id} className="hover:bg-slate-50/50 transition-colors">
                    <td>
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-indigo-50 flex items-center justify-center text-indigo-600 font-bold text-sm uppercase">
                          {cat.nombre[0]}
                        </div>
                        <span className="font-semibold text-slate-800 text-sm">{cat.nombre}</span>
                      </div>
                    </td>
                    <td className="text-center">
                      <div className="flex items-center justify-center gap-1">
                        {hasPermission('catalogo_editar') && (
                          <button 
                            onClick={() => handleEdit(cat)} 
                            className="btn-premium-icon"
                            title="Editar"
                          >
                            <Edit2 size={15} />
                          </button>
                        )}
                        {hasPermission('catalogo_eliminar') && (
                          <button 
                            onClick={() => handleDelete(cat.id)} 
                            className="btn-premium-icon btn-premium-icon-danger"
                            title="Eliminar"
                          >
                            <Trash2 size={15} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <ConfirmModal 
        isOpen={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        onConfirm={proceedDelete}
        title="¿Eliminar Categoría?"
        message="¿Estás seguro de que deseas eliminar permanentemente esta categoría? Solo se podrá eliminar si no tiene ningún producto asignado."
      />
    </div>
  );
}
