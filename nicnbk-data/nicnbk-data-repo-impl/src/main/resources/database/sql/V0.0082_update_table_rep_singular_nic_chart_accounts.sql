UPDATE rep_singularity_nic_chart_of_accounts SET chart_accounts_type_id=1 WHERE (positive_only IS NULL OR positive_only=FALSE) AND (negative_only IS NULL OR negative_only=FALSE);

UPDATE rep_singularity_nic_chart_of_accounts SET chart_accounts_type_id=2 WHERE positive_only=TRUE AND (negative_only IS NULL OR negative_only=FALSE);

UPDATE rep_singularity_nic_chart_of_accounts SET chart_accounts_type_id=3 WHERE negative_only=TRUE AND (positive_only IS NULL OR positive_only=FALSE);

