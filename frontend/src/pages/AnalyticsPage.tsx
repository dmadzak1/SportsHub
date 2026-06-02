import { useEffect, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { ReportDto } from '../types/analytics';

export default function AnalyticsPage() {
  const [data, setData] = useState<ReportDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<ReportDto[]>('/analytics/reports');
        if (alive) {
          setData(response);
        }
      } catch (exception) {
        if (alive) {
          setError(exception instanceof ApiError ? exception.message : 'Failed to load reports');
        }
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
  }, []);

  return (
    <div className="page-stack">
      <section className="panel">
        <header className="page-header">
          <div className="page-header-row">
            <div className="page-title">
              <p className="eyebrow">Analytics service</p>
              <h2>Reports</h2>
              <p>Operational reporting surfaced through the gateway for admin, manager and analyst roles.</p>
            </div>
          </div>

          <div className="metric-grid">
            <article className="metric-card">
              <span className="metric-label">Reports</span>
              <strong className="metric-value">{data.length}</strong>
              <span className="metric-copy">Pulled from `/analytics/reports`.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Audience</span>
              <strong className="metric-value">3 roles</strong>
              <span className="metric-copy">ADMIN, MANAGER and ANALYST can enter.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Mode</span>
              <strong className="metric-value">Read only</strong>
              <span className="metric-copy">Analytics are displayed, not edited.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Format</span>
              <strong className="metric-value">Table</strong>
              <span className="metric-copy">Simple reporting list for now.</span>
            </article>
          </div>
        </header>

        {loading ? <p className="muted">Loading reports...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}
        {!loading && !error && data.length === 0 ? <div className="empty-state">No reports found.</div> : null}

        {data.length > 0 ? (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Type</th>
                  <th>Generated at</th>
                </tr>
              </thead>
              <tbody>
                {data.map((report) => (
                  <tr key={report.reportId}>
                    <td>{report.reportId}</td>
                    <td>
                      <span className="status-badge status-confirmed">{report.reportType}</span>
                    </td>
                    <td>{report.generatedAt ?? 'n/a'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </section>
    </div>
  );
}
