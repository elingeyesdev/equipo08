import React from 'react';

export default function PermissionsPage() {
  return (
    <div className="full-width-container animate-fadein space-y-8">
      
      {/* Page Header */}
      <div className="page-header-bar">
        <div>
          <h1>Políticas de Acceso (ACL)</h1>
          <p>Consulte las atribuciones, alcances de seguridad y responsabilidades predefinidas para su equipo.</p>
        </div>
      </div>

      {/* Roles cards (Maximized width grid) */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        
        {/* SUPERVISOR */}
        <div className="border border-slate-200/80 rounded-2xl p-8 bg-white shadow-sm flex flex-col justify-between min-h-[520px]">
          <div>
            <h2 className="text-base font-bold text-slate-800 border-b border-slate-100 pb-4">
              SUPERVISOR / ENCARGADO DE SUCURSAL
            </h2>
            <p className="text-sm text-slate-600 mt-4 leading-relaxed">
              Este perfil está pensado para la persona encargada de una tienda o sucursal. Su función principal es apoyar en el control diario del negocio, gestionando ventas, productos, ingresos de mercadería y revisión del inventario.
            </p>
            
            <div className="mt-6 space-y-4">
              <h3 className="text-[11px] font-bold text-slate-700 uppercase tracking-wider">FUNCIONES HABILITADAS</h3>
              <ul className="text-sm text-slate-600 space-y-2.5 list-none pl-0">
                <li>• <strong>Caja y POS:</strong> puede registrar ventas, cobrar y realizar modificaciones dentro del proceso de venta.</li>
                <li>• <strong>Catálogo:</strong> puede registrar productos y actualizar precios de venta o costo.</li>
                <li>• <strong>Ingreso de mercadería:</strong> puede registrar entradas de stock, lotes nuevos y datos relacionados al proveedor.</li>
                <li>• <strong>Auditoría:</strong> puede reportar pérdidas, mermas, productos vencidos o diferencias encontradas en inventario.</li>
                <li>• <strong>Consulta:</strong> puede revisar proveedores, stock disponible e historial de movimientos de la sucursal.</li>
              </ul>
            </div>
          </div>
          
          <div className="mt-8 pt-4 border-t border-slate-100">
            <h3 className="text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-2">RESTRICCIONES</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              No puede eliminar sucursales, anular ventas históricas definitivas ni crear o despedir personal. Estas acciones quedan reservadas para perfiles administrativos superiores.
            </p>
          </div>
        </div>

        {/* VENDEDOR */}
        <div className="border border-slate-200/80 rounded-2xl p-8 bg-white shadow-sm flex flex-col justify-between min-h-[520px]">
          <div>
            <h2 className="text-base font-bold text-slate-800 border-b border-slate-100 pb-4">
              VENDEDOR / ATENCIÓN Y CAJA
            </h2>
            <p className="text-sm text-slate-650 mt-4 leading-relaxed">
              Este perfil está orientado al personal encargado del cobro diario, atención al cliente y facturación rápida en caja. Es un rol puramente transaccional sin acceso a configuraciones de catálogo o almacén.
            </p>
            
            <div className="mt-6 space-y-4">
              <h3 className="text-[11px] font-bold text-slate-700 uppercase tracking-wider">FUNCIONES HABILITADAS</h3>
              <ul className="text-sm text-slate-600 space-y-2.5 list-none pl-0">
                <li>• <strong>Caja y POS:</strong> puede registrar ventas, cobrar y utilizar la terminal de ventas en sucursal asignada.</li>
                <li>• <strong>Consulta:</strong> puede revisar el stock disponible de productos y consultar precios actualizados en catálogo en tiempo real.</li>
              </ul>
            </div>
          </div>

          <div className="mt-8 pt-4 border-t border-slate-100">
            <h3 className="text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-2">RESTRICCIONES</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              No tiene acceso a la creación de productos, modificación de precios de costo o venta, ingresos de nuevos lotes o visualización de auditorías de pérdida del negocio.
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}
