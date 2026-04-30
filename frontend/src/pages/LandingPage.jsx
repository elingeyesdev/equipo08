import React from 'react';
import { Link } from 'react-router-dom';
import { 
  TrendingUp, 
  MapPin, 
  Smartphone, 
  BookOpen, 
  FileText, 
  Bell, 
  Globe,
  ArrowRight,
  ShieldCheck,
  Zap
} from 'lucide-react';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-white text-slate-900 font-sans selection:bg-orange-200">
      
      {/* Navigation Bar - PointMeUp Style: Transparent to Solid */}
      <nav className="fixed w-full z-50 bg-blue-600/90 backdrop-blur-md border-b border-white/10 py-4">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-white flex items-center justify-center text-blue-600 font-black text-2xl shadow-lg">
                B
              </div>
              <span className="font-black text-2xl tracking-tighter text-white">BolClick</span>
            </div>
            <div className="flex items-center gap-4">
              <Link to="/login" className="text-white/90 hover:text-white font-bold text-sm hidden sm:block">
                Iniciar Sesión
              </Link>
              <Link to="/register" className="bg-white text-blue-600 px-6 py-2.5 rounded-full font-black text-sm shadow-xl hover:scale-105 transition-transform active:scale-95">
                DEMO GRATIS
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Section - The PointMeUp Bold Impact */}
      <section className="relative pt-32 pb-20 lg:pt-48 lg:pb-56 bg-gradient-to-br from-blue-600 to-indigo-700 overflow-hidden">
        {/* Abstract Background Shapes */}
        <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none opacity-20">
          <div className="absolute -top-24 -left-24 w-96 h-96 rounded-full bg-white/20 blur-3xl"></div>
          <div className="absolute top-1/2 -right-48 w-[30rem] h-[30rem] rounded-full bg-indigo-400/20 blur-3xl"></div>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
          <div className="max-w-4xl mx-auto">
            <h1 className="text-5xl lg:text-[5.5rem] font-black text-white tracking-tighter leading-[0.95] mb-8 uppercase italic">
              Controla tu negocio. <br/>
              <span className="text-blue-200">Sin límites.</span>
            </h1>
            <p className="text-xl lg:text-2xl text-white/90 mb-12 font-medium max-w-2xl mx-auto leading-relaxed">
              La plataforma SaaS definitiva para la gestión de sucursales, inventarios inteligentes y facturación de alta disponibilidad.
            </p>
            <div className="flex flex-col sm:flex-row gap-6 justify-center items-center">
              <Link to="/register" className="bg-white text-blue-600 w-full sm:w-auto px-10 py-5 rounded-full font-black text-xl shadow-[0_20px_50px_rgba(0,0,0,0.2)] hover:shadow-[0_20px_50px_rgba(0,0,0,0.3)] hover:-translate-y-1 transition-all">
                EMPEZAR AHORA
              </Link>
              <a href="#features" className="text-white border-2 border-white/30 hover:bg-white/10 w-full sm:w-auto px-10 py-5 rounded-full font-black text-xl transition-all">
                VER FUNCIONES
              </a>
            </div>
          </div>

          {/* Floating Device Mockups */}
          <div className="mt-20 lg:mt-32 relative">
             <div className="relative z-20 max-w-5xl mx-auto">
                <div className="bg-white rounded-2xl shadow-[0_30px_100px_rgba(0,0,0,0.3)] border-8 border-slate-900 overflow-hidden transform hover:scale-[1.02] transition-transform duration-500">
                  <img src="/placeholder.svg" alt="Dashboard BolClick" className="w-full h-auto aspect-video object-cover" />
                </div>
             </div>
             
             {/* Secondary Mockups (Floating) */}
             <div className="absolute -bottom-10 -left-4 lg:-left-20 z-30 w-40 lg:w-64 hidden sm:block">
                <div className="bg-white rounded-3xl shadow-2xl border-4 border-slate-900 overflow-hidden transform -rotate-12">
                   <img src="/placeholder.svg" alt="Mobile POS" className="w-full h-auto aspect-[9/19] object-cover" />
                </div>
             </div>
             <div className="absolute top-0 -right-4 lg:-right-20 z-10 w-48 lg:w-80 hidden lg:block">
                <div className="bg-white rounded-2xl shadow-2xl border-4 border-slate-900 overflow-hidden transform rotate-12 opacity-50">
                   <img src="/placeholder.svg" alt="Tableta" className="w-full h-auto aspect-[4/3] object-cover" />
                </div>
             </div>
          </div>
        </div>
      </section>

      {/* Trust Bar */}
      <section className="bg-slate-50 py-12 border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
           <p className="text-center text-slate-400 font-black text-sm uppercase tracking-widest mb-8">CONFIADO POR EMPRESAS LÍDERES</p>
           <div className="flex flex-wrap justify-center items-center gap-8 lg:gap-20 opacity-50 grayscale">
              {/* Client Logo Placeholders */}
              <div className="font-black text-2xl text-slate-900 italic">LOGOTIPO</div>
              <div className="font-black text-2xl text-slate-900 italic">EMPRESA</div>
              <div className="font-black text-2xl text-slate-900 italic">BRAND</div>
              <div className="font-black text-2xl text-slate-900 italic">CORP</div>
           </div>
        </div>
      </section>

      {/* Features - PointMeUp Circles Style */}
      <section id="features" className="py-24 lg:py-40 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col lg:flex-row justify-between items-end mb-20 gap-8">
            <div className="max-w-2xl">
              <h2 className="text-4xl lg:text-6xl font-black text-slate-900 tracking-tighter uppercase italic leading-none mb-6">
                Todo lo que <br/> <span className="text-blue-600 underline decoration-indigo-200">necesitas.</span>
              </h2>
              <p className="text-xl text-slate-500 font-medium">
                Una suite completa diseñada para optimizar cada punto de contacto de tu negocio retail.
              </p>
            </div>
            <Link to="/register" className="text-blue-600 font-black text-lg flex items-center gap-2 hover:gap-4 transition-all">
              VER TODAS LAS FUNCIONES <ArrowRight size={24} />
            </Link>
          </div>

          <div className="grid md:grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-16">
            <DetailedFeatureCard 
              icon={<TrendingUp size={40} className="text-white" />}
              title="Salud Financiera"
              description="Toma el control absoluto del rendimiento monetario de tu empresa en tiempo real, eliminando las conjeturas y basando tus decisiones estratégicas en métricas precisas."
              features={[
                "Cálculo automático de Utilidad Bruta y Márgenes.",
                "Valorización dinámica de inventario al instante.",
                "Auditorías financieras con cero discrepancias."
              ]}
            />
            <DetailedFeatureCard 
              icon={<Globe size={40} className="text-white" />}
              title="Vista Consolidada"
              description="Diseñado para dueños de múltiples sucursales. Monitorea el rendimiento comparativo de todos tus locales comerciales desde una única sala de control."
              features={[
                "Aislamiento de datos seguro por cada sucursal.",
                "Filtros dinámicos de rendimiento y cruce de datos.",
                "KPIs globales e individuales en una sola pantalla."
              ]}
            />
            <DetailedFeatureCard 
              icon={<Smartphone size={40} className="text-white" />}
              title="Terminal POS"
              description="Un facturador ágil, resistente e intuitivo. Construido para no detenerse nunca, garantizando que tus cajeros procesen ventas a máxima velocidad."
              features={[
                "Búsqueda predictiva y ultra-rápida por código o nombre.",
                "Arquitectura de alta disponibilidad para picos de ventas.",
                "Compatibilidad total (PC, Tablet y Terminal Móvil)."
              ]}
            />
            <DetailedFeatureCard 
              icon={<BookOpen size={40} className="text-white" />}
              title="Catálogo Digital"
              description="Olvídate del desorden. Unifica todos tus productos, variantes, categorías y costos en un núcleo de datos estructurado y escalable."
              features={[
                "Soporte nativo para variantes complejas (Talla/Color).",
                "Gestión centralizada de márgenes y costos operativos.",
                "Integración directa con tu red de proveedores."
              ]}
            />
            <DetailedFeatureCard 
              icon={<FileText size={40} className="text-white" />}
              title="Comprobantes"
              description="Automatiza la burocracia corporativa. Genera recibos digitales profesionales al instante, manteniendo un rastro de auditoría inmutable."
              features={[
                "Generación automática de PDFs descargables.",
                "Historial inmutable para la prevención de fraudes (0 mermas fantasma).",
                "Registro centralizado de transacciones por operador."
              ]}
            />
            <DetailedFeatureCard 
              icon={<Bell size={40} className="text-white" />}
              title="Alertas Stock"
              description="El sistema predictivo que trabaja por ti. Monitorea tus niveles de stock de forma proactiva y alerta antes de que pierdas una venta."
              features={[
                "Configuración precisa de stock mínimo por producto.",
                "Alertas visuales tempranas de déficit de inventario.",
                "Flujo ágil para generación de lotes de reabastecimiento."
              ]}
            />
          </div>
        </div>
      </section>

      {/* Deep Section - Mall & Supervision */}
      <section className="bg-slate-900 py-32 lg:py-48 overflow-hidden relative">
         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col lg:flex-row items-center gap-20">
            <div className="lg:w-1/2 relative z-10 text-center lg:text-left">
               <div className="inline-block px-4 py-2 bg-blue-600 text-white font-black text-xs uppercase mb-8 rounded-md">POTENCIA MULTI-TENANT</div>
               <h2 className="text-4xl lg:text-6xl font-black text-white tracking-tighter uppercase italic leading-[0.95] mb-10">
                 Supervisión Global <br/> <span className="text-blue-500">del Mall.</span>
               </h2>
               <p className="text-xl text-slate-400 font-medium leading-relaxed mb-12">
                 Controla a todos tus locatarios y franquicias desde una consola maestra. Gestión de permisos, reportes agregados y aislamiento de datos garantizado.
               </p>
               <button className="bg-blue-600 text-white px-10 py-5 rounded-full font-black text-xl hover:bg-blue-700 transition-all shadow-2xl">
                 RESERVAR DEMO
               </button>
            </div>
            <div className="lg:w-1/2 w-full">
               <div className="relative">
                  <div className="bg-slate-800 rounded-3xl p-4 border border-slate-700 shadow-2xl">
                     <img src="/placeholder.svg" alt="Mall Console" className="w-full h-auto rounded-xl opacity-80" />
                  </div>
                  {/* Decorative Elements */}
                  <div className="absolute -top-10 -right-10 w-40 h-40 bg-blue-600/20 rounded-full blur-2xl"></div>
               </div>
            </div>
         </div>
      </section>

      {/* CTA Final */}
      <section className="bg-blue-600 py-32 text-center">
         <div className="max-w-4xl mx-auto px-4">
            <h2 className="text-5xl lg:text-7xl font-black text-white tracking-tighter uppercase italic mb-10">
              ¿Listo para dar <br/> el gran salto?
            </h2>
            <Link to="/register" className="bg-white text-blue-600 px-12 py-6 rounded-full font-black text-2xl shadow-2xl hover:scale-105 transition-transform">
              CREAR CUENTA GRATIS
            </Link>
         </div>
      </section>

      {/* Footer PointMeUp Style */}
      <footer className="bg-slate-50 py-20 border-t border-slate-200">
         <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid md:grid-cols-4 gap-12 mb-16">
               <div className="col-span-1 lg:col-span-2">
                  <div className="flex items-center gap-3 mb-6">
                    <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white font-black text-lg">B</div>
                    <span className="font-black text-xl tracking-tighter text-slate-900">BolClick</span>
                  </div>
                  <p className="text-slate-500 font-medium max-w-sm">
                    La solución SaaS definitiva para el retail moderno. Escalabilidad, seguridad y control total de tus operaciones.
                  </p>
               </div>
               <div>
                  <h4 className="font-black text-slate-900 uppercase text-xs tracking-widest mb-6">PRODUCTO</h4>
                  <ul className="space-y-4 text-slate-500 font-bold text-sm">
                     <li className="hover:text-blue-600 cursor-pointer">Funciones</li>
                     <li className="hover:text-blue-600 cursor-pointer">Sucursales</li>
                     <li className="hover:text-blue-600 cursor-pointer">Seguridad</li>
                  </ul>
               </div>
               <div>
                  <h4 className="font-black text-slate-900 uppercase text-xs tracking-widest mb-6">CONTACTO</h4>
                  <ul className="space-y-4 text-slate-500 font-bold text-sm">
                     <li>info@bolclick.com</li>
                     <li>Soporte 24/7</li>
                     <li>Ventas</li>
                  </ul>
               </div>
            </div>
            <div className="pt-12 border-t border-slate-200 flex flex-col md:flex-row justify-between items-center gap-6">
               <p className="text-slate-400 font-bold text-sm">© {new Date().getFullYear()} BolClick Inc. Todos los derechos reservados.</p>
               <div className="flex gap-6 text-slate-400 text-sm font-black uppercase tracking-widest">
                  <span className="hover:text-slate-900 cursor-pointer">PRIVACIDAD</span>
                  <span className="hover:text-slate-900 cursor-pointer">TÉRMINOS</span>
               </div>
            </div>
         </div>
      </footer>
    </div>
  );
}

// Subcomponent: Huge Detailed Feature Card
function DetailedFeatureCard({ icon, title, description, features }) {
  return (
    <div className="bg-slate-50 rounded-[2.5rem] p-10 lg:p-14 border border-slate-200 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300">
      <div className="flex flex-col sm:flex-row sm:items-center gap-6 lg:gap-8 mb-10">
        <div className="w-24 h-24 rounded-full bg-blue-600 flex items-center justify-center flex-shrink-0 shadow-[0_15px_30px_rgba(37,99,235,0.4)]">
          {icon}
        </div>
        <h3 className="text-3xl lg:text-4xl font-black text-slate-900 uppercase italic tracking-tighter leading-none">
          {title}
        </h3>
      </div>
      <p className="text-xl text-slate-600 font-medium leading-relaxed mb-10">
        {description}
      </p>
      <ul className="space-y-5">
        {features.map((feat, i) => (
          <li key={i} className="flex items-start gap-4">
            <div className="mt-1 bg-emerald-100 p-1.5 rounded-full flex-shrink-0">
               <ShieldCheck className="text-emerald-600" size={18} />
            </div>
            <span className="text-slate-700 font-medium text-lg leading-snug">{feat}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
