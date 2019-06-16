COPY benchmark_value(asof_date,index_value,return_value,benchmark_id)
FROM 'D:\tmp\tbills.csv' DELIMITER ';' CSV HEADER;

COPY benchmark_value(asof_date,index_value,return_value,benchmark_id)
FROM 'D:\tmp\snp.csv' DELIMITER ';' CSV HEADER;