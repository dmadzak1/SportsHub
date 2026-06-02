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
        <p className="eyebrow">Promotion service</p>
        <h2>Active promotions</h2>
        {loading ? <p className="muted">Loading promotions...</p> : null}
        {error ? <p className="error-banner">{error}</p> : null}
        {!loading && !error && data.length === 0 ? <p className="muted">No active promotions.</p> : null}

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
                    <td>{promotion.packageId}</td>
                    <td>{promotion.discount}%</td>
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
