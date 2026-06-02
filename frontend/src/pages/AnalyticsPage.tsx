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
        <p className="eyebrow">Analytics service</p>
        <h2>Reports</h2>
        {loading ? <p className="muted">Loading reports...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}
        {!loading && !error && data.length === 0 ? <p className="muted">No reports found.</p> : null}

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
                    <td>{report.reportType}</td>
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
