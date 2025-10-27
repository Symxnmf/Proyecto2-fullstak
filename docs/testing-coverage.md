# 📘 Documento de Cobertura de Testing

Fecha: 27-10-2025
Repositorio local: Fullstak2-proyecto
Branch: main

---

## 1) Resumen Ejecutivo

Este documento presenta el estado de cobertura de pruebas del proyecto Dulce Sabor. Se incluye:
- Cobertura real del backend (Spring Boot) generada con JaCoCo
- Situación del frontend y plan de cobertura
- Cómo ejecutar y reproducir los reportes localmente

---

## 2) Backend (Java/Spring Boot) – Cobertura actual

Herramienta: JaCoCo 0.8.12 (reportes generados en target/site/jacoco)
JDK en ejecución de tests: 24.0.1 (El proyecto está en Java 21, ver nota de compatibilidad más abajo)

### 2.1 Métricas globales
- Coverage por líneas: 59.6% (221/371)
- Coverage por instrucciones: 54.1% (760/1405)
- Coverage por ramas: 63.6% (28/44)
- Coverage por métodos: 65.5% (97/148)

Ruta del reporte HTML: fullstak2-backend/fullstack-backend/target/site/jacoco/index.html

### 2.2 Cobertura por áreas
- Controllers: buena cobertura en Producto/Usuario/Pedido; AuthController sin pruebas (0%).
- Services: sin cobertura (impls no testeadas directamente).
- Entities y Config: alta cobertura (constructores/getters/setters y configuración CORS).

Clases con mayor foco de mejora:
- AuthController (0%)
- ProductoServiceImpl, UsuarioServiceImpl, PedidoServiceImpl, DetallePedidoServiceImpl (0%)
- RolRestControllers (parcial, ramas sin cubrir)

### 2.3 Cómo generar el reporte de cobertura (Windows PowerShell)

```powershell
# Ir al backend
cd .\fullstak2-backend\fullstack-backend

# Ejecutar tests (genera cobertura automáticamente)
.\mvnw.cmd clean test

# Abrir el reporte HTML
start .\target\site\jacoco\index.html
```

Notas técnicas:
- Se limitó la instrumentación de JaCoCo a `com/example/**` para evitar conflictos con JDK 24.
- Si usas JDK 21 para ejecutar tests, evitarás advertencias de compatibilidad con agentes (Mockito/ByteBuddy).

### 2.4 Siguiente paso recomendado para el backend
- Añadir tests unitarios de Services (mockeando repos) con Mockito.
- Cubrir AuthController (escenarios: admin OK, credenciales inválidas, usuario BD).
- Añadir tests de validación de DTOs y de reglas de negocio en capas de servicio.

---

## 3) Frontend (React/Vite) – Estado y plan

Estado actual:
- Script de test configurado a `karma start` en `package.json`.
- El archivo `karma.conf.cjs` fue eliminado en la limpieza de proyecto, por lo que los tests de Karma no se pueden ejecutar hoy.

Opciones para cobertura en frontend:

A) Rehabilitar Karma (rápido)
- Restaurar `karma.conf.cjs` con cobertura (nyc/istanbul) y ejecutar:
```powershell
cd .\fullstak2-frontend
npm install
npm test
```

B) Migrar a Vitest + React Testing Library (recomendado)
- Beneficios: ejecución más rápida, integración nativa con Vite, cobertura simple.
- Pasos básicos (resumen):
  1. `npm i -D vitest @vitest/coverage-v8 jsdom @testing-library/react @testing-library/user-event`
  2. Agregar script: `"test": "vitest --coverage"` en package.json
  3. Crear `vitest.config.js` con environment `jsdom`
  4. Ejecutar: `npm test`
- Resultado: Se genera reporte de cobertura (text/html) y se puede fijar umbrales.

---

## 4) Criterios de aceptación de cobertura

- Backend
  - Líneas: ≥ 70%
  - Ramas: ≥ 60%
  - Métodos: ≥ 70%
  - Clases críticas (AuthController, Services): ≥ 80%

- Frontend (al migrar a Vitest)
  - Líneas: ≥ 70%
  - Componentes críticos (Carrito, ProductoCard, ProtectedRoute): ≥ 80%

---

## 5) Mapa de pruebas actuales (backend)

- ProductoRestControllerTest: CRUD, validación de campos, filtros y stock bajo
- UsuarioRestControllersTest: CRUD y búsqueda por email
- PedidoRestControllersTest: creación/listado de pedidos y vínculos con detalles
- RolRestControllersTest: endpoints básicos, cobertura parcial de ramas
- DetallePedidoRestControllersTest: endpoints y validaciones

Gaps identificados:
- No hay tests de ServiceImpl
- Sin pruebas para AuthController (login/register y asignación de rol)

---

## 6) Anexo – Cómo leer el reporte de JaCoCo

- index.html: Resumen general y navegación por paquetes/clases
- jacoco.xml: Resumen en XML para CI (calidad/calculo automático)
- jacoco.csv: Datos por clase (líneas/instrucciones/métodos cubiertos)

Métricas clave:
- Instructions: granularidad a nivel de bytecode (aprox. complejidad)
- Lines: líneas ejecutadas vs totales (más intuitivo)
- Branches: `if/switch` cubiertos (importante para lógica de negocio)
- Methods: cantidad de métodos ejecutados

---

## 7) Recomendaciones de próxima iteración

- Backend: agregar suite de tests de Services con Mockito y cubrir AuthController.
- Frontend: migrar a Vitest con RTL y habilitar cobertura V8.
- CI: publicar reporte de JaCoCo y Vitest como artefactos en GitHub Actions.
- Umbrales: introducir `maven-jacoco` con `check` y fallar build si cobertura cae bajo el mínimo.
