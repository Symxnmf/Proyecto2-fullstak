describe("Producto básico", () => {
  it("tiene nombre y precio", () => {
    const producto = { nombre: "Brownie", precio: 2500 };
    expect(producto.nombre).toBeDefined();
    expect(producto.precio).toBeGreaterThan(0);
  });
});
