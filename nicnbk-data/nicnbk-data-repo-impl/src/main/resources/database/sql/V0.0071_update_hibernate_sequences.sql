SELECT setval('nb_chart_of_accounts_id_seq', (SELECT MAX(id) FROM nb_chart_of_accounts));

SELECT setval('rep_nic_chart_of_accounts_id_seq', (SELECT MAX(id) FROM rep_nic_chart_of_accounts));

SELECT setval('rep_singularity_nic_chart_of_accounts_id_seq', (SELECT MAX(id) FROM rep_singularity_nic_chart_of_accounts));
SELECT setval('rep_tarragon_nic_chart_of_accounts_id_seq', (SELECT MAX(id) FROM rep_tarragon_nic_chart_of_accounts));
SELECT setval('rep_terra_nic_chart_of_accounts_id_seq', (SELECT MAX(id) FROM rep_terra_nic_chart_of_accounts));


SELECT setval('rep_pe_balance_type_id_seq', (SELECT MAX(id) FROM rep_pe_balance_type));
SELECT setval('rep_pe_cashflows_type_id_seq', (SELECT MAX(id) FROM rep_pe_cashflows_type));
SELECT setval('rep_pe_operations_type_id_seq', (SELECT MAX(id) FROM rep_pe_operations_type));

SELECT setval('rep_hf_chart_accounts_type_id_seq', (SELECT MAX(id) FROM rep_hf_chart_accounts_type));

SELECT setval('rep_re_chart_accounts_type_id_seq', (SELECT MAX(id) FROM rep_re_chart_accounts_type));
SELECT setval('rep_re_balance_type_id_seq', (SELECT MAX(id) FROM rep_re_balance_type));
SELECT setval('rep_re_profit_loss_type_id_seq', (SELECT MAX(id) FROM rep_re_profit_loss_type));




