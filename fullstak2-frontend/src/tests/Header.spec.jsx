import { render, screen } from '@testing-library/react';
import Header from '../components/Header.jsx';
import { AuthProvider } from '../context/AuthContext.jsx';
import { CarritoProvider } from '../context/CarritoContext.jsx';
import { BrowserRouter } from 'react-router-dom';

function renderWithProviders(ui) {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <CarritoProvider>
          {ui}
        </CarritoProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

describe('Header', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('muestra Perfil cuando hay usuario logueado', () => {
    localStorage.setItem('auth', JSON.stringify({
      user: { nombre: 'Cliente', email: 'c@c.cl', rol: 'CLIENTE' },
      token: 't'
    }));

    renderWithProviders(<Header />);
    expect(screen.getByText(/Perfil/i)).toBeInTheDocument();
    expect(screen.queryByText(/Admin/i)).not.toBeInTheDocument();
  });
});
