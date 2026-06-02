import type { ReactNode } from 'react';
import { Navigate, NavLink, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import { RequireAuth, RequireRole } from './auth/RequireAuth';
import LoginPage from './pages/LoginPage';
import FacilitiesPage from './pages/FacilitiesPage';
import AnalyticsPage from './pages/AnalyticsPage';
import PromotionsPage from './pages/PromotionsPage';
import UsersPage from './pages/UsersPage';

function AppShell({ children }: { children: ReactNode }) {
  const { session, logout } = useAuth();
  const isAdmin = session?.role === 'ADMIN';
  const canViewAnalytics = session ? ['ADMIN', 'MANAGER', 'ANALYST'].includes(session.role) : false;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">S</div>
          <div>
            <strong>SportsHub</strong>
            <span>SPA frontend</span>
          </div>
        </div>

        <nav className="nav">
          <NavLink to="/" end>Dashboard</NavLink>
          {isAdmin ? <NavLink to="/users">Users</NavLink> : null}
          <NavLink to="/facilities">Facilities</NavLink>
          <NavLink to="/promotions">Promotions</NavLink>
          {canViewAnalytics ? <NavLink to="/analytics">Analytics</NavLink> : null}
          <NavLink to="/login">Login</NavLink>
        </nav>

        <div className="sidebar-note">
          <span>Gateway</span>
          <strong>http://localhost:8090/api</strong>
          {session ? (
            <button type="button" className="ghost-button" onClick={logout}>
              Sign out {session.email}
            </button>
          ) : null}
        </div>
      </aside>

      <main className="content">{children}</main>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <RequireAuth>
            <AppShell>
              <Outlet />
            </AppShell>
          </RequireAuth>
        }
      >
        <Route
          index
          element={
            <section className="panel">
              <header className="hero hero-inline">
                <div>
                  <p className="eyebrow">Microservice SPA</p>
                  <h1>Dashboard</h1>
                  <p>Central SPA shell for authenticated access to users, facilities, promotions and analytics.</p>
                </div>
                <div className="hero-card">
                  <span>Frontend mode</span>
                  <strong>TypeScript + React</strong>
                  <small>Prepared for JWT, role guards and paged API calls.</small>
                </div>
              </header>

              <div className="grid">
                <section className="panel">
                  <h2>What this frontend will cover</h2>
                  <ul className="stack">
                    <li>JWT login against `api/user/auth/login`.</li>
                    <li>Role-aware navigation and protected routes.</li>
                    <li>Paginated reads and partial fetches through the gateway.</li>
                  </ul>
                </section>
                <section className="panel">
                  <h2>Backend integration model</h2>
                  <ul className="stack">
                    <li>All traffic goes through `http://localhost:8090/api`.</li>
                    <li>Frontend owns presentation and state.</li>
                    <li>Backend only returns JSON DTOs and auth responses.</li>
                  </ul>
                </section>
              </div>
            </section>
          }
        />
        <Route path="users" element={<RequireRole roles={['ADMIN']}><UsersPage /></RequireRole>} />
        <Route path="facilities" element={<RequireAuth><FacilitiesPage /></RequireAuth>} />
        <Route path="promotions" element={<RequireAuth><PromotionsPage /></RequireAuth>} />
        <Route path="analytics" element={<RequireRole roles={['ADMIN', 'MANAGER', 'ANALYST']}><AnalyticsPage /></RequireRole>} />
      </Route>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
