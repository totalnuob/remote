INSERT INTO trip_type(id, code, name_en, name_ru, name_kz) VALUES(1, 'TRAINING', 'Training', '', '');
INSERT INTO trip_type(id, code, name_en, name_ru, name_kz) VALUES(2, 'CONFERENCE', 'Conference', '', '');


-- update trip_memo set trip_type_id = ( CASE when trip_type = 'TRAINING' then 1 when trip_type = 'CONFERENCE' then 2 END);
