UPDATE pe_fund SET calculation_type = 0 where calculation_type IS NULL;
ALTER TABLE pe_fund ALTER COLUMN investment_period TYPE CHARACTER VARYING(255);
ALTER TABLE pe_fund ALTER COLUMN traget_inv_size_range TYPE CHARACTER VARYING(255);
ALTER TABLE pe_fund ALTER COLUMN target_ev_range TYPE CHARACTER VARYING(255);