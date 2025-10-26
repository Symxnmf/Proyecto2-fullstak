-- ============================================
-- Script de Datos de Prueba (Seed Data)
-- Proyecto: Pastelería Dulce Sabor
-- ============================================

USE bd_backend;

-- ============================================
-- Insertar Roles
-- ============================================
INSERT INTO rol (nombre, estado) VALUES 
('ADMIN', TRUE),
('USER', TRUE),
('GUEST', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ============================================
-- Insertar Usuarios de Prueba
-- ============================================
INSERT INTO usuario (nombre, apellido, correo, celular, password) VALUES 
('Admin', 'Sistema', 'admin@dulcesabor.com', '+56912345678', 'admin123'),
('Juan', 'Pérez', 'juan@test.com', '+56987654321', 'user123'),
('María', 'González', 'maria@test.com', '+56965432109', 'user123'),
('Pedro', 'López', 'pedro@test.com', '+56943210987', 'user123'),
('Ana', 'Martínez', 'ana@test.com', '+56921098765', 'user123')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ============================================
-- Insertar Productos - Categoría: Postres
-- ============================================
INSERT INTO producto (nombre, descripcion, precio, stock, categoria, imagen, oferta) VALUES 
('Torta de Chocolate', 'Deliciosa torta de chocolate con crema de mantequilla', 15000, 10, 'Postres', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400', FALSE),
('Cheesecake de Frutilla', 'Suave cheesecake con salsa de frutillas frescas', 12000, 8, 'Postres', 'https://images.unsplash.com/photo-1533134486753-c833f0ed4866?w=400', TRUE),
('Torta Tres Leches', 'Clásica torta tres leches con crema chantilly', 10000, 12, 'Postres', 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400', FALSE),
('Brownie con Nueces', 'Brownie casero con trozos de nueces y chocolate', 3500, 20, 'Postres', 'https://images.unsplash.com/photo-1607920591413-4ec007e70023?w=400', FALSE),
('Pie de Limón', 'Refrescante pie de limón con merengue italiano', 11000, 6, 'Postres', 'https://images.unsplash.com/photo-1519915212116-7cfef71f1d3e?w=400', TRUE),
('Torta Selva Negra', 'Capas de bizcocho de chocolate con crema y cerezas', 16000, 5, 'Postres', 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400', FALSE),
('Tiramisú', 'Postre italiano con café, mascarpone y cacao', 8000, 15, 'Postres', 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400', FALSE),
('Pie de Manzana', 'Tradicional pie de manzana con canela', 9500, 4, 'Postres', 'https://images.unsplash.com/photo-1535920527002-b35e96722eb9?w=400', TRUE),
('Torta de Zanahoria', 'Torta húmeda de zanahoria con frosting de queso crema', 13000, 9, 'Postres', 'https://images.unsplash.com/photo-1621303837174-89787a7d4729?w=400', FALSE),
('Mousse de Chocolate', 'Suave mousse de chocolate belga', 6500, 18, 'Postres', 'https://images.unsplash.com/photo-1541599540903-216a46ca1dc0?w=400', FALSE);

-- ============================================
-- Insertar Productos - Categoría: Bebidas
-- ============================================
INSERT INTO producto (nombre, descripcion, precio, stock, categoria, imagen, oferta) VALUES 
('Café Latte', 'Espresso con leche vaporizada', 3500, 50, 'Bebidas', 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400', FALSE),
('Cappuccino', 'Espresso con espuma de leche', 3800, 45, 'Bebidas', 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=400', FALSE),
('Té Chai Latte', 'Té especiado con leche', 4000, 30, 'Bebidas', 'https://images.unsplash.com/photo-1597318181921-c1c82e0b7b09?w=400', TRUE),
('Chocolate Caliente', 'Chocolate artesanal con crema', 4200, 25, 'Bebidas', 'https://images.unsplash.com/photo-1542990253-a781e04c0082?w=400', FALSE),
('Jugo Natural de Naranja', 'Jugo fresco de naranjas', 2500, 40, 'Bebidas', 'https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400', FALSE),
('Smoothie de Frutilla', 'Batido de frutillas con yogurt', 5000, 3, 'Bebidas', 'https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=400', TRUE),
('Té Verde', 'Té verde premium', 2800, 35, 'Bebidas', 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', FALSE),
('Mocca Frappé', 'Café helado con chocolate', 5500, 20, 'Bebidas', 'https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?w=400', FALSE);

-- ============================================
-- Insertar Pedidos de Ejemplo
-- ============================================
INSERT INTO pedido (fecha, total, usuario_id) VALUES 
(CURDATE(), 28500, 2),
(CURDATE() - INTERVAL 1 DAY, 15000, 3),
(CURDATE() - INTERVAL 2 DAY, 42000, 4),
(CURDATE() - INTERVAL 3 DAY, 19500, 5),
(CURDATE() - INTERVAL 5 DAY, 31000, 2);

-- ============================================
-- Insertar Detalles de Pedidos
-- ============================================
-- Pedido 1
INSERT INTO detalle_pedido (cantidad, precio_unitario, subtotal, pedido_id, producto_id) VALUES 
(1, 15000, 15000, 1, 1),
(2, 3500, 7000, 1, 11),
(1, 6500, 6500, 1, 10);

-- Pedido 2
INSERT INTO detalle_pedido (cantidad, precio_unitario, subtotal, pedido_id, producto_id) VALUES 
(1, 15000, 15000, 2, 1);

-- Pedido 3
INSERT INTO detalle_pedido (cantidad, precio_unitario, subtotal, pedido_id, producto_id) VALUES 
(2, 12000, 24000, 3, 2),
(1, 10000, 10000, 3, 3),
(2, 4000, 8000, 3, 13);

-- Pedido 4
INSERT INTO detalle_pedido (cantidad, precio_unitario, subtotal, pedido_id, producto_id) VALUES 
(1, 11000, 11000, 4, 5),
(1, 8000, 8000, 4, 7),
(1, 500, 500, 4, 18);

-- Pedido 5
INSERT INTO detalle_pedido (cantidad, precio_unitario, subtotal, pedido_id, producto_id) VALUES 
(1, 16000, 16000, 5, 6),
(2, 3500, 7000, 5, 11),
(2, 4000, 8000, 5, 13);

-- ============================================
-- Verificar Datos Insertados
-- ============================================
SELECT 'Productos insertados:' AS Info, COUNT(*) AS Total FROM producto;
SELECT 'Usuarios insertados:' AS Info, COUNT(*) AS Total FROM usuario;
SELECT 'Pedidos insertados:' AS Info, COUNT(*) AS Total FROM pedido;
SELECT 'Detalles de pedido insertados:' AS Info, COUNT(*) AS Total FROM detalle_pedido;
SELECT 'Roles insertados:' AS Info, COUNT(*) AS Total FROM rol;

-- ============================================
-- Productos con Stock Bajo (para alertas)
-- ============================================
SELECT 'Productos con stock bajo (<=5):' AS Info;
SELECT nombre, stock, categoria FROM producto WHERE stock <= 5 ORDER BY stock ASC;

-- ============================================
-- Fin del Script
-- ============================================
