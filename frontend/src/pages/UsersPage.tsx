import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { PageResponse } from '../types/user';
import type { UserDto } from '../types/user';
import type { RoleDto } from '../types/role';

const PAGE_SIZE = 5;

type UserFormState = {
  userId: number | null;
  email: string;
  password: string;
  roleId: string;
};

const emptyForm: UserFormState = {
  userId: null,
  email: '',
  password: '',
  roleId: ''
};

export default function UsersPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [data, setData] = useState<PageResponse<UserDto> | null>(null);
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [refreshTick, setRefreshTick] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<UserFormState>(emptyForm);

  const requestPath = useMemo(() => {
    if (search.trim()) {
      return `/user/users/search?keyword=${encodeURIComponent(search.trim())}`;
    }

    return `/user/users/paginated?page=${page}&size=${PAGE_SIZE}&sortBy=email`;
  }, [page, search]);

  useEffect(() => {
    let alive = true;

    const loadRoles = async () => {
      try {
        const response = await apiClient.get<RoleDto[]>('/user/roles');
        if (alive) {
          setRoles(response);
        }
      } catch {
        if (alive) {
          setRoles([]);
        }
      }
    };

    void loadRoles();

    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<PageResponse<UserDto> | UserDto[]>(requestPath);

        if (!alive) {
          return;
        }

        if (Array.isArray(response)) {
          setData({
            content: response,
            page: 0,
            size: response.length,
            totalElements: response.length,
            totalPages: 1
          });
        } else {
          setData(response);
        }
      } catch (exception) {
        if (!alive) {
          return;
        }

        const message = exception instanceof ApiError ? exception.message : 'Failed to load users';
        setError(message);
      } finally {
        if (alive) {
          setLoading(false);
        }
      }
    };

    void run();

    return () => {
      alive = false;
    };
  }, [requestPath, refreshTick]);

  const totalPages = data?.totalPages ?? 0;
  const roleLabelById = new Map(roles.map((role) => [role.roleId, role.roleName]));

  const resetForm = () => setForm(emptyForm);

  const beginEdit = (user: UserDto) => {
    setForm({
      userId: user.userId ?? null,
      email: user.email,
      password: user.password,
      roleId: user.roleId ? String(user.roleId) : ''
    });
  };

  const submit = async () => {
    if (!form.email.trim() || !form.password.trim() || !form.roleId) {
      setError('Email, password and role are required.');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        email: form.email.trim(),
        password: form.password,
        roleId: Number(form.roleId)
      };

      if (form.userId) {
        await apiClient.put<UserDto>(`/user/users/${form.userId}`, payload);
      } else {
        await apiClient.post<UserDto>('/user/users', payload);
      }

      resetForm();
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to save user';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (user: UserDto) => {
    if (!window.confirm(`Delete ${user.email}?`)) {
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await apiClient.delete<void>(`/user/users/${user.userId}`);
      if (form.userId === user.userId) {
        resetForm();
      }
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to delete user';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="page-stack">
      <section className="panel">
        <div className="toolbar">
          <div>
            <p className="eyebrow">User service</p>
            <h2>Users</h2>
          </div>

          <input
            className="search-input"
            type="search"
            placeholder="Search by email"
            value={search}
            onChange={(event) => {
              setPage(0);
              setSearch(event.target.value);
            }}
          />
        </div>

        {loading ? <p className="muted">Loading users...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}

        <div className="editor-grid">
          <div className="table-card">
            {data?.content.length ? (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.content.map((user) => (
                      <tr key={user.userId}>
                        <td>{user.userId}</td>
                        <td>{user.email}</td>
                        <td>{user.roleId ? roleLabelById.get(user.roleId) ?? user.roleId : 'n/a'}</td>
                        <td>
                          <div className="row-actions">
                            <button type="button" onClick={() => beginEdit(user)}>Edit</button>
                            <button type="button" onClick={() => void remove(user)}>Delete</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : loading || error ? null : (
              <p className="muted">No users found.</p>
            )}

            {data ? (
              <div className="pager">
                <span>
                  Page {data.page + 1} of {Math.max(totalPages, 1)}
                </span>
                <div className="pager-actions">
                  <button
                    type="button"
                    onClick={() => setPage((current) => Math.max(0, current - 1))}
                    disabled={page === 0 || search.trim().length > 0}
                  >
                    Previous
                  </button>
                  <button
                    type="button"
                    onClick={() => setPage((current) => current + 1)}
                    disabled={search.trim().length > 0 || (totalPages > 0 && page + 1 >= totalPages)}
                  >
                    Next
                  </button>
                </div>
              </div>
            ) : null}
          </div>

          <aside className="editor-card">
            <p className="eyebrow">{form.userId ? 'Edit user' : 'Create user'}</p>
            <h3>{form.userId ? `User #${form.userId}` : 'New user'}</h3>

            <div className="form-grid">
              <label>
                Email
                <input
                  type="email"
                  value={form.email}
                  onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
                />
              </label>

              <label>
                Password
                <input
                  type="text"
                  value={form.password}
                  onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
                />
              </label>

              <label>
                Role
                <select
                  value={form.roleId}
                  onChange={(event) => setForm((current) => ({ ...current, roleId: event.target.value }))}
                >
                  <option value="">Select role</option>
                  {roles.map((role) => (
                    <option key={role.roleId} value={role.roleId}>
                      {role.roleName}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div className="editor-actions">
              <button type="button" onClick={() => void submit()} disabled={saving}>
                {saving ? 'Saving...' : form.userId ? 'Update user' : 'Create user'}
              </button>
              <button type="button" className="secondary-button" onClick={resetForm} disabled={saving}>
                Clear
              </button>
            </div>
          </aside>
        </div>
      </section>
    </div>
  );
}
