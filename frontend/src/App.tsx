import type { ReactNode } from 'react';
import { Navigate, NavLink, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import { RequireAuth, RequireRole } from './auth/RequireAuth';
import LoginPage from './pages/LoginPage';
import FacilitiesPage from './pages/FacilitiesPage';
import AnalyticsPage from './pages/AnalyticsPage';
import PromotionsPage from './pages/PromotionsPage';
import PackagesPage from './pages/PackagesPage';
import ReservationsPage from './pages/ReservationsPage';
import SchedulesPage from './pages/SchedulesPage';
import UsersPage from './pages/UsersPage';

function AppShell({ children }: { children: ReactNode }) {
  const { session, logout } = useAuth();
  const isAdmin = session?.role === 'ADMIN';
  const isFacilityManager = session ? ['ADMIN', 'MANAGER'].includes(session.role) : false;
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
          {isFacilityManager ? <NavLink to="/facilities">Facilities</NavLink> : null}
          <NavLink to="/packages">Packages</NavLink>
          {isFacilityManager ? <NavLink to="/schedules">Schedules</NavLink> : null}
          {isFacilityManager ? <NavLink to="/reservations">Reservations</NavLink> : null}
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
            <section className="page-stack">
              <header className="page-header">
                <div className="page-header-row">
                  <div className="page-title">
                    <p className="eyebrow">Microservice SPA</p>
                    <h1>Dashboard</h1>
                    <p>Central control surface for authenticated access to users, facilities, promotions, scheduling and analytics.</p>
                  </div>
                  <div className="page-actions">
                    <span className="status-badge status-confirmed">JWT protected</span>
                    <span className="status-badge status-pending">Gateway routing</span>
                  </div>
                </div>

                <div className="metric-grid">
                  <article className="metric-card">
                    <span className="metric-label">Backend entrypoint</span>
                    <strong className="metric-value">8090</strong>
                    <span className="metric-copy">All requests go through the API gateway.</span>
                  </article>
                  <article className="metric-card">
                    <span className="metric-label">Auth mode</span>
                    <strong className="metric-value">JWT</strong>
                    <span className="metric-copy">Role-aware routes and bearer tokens.</span>
                  </article>
                  <article className="metric-card">
                    <span className="metric-label">Presentation layer</span>
                    <strong className="metric-value">SPA</strong>
                    <span className="metric-copy">Frontend owns state and page transitions.</span>
                  </article>
                  <article className="metric-card">
                    <span className="metric-label">API contract</span>
                    <strong className="metric-value">JSON</strong>
                    <span className="metric-copy">Paged reads, filters and CRUD actions.</span>
                  </article>
                </div>
              </header>

              <div className="grid">
                <section className="panel">
                  <h2>What this frontend covers</h2>
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
                    <li>Backend returns DTOs and auth responses only.</li>
                  </ul>
                </section>
              </div>
            </section>
          }
        />
        <Route path="users" element={<RequireRole roles={['ADMIN']}><UsersPage /></RequireRole>} />
        <Route path="facilities" element={<RequireRole roles={['ADMIN', 'MANAGER']}><FacilitiesPage /></RequireRole>} />
        <Route path="packages" element={<RequireAuth><PackagesPage /></RequireAuth>} />
        <Route path="schedules" element={<RequireRole roles={['ADMIN', 'MANAGER']}><SchedulesPage /></RequireRole>} />
        <Route path="reservations" element={<RequireRole roles={['ADMIN', 'MANAGER']}><ReservationsPage /></RequireRole>} />
        <Route path="promotions" element={<RequireAuth><PromotionsPage /></RequireAuth>} />
        <Route path="analytics" element={<RequireRole roles={['ADMIN', 'MANAGER', 'ANALYST']}><AnalyticsPage /></RequireRole>} />
      </Route>
      <Route path="/login" element={<LoginPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
