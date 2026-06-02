import { useEffect, useState } from 'react';
import { ApiError, apiClient } from '../lib/api';
import type { PromotionDto } from '../types/promotion';

export default function PromotionsPage() {
  const [data, setData] = useState<PromotionDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    const run = async () => {
      try {
        const response = await apiClient.get<PromotionDto[]>('/promotion/promotions/active');
        if (alive) {
          setData(response);
        }
      } catch (exception) {
        if (alive) {
          setError(exception instanceof ApiError ? exception.message : 'Failed to load promotions');
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
              <p className="eyebrow">Promotion service</p>
              <h2>Active promotions</h2>
              <p>Read-only view of currently active promotion records exposed by the promotion service.</p>
            </div>
          </div>

          <div className="metric-grid">
            <article className="metric-card">
              <span className="metric-label">Active promotions</span>
              <strong className="metric-value">{data.length}</strong>
              <span className="metric-copy">Direct from `/promotion/promotions/active`.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Mode</span>
              <strong className="metric-value">Read only</strong>
              <span className="metric-copy">This screen intentionally avoids write actions.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Audience</span>
              <strong className="metric-value">All roles</strong>
              <span className="metric-copy">Accessible to authenticated users.</span>
            </article>
            <article className="metric-card">
              <span className="metric-label">Format</span>
              <strong className="metric-value">Table</strong>
              <span className="metric-copy">Flat listing with promotion metadata.</span>
            </article>
          </div>
        </header>

        {loading ? <p className="muted">Loading promotions...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}
        {!loading && !error && data.length === 0 ? <div className="empty-state">No active promotions.</div> : null}

        {data.length > 0 ? (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Package</th>
                  <th>Discount</th>
                  <th>Valid until</th>
                </tr>
              </thead>
              <tbody>
                {data.map((promotion) => (
                  <tr key={promotion.promotionId}>
                    <td>{promotion.promotionId}</td>
                    <td>
                      <div className="table-primary">
                        <strong>Package #{promotion.packageId}</strong>
                        <span>Linked promotion target</span>
                      </div>
                    </td>
                    <td>
                      <span className="status-badge status-pending">{promotion.discount}%</span>
                    </td>
                    <td>{promotion.validUntil ?? 'n/a'}</td>
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
