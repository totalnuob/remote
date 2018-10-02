
-- POSITIVE ONLY
-- 4200
UPDATE rep_singularity_nic_chart_of_accounts set negative_only=false, positive_only=true where id=23
-- 4900
UPDATE rep_singularity_nic_chart_of_accounts set negative_only=false, positive_only=true where id=25

-- NEGATIVE ONLY
--4200
UPDATE rep_singularity_nic_chart_of_accounts set positive_only=false, negative_only=true where id=36
--4900
UPDATE rep_singularity_nic_chart_of_accounts set positive_only=false, negative_only=true where id=37
