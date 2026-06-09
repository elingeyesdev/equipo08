import React, { useState, useEffect } from 'react';
import api from '../api';
import { LayoutDashboard, TrendingUp, Archive, AlertTriangle, Receipt, DollarSign, ArrowRight, ShoppingCart, BarChart2, Loader2 } from 'lucide-react';
import { useToast } from '../components/ToastContext';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export default function DashboardPage() {
  const userName = sessionStorage.getItem('user_name') || 'Usuario';
  const userRole = sessionStorage.getItem('user_role') || 'VENDEDOR';
  const toast = useToast();

  const [loading, setLoading] = useState(true);
  const [metrics, setMetrics] = useState({
    totalSales: 0,
    totalRevenue: 0,
    totalStockItems: 0,
    totalStockValue: 0,
    totalLosses: 0,
    recentSales: []
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      // Usamos Promise.all para cargar optimizadamente en paralelo.
      const [kpisRes, stockRes, ajustesRes] = await Promise.all([
        api.get('/ventas/kpis/dashboard').catch(() => ({ data: { totalVentas: 0, ingresosTotales: 0, utilidadTotal: 0, recentSales: [] } })),
        api.get('/stock').catch(() => ({ data: [] })),
        api.get('/ajustes').catch(() => ({ data: [] }))
      ]);

      const kpis = kpisRes.data;
      const stock = stockRes.data || [];
      const ajustes = ajustesRes.data || [];

      // Validar consistencia e interpretar información
      const totalSales = kpis.totalVentas;
      const totalRevenue = kpis.ingresosTotales;
      const totalProfit = kpis.utilidadTotal;
      
      const totalStockItems = stock.reduce((acc, item) => acc + Number(item.cantidadTotal || item.cantidad || 0), 0);
      const totalStockValue = stock.reduce((acc, item) => {
        const itemPrice = item.producto?.precioVenta || item.producto?.precio || 0;
        return acc + (Number(item.cantidadTotal || item.cantidad || 0) * Number(itemPrice));
      }, 0);

      const totalLosses = ajustes.reduce((acc, aj) => acc + Number(aj.valor_perdido || 0), 0);

      // Obtener últimas 5 ventas para mostrar actividad reciente
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
      </div>

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
              <h3 className="text-3xl font-semibold mb-1 text-white">Bs {metrics.totalRevenue.toFixed(0)}</h3>
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
              <h3 className="text-3xl font-semibold mb-1 text-white">Bs {metrics.totalProfit.toFixed(0)}</h3>
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
        
        {/* Gráfico Principal */}
        <div className="bg-[#2c3136] border border-[#3a3f44] rounded-xl overflow-hidden shadow-sm">
          <div className="px-6 py-4 border-b border-[#3a3f44] flex items-center justify-between">
            <h2 className="text-sm font-bold text-slate-200 uppercase tracking-wider">Valor de Ventas</h2>
            <div className="flex gap-4 items-center">
              <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-blue-500"></div><span className="text-xs text-slate-400 font-bold">Ingresos</span></div>
              <div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-emerald-400"></div><span className="text-xs text-slate-400 font-bold">Utilidad</span></div>
            </div>
          </div>
          <div className="p-6 h-64 w-full">
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
                margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
              >
                <defs>
                  <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorUtil" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#34d399" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#34d399" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#3a3f44" vertical={false} />
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px', color: '#fff' }} />
                <Area type="monotone" dataKey="total" stroke="#3b82f6" strokeWidth={3} fillOpacity={1} fill="url(#colorTotal)" />
                <Area type="monotone" dataKey="util" stroke="#34d399" strokeWidth={3} fillOpacity={1} fill="url(#colorUtil)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

      </div>

    </div>
  );
}
