describe("Carrito de Compras", () => {
  let carrito;

  beforeEach(() => {
    carrito = [];
  });

  it("debe inicializar vacío", () => {
    expect(carrito.length).toBe(0);
  });

  it("debe agregar un producto al carrito", () => {
    const producto = { id: 1, nombre: "Brownie", precio: 3500 };
    carrito.push(producto);

    expect(carrito.length).toBe(1);
    expect(carrito[0].nombre).toBe("Brownie");
  });

  it("debe eliminar un producto del carrito", () => {
    const p1 = { id: 1, nombre: "Brownie", precio: 3500 };
    const p2 = { id: 2, nombre: "Cupcake", precio: 2000 };
    
    carrito.push(p1, p2);
    expect(carrito.length).toBe(2);

    // Eliminar el primer producto
    carrito = carrito.filter(item => item.id !== 1);
    
    expect(carrito.length).toBe(1);
    expect(carrito[0].nombre).toBe("Cupcake");
  });

  it("debe calcular el total correctamente", () => {
    carrito = [
      { id: 1, nombre: "Brownie", precio: 3500 },
      { id: 2, nombre: "Cupcake", precio: 2000 },
      { id: 3, nombre: "Pastel", precio: 15000 }
    ];

    const total = carrito.reduce((sum, item) => sum + item.precio, 0);
    
    expect(total).toBe(20500);
  });

  it("debe vaciar el carrito completamente", () => {
    carrito = [
      { id: 1, nombre: "Brownie", precio: 3500 },
      { id: 2, nombre: "Cupcake", precio: 2000 }
    ];

    carrito = [];
    
    expect(carrito.length).toBe(0);
  });
});
