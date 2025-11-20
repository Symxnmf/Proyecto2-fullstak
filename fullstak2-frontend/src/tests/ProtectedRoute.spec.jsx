import { render, screen } from '@testing-library/react';
import ProtectedRoute from '../components/ProtectedRoute.jsx';
import { AuthProvider } from '../context/AuthContext.jsx';
import { BrowserRouter } from 'react-router-dom';

function renderWithAuth(ui) {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {ui}
      </AuthProvider>
    </BrowserRouter>
  );
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('bloquea acceso a no-admin', () => {
    localStorage.setItem('auth', JSON.stringify({
      user: { nombre: 'Cliente', email: 'c@c.cl', rol: 'CLIENTE' },
      token: 't'
    }));

    renderWithAuth(
      <ProtectedRoute>
        <div>Contenido Admin</div>
      </ProtectedRoute>
    );

    expect(screen.getByText(/Acceso Denegado/i)).toBeInTheDocument();
    expect(screen.queryByText('Contenido Admin')).not.toBeInTheDocument();
  });
});
