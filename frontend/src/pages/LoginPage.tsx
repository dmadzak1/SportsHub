import { useState, type FormEvent } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import type { LoginRequest } from '../types/auth';

export default function LoginPage() {
  const { session, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/';

  const [form, setForm] = useState<LoginRequest>({
    email: '',
    password: ''
  });
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (session) {
    return <Navigate to={from} replace />;
  }

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      await login(form);
      navigate(from, { replace: true });
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Login failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <section className="login-hero">
          <div>
            <p className="eyebrow">Secure access</p>
            <h1>Sign in to SportsHub</h1>
            <p>Authenticate through the gateway. The JWT session unlocks protected SPA routes and role-based navigation.</p>
          </div>

          <ul className="login-points">
            <li>Single gateway entrypoint for all services.</li>
            <li>Frontend keeps the presentation layer fully client-side.</li>
            <li>Role-aware access controls remain enforced by the backend.</li>
          </ul>
        </section>

        <section className="login-form-panel">
          <form className="login-form" onSubmit={onSubmit}>
            <label>
              Email
              <input
                type="email"
                value={form.email}
                onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
                autoComplete="email"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                value={form.password}
                onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
                autoComplete="current-password"
                required
              />
            </label>

            {error ? <p className="error-banner">{error}</p> : null}

            <button type="submit" disabled={submitting}>
              {submitting ? 'Signing in...' : 'Login'}
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}
