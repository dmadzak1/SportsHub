import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { PackageDto, PageResponse } from '../types/package';

const PAGE_SIZE = 10;

type PackageFormState = {
  packageId: number | null;
  name: string;
  price: string;
};

const emptyForm: PackageFormState = {
  packageId: null,
  name: '',
  price: ''
};

export default function PackagesPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [data, setData] = useState<PageResponse<PackageDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [refreshTick, setRefreshTick] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<PackageFormState>(emptyForm);

  const requestPath = useMemo(() => {
    if (search.trim()) {
      return `/promotion/packages/name/${encodeURIComponent(search.trim())}`;
    }

    if (minPrice.trim() || maxPrice.trim()) {
      const min = minPrice.trim() || '0';
      const max = maxPrice.trim() || '9999999';
      return `/promotion/packages/price-range?minPrice=${encodeURIComponent(min)}&maxPrice=${encodeURIComponent(max)}`;
    }

    return `/promotion/packages/paged?page=${page}&size=${PAGE_SIZE}&sort=price,asc`;
  }, [page, search, minPrice, maxPrice]);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<PageResponse<PackageDto> | PackageDto[]>(requestPath);

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

        const message = exception instanceof ApiError ? exception.message : 'Failed to load packages';
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

  const resetForm = () => setForm(emptyForm);
  const clearFilters = () => {
    setPage(0);
    setSearch('');
    setMinPrice('');
    setMaxPrice('');
  };

  const beginEdit = (pkg: PackageDto) => {
    setForm({
      packageId: pkg.packageId,
      name: pkg.name,
      price: String(pkg.price)
    });
  };

  const submit = async () => {
    if (!form.name.trim() || !form.price.trim()) {
      setError('Name and price are required.');
      return;
    }

    const parsedPrice = Number(form.price);
    if (Number.isNaN(parsedPrice) || parsedPrice <= 0) {
      setError('Price must be a number greater than 0.');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        name: form.name.trim(),
        price: parsedPrice
      };

      if (form.packageId) {
        await apiClient.put<PackageDto>(`/promotion/packages/${form.packageId}`, payload);
      } else {
        await apiClient.post<PackageDto>('/promotion/packages', payload);
      }

      resetForm();
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to save package';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (pkg: PackageDto) => {
    if (!window.confirm(`Delete package ${pkg.name}?`)) {
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await apiClient.delete<void>(`/promotion/packages/${pkg.packageId}`);
      if (form.packageId === pkg.packageId) {
        resetForm();
      }
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to delete package';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="page-stack">
      <section className="panel">
        <header className="page-header">
          <div className="page-header-row">
            <div className="page-title">
              <p className="eyebrow">Promotion service</p>
              <h2>Packages</h2>
              <p>Browse pricing plans, filter by name or price range, and manage package records from one screen.</p>
            </div>

            <div className="page-actions">
              <input
                className="search-input"
                type="search"
                placeholder="Search by name"
                value={search}
                onChange={(event) => {
                  setPage(0);
                  setSearch(event.target.value);
                  setMinPrice('');
                  setMaxPrice('');
                }}
              />
              <input
                className="search-input small"
                type="number"
                placeholder="Min price"
                value={minPrice}
                onChange={(event) => {
                  setPage(0);
                  setMinPrice(event.target.value);
                  setSearch('');
                }}
              />
              <input
                className="search-input small"
                type="number"
                placeholder="Max price"
                value={maxPrice}
                onChange={(event) => {
                  setPage(0);
                  setMaxPrice(event.target.value);
                  setSearch('');
                }}
              />
              <button type="button" className="secondary-button" onClick={clearFilters} disabled={!search && !minPrice && !maxPrice}>
                Reset filters
              </button>
            </div>
          </div>

          <div className="metric-grid">
            <article className="metric-card">
              <span className="metric-label">Loaded packages</span>
              <strong className="metric-value">{data?.totalElements ?? 0}</strong>
              <span className="metric-copy">Current package catalog from the gateway.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Filter mode</span>
              <strong className="metric-value">{search ? 'Name' : minPrice || maxPrice ? 'Price' : 'Paged'}</strong>
              <span className="metric-copy">Only one filter family is active at a time.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Editable</span>
              <strong className="metric-value">Yes</strong>
              <span className="metric-copy">Create, update and delete supported.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Sort</span>
              <strong className="metric-value">Price</strong>
              <span className="metric-copy">Default ordering is ascending by price.</span>
            </article>
          </div>
        </header>

        {loading ? <p className="muted">Loading packages...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}

        <div className="editor-grid">
          <div className="table-card">
            {data?.content.length ? (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Price</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.content.map((pkg) => (
                      <tr key={pkg.packageId}>
                        <td>{pkg.packageId}</td>
                        <td>
                          <div className="table-primary">
                            <strong>{pkg.name}</strong>
                            <span>Package #{pkg.packageId}</span>
                          </div>
                        </td>
                        <td>
                          <span className="status-badge status-pending">${pkg.price.toFixed(2)}</span>
                        </td>
                        <td>
                          <div className="row-actions">
                            <button type="button" onClick={() => beginEdit(pkg)}>Edit</button>
                            <button type="button" onClick={() => void remove(pkg)}>Delete</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : loading || error ? null : (
              <p className="muted">No packages found.</p>
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
                    disabled={page === 0 || search.trim().length > 0 || minPrice.trim().length > 0 || maxPrice.trim().length > 0}
                  >
                    Previous
                  </button>
                  <button
                    type="button"
                    onClick={() => setPage((current) => current + 1)}
                    disabled={search.trim().length > 0 || minPrice.trim().length > 0 || maxPrice.trim().length > 0 || (totalPages > 0 && page + 1 >= totalPages)}
                  >
                    Next
                  </button>
                </div>
              </div>
            ) : null}
          </div>

          <aside className="editor-card">
            <p className="eyebrow">{form.packageId ? 'Edit package' : 'Create package'}</p>
            <h3>{form.packageId ? `Package #${form.packageId}` : 'New package'}</h3>
            <p className="field-hint">Packages are used by the promotion service and are searchable by name or price range.</p>

            <div className="form-grid">
              <label>
                Name
                <input
                  type="text"
                  value={form.name}
                  onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
                />
                <span className="field-hint">Use a descriptive package name.</span>
              </label>

              <label>
                Price
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={form.price}
                  onChange={(event) => setForm((current) => ({ ...current, price: event.target.value }))}
                />
                <span className="field-hint">Enter price in convertible format, e.g. 49.99.</span>
              </label>
            </div>

            <div className="editor-actions">
              <button type="button" onClick={() => void submit()} disabled={saving}>
                {saving ? 'Saving...' : form.packageId ? 'Update package' : 'Create package'}
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
