# MANUAL DE USUARIO - PLATAFORMA WEB
## GESTIÓN Y POS PARA TIENDAS (MALL MULTI-TENANT)

**UNIVERSIDAD PRIVADA DEL VALLE**  
**FACULTAD DE INFORMÁTICA Y ELECTRÓNICA**  
**CARRERA DE INGENIERÍA DE SISTEMAS INFORMÁTICOS**  

**Proyecto:** Plataforma de Gestión y Terminal de Ventas POS (Mall Multi-Tenant)  
**Asignatura:** Proyecto de Sistemas II  
**Gestión Académica:** 1/2026  
**Ubicación:** Santa Cruz – Bolivia  

---

## ÍNDICE DE CONTENIDO

*   [1. INTRODUCCIÓN](#1-introducción)
    *   [1.1. Ingresar al Sistema](#11-ingresar-al-sistema)
*   [2. INSTRUCCIONES PARA EL ROL DUEÑO DE TIENDA (OWNER)](#2-instrucciones-para-el-rol-dueño-de-tienda-owner)
    *   [2.1 Acceso e Ingreso al Sistema](#21-acceso-e-ingreso-al-sistema)
        *   [2.1.1 Ingresar al Sistema](#211-ingresar-al-sistema)
        *   [2.1.2 Inicio de Sesión y Acceso al Sistema](#212-inicio-de-sesión-y-acceso-al-sistema)
    *   [2.2 Configuración Estructural del Negocio](#22-configuración-estructural-del-negocio)
        *   [2.2.1 Ajustes Estéticos y Configuración de WhatsApp](#221-ajustes-estéticos-y-configuración-de-whatsapp)
        *   [2.2.2 Creación de la Primera Sucursal de la Empresa](#222-creación-de-la-primera-sucursal-de-la-empresa)
        *   [2.2.3 Administración de Empleados y Personal](#223-administración-de-empleados-y-personal)
        *   [2.2.4 Parametrización y Matriz de Permisos Dinámicos](#224-parametrización-y-matriz-de-permisos-dinámicos)
    *   [2.3 Módulo de Abastecimiento y Proveedores](#23-módulo-de-abastecimiento-y-proveedores)
        *   [2.3.1 Registro de Proveedores](#231-registro-de-proveedores)
        *   [2.3.2 Creación de Catálogo Maestro y Variantes](#232-creación-de-catálogo-maestro-y-variantes)
        *   [2.3.3 Ingreso de Lotes (Sourcing) y Perecederos](#233-ingreso-de-lotes-sourcing-y-perecederos)
        *   [2.3.4 Historial de Lotes y Anulación de Entrada](#234-historial-de-lotes-y-anulación-de-entrada)
    *   [2.4 Control de Inventario y Auditorías](#24-control-de-inventario-y-auditorías)
        *   [2.4.1 Control de Inventario y Alertas](#241-control-de-inventario-y-alertas)
        *   [2.4.2 Traslados de Mercancía entre Sucursales](#242-traslados-de-mercancía-entre-sucursales)
        *   [2.4.3 Auditoría de Pérdidas y Ajustes de Stock](#243-auditoría-de-pérdidas-y-ajustes-de-stock)
    *   [2.5 Terminal de Ventas (POS Web)](#25-terminal-de-ventas-pos-web)
        *   [2.5.1 Selección de Sucursal Operativa en POS](#251-selección-de-sucursal-operativa-en-pos)
        *   [2.5.2 Búsqueda de Productos y Selección de Variantes en Caja](#252-búsqueda-de-productos-y-selección-de-variantes-en-caja)
        *   [2.5.3 Gestión del Carrito y Control de Stock en POS](#253-gestión-del-carrito-y-control-de-stock-en-pos)
        *   [2.5.4 Tickets en Espera (Ventas Suspendidas)](#254-tickets-en-espera-ventas-suspendidas)
        *   [2.5.5 Proceso de Cobro y Métodos de Pago](#255-proceso-de-cobro-y-métodos-de-pago)
        *   [2.5.6 Cálculo de Vuelto en Efectivo](#256-cálculo-de-vuelto-en-efectivo)
    *   [2.6 Registro e Historial de Ventas](#26-registro-e-historial-de-ventas)
        *   [2.6.1 Consulta Contable e Historial de Ventas](#261-consulta-contable-e-historial-de-ventas)
*   [3. INSTRUCCIONES PARA EL ROL SUPERVISOR](#3-instrucciones-para-el-rol-supervisor)
*   [4. INSTRUCCIONES PARA EL ROL VENDEDOR](#4-instrucciones-para-el-rol-vendedor)
*   [5. INSTRUCCIONES DE LA APLICACIÓN MÓVIL](#5-instrucciones-de-la-aplicación-móvil)
    *   [5.1 Acceso y Onboarding Móvil](#51-acceso-y-onboarding-móvil)
    *   [5.2 Operaciones del Owner en Móvil](#52-operaciones-del-owner-en-móvil)
    *   [5.3 Operaciones del Supervisor en Móvil](#53-operaciones-del-supervisor-en-móvil)
    *   [5.4 Operaciones del Vendedor (POS Móvil)](#54-operaciones-del-vendedor-pos-móvil)
*   [6. EXPERIENCIA DEL CLIENTE (TIENDA PÚBLICA Y WHATSAPP)](#6-experiencia-del-cliente-tienda-pública-y-whatsapp)
*   [7. MANEJO DE EXCEPCIONES Y ALERTAS OPERATIVAS](#7-manejo-de-excepciones-y-alertas-operativas)

---

## ÍNDICE DE FIGURAS
*   [**Figura 1:** Portal de Inicio Público (Landing Page) y Acceso a Inicio de Sesión / Registro](#figura-1)
*   [**Figura 2:** Formulario de Inicio de Sesión (Login)](#figura-2)
*   [**Figura 3:** Dashboard Principal del Negocio (Métricas y Ventas del Día)](#figura-3)
*   [**Figura 4:** Módulo de Sucursales y Registro de Horarios](#figura-4)
*   [**Figura 5:** Formulario de Ajustes del Negocio (Personalización y WhatsApp)](#figura-5)
*   [**Figura 6:** Panel de Creación y Listado de Empleados](#figura-6)
*   [**Figura 7:** Configuración Dinámica de la Matriz de Permisos](#figura-7)
*   [**Figura 8:** Formulario de Registro de Proveedores](#figura-8)
*   [**Figura 9:** Creación de Productos y Definición de Variantes (Atributos)](#figura-9)
*   [**Figura 10:** Registro de Lote de Entrada (Sourcing) y Control de Perecederos](#figura-10)
*   [**Figura 11:** Historial de Lotes y Botón de Anulación de Entrada](#figura-11)
*   [**Figura 12:** Inventario Valorado y Chips de Alerta por Stock Mínimo](#figura-12)
*   [**Figura 13:** Formulario de Traslado Físico de Mercancía entre Sucursales](#figura-13)
*   [**Figura 14:** Formulario para Registro de Auditorías e Ingreso de Pérdidas](#figura-14)
*   [**Figura 15:** Interfaz Principal de la Terminal POS Web (Grid de Productos)](#figura-15)
*   [**Figura 16:** Selector de Variantes con Stock Disponible en POS Web](#figura-16)
*   [**Figura 17:** Carrito de Compras POS Web con Validación de Existencias](#figura-17)
*   [**Figura 18:** Historial de Tickets en Espera (Ventas Suspendidas)](#figura-18)
*   [**Figura 19:** Modal de Cobro, Selección de Cliente y Métodos de Pago](#figura-19)
*   [**Figura 20:** Historial y Detalles Contables de Ventas Realizadas](#figura-20)
*   [**Figura 21:** Pantalla de Bienvenida y Onboarding Móvil](#figura-21)
*   [**Figura 22:** Formulario de Inicio de Sesión Móvil](#figura-22)
*   [**Figura 23:** Panel de Ajustes y Personalización de la Tienda en Móvil](#figura-23)
*   [**Figura 24:** Registro de Entrada de Lotes (Sourcing) en Móvil](#figura-24)
*   [**Figura 25:** Inventario Valorado y Alertas de Stock Mínimo en Móvil](#figura-25)
*   [**Figura 26:** Registro de Auditorías e Ingreso de Pérdidas en Móvil](#figura-26)
*   [**Figura 27:** Interfaz Principal de la Terminal POS Móvil](#figura-27)
*   [**Figura 28:** Selector de Variantes con Stock Disponible en POS Móvil](#figura-28)
*   [**Figura 29:** Modal de Cobro y Métodos de Pago en POS Móvil](#figura-29)
*   [**Figura 30:** Detalle del Comprobante de Venta Digital en Móvil](#figura-30)

---

## 1. INTRODUCCIÓN

En este manual se explica de manera detallada cómo proceder para utilizar las diferentes funcionalidades de la plataforma web de gestión y terminal de ventas POS para tiendas del centro comercial (Mall Multi-Tenant).

El uso de la aplicación web que se describe a continuación cuenta con una explicación paso a paso de las herramientas operativas y administrativas del Dueño de Tienda (Owner), como ser: personalización de la marca y de WhatsApp, la creación y gestión de sucursales operativas, el alta del personal de caja, la parametrización de permisos dinámicos, la gestión del catálogo de productos con sus atributos y variantes, el ingreso físico de mercancía mediante lotes (sourcing), el control contable de existencias valoradas, la transferencia interna de stock, las auditorías físicas por mermas y pérdidas, y el registro de ventas con emisión de comprobantes en la terminal POS.

Para los fines de este documento, se asume que el sistema web ya se encuentra implementado y desplegado, que el usuario cuenta con una cuenta administrativa de tipo Dueño de Tienda (Owner) registrada y activa, y que accede a través de un navegador web moderno con acceso estable a internet.

---

## 2. INSTRUCCIONES PARA EL ROL DUEÑO DE TIENDA (OWNER)

### 2.1 Acceso e Ingreso al Sistema

#### 2.1.1 Ingresar al Sistema

Figura 1. Portal de Inicio Publico (Debe capturarse la presentación general de la plataforma con la barra de navegación superior, los botones "Iniciar Sesión" y "Comenzar" resaltados)

Fuente: Elaboracion propia.

Paso 1.
Abra su navegador e ingrese a la dirección web del centro comercial.

Paso 2.
En el portal de bienvenida, identifique los botones principales de interacción:
*   Si ya posee su cuenta de Owner registrada y aprobada, haga clic en el botón **"Iniciar Sesión"** en el encabezado superior o en la sección central.
*   Si es un comerciante nuevo y requiere registrar su tienda, haga clic en el botón **"Comenzar"** para ir al formulario de afiliación.

---

#### 2.1.2 Inicio de Sesión y Acceso al Sistema

Figura 2. Formulario de Inicio de Sesion (Debe capturarse el formulario de acceso con los campos de correo y contraseña vacíos o con datos de ejemplo)

Fuente: Elaboracion propia.

Paso 1.
En la landing page, haga clic en el botón **"Iniciar Sesión"** para abrir el formulario.

Paso 2.
Ingrese su correo electrónico y contraseña registrados de tipo Owner.

Paso 3.
Haga clic en **Iniciar Sesión** para ser redirigido al panel administrativo principal.

---

### 2.2 Configuración Estructural del Negocio

#### 2.2.1 Ajustes Estéticos y Configuración de WhatsApp

Figura 3. Formulario de Ajustes del Negocio (Debe capturarse la pantalla de Ajustes con el selector de logotipo, paleta de colores y el campo del teléfono de WhatsApp visible)

Fuente: Elaboracion propia.

Paso 1.
En el menú lateral, seleccione la opción **Ajustes**.

Paso 2.
Cargue el logotipo oficial de su marca presionando sobre el selector de archivos.

Paso 3.
Defina el color primario de su negocio utilizando el selector de color interactivo (este tono se aplicará dinámicamente al catálogo web público).

Paso 4.
Digite el número telefónico de **WhatsApp** corporativo (incluyendo el prefijo del país, ej. `59170000000`). Este número recibirá las órdenes de los clientes. Pulse el botón **Guardar Ajustes**.

---

#### 2.2.2 Creación de la Primera Sucursal de la Empresa

Figura 4. Módulo de Sucursales y Registro de Horarios (Debe capturarse el formulario de alta de nueva sucursal, mostrando los campos de Nombre Identificador, Dirección Física, Teléfono/Contacto, la sección interactiva para agregar Horarios de Atención por días y los botones de guardar)

Fuente: Elaboracion propia.

Paso 1.
En el menú de navegación lateral, haga clic sobre la opción **Sucursales**.

Paso 2.
Haga clic en el botón **"Aperturar Sucursal"** ubicado en el encabezado superior de la página para desplegar el formulario de registro.

Paso 3.
Llene la información de la sucursal en el formulario:
*   **Nombre Identificador**: Ingrese el nombre comercial de la sucursal (ej. "Agencia Centro").
*   **Dirección Física**: Introduzca la calle o avenida donde se encuentra ubicada la tienda.
*   **Teléfono / Contacto**: Digite el número telefónico de contacto de 8 dígitos (es un campo requerido).

Paso 4.
Configure los **Horarios de Atención** de la sucursal:
*   Haga clic sobre el botón **"Agregar Bloque de Horario"** para añadir un nuevo bloque.
*   En el listado de días (Lunes a Domingo), haga clic sobre los días de la semana en los que operará este horario (los botones cambiarán de color al estar seleccionados).
*   Defina la hora de apertura en el campo **"Desde"** (ej. `08:00`) y la hora de cierre en el campo **"Hasta"** (ej. `18:00`).
*   (Opcional) Si la tienda abre en horarios discontinuos o diferentes según el día, agregue bloques adicionales haciendo clic nuevamente en *"Agregar Bloque de Horario"* y configurando los respectivos días y horas.

Paso 5.
Asegúrese de que el **Estado de Operación** esté seleccionado como "Activa" y presione el botón **"Aperturar Sucursal"** en la parte inferior del formulario para guardar y habilitar la sucursal en el sistema.

---

#### 2.2.3 Administración de Empleados y Personal

Figura 5. Panel de Creación y Listado de Empleados (Debe capturarse el formulario con los campos de nombre, correo, contraseña, selector de Rol de trabajo y la Sucursal asignada)

Fuente: Elaboracion propia.

Paso 1.
Ingrese a la pestaña de **Empleados** en el menú y haga clic en **Nuevo Empleado**.

Paso 2.
Registre la información de acceso del colaborador (Nombre, Correo y Contraseña).

Paso 3.
Seleccione el Rol que desempeñará (*Vendedor* o *Supervisor*) y elija la sucursal física donde laborará.

Paso 4.
Presione el botón **Registrar** para guardar los datos.

---

#### 2.2.4 Parametrización y Matriz de Permisos Dinámicos

Figura 6. Configuración Dinámica de la Matriz de Permisos (Debe capturarse la grilla de checkboxes que permite activar o desactivar los accesos de Supervisor y Vendedor)

Fuente: Elaboracion propia.

Paso 1.
Seleccione **Permisos** en el menú de navegación lateral.

Paso 2.
Configure los accesos de cada rol marcando o desmarcando los checkboxes correspondientes de la matriz de permisos.

> [!IMPORTANT]
> **Matriz de Permisos y Dependencia del Owner:**
> Las capacidades operativas de los empleados subordinados dependen al 100% de la configuración establecida por el Owner en esta sección.
> * **Rol Supervisor (Configuración Estándar):** De manera predeterminada, está diseñado para administrar el abastecimiento y control de inventario (Proveedores, Catálogo, Lotes, Stock y Auditorías), pero el Owner puede restringir cualquiera de estos módulos o facultarlo a realizar cobros en la terminal POS.
> * **Rol Vendedor (Configuración Estándar):** De manera predeterminada, está restringido únicamente a la operación de ventas en mostrador (POS) y a la consulta de su propio historial. El Owner puede otorgarle permisos adicionales para realizar traslados de stock, registrar pérdidas o recibir lotes de sourcing si la operación en sucursal lo requiere.

Paso 3.
Presione el botón **Actualizar Permisos** para aplicar los cambios a las cuentas de los empleados.

---

### 2.3 Módulo de Abastecimiento y Proveedores

#### 2.3.1 Registro de Proveedores

Figura 7. Formulario de Registro de Proveedores (Debe capturarse la tabla de proveedores registrados y el formulario superior con los campos de Razón Social, Teléfono y Correo)

Fuente: Elaboracion propia.

Paso 1.
Ingrese a la sección **Proveedores** y presione el botón **Nuevo Proveedor**.

Paso 2.
Ingrese la Razón Social, número de teléfono y correo electrónico del contacto distribuidor. Presione **Registrar Proveedor**.

---

#### 2.3.2 Creación de Catálogo Maestro y Variantes

Figura 8. Creación de Productos y Definición de Variantes (Debe capturarse la pantalla de creación del catálogo maestro con los campos de SKU, categoría y la sección de variantes con chips)

Fuente: Elaboracion propia.

Paso 1.
Vaya a la pestaña **Catálogo** y haga clic en **Nuevo Producto**.

Paso 2.
Registre el nombre del producto, la categoría, el precio al consumidor y su SKU único de barras.

Paso 3.
Si comercializa artículos con atributos múltiples, active la sección **Variantes** e introduzca dichos valores (ej. tallas S, M, L o colores Azul, Negro).

Paso 4.
Presione **Guardar Producto** para estructurar el catálogo.

---

#### 2.3.3 Ingreso de Lotes (Sourcing) y Perecederos

Figura 9. Registro de Lote de Entrada (Debe capturarse la interfaz de entrada del lote, mostrando campos de sucursal destino, cantidad física, costo de compra y fechas de elab/vencimiento)

Fuente: Elaboracion propia.

Paso 1.
Ingrese al menú **Lotes** y haga clic en **Registrar Entrada**.

Paso 2.
Seleccione la **Sucursal de Destino** donde se almacenará la mercancía física.

Paso 3.
Busque y elija el producto por su nombre o SKU (el sistema auto-cargará al proveedor predeterminado).

Paso 4.
Ingrese la cantidad de unidades físicas recibidas y el costo de adquisición unitario.

Paso 5.
*Si el producto es perecedero*, indique de manera obligatoria la *Fecha de Elaboración* y *Fecha de Vencimiento* para activar alertas automáticas en el stock. Presione **Guardar**.

---

#### 2.3.4 Historial de Lotes y Anulación de Entrada

Figura 10. Historial de Lotes y Botón de Anulación de Entrada (Debe capturarse la tabla histórica de lotes cargados y el botón rojo de anulación en la parte derecha)

Fuente: Elaboracion propia.

Paso 1.
Si cometió un error de digitación, localice el lote correspondiente en el historial de Lotes.

Paso 2.
Haga clic sobre el botón **Anular Lote**.  
> [!WARNING]
> Si parte del stock de este lote ya fue vendido en la terminal POS y el saldo actual de la sucursal es menor al del lote original, el sistema bloqueará la anulación para proteger la base de datos de stock negativo.

---

### 2.4 Control de Inventario y Auditorías

#### 2.4.1 Control de Inventario y Alertas

Figura 11. Inventario Valorado y Chips de Alerta por Stock Mínimo (Debe capturarse la tabla de stock general con las existencias, alertas en rojo de mínimos y la valuación monetaria total)

Fuente: Elaboracion propia.

Paso 1.
Acceda a **Stock** en el menú de navegación para monitorizar la cantidad actual de productos, las alertas de stock crítico por debajo del mínimo, y el valor del inventario en base al costo promedio.

---

#### 2.4.2 Traslados de Mercancía entre Sucursales

Figura 12. Formulario de Traslado Físico de Mercancía (Debe capturarse la ventana flotante que aparece al presionar Trasladar, indicando la sucursal de destino y la cantidad a transferir)

Fuente: Elaboracion propia.

Paso 1.
En el módulo de Stock, ubique el producto y presione el botón **Trasladar**.

Paso 2.
En el cuadro emergente, seleccione la sucursal que recibirá el inventario y digite la cantidad a mover.

Paso 3.
Presione **Confirmar Traslado** para sincronizar la transferencia de existencias de manera inmediata.

---

#### 2.4.3 Auditoría de Pérdidas y Ajustes de Stock

Figura 13. Formulario para Registro de Auditorías e Ingreso de Pérdidas (Debe capturarse la pantalla de auditoría con la sucursal, producto, cantidad a retirar, selector del motivo del ajuste y observación)

Fuente: Elaboracion propia.

Paso 1.
Acceda a **Auditorías** y presione el botón **Registrar Ajuste**.

Paso 2.
Seleccione la sucursal y busque la variante de producto afectada.

Paso 3.
Ingrese la cantidad física a dar de baja de la sucursal.

Paso 4.
Marque el motivo del desajuste (*Error de registro, Artículo dañado/merma, Robo/no habido, Vencido*) y redacte una observación justificativa. Presione **Guardar**.

---

### 2.5 Terminal de Ventas (POS Web)

#### 2.5.1 Selección de Sucursal Operativa en POS

Figura 14. Selector de Sucursal en POS (Debe capturarse la ventana de diálogo inicial que obliga al Owner a elegir qué sucursal operar al abrir la terminal de ventas)

Fuente: Elaboracion propia.

Paso 1.
Al ingresar a la Terminal POS, elija del listado de sucursales aquella que operará. Al ser el Owner, podrá alternar de sucursal desde el POS cuando necesite revisar las cajas.

---

#### 2.5.2 Búsqueda de Productos y Selección de Variantes en Caja

Figura 15. Selector de Variantes con Stock Disponible en POS Web (Debe capturarse el modal de variantes que se abre al hacer clic en un producto del grid con atributos y stock por chip)

Fuente: Elaboracion propia.

Paso 1.
Use la barra de búsqueda de productos o el filtro de categorías en el POS para acotar los artículos visibles.

Paso 2.
Haga clic sobre el artículo. Si cuenta con variantes, se desplegará una modal.

Paso 3.
Seleccione la variante deseada. El sistema deshabilitará las opciones sin stock disponible en la sucursal seleccionada. Pulse **Agregar**.

---

#### 2.5.3 Gestión del Carrito y Control de Stock en POS

Figura 16. Carrito de Compras POS Web con Validación de Existencias (Debe capturarse la barra lateral del carrito de compras con productos en orden, cantidades cargadas y subtotales)

Fuente: Elaboracion propia.

Paso 1.
Gestione las cantidades deseadas desde el carrito de compras lateral utilizando los botones **(+)** y **(-)**. El sistema POS deshabilitará el botón **(+)** si el pedido alcanza el límite de existencias físicas reales de la sucursal.

---

#### 2.5.4 Tickets en Espera (Ventas Suspendidas)

Figura 17. Historial de Tickets en Espera (Debe capturarse la vista del listado de tickets suspendidos, indicando montos acumulados, fecha/hora y botones de cargar al POS)

Fuente: Elaboracion propia.

Paso 1.
Si un cliente requiere demorar su cobro, pulse el botón **"Ticket en Espera"** en la parte inferior del carrito. La orden se vaciará y quedará guardada de manera temporal.

Paso 2.
Para restaurar la compra, acceda a la pestaña lateral de **Tickets Suspendidos** y presione **Recuperar** en la orden para enviarla nuevamente al POS.

---

#### 2.5.5 Proceso de Cobro y Métodos de Pago

Figura 18. Modal de Cobro, Selección de Cliente y Métodos de Pago (Debe capturarse el formulario de cobro mostrando campos de NIT/CI, botón Buscar, y selectores de método de pago QR/Tarjeta/Transferencia)

Fuente: Elaboracion propia.

Paso 1.
Presione el botón **Cobrar** en el carrito de compras.

Paso 2.
Ingrese el NIT o CI y presione **Buscar**. Si el cliente cuenta con un registro en el mall, se auto-completará su nombre comercial; de lo contrario, digite los datos en el campo.

Paso 3.
Marque la pestaña del método de pago (*QR, Tarjeta, Transferencia* o *Efectivo*).

---

#### 2.5.6 Cálculo de Vuelto en Efectivo

Figura 19. Cálculo de Vuelto en Efectivo (Debe capturarse la sección de cobro en efectivo mostrando el monto de venta, el input de dinero recibido y la casilla de vuelto calculado)

Fuente: Elaboracion propia.

Paso 1.
Al seleccionar el método de pago en *Efectivo*, digite el dinero entregado por el comprador en el campo **"Monto Recibido"**.

Paso 2.
Revise el valor devuelto en la casilla **"Vuelto/Cambio"** para realizar la entrega de dinero.

Paso 3.
Haga clic en **Confirmar Venta**. Se guardará la venta, se generará el comprobante digital y se restará la cantidad del stock.

---

### 2.6 Registro e Historial de Ventas

#### 2.6.1 Consulta Contable e Historial de Ventas

Figura 20. Historial y Detalles Contables de Ventas Realizadas (Debe capturarse el módulo de Ventas con el listado cronológico de tickets cobrados y el modal lateral de detalle)

Fuente: Elaboracion propia.

Paso 1.
Ingrese a la sección **Ventas** desde el menú lateral.

Paso 2.
Use los filtros por fechas o sucursales para refinar la búsqueda del ticket deseado en el historial.

Paso 3.
Pulse sobre una fila para abrir el desglose de productos vendidos, costos, vendedor asignado e información del comprobante del cliente.

---

## 3. INSTRUCCIONES PARA EL ROL SUPERVISOR

El Supervisor tiene asignada por defecto una configuración de privilegios estándar enfocada principalmente en el abastecimiento, resguardo físico de existencias y control de proveedores de la tienda. No obstante, las acciones finales que puede realizar dependen de los accesos habilitados por el Dueño (Owner) en el panel de **Permisos** (Sección 2.2.4).

*   **Alcance y Módulos de Operación (Configuración Estándar)**:
    *   **Proveedores**: Habilitado para consultar y registrar proveedores que suministran mercadería (Sección 2.3.1).
    *   **Catálogo**: Habilitado para dar de alta nuevos productos y configurar variantes de venta en el catálogo (Sección 2.3.2).
    *   **Lotes (Sourcing)**: Habilitado para dar de alta entradas de lotes físicos y anular lotes cargados erróneamente (Sección 2.3.3 y 2.3.4).
    *   **Inventario y Traslados**: Habilitado para inspeccionar stock valorado, revisar costos y realizar transferencias de existencias entre sucursales (Sección 2.4.1 y 2.4.2).
    *   **Auditorías**: Habilitado para registrar pérdidas de mercancía y ajustes de inventario por robo, daño o vencimiento (Sección 2.4.3).
    *   **Terminal POS / Ventas**: Deshabilitado de forma estándar. Solo podrá acceder a realizar cobros o ver ventas si el Owner activa explícitamente dicho permiso en la matriz de configuración de la tienda.
*   **Restricciones de Sistema Inalterables**:
    *   No puede realizar configuraciones estéticas del negocio, cambios de logotipo o modificación del WhatsApp (Sección 2.2.1).
    *   No posee privilegios para crear o editar sucursales operativas (Sección 2.2.2).
    *   No puede dar de alta, editar o suspender otros empleados ni cambiar contraseñas de acceso (Sección 2.2.3).

---

## 4. INSTRUCCIONES PARA EL ROL VENDEDOR

El Vendedor es el operador final de caja. Su configuración estándar está estrictamente optimizada para la atención rápida en la terminal de ventas, el cobro y la emisión de comprobantes digitales. Al igual que el Supervisor, sus privilegios dependen de los accesos dinámicos otorgados por el Dueño (Owner) en la sección de **Permisos** (Sección 2.2.4).

*   **Alcance y Módulos de Operación (Configuración Estándar)**:
    *   **Terminal de Ventas (POS)**: Habilitado para vender productos, seleccionar variantes, gestionar el carrito de compras, suspender ventas temporales y calcular cambios en efectivo (Sección 2.5). 
    *   **Inicio de Sesión y Sucursal Asignada**: A diferencia del Owner, el Vendedor no tiene permitido cambiar de sucursal operativa de forma dinámica en la terminal de caja. El sistema detecta y asienta su sucursal de trabajo base configurada durante su registro (Sección 2.2.3).
    *   **Historial de Ventas**: Habilitado de forma estándar únicamente para consultar transacciones diarias y reimprimir comprobantes de la sucursal o de su usuario activo, protegiendo las métricas del resto de agencias del negocio.
*   **Privilegios Adicionales Opcionales**:
    *   El Vendedor no tiene acceso estándar a los módulos de inventario ni de abastecimiento (Proveedores, Catálogo, Sourcing, Stock, Auditorías). Sin embargo, el Owner puede concederle accesos dinámicos a estos módulos si requiere que el cajero colabore en el conteo de stock o en los traslados de mercadería.
*   **Restricciones de Sistema Inalterables**:
    *   No puede modificar la apariencia visual, la paleta de colores ni el número receptor de pedidos de WhatsApp (Sección 2.2.1).
    *   No tiene acceso a la creación de sucursales físicas ni a la administración de personal de la marca (Sección 2.2.2 y 2.2.3).

---

## 5. INSTRUCCIONES DE LA APLICACIÓN MÓVIL

La aplicación móvil para Android está diseñada como un complemento portable de la plataforma de gestión, permitiendo realizar operaciones clave en campo o directamente en mostrador físico. El sistema adapta su interfaz de manera automática según el rol asignado al usuario (Dueño de Tienda, Supervisor o Vendedor) de acuerdo con los permisos dinámicos configurados en la central web.

### 5.1 Acceso y Onboarding Móvil

#### 5.1.1 Bienvenida y Onboarding

Figura 21. Pantalla de Bienvenida y Onboarding Móvil (Debe capturarse la presentación móvil del negocio, mostrando la identidad visual de la tienda y las opciones de ingreso rápido en el smartphone)

Fuente: Elaboracion propia.

Paso 1.
Abra la aplicación en su dispositivo móvil y visualice el carrusel interactivo de bienvenida con las características generales del sistema.

Paso 2.
Deslice hacia la izquierda para avanzar en las diapositivas informativas o presione el botón **"Siguiente"**.

Paso 3.
Presione el botón **"Comenzar"** o **"Saltar"** para ir directamente a la pantalla de inicio de sesión.

---

#### 5.1.2 Inicio de Sesión Móvil

Figura 22. Formulario de Inicio de Sesión Móvil (Debe capturarse la pantalla de Login con los campos de correo electrónico y contraseña listos para ingresar las credenciales del usuario)

Fuente: Elaboracion propia.

Paso 1.
En la pantalla de autenticación móvil, ingrese su dirección de correo electrónico registrada.

Paso 2.
Digite su contraseña secreta asignada.

Paso 3.
Presione el botón **"Iniciar Sesión"**. El sistema validará su rol de trabajo y cargará el panel principal de la aplicación móvil con sus respectivos módulos autorizados.

---

### 5.2 Operaciones del Owner en Móvil

#### 5.2.1 Ajustes y Personalización en Móvil

Figura 23. Panel de Ajustes y Personalización de la Tienda en Móvil (Debe capturarse la sección de Ajustes en el dispositivo móvil mostrando el formulario de carga de logotipo, selección de color temático y campo de teléfono de WhatsApp)

Fuente: Elaboracion propia.

Paso 1.
Despliegue el menú de navegación lateral en la aplicación móvil y seleccione la opción **Ajustes**.

Paso 2.
Cargue un nuevo logotipo para la tienda, elija un color primario para la paleta del catálogo digital o edite el número de WhatsApp receptor de pedidos.

Paso 3.
Presione el botón **Guardar Ajustes** en la parte inferior para aplicar los cambios a nivel global en tiempo real.

---

### 5.3 Operaciones del Supervisor en Móvil

#### 5.3.1 Registro de Lotes (Sourcing) en Móvil

Figura 24. Registro de Entrada de Lotes (Sourcing) en Móvil (Debe capturarse el formulario de nuevo lote de entrada en la app móvil con la sucursal de destino, producto, cantidad física, costo de compra y fechas de expiración)

Fuente: Elaboracion propia.

Paso 1.
Acceda a la sección **Lotes** en el menú de navegación y presione el botón **"Registrar Entrada"**.

Paso 2.
Seleccione la sucursal de destino donde se depositará la mercancía física.

Paso 3.
Busque y elija el producto por su nombre o código SKU, introduzca la cantidad física recibida y el costo unitario de adquisición.

Paso 4.
Si los artículos son de naturaleza perecedera, active de forma requerida las casillas de fecha de elaboración y fecha de vencimiento. Presione **Guardar**.

---

#### 5.3.2 Monitoreo de Stock en Móvil

Figura 25. Inventario Valorado y Alertas de Stock Mínimo en Móvil (Debe capturarse la lista de existencias en la app móvil con las cantidades disponibles, valor de inventario por producto y las etiquetas rojas indicando alertas de stock crítico)

Fuente: Elaboracion propia.

Paso 1.
Diríjase a la sección **Stock** en la aplicación móvil.

Paso 2.
Inspeccione los niveles de existencias físicas de los artículos de la sucursal actual junto a su valuación contable ponderada.

Paso 3.
Preste atención a las alertas de stock crítico (marcadas con chips de color rojo), que le indicarán la urgencia de realizar un nuevo aprovisionamiento de stock.

---

#### 5.3.3 Auditoría de Pérdidas en Móvil

Figura 26. Registro de Auditorías e Ingreso de Pérdidas en Móvil (Debe capturarse la pantalla de auditoría física en el celular, con el selector de sucursal, la variante del artículo afectado, la cantidad y el motivo de merma/pérdida)

Fuente: Elaboracion propia.

Paso 1.
Vaya a la sección de **Auditorías** en el menú lateral y pulse sobre **Registrar Ajuste**.

Paso 2.
Seleccione la sucursal correspondiente y busque la variante de producto afectada.

Paso 3.
Digite el número de unidades físicas a dar de baja en el campo de cantidad.

Paso 4.
Marque el motivo correspondiente (merma, daño, robo o artículo vencido), ingrese una justificación textual en observaciones y presione el botón **Guardar**.

---

### 5.4 Operaciones del Vendedor (POS Móvil)

#### 5.4.1 Interfaz de Ventas en Caja Móvil

Figura 27. Interfaz Principal de la Terminal POS Móvil (Debe capturarse la interfaz del POS en el smartphone mostrando la barra superior de búsqueda, el filtro de categorías y la grilla táctil de productos)

Fuente: Elaboracion propia.

Paso 1.
Abra la **Terminal POS** desde el menú principal. Si su usuario posee el rol de Vendedor, la terminal móvil fijará automáticamente su sucursal base asignada de forma predeterminada sin opción de alternarla.

Paso 2.
Utilice la barra de búsqueda o el listado de categorías para filtrar rápidamente los productos del catálogo táctil.

---

#### 5.4.2 Selección de Variantes en POS Móvil

Figura 28. Selector de Variantes con Stock Disponible en POS Móvil (Debe capturarse el cuadro de diálogo flotante en el POS móvil que muestra los atributos del producto seleccionado y deshabilita las opciones sin stock)

Fuente: Elaboracion propia.

Paso 1.
Toque sobre el producto deseado en la grilla. Si el artículo cuenta con atributos específicos, se desplegará una ventana emergente.

Paso 2.
Seleccione la variante requerida (ej. talla o color). El sistema marcará y deshabilitará las opciones que no cuenten con unidades físicas disponibles en la sucursal actual.

Paso 3.
Presione **Agregar** para enviarlo al carrito de compras móvil.

---

#### 5.4.3 Interfaz de Cobro Móvil

Figura 29. Modal de Cobro y Métodos de Pago en POS Móvil (Debe capturarse la ventana de pago en la terminal móvil con el total a pagar, el campo para NIT/CI, el botón de consulta de clientes y los botones de pago QR, tarjeta y efectivo)

Fuente: Elaboracion propia.

Paso 1.
Toque sobre el icono del carrito en el borde inferior para ver el pedido actual y pulse sobre el botón **Cobrar**.

Paso 2.
Digite el NIT o CI del cliente y pulse el botón de búsqueda para verificar sus datos comerciales. Si no está registrado en el mall, digite su nombre comercial de manera manual.

Paso 3.
Marque la pestaña con el método de pago correspondiente (QR, Tarjeta, Transferencia Bancaria o Efectivo) y pulse **Confirmar Venta**.

---

#### 5.4.4 Emisión de Comprobante Móvil

Figura 30. Detalle del Comprobante de Venta Digital en Móvil (Debe capturarse la vista del recibo de venta final generado en la pantalla del celular mostrando la sucursal, los productos cobrados, el total de la transacción y el número de ticket)

Fuente: Elaboracion propia.

Paso 1.
Al confirmarse el registro de la venta, observe la modal interactiva con el comprobante digital de venta generado por la aplicación.

Paso 2.
Verifique el desglose del ticket, incluyendo los productos facturados, el total y la sucursal de origen.

Paso 3.
Presione **Nueva Venta** o **Cerrar** para limpiar el carrito de compras móvil y retornar a la terminal táctil principal.

---

## 6. EXPERIENCIA DEL CLIENTE (TIENDA PÚBLICA Y WHATSAPP)

Los clientes finales acceden a la tienda online auto-generada para realizar pedidos directos:

1.  **Visita del Catálogo**: El cliente ingresa a la dirección web `/tienda/[subdominio]` asignada a su negocio, cargando la identidad visual y colores primarios configurados en la consola del Owner.
2.  **Búsqueda y Carrito**: El usuario navega en el catálogo, busca productos, y selecciona variantes habilitadas. El sistema bloquea de manera visual aquellas variantes que no cuenten con existencias físicas en ninguna sucursal.
3.  **Confirmación y Pedido**: El cliente hace clic sobre su carrito de pedidos y presiona **"Confirmar pedido por WhatsApp"**.
4.  **Generación de Pedido en WhatsApp**: El sistema web genera una plantilla de texto limpia y formateada detallando el nombre de cada artículo, variante (talla, color), cantidades, precio unitario, subtotal y total de compra. Inmediatamente después, el sistema redirige al cliente a la aplicación de WhatsApp abriendo un chat con el número telefónico del Owner para coordinar el pago (transferencia, QR) y la entrega física de su pedido.

---

## 7. MANEJO DE EXCEPCIONES Y ALERTAS OPERATIVAS

Para garantizar que la plataforma no experimente interrupciones y proteger la base de datos distribuida, el sistema cuenta con las siguientes excepciones y alertas controladas:

1.  **Excepción por Datos Vacíos**: Todos los formularios de registro (sucursales, catálogo, empleados, lotes, etc.) validan que no se dejen campos requeridos vacíos (marcados con asterisco `*`). De lo contrario, se bloqueará el registro y se mostrará la advertencia: *"Los campos marcados con * son obligatorios"*.
2.  **Excepción por Tipo de Caracter**: El sistema valida e impide el almacenamiento de datos alfanuméricos en campos de cantidades, costos o teléfonos, mostrando el mensaje: *"Ingresar únicamente valores numéricos"*.
3.  **Alerta de Stock en POS**: El carrito de ventas bloquea de manera interactiva el ingreso de unidades en caja por encima del stock real de la sucursal para evitar ventas con inventarios desfasados.
4.  **Bloqueo Contable de Anulación de Lotes**: Si un lote a anular ya ha sido comprometido comercialmente (ventas en POS que redujeron el stock general por debajo del tamaño de la entrada del lote), la plataforma lanzará un mensaje de excepción impidiendo la anulación del lote y protegiendo el historial del inventario.
