import React, { useState, useEffect } from 'react';
import api from '../api';
import { LayoutDashboard, TrendingUp, Archive, AlertTriangle, Receipt, DollarSign, ArrowRight, ShoppingCart, BarChart2, Loader2, Filter } from 'lucide-react';
import { useToast } from '../components/ToastContext';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function DashboardPage() {
  const userName = sessionStorage.getItem('user_name') || 'Usuario';
  const userRole = sessionStorage.getItem('user_role') || 'VENDEDOR';
  const userSucursalId = sessionStorage.getItem('user_sucursal_id') || '';
  const userSucursalName = sessionStorage.getItem('user_sucursal_name') || '';
  const toast = useToast();

  const [loading, setLoading] = useState(true);
  const [sucursales, setSucursales] = useState([]);
  const [selectedSucursal, setSelectedSucursal] = useState(userRole !== 'OWNER' && userSucursalId ? userSucursalId : 'ALL');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  const [metrics, setMetrics] = useState({
    totalSales: 0,
    totalRevenue: 0,
    totalProfit: 0,
    totalStockItems: 0,
    totalStockValue: 0,
    totalLosses: 0,
    recentSales: []
  });

  useEffect(() => {
    fetchSucursales();
  }, []);

  useEffect(() => {
    loadDashboardData();
  }, [selectedSucursal, startDate, endDate]);

  const fetchSucursales = async () => {
    try {
      const { data } = await api.get('/sucursales');
      setSucursales(data || []);
    } catch (err) {
      console.error('Error al cargar sucursales:', err);
    }
  };

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      const params = {};
      if (selectedSucursal && selectedSucursal !== 'ALL') {
        params.sucursal_id = selectedSucursal;
      }
      if (startDate) {
        params.startDate = startDate;
      }
      if (endDate) {
        params.endDate = endDate;
      }

      const [kpisRes, stockRes, ajustesRes] = await Promise.all([
        api.get('/ventas/kpis/dashboard', { params }).catch(() => ({ data: { totalVentas: 0, ingresosTotales: 0, utilidadTotal: 0, recentSales: [] } })),
        api.get('/stock').catch(() => ({ data: [] })),
        api.get('/ajustes').catch(() => ({ data: [] }))
      ]);

      const kpis = kpisRes.data;
      const stock = stockRes.data || [];
      const ajustes = ajustesRes.data || [];

      // Filtrado local de stock
      const filteredStock = selectedSucursal && selectedSucursal !== 'ALL'
        ? stock.filter(item => item.sucursal_id === selectedSucursal)
        : stock;

      // Filtrado local de pérdidas (ajustes) por fecha y sucursal
      const filteredAjustes = ajustes.filter(aj => {
        if (selectedSucursal && selectedSucursal !== 'ALL' && aj.sucursal_id !== selectedSucursal) {
          return false;
        }
        if (startDate || endDate) {
          const ajDate = new Date(aj.createdAt || aj.fecha).getTime();
          if (startDate) {
            const start = new Date(`${startDate}T00:00:00`).getTime();
            if (ajDate < start) return false;
          }
          if (endDate) {
            const end = new Date(`${endDate}T23:59:59`).getTime();
            if (ajDate > end) return false;
          }
        }
        return true;
      });

      const totalSales = kpis.totalVentas;
      const totalRevenue = kpis.ingresosTotales;
      const totalProfit = kpis.utilidadTotal;
      
      const totalStockItems = filteredStock.reduce((acc, item) => acc + Number(item.cantidadTotal || item.cantidad || 0), 0);
      const totalStockValue = filteredStock.reduce((acc, item) => {
        const itemPrice = item.producto?.precioVenta || item.producto?.precio || 0;
        return acc + (Number(item.cantidadTotal || item.cantidad || 0) * Number(itemPrice));
      }, 0);

      const totalLosses = filteredAjustes.reduce((acc, aj) => acc + Number(aj.valor_perdido || 0), 0);
      const recentSales = kpis.recentSales || [];

      setMetrics({
        totalSales: Number(totalSales) || 0,
        totalRevenue: Number(totalRevenue) || 0,
        totalProfit: Number(totalProfit) || 0,
        totalStockItems: Number(totalStockItems) || 0,
        totalStockValue: Number(totalStockValue) || 0,
        totalLosses: Number(totalLosses) || 0,
        recentSales
      });

    } catch (err) {
      toast.error('Error al cargar datos del dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-[70vh]">
        <Loader2 className="animate-spin text-[var(--txt-muted)] mb-4" size={32} />
        <span className="text-[var(--txt-secondary)] font-medium">Consolidando módulos...</span>
      </div>
    );
  }

  // Ocultar métricas financieras detalladas si es un vendedor básico, para privacidad del dueño.
  const isBasicSeller = userRole === 'VENDEDOR';

  return (
    <div className="full-width-container animate-fadein space-y-6">
      
      {/* HEADER */}
      <div className="page-header-bar">
        <div>
          <h1>Dashboard</h1>
          <p>Hola, <b>{userName}</b>. Aquí tienes un resumen unificado del estado de tu tienda.</p>
        </div>
        <div className="flex items-center gap-2">
          <button 
            onClick={() => setShowFilters(!showFilters)} 
            className={`py-2 px-4 rounded-xl text-sm font-bold flex items-center gap-2 transition-all shadow-sm border ${
              showFilters ? 'bg-white text-slate-900 border-slate-300' : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
            }`}
          >
            <Filter size={16} /> {showFilters ? 'Ocultar Filtros' : 'Filtrar'}
          </button>
        </div>
      </div>

      {/* FILTROS DE DASHBOARD */}
      {showFilters && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-2xl p-4 shadow-sm flex flex-wrap items-center gap-6 animate-fadeIn">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Filtrar Dashboard:</span>
          </div>
          <div className="flex flex-wrap items-center gap-4 flex-1">
            <div className="flex items-center gap-2">
              <label className="text-xs font-semibold text-slate-500">Sucursal:</label>
              <select
                value={selectedSucursal}
                onChange={e => setSelectedSucursal(e.target.value)}
                disabled={userRole !== 'OWNER' && !!userSucursalId}
                className="h-[38px] px-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-350 focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/10 disabled:opacity-75 disabled:cursor-not-allowed"
              >
                {userRole === 'OWNER' && <option value="ALL">-- Todas las sucursales --</option>}
                {sucursales.map(s => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
                {userRole !== 'OWNER' && userSucursalId && sucursales.length === 0 && (
                  <option value={userSucursalId}>{userSucursalName || 'Mi Sucursal'}</option>
                )}
              </select>
            </div>

            <div className="flex items-center gap-2">
              <label className="text-xs font-semibold text-slate-500">Desde:</label>
              <input 
                type="date" 
                value={startDate} 
                onChange={e => setStartDate(e.target.value)} 
                className="h-[38px] px-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-350 focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/10"
              />
            </div>
            <div className="flex items-center gap-2">
              <label className="text-xs font-semibold text-slate-500">Hasta:</label>
              <input 
                type="date" 
                value={endDate} 
                onChange={e => setEndDate(e.target.value)} 
                className="h-[38px] px-3 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-semibold text-slate-700 dark:text-slate-350 focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/10"
              />
            </div>
          </div>

          {(selectedSucursal !== (userRole !== 'OWNER' && userSucursalId ? userSucursalId : 'ALL') || startDate || endDate) && (
            <button 
              type="button"
              onClick={() => {
                setSelectedSucursal(userRole !== 'OWNER' && userSucursalId ? userSucursalId : 'ALL');
                setStartDate('');
                setEndDate('');
              }}
              className="text-xs font-bold text-rose-600 hover:text-rose-700 uppercase tracking-wider cursor-pointer transition-colors bg-transparent border-none p-0 h-auto self-center"
            >
              Limpiar Filtro
            </button>
          )}
        </div>
      )}

      {/* KPI GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 lg:grid-cols-3 gap-6 mb-8">
        
        {/* Card: Ventas Realizadas */}
        <div className="relative overflow-hidden rounded-xl bg-[#2563eb] text-white flex flex-col justify-between">
          <div className="absolute -right-2 -top-2 opacity-10 pointer-events-none">
            <ShoppingCart size={100} strokeWidth={2} />
          </div>
          <div className="p-6 relative z-10 flex-1">
            <h3 className="text-3xl font-semibold mb-1 text-white">{metrics.totalSales}</h3>
            <p className="text-sm font-medium text-white/90 uppercase tracking-wide">Ventas Históricas</p>
          </div>
          <a href="/sales#historial" className="w-full bg-black/10 hover:bg-black/20 transition-colors py-2 px-6 flex items-center justify-between text-sm font-medium text-white relative z-10">
            Más información <ArrowRight size={14} />
          </a>
        </div>

        {/* Card: Ingresos */}
        {!isBasicSeller && (
          <div className="relative overflow-hidden rounded-xl bg-emerald-600 text-white flex flex-col justify-between">
            <div className="absolute -right-2 -top-2 opacity-10 pointer-events-none">
              <DollarSign size={100} strokeWidth={2} />
            </div>
            <div className="p-6 relative z-10 flex-1">
              <h3 className="text-3xl font-semibold mb-1 text-white">Bs {(metrics.totalRevenue || 0).toFixed(0)}</h3>
              <p className="text-sm font-medium text-white/90 uppercase tracking-wide">Ingreso Bruto</p>
            </div>
            <a href="/sales#historial" className="w-full bg-black/10 hover:bg-black/20 transition-colors py-2 px-6 flex items-center justify-between text-sm font-medium text-white relative z-10">
              Más información <ArrowRight size={14} />
            </a>
          </div>
        )}

        {/* Card: Utilidad Bruta */}
        {!isBasicSeller && (
          <div className="relative overflow-hidden rounded-xl bg-amber-500 text-white flex flex-col justify-between">
            <div className="absolute -right-2 -top-2 opacity-10 pointer-events-none">
              <BarChart2 size={100} strokeWidth={2} />
            </div>
            <div className="p-6 relative z-10 flex-1">
              <h3 className="text-3xl font-semibold mb-1 text-white">Bs {(metrics.totalProfit || 0).toFixed(0)}</h3>
              <p className="text-sm font-medium text-white/90 uppercase tracking-wide">Utilidad Neta</p>
            </div>
            <a href="/sales#historial" className="w-full bg-black/10 hover:bg-black/20 transition-colors py-2 px-6 flex items-center justify-between text-sm font-medium text-white relative z-10">
              Más información <ArrowRight size={14} />
            </a>
          </div>
        )}

        {/* Card: Inventario */}
        <div className="relative overflow-hidden rounded-xl bg-[#4f46e5] text-white flex flex-col justify-between">
          <div className="absolute -right-2 -top-2 opacity-10 pointer-events-none">
            <Archive size={100} strokeWidth={2} />
          </div>
          <div className="p-6 relative z-10 flex-1">
            <h3 className="text-3xl font-semibold mb-1 text-white">{metrics.totalStockItems}</h3>
            <p className="text-sm font-medium text-white/90 uppercase tracking-wide">Unidades en Stock</p>
          </div>
          <a href="/stock" className="w-full bg-black/10 hover:bg-black/20 transition-colors py-2 px-6 flex items-center justify-between text-sm font-medium text-white relative z-10">
            Más información <ArrowRight size={14} />
          </a>
        </div>

        {/* Card: Pérdidas / Ajustes */}
        {!isBasicSeller && (
          <div className="relative overflow-hidden rounded-xl bg-rose-600 text-white flex flex-col justify-between">
            <div className="absolute -right-2 -top-2 opacity-10 pointer-events-none">
              <AlertTriangle size={100} strokeWidth={2} />
            </div>
            <div className="p-6 relative z-10 flex-1">
              <h3 className="text-3xl font-semibold mb-1 text-white">Bs {metrics.totalLosses.toFixed(0)}</h3>
              <p className="text-sm font-medium text-white/90 uppercase tracking-wide">Pérdidas Registradas</p>
            </div>
            <a href="/audit-reports" className="w-full bg-black/10 hover:bg-black/20 transition-colors py-2 px-6 flex items-center justify-between text-sm font-medium text-white relative z-10">
              Más información <ArrowRight size={14} />
            </a>
          </div>
        )}
      </div>

      {/* CHARTS SECTION */}
      <div className="mb-8">
        <div className="pb-2">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-xs font-semibold tracking-widest uppercase text-slate-500">Desempeño Semanal</h2>
            <div className="flex gap-5">
              <div className="flex items-center gap-1.5"><div className="w-4 h-0.5 rounded-full" style={{background:'#4a9eed'}}></div><span className="text-xs text-slate-500">Ingresos</span></div>
              <div className="flex items-center gap-1.5"><div className="w-4 h-0.5 rounded-full" style={{background:'#26a69a'}}></div><span className="text-xs text-slate-500">Utilidad</span></div>
            </div>
          </div>
          <div className="h-56 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart
                data={(() => {
                  const days = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
                  const weeklyData = [];
                  for (let i = 6; i >= 0; i--) {
                    const d = new Date();
                    d.setDate(d.getDate() - i);
                    const salesForDay = metrics.recentSales.filter(s => new Date(s.fecha || s.createdAt).getDate() === d.getDate());
                    const total = salesForDay.reduce((acc, curr) => acc + (Number(curr.total) || 0), 0);
                    const util = salesForDay.reduce((acc, curr) => acc + (Number(curr.utilidadTotal) || (Number(curr.total)*0.4) || 0), 0);
                    weeklyData.push({ name: days[d.getDay()], total, util });
                  }
                  return weeklyData;
                })()}
                margin={{ top: 8, right: 4, left: -20, bottom: 0 }}
              >
                <defs>
                  <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%"   stopColor="#4a9eed" stopOpacity={0.3}/>
                    <stop offset="100%" stopColor="#4a9eed" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorUtil" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%"   stopColor="#26a69a" stopOpacity={0.4}/>
                    <stop offset="100%" stopColor="#26a69a" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid stroke="var(--border)" strokeOpacity={0.5} vertical={false} strokeDasharray="0" />
                <XAxis dataKey="name" tick={{fill:'var(--txt-secondary)', fontSize:11}} tickLine={false} axisLine={false} />
                <YAxis tick={{fill:'var(--txt-secondary)', fontSize:11}} tickLine={false} axisLine={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '6px', fontSize: '12px', color: 'var(--txt-primary)' }}
                  cursor={{ stroke: 'var(--border)', strokeWidth: 1 }}
                />
                <Area type="monotone" dataKey="util"  stroke="#26a69a" strokeWidth={2} fillOpacity={1} fill="url(#colorUtil)"  dot={false} />
                <Area type="monotone" dataKey="total" stroke="#4a9eed" strokeWidth={2} fillOpacity={1} fill="url(#colorTotal)" dot={false} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

    </div>
  );
}
