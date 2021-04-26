--1204
SELECT count(*) FROM benchmark_value e

--894
SELECT count(*) FROM benchmark_value e
WHERE (date_trunc('MONTH', (e.asof_date)::date) + INTERVAL '1 MONTH - 1 day')::DATE = e.asof_date

--310
SELECT count(*) FROM benchmark_value e
WHERE (date_trunc('MONTH', (e.asof_date)::date) + INTERVAL '1 MONTH - 1 day')::DATE <> e.asof_date

-- UPDATE date to END OF MONTH
UPDATE benchmark_value
SET asof_date=(date_trunc('MONTH', (asof_date)::date) + INTERVAL '1 MONTH - 1 day')::DATE