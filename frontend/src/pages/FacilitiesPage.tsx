import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { FacilityDto, PageResponse } from '../types/facility';

const PAGE_SIZE = 5;

type FacilityFormState = {
  facilityId: number | null;
  name: string;
  type: string;
};

const emptyForm: FacilityFormState = {
  facilityId: null,
  name: '',
  type: ''
};

export default function FacilitiesPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [data, setData] = useState<PageResponse<FacilityDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [refreshTick, setRefreshTick] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<FacilityFormState>(emptyForm);

  const requestPath = useMemo(() => {
    if (search.trim()) {
      return `/facility/facilities/search?keyword=${encodeURIComponent(search.trim())}`;
    }

    return `/facility/facilities/paginated?page=${page}&size=${PAGE_SIZE}&sortBy=name`;
  }, [page, search]);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<PageResponse<FacilityDto> | FacilityDto[]>(requestPath);

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

        const message = exception instanceof ApiError ? exception.message : 'Failed to load facilities';
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

  const beginEdit = (facility: FacilityDto) => {
    setForm({
      facilityId: facility.facilityId,
      name: facility.name,
      type: facility.type
    });
  };

  const submit = async () => {
    if (!form.name.trim() || !form.type.trim()) {
      setError('Name and type are required.');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        name: form.name.trim(),
        type: form.type.trim()
      };

      if (form.facilityId) {
        await apiClient.put<FacilityDto>(`/facility/facilities/${form.facilityId}`, payload);
      } else {
        await apiClient.post<FacilityDto>('/facility/facilities', payload);
      }

      resetForm();
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to save facility';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (facility: FacilityDto) => {
    if (!window.confirm(`Delete ${facility.name}?`)) {
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await apiClient.delete<void>(`/facility/facilities/${facility.facilityId}`);
      if (form.facilityId === facility.facilityId) {
        resetForm();
      }
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to delete facility';
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
            <p className="eyebrow">Facility service</p>
            <h2>Facilities</h2>
          </div>

          <input
            className="search-input"
            type="search"
            placeholder="Search by name"
            value={search}
            onChange={(event) => {
              setPage(0);
              setSearch(event.target.value);
            }}
          />
        </div>

        {loading ? <p className="muted">Loading facilities...</p> : null}
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
                      <th>Type</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.content.map((facility) => (
                      <tr key={facility.facilityId}>
                        <td>{facility.facilityId}</td>
                        <td>{facility.name}</td>
                        <td>{facility.type}</td>
                        <td>
                          <div className="row-actions">
                            <button type="button" onClick={() => beginEdit(facility)}>Edit</button>
                            <button type="button" onClick={() => void remove(facility)}>Delete</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : loading || error ? null : (
              <p className="muted">No facilities found.</p>
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
            <p className="eyebrow">{form.facilityId ? 'Edit facility' : 'Create facility'}</p>
            <h3>{form.facilityId ? `Facility #${form.facilityId}` : 'New facility'}</h3>

            <div className="form-grid">
              <label>
                Name
                <input
                  type="text"
                  value={form.name}
                  onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
                />
              </label>

              <label>
                Type
                <input
                  type="text"
                  value={form.type}
                  onChange={(event) => setForm((current) => ({ ...current, type: event.target.value }))}
                />
              </label>
            </div>

            <div className="editor-actions">
              <button type="button" onClick={() => void submit()} disabled={saving}>
                {saving ? 'Saving...' : form.facilityId ? 'Update facility' : 'Create facility'}
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
