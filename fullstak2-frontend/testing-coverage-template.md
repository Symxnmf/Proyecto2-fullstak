# Documento de Cobertura de Testing - Plantilla

Proyecto: Pastelería Dulce Sabor
Equipo: [Nombres]
Fecha: [2025-10-20]

## 1. Objetivo
Describir los tests realizados con Jasmine/Karma y la cobertura alcanzada.

## 2. Resumen de herramientas
- Framework de pruebas: Jasmine 5
- Runner: Karma
- Navegador: ChromeHeadless

## 3. Lista de tests implementados
- test/producto.spec.js: comprobaciones básicas de objeto producto.
- test/components/ProductoCard.spec.js: renderizado y props (por implementar).
- test/components/Carrito.spec.js: añadir/eliminar items y total (por implementar).

## 4. Cobertura (por defecto manual)
- Funciones críticas: getProductos, addProducto, updateProducto, deleteProducto -> pruebas unitarias recomendadas.
- UI: renderizado de lista de productos, filtros, checkout forms -> pruebas unitarias con Jasmine.

## 5. Cómo ejecutar los tests
1. Instalar dependencias: `npm install`
2. Ejecutar: `npm test` (ejecuta `karma start`)

## 6. Observaciones y recomendaciones
- Añadir `karma-coverage` para reportes de cobertura automatizados.
- Considerar migrar a Jest en próximas entregas si se requiere mayor ecosistema.


---

_Actualizar con resultados reales después de ejecutar las pruebas._
