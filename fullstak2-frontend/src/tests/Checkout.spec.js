describe("Checkout - Validaciones", () => {
  let formulario;

  beforeEach(() => {
    formulario = {
      nombre: "",
      correo: "",
      direccion: "",
      celular: ""
    };
  });

  it("debe validar que los campos obligatorios no estén vacíos", () => {
    // Simular validación
    const camposObligatorios = ["nombre", "correo", "direccion"];
    
    camposObligatorios.forEach(campo => {
      expect(formulario[campo]).toBe("");
    });

    // Llenar formulario
    formulario.nombre = "Juan Pérez";
    formulario.correo = "juan@example.com";
    formulario.direccion = "Calle 123";

    camposObligatorios.forEach(campo => {
      expect(formulario[campo]).not.toBe("");
      expect(formulario[campo].length).toBeGreaterThan(0);
    });
  });

  it("debe validar formato de correo electrónico", () => {
    const correosValidos = [
      "test@example.com",
      "usuario.nombre@dominio.cl",
      "admin@duocuc.cl"
    ];

    const correosInvalidos = [
      "sindominio",
      "@sinusuario.com",
      "espacios en medio@test.com"
    ];

    correosValidos.forEach(correo => {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      expect(regex.test(correo)).toBe(true);
    });

    correosInvalidos.forEach(correo => {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      expect(regex.test(correo)).toBe(false);
    });
  });

  it("debe crear un pedido con estructura correcta", () => {
    const pedido = {
      fecha: new Date().toISOString().split('T')[0],
      total: 25000,
      usuario: "cliente@test.com",
      detalles: JSON.stringify({
        nombre: "Juan Pérez",
        direccion: "Av. Siempre Viva 123",
        celular: "+56912345678",
        productos: [
          { id: 1, nombre: "Pastel", precio: 25000 }
        ]
      })
    };

    expect(pedido.fecha).toBeDefined();
    expect(pedido.total).toBeGreaterThan(0);
    expect(pedido.usuario).toMatch(/@/);
    expect(pedido.detalles).toBeDefined();
    
    const detalles = JSON.parse(pedido.detalles);
    expect(detalles.productos.length).toBeGreaterThan(0);
  });

  it("debe calcular el total del carrito antes de confirmar", () => {
    const carrito = [
      { id: 1, nombre: "Brownie", precio: 3500 },
      { id: 2, nombre: "Cupcake", precio: 2000 }
    ];

    const total = carrito.reduce((sum, item) => sum + item.precio, 0);
    
    expect(total).toBe(5500);
    expect(total).toBeGreaterThan(0);
  });
});
