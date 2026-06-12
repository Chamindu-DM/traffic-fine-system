import { useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';
import { lookupFine, submitPayment } from './api';
import type { FineLookupResponse, PaymentResponse } from './types';

type ViewState = 'lookup' | 'payment' | 'confirmation';

type FormError = {
  title: string;
  details?: string;
};

const initialLookupForm = {
  referenceNumber: '',
  categoryCode: '',
};

const initialPaymentForm = {
  paymentMethod: 'CARD',
  cardLastFourDigits: '',
};

function formatDateTime(value?: string | null) {
  if (!value) {
    return 'N/A';
  }

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString();
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat('en-LK', {
    style: 'currency',
    currency: 'LKR',
    maximumFractionDigits: 2,
  }).format(value);
}

function isAxiosMessage(error: unknown) {
  if (!axios.isAxiosError(error)) {
    return 'Something went wrong while contacting the backend.';
  }

  const backendMessage =
    typeof error.response?.data?.message === 'string'
      ? error.response.data.message
      : typeof error.response?.data?.error === 'string'
        ? error.response.data.error
        : undefined;

  return backendMessage ?? error.message ?? 'Request failed.';
}

export default function App() {
  const [view, setView] = useState<ViewState>('lookup');
  const [lookupForm, setLookupForm] = useState(initialLookupForm);
  const [paymentForm, setPaymentForm] = useState(initialPaymentForm);
  const [fine, setFine] = useState<FineLookupResponse | null>(null);
  const [paymentResult, setPaymentResult] = useState<PaymentResponse | null>(null);
  const [lookupLoading, setLookupLoading] = useState(false);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [lookupError, setLookupError] = useState<FormError | null>(null);
  const [paymentError, setPaymentError] = useState<FormError | null>(null);

  const isPaid = fine?.status?.toUpperCase() === 'PAID';
  const isUnpaid = fine?.status?.toUpperCase() === 'UNPAID';

  const statusLabel = useMemo(() => {
    if (!fine) {
      return '';
    }

    const normalized = fine.status.toUpperCase();
    return normalized === 'PAID' ? 'Already Paid' : normalized;
  }, [fine]);

  async function handleLookupSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLookupError(null);
    setPaymentError(null);
    setPaymentResult(null);
    setLookupLoading(true);

    try {
      const result = await lookupFine(
        lookupForm.referenceNumber.trim(),
        lookupForm.categoryCode.trim(),
      );
      setFine(result);
      setPaymentForm(initialPaymentForm);
      setView(result.status.toUpperCase() === 'UNPAID' ? 'payment' : 'lookup');
    } catch (error) {
      setFine(null);
      setView('lookup');
      setLookupError({
        title: 'Fine lookup failed',
        details: isAxiosMessage(error),
      });
    } finally {
      setLookupLoading(false);
    }
  }

  async function handlePaymentSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!fine) {
      return;
    }

    setPaymentError(null);
    setPaymentLoading(true);

    try {
      const result = await submitPayment({
        referenceNumber: fine.referenceNumber,
        categoryCode: fine.categoryCode,
        paymentMethod: paymentForm.paymentMethod.trim(),
        cardLastFourDigits: paymentForm.cardLastFourDigits.trim(),
      });

      setPaymentResult(result);
      setView('confirmation');
    } catch (error) {
      setPaymentError({
        title: 'Payment could not be completed',
        details: isAxiosMessage(error),
      });
    } finally {
      setPaymentLoading(false);
    }
  }

  function resetPortal() {
    setView('lookup');
    setFine(null);
    setPaymentResult(null);
    setLookupError(null);
    setPaymentError(null);
    setPaymentForm(initialPaymentForm);
    setLookupForm(initialLookupForm);
  }

  return (
    <main className="app-shell">
      <section className="hero-panel">
        <div className="hero-copy">
          <span className="eyebrow">Sri Lanka Police Public Portal</span>
          <h1>Traffic Fine Lookup and Payment</h1>
          <p>
            Search a fine using your reference number and category code, then complete payment
            online when the fine is still unpaid.
          </p>
        </div>

        <div className="hero-badges">
          <div className="badge">Public access</div>
          <div className="badge">Axios-powered API</div>
          <div className="badge">Backend: localhost:8080</div>
        </div>
      </section>

      <section className="card-grid">
        <article className="panel">
          <header className="panel-header">
            <div>
              <p className="panel-kicker">Step 1</p>
              <h2>Fine Lookup</h2>
            </div>
            {fine ? (
              <button type="button" className="ghost-button" onClick={resetPortal}>
                Start over
              </button>
            ) : null}
          </header>

          <form className="form-stack" onSubmit={handleLookupSubmit}>
            <label className="field">
              <span>Reference number</span>
              <input
                value={lookupForm.referenceNumber}
                onChange={(event) =>
                  setLookupForm((current) => ({
                    ...current,
                    referenceNumber: event.target.value,
                  }))
                }
                placeholder="TF-2026-000123"
                autoComplete="off"
                required
              />
            </label>

            <label className="field">
              <span>Category code</span>
              <input
                value={lookupForm.categoryCode}
                onChange={(event) =>
                  setLookupForm((current) => ({
                    ...current,
                    categoryCode: event.target.value,
                  }))
                }
                placeholder="SPD"
                autoComplete="off"
                required
              />
            </label>

            <button type="submit" className="primary-button" disabled={lookupLoading}>
              {lookupLoading ? 'Looking up fine...' : 'Lookup Fine'}
            </button>
          </form>

          {lookupError ? (
            <div className="alert error-alert" role="alert">
              <strong>{lookupError.title}</strong>
              <span>{lookupError.details}</span>
            </div>
          ) : null}

          {fine ? (
            <div className="fine-summary">
              <div className="status-row">
                <span className="pill">{statusLabel}</span>
                <span className="muted">{fine.category}</span>
              </div>

              <dl className="details-grid">
                <div>
                  <dt>Reference</dt>
                  <dd>{fine.referenceNumber}</dd>
                </div>
                <div>
                  <dt>Category code</dt>
                  <dd>{fine.categoryCode}</dd>
                </div>
                <div>
                  <dt>Amount</dt>
                  <dd>{formatCurrency(fine.amount)}</dd>
                </div>
                <div>
                  <dt>District</dt>
                  <dd>{fine.district}</dd>
                </div>
                <div>
                  <dt>Officer</dt>
                  <dd>{fine.officer}</dd>
                </div>
                <div>
                  <dt>Issued at</dt>
                  <dd>{formatDateTime(fine.issuedAt)}</dd>
                </div>
                <div>
                  <dt>Paid at</dt>
                  <dd>{formatDateTime(fine.paidAt)}</dd>
                </div>
                <div>
                  <dt>Status</dt>
                  <dd>{fine.status}</dd>
                </div>
              </dl>

              {isPaid ? (
                <div className="alert info-alert" role="status">
                  Already Paid
                </div>
              ) : null}
            </div>
          ) : null}
        </article>

        {view === 'payment' && fine && isUnpaid ? (
          <article className="panel">
            <header className="panel-header">
              <div>
                <p className="panel-kicker">Step 2</p>
                <h2>Payment Form</h2>
              </div>
            </header>

            <form className="form-stack" onSubmit={handlePaymentSubmit}>
              <label className="field">
                <span>Payment method</span>
                <select
                  value={paymentForm.paymentMethod}
                  onChange={(event) =>
                    setPaymentForm((current) => ({
                      ...current,
                      paymentMethod: event.target.value,
                    }))
                  }
                  required
                >
                  <option value="CARD">Card</option>
                  <option value="MOBILE_BANKING">Mobile Banking</option>
                  <option value="QR">QR Payment</option>
                </select>
              </label>

              <label className="field">
                <span>Card last four digits</span>
                <input
                  value={paymentForm.cardLastFourDigits}
                  onChange={(event) =>
                    setPaymentForm((current) => ({
                      ...current,
                      cardLastFourDigits: event.target.value.replace(/\D/g, '').slice(0, 4),
                    }))
                  }
                  inputMode="numeric"
                  placeholder="1234"
                  minLength={4}
                  maxLength={4}
                  required
                />
              </label>

              <button type="submit" className="primary-button" disabled={paymentLoading}>
                {paymentLoading ? 'Processing payment...' : 'Pay Fine'}
              </button>
            </form>

            {paymentError ? (
              <div className="alert error-alert" role="alert">
                <strong>{paymentError.title}</strong>
                <span>{paymentError.details}</span>
              </div>
            ) : null}
          </article>
        ) : null}

        {view === 'confirmation' && paymentResult ? (
          <article className="panel confirmation-panel">
            <header className="panel-header">
              <div>
                <p className="panel-kicker">Step 3</p>
                <h2>Confirmation</h2>
              </div>
            </header>

            <div className="confirmation-card">
              <div className="success-mark">Payment successful</div>
              <p className="confirmation-message">{paymentResult.message}</p>

              <dl className="details-grid compact-grid">
                <div>
                  <dt>Payment reference</dt>
                  <dd>{paymentResult.paymentReference}</dd>
                </div>
                <div>
                  <dt>Status</dt>
                  <dd>{paymentResult.status}</dd>
                </div>
                <div>
                  <dt>Paid amount</dt>
                  <dd>{formatCurrency(paymentResult.amount)}</dd>
                </div>
                <div>
                  <dt>Paid at</dt>
                  <dd>{formatDateTime(paymentResult.paidAt)}</dd>
                </div>
              </dl>

              <button type="button" className="primary-button secondary-button" onClick={resetPortal}>
                Look up another fine
              </button>
            </div>
          </article>
        ) : null}
      </section>
    </main>
  );
}