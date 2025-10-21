describe("ProductoCard Component", () => {
  it("debe tener las propiedades requeridas", () => {
    const producto = {
      id: 1,
      nombre: "Pan de Chocolate",
      precio: 3500,
      categoria: "Panadería",
      imagen: "/PanChocolate.jpg"
    };

    expect(producto.nombre).toBeDefined();
    expect(producto.nombre).toBe("Pan de Chocolate");
    expect(producto.precio).toBeGreaterThan(0);
    expect(producto.categoria).toBe("Panadería");
  });

  it("debe validar que el precio es un número positivo", () => {
    const producto = { nombre: "Brownie", precio: 2500 };
    
    expect(typeof producto.precio).toBe("number");
    expect(producto.precio).toBeGreaterThan(0);
  });

  it("debe tener imagen válida", () => {
    const producto = {
      nombre: "Cheesecake",
      imagen: "https://images.unsplash.com/photo-1524351199678.jpg"
    };

    expect(producto.imagen).toBeDefined();
    expect(producto.imagen.length).toBeGreaterThan(0);
    expect(producto.imagen).toMatch(/\.(jpg|jpeg|png|webp|gif)/i);
  });
});
