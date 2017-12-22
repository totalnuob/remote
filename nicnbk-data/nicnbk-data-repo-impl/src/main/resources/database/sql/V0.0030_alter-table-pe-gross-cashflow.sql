ALTER TABLE pe_gross_cashflow ALTER COLUMN grosscf DROP NOT NULL;
ALTER TABLE pe_gross_cashflow ALTER COLUMN invested DROP NOT NULL;
ALTER TABLE pe_gross_cashflow ALTER COLUMN irr DROP NOT NULL;
ALTER TABLE pe_gross_cashflow ALTER COLUMN realized DROP NOT NULL;
ALTER TABLE pe_gross_cashflow ALTER COLUMN unrealized DROP NOT NULL;