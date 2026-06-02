import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, MousePointerClick, Package2, ShoppingBag, BarChart3, Users2, Shield, Store } from 'lucide-react';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-[var(--bg)] font-sans text-[var(--txt-primary)] selection:bg-[var(--txt-primary)] selection:text-[var(--bg-card)]">
      
      {/* NAV */}
      <nav className="fixed top-0 left-0 right-0 z-50 bg-[var(--bg-card)]/80 backdrop-blur-md border-b border-[var(--border)]">
        <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-xl font-bold tracking-tight flex items-center">
              BolCl
              <MousePointerClick size={20} className="text-[var(--txt-secondary)] mx-px" strokeWidth={2.5} />
              ck
            </span>
          </div>
          <div className="hidden md:flex gap-8">
            <a href="#modulos" className="text-sm font-medium text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] transition-colors">Módulos</a>
            <a href="#precios" className="text-sm font-medium text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] transition-colors">Precios</a>
          </div>
          <div className="flex items-center gap-4">
            <Link to="/login" className="text-sm font-medium text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] transition-colors">Entrar</Link>
            <Link to="/register" className="text-sm font-bold bg-[var(--txt-primary)] text-[var(--bg-card)] px-5 py-2 rounded-xl hover:opacity-90 transition-opacity">
              Registrar tienda
            </Link>
          </div>
        </div>
      </nav>

      {/* HERO */}
      <section className="pt-40 pb-20 px-6">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-5xl md:text-6xl font-bold tracking-tight leading-tight mb-8">
            Control de inventario básico, <br className="hidden md:block" />
            <span className="text-[var(--txt-secondary)]">elegante y sin complicaciones.</span>
          </h1>
          <p className="text-lg text-[var(--txt-secondary)] max-w-2xl mx-auto mb-10 leading-relaxed">
            Un sistema de Punto de Venta (POS) y gestión de sucursales diseñado para funcionar rápido. Sin colores brillantes ni interfaces confusas. Solo lo que necesitas para vender.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link to="/register" className="w-full sm:w-auto flex items-center justify-center gap-2 bg-[var(--txt-primary)] text-[var(--bg-card)] px-8 py-3.5 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-sm">
              Empezar ahora <ArrowRight size={18} />
            </Link>
            <Link to="/login" className="w-full sm:w-auto flex items-center justify-center px-8 py-3.5 rounded-xl font-bold bg-[var(--bg-card)] border border-[var(--border)] text-[var(--txt-primary)] hover:bg-[var(--bg)] transition-colors shadow-sm">
              Ya tengo cuenta
            </Link>
          </div>
        </div>
      </section>

      {/* FEATURES / MODULES */}
      <section id="modulos" className="py-24 px-6 bg-[var(--bg-card)] border-y border-[var(--border)]">
        <div className="max-w-6xl mx-auto">
          <div className="mb-16 text-center">
            <h2 className="text-3xl font-bold tracking-tight mb-4">Todo lo que necesitas, nada de lo que no.</h2>
            <p className="text-[var(--txt-secondary)]">Diseñado para ser rápido y eficiente.</p>
          </div>
          <div className="grid md:grid-cols-3 gap-12">
            {[
              { icon: Package2, title: 'Inventario Exacto', desc: 'Conteo físico, auditorías y control de stock en tiempo real. Cero suposiciones.' },
              { icon: ShoppingBag, title: 'Punto de Venta', desc: 'Facturación y registro de ventas optimizado para la velocidad en mostrador.' },
              { icon: Store, title: 'Multi-Sucursal', desc: 'Gestiona varias tiendas desde una sola cuenta centralizada de forma transparente.' },
              { icon: Users2, title: 'Control de Equipo', desc: 'Roles y permisos detallados (dueño, vendedor, supervisor) para tu equipo.' },
              { icon: BarChart3, title: 'Reportes Claros', desc: 'Métricas de rendimiento, alertas de stock bajo y auditorías fáciles de leer.' },
              { icon: Shield, title: 'Seguridad Total', desc: 'Tus datos comerciales protegidos y respaldados automáticamente.' }
            ].map((f, i) => (
              <div key={i} className="flex flex-col items-center text-center p-6 bg-[var(--bg)] rounded-2xl border border-[var(--border)]">
                <div className="w-12 h-12 rounded-xl bg-[var(--bg-card)] border border-[var(--border)] flex items-center justify-center mb-6 shadow-sm">
                  <f.icon size={22} className="text-[var(--txt-primary)]" />
                </div>
                <h3 className="text-lg font-bold mb-2 tracking-tight">{f.title}</h3>
                <p className="text-[var(--txt-secondary)] text-sm leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-32 px-6 text-center">
        <h2 className="text-4xl font-bold tracking-tight mb-8">¿Listo para ordenar tu negocio?</h2>
        <Link to="/register" className="inline-flex items-center gap-2 bg-[var(--txt-primary)] text-[var(--bg-card)] px-10 py-4 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-sm">
          Crear mi cuenta gratis
        </Link>
      </section>

      {/* FOOTER */}
      <footer className="py-8 px-6 border-t border-[var(--border)] bg-[var(--bg-card)]">
        <div className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <span className="text-lg font-bold tracking-tight flex items-center">
              BolCl
              <MousePointerClick size={18} className="text-[var(--txt-secondary)] mx-px" strokeWidth={2.5} />
              ck
            </span>
          </div>
          <div className="flex gap-6">
            <a href="#" className="text-xs font-semibold text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] uppercase tracking-wider">Privacidad</a>
            <a href="#" className="text-xs font-semibold text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] uppercase tracking-wider">Términos</a>
            <a href="#" className="text-xs font-semibold text-[var(--txt-secondary)] hover:text-[var(--txt-primary)] uppercase tracking-wider">Contacto</a>
          </div>
          <p className="text-sm text-[var(--txt-muted)] font-medium">© {new Date().getFullYear()} BolClick. Todos los derechos reservados.</p>
        </div>
      </footer>
    </div>
  );
}
