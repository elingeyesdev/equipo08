import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api';
import { Search, Loader2, PackageSearch, Tag, Info, ArrowLeft, Store, X, ShoppingCart, Plus, Minus, Send, Sun, Moon } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export default function PublicCatalogPage() {
  const { domain } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCategory, setFilterCategory] = useState('ALL');
  const [selectedProduct, setSelectedProduct] = useState(null);

  const getImageUrl = (url) => {
    if (!url) return '';
    if (url.startsWith('http://') || url.startsWith('https://')) return url;
    return `http://localhost:3000${url}`;
  };

  const [cart, setCart] = useState([]);
  const [isCartOpen, setIsCartOpen] = useState(false);

  const [theme, setTheme] = useState(() => localStorage.getItem('theme') || 'light');

  useEffect(() => {
    localStorage.setItem('theme', theme);
  }, [theme]);

  const addToCart = (product) => {
    setCart(prev => {
      const existing = prev.find(item => item.id === product.id);
      if (existing) {
        return prev.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item);
      }
      return [...prev, { ...product, quantity: 1 }];
    });
  };

  const removeFromCart = (productId, completely = false) => {
    setCart(prev => {
      const existing = prev.find(item => item.id === productId);
      if (completely || existing.quantity === 1) {
        return prev.filter(item => item.id !== productId);
      }
      return prev.map(item => item.id === productId ? { ...item, quantity: item.quantity - 1 } : item);
    });
  };

  const cartTotal = cart.reduce((acc, item) => acc + (item.precioVenta * item.quantity), 0);
  const cartItemsCount = cart.reduce((acc, item) => acc + item.quantity, 0);

  const sendWhatsApp = () => {
    let text = `Hola! Quisiera hacer un pedido de ${tienda.name}:\n\n`;
    cart.forEach(item => {
      text += `- ${item.quantity}x ${item.name} (Bs ${Number(item.precioVenta).toFixed(2)})\n`;
    });
    text += `\n*Total estimado: Bs ${cartTotal.toFixed(2)}*`;
    
    let phone = (tienda.phone || '').replace(/\D/g, '');
    if (phone.length === 8) phone = `591${phone}`; // Asumimos prefijo boliviano si solo hay 8 dígitos
    if (!phone) {
      alert("La tienda no tiene un número registrado para WhatsApp.");
      return;
    }
    window.open(`https://wa.me/${phone}?text=${encodeURIComponent(text)}`, '_blank');
  };

  useEffect(() => {
    const fetchCatalog = async () => {
      try {
        const res = await api.get(`/catalog/${domain}`);
        setData(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Error al cargar el catálogo');
      } finally {
        setLoading(false);
      }
    };
    fetchCatalog();
  }, [domain]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[var(--bg)] flex flex-col items-center justify-center">
        <Loader2 className="animate-spin text-[var(--txt-secondary)] mb-4" size={40} />
        <h2 className="text-lg font-semibold text-[var(--txt-primary)] tracking-tight">Cargando Catálogo...</h2>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="min-h-screen bg-[var(--bg)] flex flex-col items-center justify-center p-6 text-center">
        <div className="w-20 h-20 bg-rose-50 dark:bg-rose-500/10 rounded-full flex items-center justify-center mb-6">
          <Store className="text-rose-500" size={32} />
        </div>
        <h2 className="text-2xl font-bold text-[var(--txt-primary)] tracking-tight mb-2">Tienda no encontrada</h2>
        <p className="text-[var(--txt-secondary)] mb-8 max-w-md">{error}</p>
        <Link to="/" className="px-6 py-2.5 bg-[var(--txt-primary)] text-[var(--bg-card)] font-semibold rounded-xl transition-transform hover:scale-105">
          Volver al Inicio
        </Link>
      </div>
    );
  }

  const { tienda, productos } = data;
  const categorias = ['ALL', ...new Set(productos.map(p => p.category || 'Otros'))];

  const filteredProducts = productos.filter(p => {
    if (filterCategory !== 'ALL' && (p.category || 'Otros') !== filterCategory) return false;
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      if (!p.name.toLowerCase().includes(q) && !p.sku.toLowerCase().includes(q)) return false;
    }
    return true;
  });

  return (
    <div className={theme === 'dark' ? 'dark' : ''}>
      <div className="min-h-screen bg-[var(--bg)] pb-24 font-sans text-[var(--txt-primary)] selection:bg-[var(--txt-primary)] selection:text-[var(--bg-card)]">
      {/* Navbar Minimalista */}
      <nav className="bg-[var(--bg-card)]/80 backdrop-blur-md border-b border-[var(--border)] sticky top-0 z-40">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2 text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] transition-colors font-medium text-sm">
            <ArrowLeft size={18} />
            <span className="hidden sm:inline">Volver al Mall</span>
          </Link>
          <div className="font-bold text-lg tracking-tight absolute left-1/2 -translate-x-1/2">
            {tienda.name}
          </div>
          <button 
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
            className="bg-transparent border-none p-2 rounded-xl flex items-center justify-center text-[var(--txt-secondary)] hover:bg-[var(--txt-primary)]/10 hover:text-[var(--txt-primary)] transition-colors"
            title={theme === 'dark' ? 'Modo Claro' : 'Modo Oscuro'}
          >
            {theme === 'dark' ? <Sun size={20} strokeWidth={2} /> : <Moon size={20} strokeWidth={2} />}
          </button>
        </div>
      </nav>

      {/* Hero Section Minimalista */}
      <div className="bg-[var(--bg-card)] border-b border-[var(--border)] overflow-hidden">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12 relative flex flex-col items-center text-center">
          {tienda.logoUrl ? (
            <img src={tienda.logoUrl} alt={tienda.name} className="w-24 h-24 rounded-2xl object-cover shadow-sm border border-[var(--border)] mb-6" />
          ) : (
            <div className="w-24 h-24 rounded-2xl bg-slate-100 dark:bg-white/5 shadow-sm border border-[var(--border)] flex items-center justify-center text-[var(--txt-primary)] text-4xl font-bold mb-6">
              {tienda.name.charAt(0).toUpperCase()}
            </div>
          )}
          <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-3">
            {tienda.name}
          </h1>
          <p className="text-[var(--txt-secondary)] font-medium max-w-xl text-sm leading-relaxed">
            Explora nuestro catálogo digital y descubre los mejores productos que tenemos para ti.
          </p>
        </div>
      </div>

      {/* Buscador y Filtros */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-[var(--bg-card)] rounded-2xl p-2 shadow-sm border border-[var(--border)] flex flex-col md:flex-row gap-2 items-center">
          <div className="relative flex-1 w-full">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--txt-muted)]" size={18} />
            <input
              type="text"
              placeholder="Buscar productos por nombre o SKU..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-11 pr-4 py-3 bg-transparent border-none text-sm font-medium focus:ring-0 focus:outline-none placeholder:text-[var(--txt-muted)]"
            />
          </div>
          <div className="w-full md:w-auto flex overflow-x-auto px-2 pb-2 md:pb-0 md:px-0 hide-scrollbar gap-1 border-t md:border-t-0 md:border-l border-[var(--border)] pt-2 md:pt-0 md:pl-2">
            {categorias.map(cat => (
              <button
                key={cat}
                onClick={() => setFilterCategory(cat)}
                className={`whitespace-nowrap px-4 py-2 rounded-xl text-sm font-semibold transition-colors ${
                  filterCategory === cat 
                    ? 'bg-[var(--txt-primary)] text-[var(--bg-card)] shadow-md' 
                    : 'bg-transparent text-[var(--txt-secondary)] hover:bg-[var(--txt-primary)] hover:text-[var(--bg-card)] hover:bg-opacity-10 dark:hover:bg-opacity-20'
                }`}
              >
                {cat === 'ALL' ? 'Todos' : cat}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Grid de Productos */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        {filteredProducts.length === 0 ? (
          <div className="bg-[var(--bg-card)] rounded-2xl border border-[var(--border)] p-16 text-center flex flex-col items-center justify-center">
            <PackageSearch size={48} className="text-[var(--txt-muted)] mb-4" strokeWidth={1.5} />
            <h3 className="text-lg font-semibold mb-1">No se encontraron productos</h3>
            <p className="text-[var(--txt-secondary)] text-sm">Intenta buscar con otros términos o cambia la categoría.</p>
          </div>
        ) : (
          <motion.div 
            layout
            className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
          >
            <AnimatePresence>
              {filteredProducts.map(p => (
                 <motion.div
                 key={p.id}
                 layout
                 initial={{ opacity: 0, scale: 0.95 }}
                 animate={{ opacity: 1, scale: 1 }}
                 exit={{ opacity: 0, scale: 0.95 }}
                 whileHover={{ y: -4 }}
                 className="bg-[var(--bg-card)] rounded-2xl border border-[var(--border)] shadow-sm overflow-hidden flex flex-col group cursor-pointer transition-all hover:shadow-md"
                 onClick={() => setSelectedProduct(p)}
               >
                 <div className="aspect-square relative bg-[var(--bg)] overflow-hidden">
                   {p.imagen_url ? (
                     <img 
                       src={getImageUrl(p.imagen_url)} 
                       alt={p.name} 
                       className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" 
                     />
                   ) : (
                     <div className="w-full h-full flex items-center justify-center bg-[var(--bg)]">
                       <Tag size={32} className="text-[var(--txt-muted)]" />
                     </div>
                   )}
                   <div className="absolute top-3 left-3">
                     <span className="bg-[var(--bg-card)]/80 backdrop-blur border border-[var(--border)] px-2 py-1 rounded-md text-[10px] font-bold shadow-sm uppercase tracking-wider">
                       {p.category || 'Otros'}
                     </span>
                   </div>
                 </div>
                 
                 <div className="p-5 flex flex-col flex-grow">
                   <div className="flex-grow">
                     <h3 className="font-bold text-base leading-tight mb-1 group-hover:opacity-80 transition-opacity">
                       {p.name}
                     </h3>
                     <div className="text-xs text-[var(--txt-secondary)] mb-3">
                       {p.attributes && Object.keys(p.attributes).length > 0 ? (
                          <div className="flex flex-wrap gap-1 mt-2">
                            {Object.entries(p.attributes).slice(0,2).map(([key, val]) => val ? (
                              <span key={key} className="bg-[var(--bg)] border border-[var(--border)] px-1.5 py-0.5 rounded text-[10px] uppercase font-medium">
                                {key}: {val}
                              </span>
                            ) : null)}
                            {Object.keys(p.attributes).length > 2 && (
                              <span className="bg-[var(--bg)] border border-[var(--border)] px-1.5 py-0.5 rounded text-[10px] uppercase font-medium">
                                +{Object.keys(p.attributes).length - 2}
                              </span>
                            )}
                          </div>
                       ) : (
                          <span className="line-clamp-2 mt-1">{p.description || 'Sin detalles'}</span>
                       )}
                     </div>
                   </div>
                   <div className="flex items-end justify-between mt-4">
                     <div>
                       <span className="font-black text-lg">Bs {Number(p.precioVenta).toFixed(2)}</span>
                     </div>
                   </div>
                 </div>
               </motion.div>
              ))}
            </AnimatePresence>
          </motion.div>
        )}
      </div>

      {/* Modal de Detalle de Producto */}
      <AnimatePresence>
        {selectedProduct && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <motion.div 
              initial={{ opacity: 0 }} 
              animate={{ opacity: 1 }} 
              exit={{ opacity: 0 }}
              onClick={() => setSelectedProduct(null)}
              className="absolute inset-0 bg-slate-900/40 dark:bg-black/60 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ opacity: 0, y: 20, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 20, scale: 0.95 }}
              className="relative bg-[var(--bg-card)] rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden flex flex-col md:flex-row border border-[var(--border)]"
            >
              <button 
                onClick={() => setSelectedProduct(null)}
                className="absolute top-4 right-4 z-10 bg-[var(--bg-card)]/60 backdrop-blur-md p-2 rounded-full text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] transition-colors border border-[var(--border)]"
              >
                <X size={18} />
              </button>

              <div className="md:w-1/2 bg-[var(--bg)] relative min-h-[300px]">
                {selectedProduct.imagen_url ? (
                  <img src={getImageUrl(selectedProduct.imagen_url)} alt={selectedProduct.name} className="w-full h-full object-cover absolute inset-0" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center absolute inset-0">
                     <Tag size={48} className="text-[var(--txt-muted)]" />
                  </div>
                )}
              </div>

              <div className="md:w-1/2 p-8 flex flex-col overflow-y-auto">
                <span className="text-xs font-bold uppercase tracking-wider text-[var(--txt-secondary)] mb-2 block">
                  {selectedProduct.category || 'Otros'}
                </span>
                <h2 className="text-2xl font-bold mb-2 leading-tight">
                  {selectedProduct.name}
                </h2>
                <span className="font-mono text-[10px] font-bold bg-[var(--bg)] border border-[var(--border)] text-[var(--txt-secondary)] px-2 py-1 rounded-md w-max mb-6 block">
                  SKU: {selectedProduct.sku}
                </span>

                <div className="mb-6 flex-grow">
                  <div className="text-sm leading-relaxed mb-4 text-[var(--txt-secondary)]">
                    {selectedProduct.attributes && Object.keys(selectedProduct.attributes).length > 0 ? (
                      <div className="flex flex-col gap-2">
                        {Object.entries(selectedProduct.attributes).map(([key, val]) => val ? (
                          <div key={key} className="flex items-center justify-between border-b border-[var(--border)] pb-2 last:border-0 last:pb-0">
                            <span className="text-xs font-semibold uppercase tracking-wider text-[var(--txt-muted)]">{key}</span>
                            <span className="text-sm font-medium text-[var(--txt-primary)]">{val}</span>
                          </div>
                        ) : null)}
                        {selectedProduct.description && (
                          <div className="mt-4 pt-4 border-t border-[var(--border)] text-sm">
                            {selectedProduct.description}
                          </div>
                        )}
                      </div>
                    ) : (
                      <p>{selectedProduct.description || 'Este producto no cuenta con especificaciones adicionales.'}</p>
                    )}
                  </div>
                </div>

                <div className="bg-[var(--bg)] p-5 rounded-2xl border border-[var(--border)] mb-6 flex justify-between items-center">
                   <span className="text-sm font-semibold text-[var(--txt-secondary)]">Precio</span>
                   <span className="text-2xl font-black">
                     Bs {Number(selectedProduct.precioVenta).toFixed(2)}
                   </span>
                </div>

                <button 
                  onClick={() => { addToCart(selectedProduct); setSelectedProduct(null); setIsCartOpen(true); }}
                  className="w-full bg-[var(--txt-primary)] text-[var(--bg-card)] font-bold py-3.5 rounded-xl flex items-center justify-center gap-2 mb-3 transition-transform hover:scale-[1.02] shadow-sm"
                >
                  <ShoppingCart size={18} /> Añadir al Pedido
                </button>

                <p className="text-[10px] text-center text-[var(--txt-muted)] font-medium uppercase tracking-wider">
                  Coordinarás el pedido por WhatsApp
                </p>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Botón flotante del carrito */}
      <AnimatePresence>
        {cartItemsCount > 0 && !isCartOpen && (
          <motion.button
            initial={{ scale: 0, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0, opacity: 0 }}
            onClick={() => setIsCartOpen(true)}
            className="fixed bottom-6 right-6 z-40 bg-[var(--txt-primary)] text-[var(--bg-card)] rounded-full p-4 shadow-xl flex items-center gap-3 transition-transform hover:scale-105"
          >
            <div className="relative">
              <ShoppingCart size={22} />
              <span className="absolute -top-2 -right-3 bg-rose-500 text-white text-[9px] font-black w-4 h-4 flex items-center justify-center rounded-full border-2 border-[var(--bg-card)]">
                {cartItemsCount}
              </span>
            </div>
            <span className="font-bold text-sm pr-1">Bs {cartTotal.toFixed(2)}</span>
          </motion.button>
        )}
      </AnimatePresence>

      {/* Modal Carrito (Drawer Right) */}
      <AnimatePresence>
        {isCartOpen && (
          <div className="fixed inset-0 z-[100] flex justify-end">
            <motion.div 
              initial={{ opacity: 0 }} 
              animate={{ opacity: 1 }} 
              exit={{ opacity: 0 }}
              onClick={() => setIsCartOpen(false)}
              className="absolute inset-0 bg-slate-900/40 dark:bg-black/60 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="relative w-full max-w-md bg-[var(--bg-card)] h-full shadow-2xl flex flex-col border-l border-[var(--border)]"
            >
              <div className="p-6 border-b border-[var(--border)] flex items-center justify-between bg-[var(--bg)]">
                <h2 className="text-lg font-bold flex items-center gap-2">
                  <ShoppingCart size={20} /> Mi Pedido
                </h2>
                <button onClick={() => setIsCartOpen(false)} className="text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] p-1 transition-colors">
                  <X size={20} />
                </button>
              </div>
              
              <div className="flex-1 overflow-y-auto p-6 flex flex-col gap-3">
                {cart.length === 0 ? (
                  <div className="text-center text-[var(--txt-muted)] my-auto pb-10 flex flex-col items-center justify-center h-full">
                    <ShoppingCart size={40} className="opacity-30 mb-4" strokeWidth={1.5} />
                    Tu pedido está vacío.
                  </div>
                ) : (
                  cart.map(item => (
                    <div key={item.id} className="bg-[var(--bg)] p-3 rounded-xl border border-[var(--border)] flex gap-4 items-center">
                      <div className="w-14 h-14 bg-[var(--bg-card)] rounded-lg overflow-hidden flex-shrink-0 border border-[var(--border)]">
                        {item.imagen_url ? (
                          <img src={getImageUrl(item.imagen_url)} alt={item.name} className="w-full h-full object-cover" />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center"><Tag size={16} className="text-[var(--txt-muted)]"/></div>
                        )}
                      </div>
                      <div className="flex-1 flex flex-col justify-between">
                        <div>
                          <h4 className="font-semibold text-sm leading-tight line-clamp-1 mb-1">{item.name}</h4>
                          <span className="font-bold text-xs text-[var(--txt-secondary)]">Bs {Number(item.precioVenta).toFixed(2)}</span>
                        </div>
                        <div className="flex items-center gap-3 mt-2">
                          <button onClick={() => removeFromCart(item.id)} className="w-6 h-6 bg-[var(--bg-card)] hover:bg-[var(--txt-primary)] hover:text-[var(--bg-card)] border border-[var(--border)] rounded-md flex items-center justify-center transition-colors">
                            <Minus size={12} />
                          </button>
                          <span className="font-bold text-xs min-w-[12px] text-center">{item.quantity}</span>
                          <button onClick={() => addToCart(item)} className="w-6 h-6 bg-[var(--bg-card)] hover:bg-[var(--txt-primary)] hover:text-[var(--bg-card)] border border-[var(--border)] rounded-md flex items-center justify-center transition-colors">
                            <Plus size={12} />
                          </button>
                        </div>
                      </div>
                      <div className="text-right pl-2">
                         <span className="font-bold text-sm block">Bs {(item.precioVenta * item.quantity).toFixed(2)}</span>
                      </div>
                    </div>
                  ))
                )}
              </div>

              {cart.length > 0 && (
                <div className="p-6 border-t border-[var(--border)] bg-[var(--bg)]">
                  <div className="flex justify-between items-center mb-6">
                    <span className="text-[var(--txt-secondary)] font-semibold text-sm">Total estimado</span>
                    <span className="text-xl font-black">Bs {cartTotal.toFixed(2)}</span>
                  </div>
                  <button onClick={sendWhatsApp} className="w-full bg-[var(--txt-primary)] text-[var(--bg-card)] font-bold py-3.5 rounded-xl flex items-center justify-center gap-2 transition-transform hover:scale-[1.02] shadow-sm">
                    <Send size={18} /> Confirmar por WhatsApp
                  </button>
                </div>
              )}
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 mt-12 text-center text-[var(--txt-muted)] text-xs font-semibold uppercase tracking-widest">
        Desarrollado en MallLink &copy; 2026
      </div>
      </div>
    </div>
  );
}
