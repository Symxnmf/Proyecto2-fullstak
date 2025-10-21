describe("Lista de Productos", () => {
  let productosEjemplo;

  beforeEach(() => {
    productosEjemplo = [
      { id: 1, nombre: "Pan de Chocolate", precio: 3500, categoria: "Panadería" },
      { id: 2, nombre: "Pastel de Cumpleaños", precio: 25000, categoria: "Pasteles", oferta: true },
      { id: 3, nombre: "Pie de Limón", precio: 12000, categoria: "Pies" }
    ];
  });

  it("debe renderizar todos los productos de la lista", () => {
    expect(productosEjemplo.length).toBe(3);
    
    productosEjemplo.forEach(producto => {
      expect(producto.id).toBeDefined();
      expect(producto.nombre).toBeDefined();
      expect(producto.precio).toBeGreaterThan(0);
    });
  });

  it("debe filtrar productos en oferta correctamente", () => {
    const ofertas = productosEjemplo.filter(p => p.oferta === true);
    
    expect(ofertas.length).toBe(1);
    expect(ofertas[0].nombre).toBe("Pastel de Cumpleaños");
  });

  it("debe filtrar por categoría correctamente", () => {
    const panaderia = productosEjemplo.filter(p => p.categoria === "Panadería");
    
    expect(panaderia.length).toBe(1);
    expect(panaderia[0].nombre).toBe("Pan de Chocolate");
  });

  it("debe calcular total de productos correctamente", () => {
    const total = productosEjemplo.reduce((sum, p) => sum + p.precio, 0);
    
    expect(total).toBe(40500); // 3500 + 25000 + 12000
  });
});
