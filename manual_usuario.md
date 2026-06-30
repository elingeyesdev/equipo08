# MANUAL DE USUARIO - PLATAFORMA MALL MULTI-TENANT

Este documento es una guía detallada estructurada para todos los roles del sistema (Super Administrador, Dueño de Tienda, Supervisor, Vendedor y Cliente Final). Está optimizado para ser copiado y pegado directamente en Microsoft Word manteniendo los estilos, encabezados, tablas y listas.

---

## 1. Arquitectura y Roles del Sistema

El sistema utiliza un modelo de control de acceso basado en roles y aislamiento multi-tenant. Cada tienda posee su propio catálogo, empleados, sucursales y clientes.

### Tabla de Permisos por Rol

| Módulo / Acción | SUPER_ADMIN | OWNER (Dueño) | SUPERVISOR | VENDEDOR |
| :--- | :---: | :---: | :---: | :---: |
| **Consola Global (Aprobar/Bloquear Tiendas)** | Sí | No | No | No |
| **Ajustes de Tienda (Logo, Color, Info)** | No | Sí | No | No |
| **Gestión de Sucursales** | No | Sí | Solo Ver* | No |
| **Gestión de Empleados y Permisos** | No | Sí | No | No |
| **Módulo Proveedores** | No | Sí | Sí | No |
| **Catálogo de Productos** | No | Sí | Sí | Solo Ver* |
| **Ingreso de Lotes (Sourcing)** | No | Sí | Sí | No |
| **Inventario (Valuación y Traslados)** | No | Sí | Sí | No |
| **Auditoría (Ajustes Físicos)** | No | Sí | Sí | No |
| **Terminal de Ventas POS** | No | Sí | Sí | Sí |

*\*Los permisos específicos para roles como **SUPERVISOR** o **VENDEDOR** pueden ser personalizados y activados/desactivados dinámicamente por el **OWNER** en la pantalla de Permisos.*

---

## 2. Flujo del Administrador Global (SUPER_ADMIN)

El Super Administrador es el operador global de la plataforma de centros comerciales. Su función principal es controlar el ingreso y el estado operativo de los negocios participantes.

### Acciones Principales

1. **Ingreso a la Consola Global**: Al iniciar sesión con credenciales de administrador global, se le redirigirá automáticamente a la Consola Global del Sistema.
2. **Aprobación de Nuevas Tiendas (Tenants)**:
   * En la lista de tiendas, localice las solicitudes en estado **PENDIENTE**.
   * Revise los datos y haga clic en **Aprobar** para habilitar la tienda, cambiando su estado a **APPROVED**. La tienda ya podrá operar e iniciar sesión.
3. **Bloqueo / Suspensión de Tiendas**:
   * En caso de incidencias o baja del servicio, haga clic en **Bloquear**.
   * Esto revoca de inmediato el acceso a todos los usuarios vinculados a esa tienda, mostrando una advertencia de acceso restringido en sus pantallas.

---

## 3. Flujo del Dueño de Tienda (OWNER)

El Dueño de Tienda (Owner) es el administrador del tenant. Es responsable de estructurar la empresa y su personal para habilitar las ventas.

### Paso a Paso de Configuración:

1. **Registro Inicial**:
   * En la Landing Page pública, haga clic en **Registrar mi Tienda**.
   * Ingrese el nombre de la empresa, correo, contraseña y el **Subdominio deseado** (ej. `mi-tienda`, que servirá para la URL pública `/tienda/mi-tienda`).
2. **Personalización de la Marca**:
   * Ingrese a **Ajustes** en el menú de navegación lateral.
   * **Identidad Visual**: Suba el logo de su negocio y defina el color primario de su marca. El sistema lo aplicará automáticamente al catálogo online.
   * **Información de WhatsApp**: Registre su teléfono celular de contacto (vital para recibir pedidos de clientes).
3. **Configuración de Sucursales**:
   * Ingrese al menú **Sucursales**.
   * Cree las ubicaciones físicas de venta (mínimo 1). Ingrese nombre (ej. "Sucursal Central") y dirección.
   * Puede activar/desactivar sucursales. Si se desactiva una, el inventario asociado se congela temporalmente.
4. **Alta de Empleados y Ajuste de Permisos**:
   * En el menú **Empleados**, agregue al personal con su nombre, correo, contraseña, rol (Supervisor o Vendedor) y la sucursal de trabajo que les corresponde.
   * En **Permisos**, configure qué pantallas o acciones específicas tiene permitido realizar cada rol (ej. autorizar si el vendedor puede o no ver el catálogo general).

---

## 4. Flujo del Supervisor / Gerente de Operaciones

El Supervisor gestiona el catálogo, las compras a proveedores, el inventario físico y las auditorías de stock.

### Módulos Bajo su Cargo:

1. **Gestión de Proveedores**:
   * Ingrese a **Proveedores** y registre el Nombre, Teléfono y Correo del contacto mayorista.
2. **Catálogo de Productos**:
   * Ingrese a **Catálogo** y haga clic en **Nuevo Producto**.
   * Capture el Nombre, SKU único (código de barra o interno), categoría y precio de venta.
   * **Variantes/Atributos**: Si cuenta con artículos con atributos (ej. Tallas S, M, L o Colores Rojo, Azul), registre estas opciones para un control de stock segregado.
3. **Lotes (Entrada de Mercancía)**:
   * Ingrese a **Lotes** y haga clic en **Registrar Nueva Entrada**.
   * Seleccione la **Sucursal de Destino** y busque el producto por nombre o SKU. El sistema auto-seleccionará el proveedor oficial de su catálogo.
   * Ingrese la **Cantidad de Unidades** recibidas.
   * **Perecederos**: Si es una categoría de consumo (ej. Bebidas o Alimentos), ingrese la *Fecha de Elaboración* y *Fecha de Vencimiento* para alertas automáticas.
   * **Anulación de Entrada**: Desde el historial puede anular una entrada (eliminar lote erróneo). El sistema descontará automáticamente ese stock. Si no cuenta con suficientes unidades en almacén debido a ventas previas, el sistema bloqueará la anulación para prevenir stocks negativos.
4. **Inventario y Traslados**:
   * En **Inventario**, visualice la **Valuación Activa** en base al costo promedio acumulado y los artículos en alerta por debajo de su stock mínimo.
   * **Traslado entre Sucursales**: Haga clic en el botón **Trasladar** en la fila del producto deseado, seleccione la sucursal de destino y la cantidad a mover. Las existencias se actualizarán de forma sincronizada.
5. **Auditoría y Ajustes de Stock**:
   * Cuando se realice un recuento físico y existan mermas o pérdidas, vaya al menú **Auditoría** y haga clic en **Registrar Auditoría**.
   * Seleccione la sucursal, producto e ingrese la **Cantidad de Unidades Perdidas**.
   * Seleccione el motivo del ajuste (*Error de registro, Artículo dañado/merma, Robo/no habido, Vencido*).
   * Ingrese una observación detallada del incidente y confirme. El sistema restará el inventario en tiempo real e impactará contablemente el valor de la pérdida.

---

## 5. Flujo del Vendedor (POS / Terminal de Caja)

La interfaz POS está diseñada para una atención ágil y táctil en los mostradores de venta física.

### Pasos Operativos en Caja:

1. **Selección de Sucursal**: Al iniciar sesión, el sistema abrirá la sucursal de trabajo asignada. Si es el Owner, podrá seleccionar libremente qué sucursal operar.
2. **Búsqueda de Productos**: Use la barra lateral para filtrar por categorías o el buscador general para localizar productos.
3. **Selección de Variantes**: 
   * Si hace clic en un artículo con variantes, se desplegará un modal flotante para elegir el atributo deseado (ej. Color y Talla). El sistema validará al instante si esa combinación exacta tiene existencias en la sucursal activa.
4. **Gestión del Carrito**:
   * Aumente (+) o disminuya (-) unidades. El sistema impedirá superar el stock real del almacén.
   * **Ticket en Espera**: Si el cliente necesita tiempo adicional para pagar o agregar más compras, haga clic en **Ticket**. La orden actual se guardará temporalmente en la pestaña **Historial**. Esto libera la pantalla para atender al siguiente cliente. Al regresar el comprador, vaya a *Historial* e ingrese a **Cargar** para recuperar sus artículos en pantalla.
5. **Cobro de Venta**:
   * Haga clic en el botón **Cobrar**.
   * **Búsqueda de Cliente**: Ingrese el NIT o CI. Presione el botón **Buscar**. Si el cliente ha comprado antes en el mall, el sistema recuperará automáticamente su nombre para ahorrar tiempo de escritura.
   * **Método de Pago**: Seleccione entre *Efectivo, QR, Tarjeta* o *Transferencia*.
   * **Monto Recibido**: Ingrese la cantidad de dinero que entrega el cliente. La pantalla calculará y mostrará el **Cambio / Vuelto** de forma exacta.
   * Confirme la venta. Se emitirá el comprobante digital y el stock de la sucursal se reducirá inmediatamente.

---

## 6. Flujo del Cliente Final (Tienda Online Pública)

Cada negocio cuenta con una tienda virtual auto-generada bajo la ruta `/tienda/[nombre-de-dominio]`.

### Proceso de Compra del Cliente:

1. **Acceso al Catálogo**: El cliente ingresa a la dirección web provista por el negocio. El portal mostrará el logotipo, colores y banner oficiales configurados por el dueño.
2. **Búsqueda e Interacción**: El usuario puede buscar artículos mediante la barra de búsqueda o usar el cajón de **Filtros** para seleccionar categorías específicas.
3. **Selección con Stock Real**: 
   * Al hacer clic en un producto con variantes, se abren selectores interactivos.
   * Si una variante no cuenta con inventario disponible, el sistema la desactivará visualmente para que el cliente no agregue combinaciones agotadas.
4. **Pedido e Integración con WhatsApp**:
   * El cliente añade los productos a su pedido. El botón flotante inferior muestra el total acumulado de compras.
   * Al hacer clic en el carrito, se abre el resumen. El cliente hace clic en **Confirmar por WhatsApp**.
   * El sistema genera una plantilla de texto detallada (Nombre de producto, cantidad, precios y total) y redirige automáticamente al cliente a un chat de WhatsApp con el número del comerciante para pactar el pago y la entrega.

---

## 7. Guía de Solución de Alertas Frecuentes

* **Acceso Interrumpido (Tu sesión ha expirado / cuenta inactiva)**:
  * *Causa*: El administrador suspendió su cuenta, cambió su sucursal de trabajo en tiempo real, o el Super Admin bloqueó la tienda por falta de pago.
  * *Solución*: Comuníquese con el administrador del negocio para verificar el estado de su usuario o sucursal activa.
* **Mensaje "No hay más stock disponible" al facturar**:
  * *Causa*: No existen suficientes existencias en el almacén de esa sucursal.
  * *Solución*: Pida al Supervisor de tienda que realice un **Traslado de Inventario** o registre una nueva entrada física en **Lotes**.
* **El proveedor no coincide al ingresar mercancía**:
  * *Causa*: Seleccionó un proveedor que no corresponde al asignado originalmente en la ficha de catálogo.
  * *Solución*: Utilice el proveedor oficial configurado en el catálogo. Si se ha cambiado permanentemente de proveedor, modifique la ficha del producto maestro en la pantalla **Catálogo** primero.
* **El botón de WhatsApp de la tienda online no envía el mensaje**:
  * *Causa*: No se ha registrado un teléfono celular o el formato es incorrecto en la sección de Ajustes del negocio.
  * *Solución*: El dueño debe ir al menú **Ajustes** y guardar un número celular de 8 dígitos válido.
