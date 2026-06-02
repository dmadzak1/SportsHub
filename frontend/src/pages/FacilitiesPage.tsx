import { useEffect, useMemo, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { FacilityDto, PageResponse } from '../types/facility';

const PAGE_SIZE = 5;

export default function FacilitiesPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [data, setData] = useState<PageResponse<FacilityDto> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
        setError(exception instanceof ApiError ? exception.message : 'Failed to load facilities');
      } finally {
        if (alive) setLoading(false);
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

        {data?.content.length ? (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Type</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((facility) => (
                  <tr key={facility.facilityId}>
                    <td>{facility.facilityId}</td>
                    <td>{facility.name}</td>
                    <td>{facility.type}</td>
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
