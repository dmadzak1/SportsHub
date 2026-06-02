import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import { useAuth } from '../auth/AuthContext';
import type { UserDto } from '../types/user';
import type { FacilityDto } from '../types/facility';
import type { ScheduleDto } from '../types/schedule';
import type { PageResponse } from '../types/package';
import type { ReservationDto } from '../types/reservation';

const PAGE_SIZE = 5;

type ReservationFormState = {
  userId: string;
  scheduleId: string;
};

const emptyForm: ReservationFormState = {
  userId: '',
  scheduleId: ''
};

const statusOptions = ['PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'];

export default function ReservationsPage() {
  const { session } = useAuth();
  const [page, setPage] = useState(0);
  const [userFilter, setUserFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  const [data, setData] = useState<PageResponse<ReservationDto> | null>(null);
  const [users, setUsers] = useState<UserDto[]>([]);
  const [facilities, setFacilities] = useState<FacilityDto[]>([]);
  const [schedules, setSchedules] = useState<ScheduleDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [refreshTick, setRefreshTick] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<ReservationFormState>(emptyForm);

  const requestPath = useMemo(() => {
    if (userFilter.trim() && statusFilter.trim()) {
      return `/facility/reservations/user/${userFilter}/status/${statusFilter}`;
    }

    if (userFilter.trim()) {
      return `/facility/reservations/user/${userFilter}`;
    }

    if (statusFilter.trim()) {
      return `/facility/reservations/status/${statusFilter}`;
    }

    if (fromDate.trim() && toDate.trim()) {
      return `/facility/reservations/date-range?from=${fromDate}&to=${toDate}`;
    }

    return `/facility/reservations/paginated?page=${page}&size=${PAGE_SIZE}&sortBy=reservationId`;
  }, [page, userFilter, statusFilter, fromDate, toDate]);

  useEffect(() => {
    let alive = true;

    const loadSelectors = async () => {
      try {
        const requests: Promise<unknown>[] = [
          apiClient.get<FacilityDto[]>('/facility/facilities'),
          apiClient.get<ScheduleDto[]>('/facility/schedules/available')
        ];

        if (session?.role === 'ADMIN') {
          requests.unshift(apiClient.get<UserDto[]>('/user/users'));
        }

        const response = await Promise.all(requests);
        const usersResponse = session?.role === 'ADMIN' ? (response[0] as UserDto[]) : [];
        const facilitiesResponse = session?.role === 'ADMIN' ? (response[1] as FacilityDto[]) : (response[0] as FacilityDto[]);
        const schedulesResponse = session?.role === 'ADMIN' ? (response[2] as ScheduleDto[]) : (response[1] as ScheduleDto[]);

        if (!alive) return;
        setUsers(usersResponse);
        setFacilities(facilitiesResponse);
        setSchedules(schedulesResponse);
      } catch {
        if (!alive) return;
        setUsers([]);
        setFacilities([]);
        setSchedules([]);
      }
    };

    void loadSelectors();

    return () => {
      alive = false;
    };
  }, [session?.role]);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<PageResponse<ReservationDto> | ReservationDto[]>(requestPath);

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
        const message = exception instanceof ApiError ? exception.message : 'Failed to load reservations';
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

  const submit = async () => {
    if (!form.userId || !form.scheduleId) {
      setError('User and schedule are required.');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      const payload = {
        userId: Number(form.userId),
        scheduleId: Number(form.scheduleId)
      };

      await apiClient.post<ReservationDto>('/facility/reservations', payload);
      resetForm();
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to save reservation';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const cancel = async (reservation: ReservationDto) => {
    setSaving(true);
    setError(null);

    try {
      await apiClient.patch<ReservationDto>(`/facility/reservations/${reservation.reservationId}/cancel`);
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to cancel reservation';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const updateStatus = async (reservation: ReservationDto, nextStatus: string) => {
    setSaving(true);
    setError(null);

    try {
      await apiClient.patch<ReservationDto>(`/facility/reservations/${reservation.reservationId}/status?status=${encodeURIComponent(nextStatus)}`);
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to update reservation status';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (reservation: ReservationDto) => {
    if (!window.confirm(`Delete reservation #${reservation.reservationId}?`)) {
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await apiClient.delete<void>(`/facility/reservations/${reservation.reservationId}`);
      setRefreshTick((current) => current + 1);
    } catch (exception) {
      const message = exception instanceof ApiError ? exception.message : 'Failed to delete reservation';
      setError(message);
    } finally {
      setSaving(false);
    }
  };

  const userLabelById = new Map(users.map((user) => [user.userId, user.email]));
  const facilityLabelById = new Map(facilities.map((facility) => [facility.facilityId, facility.name]));
  const scheduleLabelById = new Map(schedules.map((schedule) => [schedule.scheduleId, `${schedule.date} ${schedule.timeSlot.slice(0, 5)}`]));

  return (
    <div className="page-stack">
      <section className="panel">
        <div className="toolbar">
          <div>
            <p className="eyebrow">Facility service</p>
            <h2>Reservations</h2>
          </div>

          <div className="toolbar-filters">
            <select
              className="search-input small"
              value={userFilter}
              onChange={(event) => {
                setPage(0);
                setUserFilter(event.target.value);
              }}
            >
              <option value="">All users</option>
              {users.map((user) => (
                <option key={user.userId} value={user.userId}>
                  {user.email}
                </option>
              ))}
            </select>
            <select
              className="search-input small"
              value={statusFilter}
              onChange={(event) => {
                setPage(0);
                setStatusFilter(event.target.value);
              }}
            >
              <option value="">All statuses</option>
              {statusOptions.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
            <input
              className="search-input small"
              type="date"
              value={fromDate}
              onChange={(event) => {
                setPage(0);
                setFromDate(event.target.value);
              }}
            />
            <input
              className="search-input small"
              type="date"
              value={toDate}
              onChange={(event) => {
                setPage(0);
                setToDate(event.target.value);
              }}
            />
          </div>
        </div>

        {loading ? <p className="muted">Loading reservations...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}

        <div className="editor-grid">
          <div className="table-card">
            {data?.content.length ? (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>User</th>
                      <th>Schedule</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.content.map((reservation) => (
                      <tr key={reservation.reservationId}>
                        <td>{reservation.reservationId}</td>
                        <td>{userLabelById.get(reservation.userId) ?? reservation.userId}</td>
                        <td>{scheduleLabelById.get(reservation.scheduleId) ?? reservation.scheduleId}</td>
                        <td>{reservation.status ?? 'n/a'}</td>
                        <td>
                          <div className="row-actions">
                            <button type="button" onClick={() => void updateStatus(reservation, 'CONFIRMED')}>Confirm</button>
                            <button type="button" onClick={() => void cancel(reservation)}>Cancel</button>
                            <button type="button" onClick={() => void remove(reservation)}>Delete</button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : loading || error ? null : (
              <p className="muted">No reservations found.</p>
            )}

            {data ? (
              <div className="pager">
                <span>
                  Page {data.page + 1} of {Math.max(totalPages, 1)}
                </span>
                <div className="pager-actions">
                  <button type="button" onClick={() => setPage((current) => Math.max(0, current - 1))} disabled={page === 0 || userFilter.trim().length > 0 || statusFilter.trim().length > 0 || fromDate.trim().length > 0 || toDate.trim().length > 0}>
                    Previous
                  </button>
                  <button type="button" onClick={() => setPage((current) => current + 1)} disabled={userFilter.trim().length > 0 || statusFilter.trim().length > 0 || fromDate.trim().length > 0 || toDate.trim().length > 0 || (totalPages > 0 && page + 1 >= totalPages)}>
                    Next
                  </button>
                </div>
              </div>
            ) : null}
          </div>

          <aside className="editor-card">
            <p className="eyebrow">Create reservation</p>
            <h3>New reservation</h3>

            <div className="form-grid">
              {session?.role === 'ADMIN' && users.length > 0 ? (
                <label>
                  User
                  <select
                    value={form.userId}
                    onChange={(event) => setForm((current) => ({ ...current, userId: event.target.value }))}
                  >
                    <option value="">Select user</option>
                    {users.map((user) => (
                      <option key={user.userId} value={user.userId}>
                        {user.email}
                      </option>
                    ))}
                  </select>
                </label>
              ) : (
                <label>
                  User ID
                  <input
                    type="number"
                    min="1"
                    value={form.userId}
                    onChange={(event) => setForm((current) => ({ ...current, userId: event.target.value }))}
                  />
                </label>
              )}

              <label>
                Schedule
                <select
                  value={form.scheduleId}
                  onChange={(event) => setForm((current) => ({ ...current, scheduleId: event.target.value }))}
                >
                  <option value="">Select schedule</option>
                  {schedules.map((schedule) => (
                    <option key={schedule.scheduleId} value={schedule.scheduleId}>
                      {facilityLabelById.get(schedule.facilityId) ?? schedule.facilityId} - {schedule.date} {schedule.timeSlot.slice(0, 5)}
                    </option>
                  ))}
                </select>
              </label>

              <p className="muted">New reservations are created as `PENDING` in the backend.</p>
            </div>

            <div className="editor-actions">
              <button type="button" onClick={() => void submit()} disabled={saving}>
                {saving ? 'Saving...' : 'Create reservation'}
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
