import { useEffect, useMemo, useState } from 'react';
import { apiClient, ApiError } from '../lib/api';
import type { PageResponse } from '../types/user';
import type { UserDto } from '../types/user';

const PAGE_SIZE = 5;

export default function UsersPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [data, setData] = useState<PageResponse<UserDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const requestPath = useMemo(() => {
    if (search.trim()) {
      return `/user/users/search?keyword=${encodeURIComponent(search.trim())}`;
    }

    return `/user/users/paginated?page=${page}&size=${PAGE_SIZE}&sortBy=email`;
  }, [page, search]);

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
  }, [requestPath]);

  const totalPages = data?.totalPages ?? 0;

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

        {data?.content.length ? (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Email</th>
                  <th>Role ID</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((user) => (
                  <tr key={user.userId}>
                    <td>{user.userId}</td>
                    <td>{user.email}</td>
                    <td>{user.roleId ?? 'n/a'}</td>
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
              <button type="button" onClick={() => setPage((current) => Math.max(0, current - 1))} disabled={page === 0 || search.trim().length > 0}>
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
      </section>
    </div>
  );
}
