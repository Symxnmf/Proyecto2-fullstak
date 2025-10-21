# Documento ERS (Versión 2) - Plantilla

Proyecto: Pastelería Dulce Sabor
Equipo: [Nombres]
Fecha: [2025-10-20]

## 1. Resumen ejecutivo
Breve descripción del sistema, objetivos y alcance de la entrega 2.

## 2. Requerimientos funcionales (FR)
- FR1: Mostrar catálogo de productos con filtros y búsqueda.
- FR2: Vista de Categorías que filtra por `categoria`.
- FR3: Vista de Ofertas que muestra productos con `oferta: true`.
- FR4: Carrito de compras con añadir/quitar/limpiar y resumen.
- FR5: Checkout que envía pedido al backend y muestra páginas de éxito/fracaso.
- FR6: Panel administrador con CRUD de productos.
- FR7: Autenticación básica para panel admin.

## 3. Requerimientos no funcionales (NFR)
- NFR1: Interfaz responsiva (Bootstrap).
- NFR2: Respuesta en < 1s para interacciones básicas en frontend.
- NFR3: Codificación en UTF-8 y compatibilidad con caracteres en español.

## 4. Casos de uso principales
- UC1: Cliente navega productos, filtra, agrega al carrito y finaliza compra.
- UC2: Admin autentica y gestiona productos (crear/editar/eliminar).

## 5. Arquitectura y componentes (alto nivel)
- Frontend: React (Vite), rutas en `src/Pages`, componentes en `src/components`.
- Backend: Spring Boot, endpoints REST en `/api`.
- Persistencia: MySQL en producción; `src/Data/productosData.js` como fuente local para desarrollo.

## 6. Contratos API relevantes
- POST /api/pedidos
- GET/POST/PUT/DELETE /api/productos

## 7. Requerimientos de testing
- Se utilizará Jasmine + Karma (entregable) para pruebas unitarias.
- Crear tests de renderizado, props, state y eventos.

## 8. Entregables
- Código fuente frontend (GitHub public).
- ZIP del proyecto frontend.
- ERS (este documento completado).
- Documento de cobertura de testing.

## 9. Notas para el docente
- Indicar pasos para levantar el proyecto y ejecutar tests.


---

_Editar y completar con la información de tu equipo y decisiones técnicas._
