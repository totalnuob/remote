INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(1, 'OPERATIONS', 'Cash flows from operating activities', '', '', null);
INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(2, 'FINANCING', 'Cash flows from financing activities', '', '', null);


INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(51, 'ADJ_RECON', 'Adjustments to reconcile net increase (decrease) in partner''s capital resulting from operations to net cash provided by (used in) operating activities', '', '', 1);
INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(52, 'CHNG_AST_L', 'Change in assets and liabilities', '', '', 51);
INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(53, 'PREP_EXP', 'Prepaid expenses', '', '', 52);
INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(54, 'DIST_FUND', 'Distributions received from fund investments accounted for as', '', '', 51);

INSERT INTO rep_pe_cashflows_type(id, code, name_en, name_ru, name_kz, parent_id) VALUES(101, 'SUPP_DISCL', 'Supplemental disclosure of cash flow information', '', '', null);


