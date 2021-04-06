UPDATE currency_rates SET avg_value=331.14 WHERE date='2017-01-31' AND currency_id=1;
UPDATE currency_rates SET avg_value=320.18 WHERE date='2017-02-28' AND currency_id=1;
UPDATE currency_rates SET avg_value=316.07 WHERE date='2017-03-31' AND currency_id=1;
UPDATE currency_rates SET avg_value=312.24 WHERE date='2017-04-30' AND currency_id=1;
UPDATE currency_rates SET avg_value=313.51 WHERE date='2017-05-31' AND currency_id=1;
UPDATE currency_rates SET avg_value=318.42 WHERE date='2017-06-30' AND currency_id=1;
UPDATE currency_rates SET avg_value=325.31 WHERE date='2017-07-31' AND currency_id=1;
UPDATE currency_rates SET avg_value=332.69 WHERE date='2017-08-31' AND currency_id=1;
UPDATE currency_rates SET avg_value=339.22 WHERE date='2017-09-30' AND currency_id=1;
UPDATE currency_rates SET avg_value=337.1 WHERE date='2017-10-31' AND currency_id=1;


SELECT * FROM currency_rates
WHERE value IS NULL AND currency_id=1
AND date IN ('2017-01-31','2017-02-28','2017-03-31','2017-04-30','2017-05-31','2017-06-30','2017-07-31','2017-08-31','2017-09-30','2017-10-31');

DELETE FROM currency_rates
WHERE value IS NULL AND currency_id=1
AND date IN ('2017-01-31','2017-02-28','2017-03-31','2017-04-30','2017-05-31','2017-06-30','2017-07-31','2017-08-31','2017-09-30','2017-10-31');



SELECT e1.id, e1.date, e1.currency_id, e1.value, e1.avg_value
FROM currency_rates e1, currency_rates e2
WHERE e1.id != e2.id AND e1.date=e2.date AND e1.currency_id=e2.currency_id
--AND e1.value is null