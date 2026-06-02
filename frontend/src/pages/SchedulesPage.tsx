import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { FacilityDto } from '../types/facility';
import type { PageResponse, ScheduleDto } from '../types/schedule';

const PAGE_SIZE = 5;

type ScheduleFormState = {
  scheduleId: number | null;
  facilityId: string;
  date: string;
  timeSlot: string;
};

const emptyForm: ScheduleFormState = {
  scheduleId: null,
  facilityId: '',
  date: '',
  timeSlot: ''
};

export default function SchedulesPage() {
  const [page, setPage] = useState(0);
  const [facilityFilter, setFacilityFilter] = useState('');
  const [dateFilter, setDateFilter] = useState('');
  const [data, setData] = useState<PageResponse<ScheduleDto> | null>(null);
  const [facilities, setFacilities] = useState<FacilityDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [refreshTick, setRefreshTick] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<ScheduleFormState>(emptyForm);
  const facilityLabelById = new Map(facilities.map((facility) => [facility.facilityId, facility.name]));

  const requestPath = useMemo(() => {
    if (facilityFilter.trim() && dateFilter.trim()) {
      return `/facility/schedules/facility/${facilityFilter}/date/${dateFilter}`;
    }

    if (facilityFilter.trim()) {
      return `/facility/schedules/facility/${facilityFilter}`;
    }

    if (dateFilter.trim()) {
      return `/facility/schedules/date/${dateFilter}`;
    }

    return `/facility/schedules/paginated?page=${page}&size=${PAGE_SIZE}&sortBy=date`;
  }, [page, facilityFilter, dateFilter]);

  useEffect(() => {
    let alive = true;

    const loadFacilities = async () => {
      try {
        const response = await apiClient.get<FacilityDto[]>('/facility/facilities');
        if (alive) {
          setFacilities(response);
        }
      } catch {
        if (alive) {
          setFacilities([]);
        }
      }
    };

    void loadFacilities();

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
        const response = await apiClient.get<PageResponse<ScheduleDto> | ScheduleDto[]>(requestPath);

        if (!alive) return;

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
        if (!alive) return;
        const message = exception instanceof ApiError ? exception.message : 'Failed to load schedules';
        setError(message);
      } finally {
        if (alive) setLoading(false);
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
    setFacilityFilter('');
    setDateFilter('');
  };

  const submit = async () => {
    if (!form.facilityId || !form.date || !form.timeSlot) {
      setError('Facility, date and time are required.');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        facilityId: Number(form.facilityId),
        date: form.date,
        timeSlot: `${form.timeSlot}:00`
      };

      await apiClient.post<ScheduleDto>('/facility/schedules', payload);

      resetForm();
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to save schedule';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (schedule: ScheduleDto) => {
    if (!window.confirm(`Delete schedule #${schedule.scheduleId}?`)) {
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await apiClient.delete<void>(`/facility/schedules/${schedule.scheduleId}`);
      if (form.scheduleId === schedule.scheduleId) {
        resetForm();
      }
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to delete schedule';
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
              <p className="eyebrow">Facility service</p>
              <h2>Schedules</h2>
              <p>Track available time slots by facility and date, and manage new schedule entries from the sidebar form.</p>
            </div>

            <div className="page-actions">
              <select
                className="search-input small"
                value={facilityFilter}
                onChange={(event) => {
                  setPage(0);
                  setFacilityFilter(event.target.value);
                }}
              >
                <option value="">All facilities</option>
                {facilities.map((facility) => (
                  <option key={facility.facilityId} value={facility.facilityId}>
                    {facility.name}
                  </option>
                ))}
              </select>
              <input
                className="search-input small"
                type="date"
                value={dateFilter}
                onChange={(event) => {
                  setPage(0);
                  setDateFilter(event.target.value);
                }}
              />
              <button type="button" className="secondary-button" onClick={clearFilters} disabled={!facilityFilter && !dateFilter}>
                Reset filters
              </button>
            </div>
          </div>

          <div className="metric-grid">
            <article className="metric-card">
              <span className="metric-label">Loaded schedules</span>
              <strong className="metric-value">{data?.totalElements ?? 0}</strong>
              <span className="metric-copy">Current schedule pool from the facility service.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Facility filter</span>
              <strong className="metric-value">{facilityFilter ? 'Active' : 'Off'}</strong>
              <span className="metric-copy">Filter by venue or a specific date.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Edit mode</span>
              <strong className="metric-value">Create/Delete</strong>
              <span className="metric-copy">Backend currently exposes no schedule update endpoint.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Access</span>
              <strong className="metric-value">Admin/Manager</strong>
              <span className="metric-copy">Protected by role guard and gateway rules.</span>
            </article>
          </div>
        </header>

        {loading ? <p className="muted">Loading schedules...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}

        <div className="editor-grid">
          <div className="table-card">
            {data?.content.length ? (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Facility</th>
                      <th>Date</th>
                      <th>Time</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.content.map((schedule) => (
                      <tr key={schedule.scheduleId}>
                        <td>{schedule.scheduleId}</td>
                        <td>
                          <div className="table-primary">
                            <strong>{facilityLabelById.get(schedule.facilityId) ?? `Facility #${schedule.facilityId}`}</strong>
                            <span>#{schedule.facilityId}</span>
                          </div>
                        </td>
                        <td>{schedule.date}</td>
                        <td>
                          <span className="status-badge status-pending">{schedule.timeSlot.slice(0, 5)}</span>
                        </td>
                        <td>
                          <div className="row-actions">
                            <button type="button" onClick={() => void remove(schedule)} disabled={saving}>
                              {saving ? 'Working...' : 'Delete'}
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : loading || error ? null : (
              <p className="muted">No schedules found.</p>
            )}

            {data ? (
              <div className="pager">
                <span>
                  Page {data.page + 1} of {Math.max(totalPages, 1)}
                </span>
                <div className="pager-actions">
                  <button type="button" onClick={() => setPage((current) => Math.max(0, current - 1))} disabled={page === 0 || facilityFilter.trim().length > 0 || dateFilter.trim().length > 0}>
                    Previous
                  </button>
                  <button type="button" onClick={() => setPage((current) => current + 1)} disabled={facilityFilter.trim().length > 0 || dateFilter.trim().length > 0 || (totalPages > 0 && page + 1 >= totalPages)}>
                    Next
                  </button>
                </div>
              </div>
            ) : null}
          </div>

          <aside className="editor-card">
            <p className="eyebrow">Create schedule</p>
            <h3>New schedule</h3>
            <p className="field-hint">Schedules are created per facility, date and time slot. There is no edit flow in the current backend.</p>

            <div className="form-grid">
              <label>
                Facility
                <select
                  value={form.facilityId}
                  onChange={(event) => setForm((current) => ({ ...current, facilityId: event.target.value }))}
                >
                  <option value="">Select facility</option>
                  {facilities.map((facility) => (
                    <option key={facility.facilityId} value={facility.facilityId}>
                      {facility.name}
                    </option>
                  ))}
                </select>
                <span className="field-hint">Used for both creation and list filtering.</span>
              </label>

              <label>
                Date
                <input
                  type="date"
                  value={form.date}
                  onChange={(event) => setForm((current) => ({ ...current, date: event.target.value }))}
                />
                <span className="field-hint">Pick the day the slot belongs to.</span>
              </label>

              <label>
                Time
                <input
                  type="time"
                  value={form.timeSlot}
                  onChange={(event) => setForm((current) => ({ ...current, timeSlot: event.target.value }))}
                />
                <span className="field-hint">Stored as a full time value in the backend.</span>
              </label>
            </div>

            <div className="editor-actions">
              <button type="button" onClick={() => void submit()} disabled={saving}>
                {saving ? 'Saving...' : 'Create schedule'}
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
