import type { ReactNode } from 'react';
import { Navigate, NavLink, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import { RequireAuth } from './auth/RequireAuth';
import LoginPage from './pages/LoginPage';

function Shell({ title, description, children }: { title: string; description: string; children: ReactNode }) {
  const { session, logout } = useAuth();

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
          <NavLink to="/users">Users</NavLink>
          <NavLink to="/facilities">Facilities</NavLink>
          <NavLink to="/promotions">Promotions</NavLink>
          <NavLink to="/analytics">Analytics</NavLink>
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

      <main className="content">
        <header className="hero">
          <div>
            <p className="eyebrow">Microservice SPA</p>
            <h1>{title}</h1>
            <p>{description}</p>
          </div>
          <div className="hero-card">
            <span>Frontend mode</span>
            <strong>TypeScript + React</strong>
            <small>Prepared for JWT, role guards and paged API calls.</small>
          </div>
        </header>

        {children}
      </main>
    </div>
  );
}

function Panel({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="panel">
      <h2>{title}</h2>
      {children}
    </section>
  );
}

function PlaceholderPage({ name, detail }: { name: string; detail: string }) {
  return (
    <Shell title={name} description={detail}>
      <Panel title="Initial scaffold">
        <p>
          This screen will become a live CRUD view backed by the gateway.
          The current step is only the project bootstrap.
        </p>
      </Panel>
    </Shell>
  );
}

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <RequireAuth>
            <Shell
              title="Dashboard"
              description="Central SPA shell for authenticated access to users, facilities, promotions and analytics."
            >
              <div className="grid">
                <Panel title="What this frontend will cover">
                  <ul className="stack">
                    <li>JWT login against `api/user/auth/login`.</li>
                    <li>Role-aware navigation and protected routes.</li>
                    <li>Paginated reads and partial fetches through the gateway.</li>
                  </ul>
                </Panel>
                <Panel title="Backend integration model">
                  <ul className="stack">
                    <li>All traffic goes through `http://localhost:8090/api`.</li>
                    <li>Frontend owns presentation and state.</li>
                    <li>Backend only returns JSON DTOs and auth responses.</li>
                  </ul>
                </Panel>
              </div>
            </Shell>
          </RequireAuth>
        }
      />
      <Route
        path="/login"
        element={<LoginPage />}
      />
      <Route path="/users" element={<RequireAuth><PlaceholderPage name="Users" detail="List, search, paging and user detail views." /></RequireAuth>} />
      <Route path="/facilities" element={<RequireAuth><PlaceholderPage name="Facilities" detail="Facilities, schedules and reservations." /></RequireAuth>} />
      <Route path="/promotions" element={<RequireAuth><PlaceholderPage name="Promotions" detail="Packages, discounts and usage records." /></RequireAuth>} />
      <Route path="/analytics" element={<RequireAuth><PlaceholderPage name="Analytics" detail="Reports, statistics and revenue logs." /></RequireAuth>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
