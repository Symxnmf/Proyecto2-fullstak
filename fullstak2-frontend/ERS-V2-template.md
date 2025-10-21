# Especificación de Requisitos del Software (ERS) - Versión 2  
**Proyecto:** Pastelería Dulce Sabor - E-commerce  
**Fecha:** 03/01/2025  
**Versión:** 2.0  

---

## 1. Resumen Ejecutivo

**Pastelería Dulce Sabor** es una aplicación web fullstack desarrollada con React + Spring Boot que permite a clientes navegar productos de pastelería, gestionar un carrito de compras y realizar pedidos. Los administradores pueden gestionar el catálogo (CRUD completo). El sistema implementa persistencia en MySQL, diseño responsive con Bootstrap, y testing con Jasmine/Karma.

**Objetivos principales:**
- Migración completa de HTML estático a React (componentes reutilizables)
- CRUD de productos con persistencia en base de datos
- Interfaz responsive y moderna (Bootstrap 5)
- Gestión de carrito con Context API
- Panel administrativo para gestionar productos
- Testing unitario con Jasmine

---

## 2. Requerimientos Funcionales

**RF1 - Navegación de Productos:** El usuario puede explorar el catálogo completo de productos en `/productos`, visualizando nombre, precio, imagen y categoría.

**RF2 - Filtrado por Categorías:** En `/categorias` el usuario puede filtrar productos por tipo (Panadería, Pasteles, Galletas, Pies, Postres, Bebidas).

**RF3 - Productos en Oferta:** La página `/ofertas` muestra únicamente productos marcados con oferta=true y precio reducido.

**RF4 - Gestión del Carrito:** Los usuarios pueden añadir productos al carrito, eliminar items individuales y vaciar el carrito completo. El total se calcula automáticamente.

**RF5 - Proceso de Checkout:** En `/checkout` el usuario completa formulario (nombre, correo, dirección, celular) y confirma pedido. El sistema envía POST a `/api/pedidos` con fecha, total, usuario y detalles.

**RF6 - Panel de Administración:** En `/admin` (ruta protegida) los administradores pueden:
  - Ver lista completa de productos
  - Crear nuevos productos (formulario con nombre, precio, categoría, imagen, stock, oferta)
  - Editar productos existentes (modal con datos precargados)
  - Eliminar productos con confirmación

**RF7 - Autenticación:** Sistema de login en `/login` con validación de credenciales (usuario: admin@pasteleria.cl, contraseña: admin123).

---

## 3. Requerimientos No Funcionales

**RNF1 - Rendimiento:** Tiempo de carga de página principal < 2 segundos.

**RNF2 - Responsive Design:** La aplicación debe ser totalmente responsive en dispositivos móviles (320px+), tablets (768px+) y desktop (1024px+).

**RNF3 - Compatibilidad:** Compatible con Chrome 120+, Firefox 120+, Safari 17+, Edge 120+.

**RNF4 - Accesibilidad:** Implementa atributos ARIA básicos, navegación por teclado, contraste de colores adecuado (WCAG AA).

**RNF5 - Seguridad:** 
  - Validación de inputs en formularios
  - Configuración CORS en backend
  - Rutas protegidas con ProtectedRoute component
  - Credenciales no hardcodeadas (usar .env en producción)

**RNF6 - Mantenibilidad:** Código modularizado con componentes reutilizables, separación de concerns (API calls en carpeta `api/`, context en `context/`), comentarios en código complejo.

---

## 4. Casos de Uso

### CU1: Navegar y Comprar Productos
**Actor:** Cliente  
**Flujo Principal:**
1. Cliente ingresa a `/productos`
2. Explora productos disponibles
3. Hace clic en "Añadir al carrito" en productos deseados
4. Navega a `/checkout`
5. Completa formulario con datos personales
6. Confirma pedido
7. Sistema muestra `/compra-exitosa` con detalles del pedido

**Flujo Alternativo:** Si el pago falla (error en API), redirige a `/compra-fallida`.

### CU2: Gestionar Catálogo (Admin)
**Actor:** Administrador  
**Flujo Principal:**
1. Admin hace login en `/login`
2. Accede a `/admin`
3. Ve tabla con todos los productos
4. Para crear: llena formulario y hace clic en "Crear Producto"
5. Para editar: hace clic en "Editar", modifica datos en modal, guarda cambios
6. Para eliminar: hace clic en "Eliminar", confirma acción

**Precondición:** Usuario autenticado con rol admin.

---

## 5. Arquitectura del Sistema

**Arquitectura:** Cliente-Servidor en 3 capas

**Frontend:**
- Framework: React 19.1.1 + Vite 7.1.14
- Routing: React Router DOM 7.9.4
- Estilos: Bootstrap 5.3.8 + CSS custom
- Estado global: Context API (AuthContext, CarritoContext)
- HTTP Client: Axios 1.12.2

**Backend:**
- Framework: Spring Boot 3.4.10
- Lenguaje: Java 21
- ORM: Hibernate (JPA)
- Validación: Jakarta Validation
- Configuración: CORS habilitado para http://192.168.100.45:5173

**Base de Datos:**
- SGBD: MySQL 8.0 (puerto 3307)
- Tablas: productos, pedidos, categorias, usuarios
- Relaciones: productos N:1 categorias, pedidos N:M productos

**Infraestructura:**
- Dev Server: Vite (puerto 5173, host: 0.0.0.0 para acceso LAN)
- Backend Server: Tomcat embebido (puerto 8080)
- Firewall: Reglas Windows Firewall para puertos 5173 y 8080

---

## 6. Contratos de API (Endpoints)

### Productos
```
GET /api/productos
Descripción: Obtiene lista completa de productos
Response: 200 OK
[
  { "id": 1, "nombre": "Pan de Chocolate", "precio": 3500, "categoria": "Panadería", "imagen": "...", "stock": 20, "oferta": false }
]

POST /api/productos
Descripción: Crea nuevo producto
Body: { "nombre": "...", "precio": 0, "categoria": "...", "imagen": "...", "stock": 0, "oferta": false }
Response: 201 Created

PUT /api/productos/{id}
Descripción: Actualiza producto existente
Body: { "nombre": "...", "precio": 0, ... }
Response: 200 OK

DELETE /api/productos/{id}
Descripción: Elimina producto
Response: 204 No Content
```

### Pedidos
```
POST /api/pedidos
Descripción: Crea nuevo pedido
Body: { "fecha": "2025-01-03", "total": 25000, "usuario": "cliente@test.com", "detalles": "{...}" }
Response: 201 Created
```

### Autenticación
```
POST /api/auth/login
Descripción: Autenticación de usuario
Body: { "email": "admin@pasteleria.cl", "password": "admin123" }
Response: 200 OK { "token": "...", "role": "admin" }
```

---

## 7. Estrategia de Testing

**Framework:** Jasmine 5.12.0 + Karma 6.4.4

**Tipos de Tests:**
1. **Tests Unitarios:** Validación de lógica de componentes (props, state, eventos)
2. **Tests de Integración:** Verificación de flujos completos (añadir al carrito → checkout)
3. **Tests de Validación:** Formularios (campos requeridos, formatos email, números positivos)

**Archivos de Tests:**
- `ProductoCard.spec.js`: Validación de props de productos (nombre, precio, imagen, categoría)
- `ProductosList.spec.js`: Renderizado de lista, filtros por categoría y ofertas
- `Carrito.spec.js`: Operaciones de carrito (añadir, eliminar, vaciar, calcular total)
- `Checkout.spec.js`: Validaciones de formulario y estructura de pedidos

**Cobertura Objetivo:** 70% (componentes críticos: Carrito, Checkout, AdminPanel)

**Ejecución:** `npm test` (lanza Karma en modo watch con Chrome Headless)

---

## 8. Entregables

✅ **Código Fuente:** Repositorio Git https://github.com/Symxnmf/Proyecto2-fullstak.git  
✅ **Frontend React:** 8 páginas funcionales (Inicio, Productos, Categorías, Ofertas, Login, Admin, Checkout, Confirmación)  
✅ **Backend Spring Boot:** API REST con 4 entidades y CRUD completo  
✅ **Base de Datos:** Scripts SQL de creación e inserción de datos  
✅ **Tests Jasmine:** 5 archivos con 17 casos de prueba  
✅ **Documentación ERS:** Este documento (especificación de requisitos)  
✅ **Documentación Testing:** Documento de cobertura de pruebas  
✅ **README.md:** Instrucciones de instalación y ejecución

---

## 9. Notas para el Docente

### Instalación y Ejecución

**Backend (Spring Boot):**
```bash
cd fullstak2-backend/fullstack-backend
./mvnw spring-boot:run
```
El backend estará disponible en http://localhost:8080

**Frontend (React + Vite):**
```bash
cd fullstak2-frontend
npm install
npm run dev
```
El frontend estará disponible en http://localhost:5173

**Ejecutar Tests:**
```bash
cd fullstak2-frontend
npm test
```

### Credenciales de Prueba
- **Admin:** admin@pasteleria.cl / admin123
- **MySQL:** root / root (puerto 3307)

### Repositorio
https://github.com/Symxnmf/Proyecto2-fullstak.git
