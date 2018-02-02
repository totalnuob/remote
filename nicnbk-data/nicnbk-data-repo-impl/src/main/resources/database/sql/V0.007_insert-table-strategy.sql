  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(1, 'MEGA_CAP', 'Mega/Large Cap Buyout', '', '', 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(2, 'MID_CAP', 'Mid Cap Buyout', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(3, 'SM_CAP', 'Small Cap Buyout', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(4, 'PE_CREDIT', 'Credit', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(5, 'PE_MULTI', 'Multi', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(6, 'PE_OTHER', 'Other', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(7, 'CFB', 'Corporate Finance/ Buyout', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(8, 'CFB_USD', 'Corporate Finance/ Buyout - USD', '', '' , 1, 7);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(9, 'CFB_EUR', 'Corporate Finance/ Buyout - Euro', '', '' , 1, 7);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(10, 'CFB_GBP', 'Corporate Finance/ Buyout - GBP', '', '' , 1, 7);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(11, 'SPEC_S', 'Special Situations', '', '' , 1, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(12, 'GROW_EQ', 'Growth Equity', '', '' , 1, 11);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(13, 'VC', 'Venture Capital', '', '' , 1, null);

  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(21, 'EQUITY', 'Equity', '', '', 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(22, 'EVENT', 'Event Driven', '', '', 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(23, 'REL_VAL', 'Relative Value', '', '', 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(24, 'MACRO', 'Macro/Directional', '', '' , 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(25, 'HF_CREDIT', 'Credit', '', '', 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(26, 'COMMOD', 'Commodities', '', '', 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(27, 'HF_MULTI', 'Multistrategy', '', '' , 2, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(28, 'LONGBIAS', 'Long Biased', '', '' , 2, null);

  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(41, 'CORE', 'Core', '', '', 3, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(42, 'CORE+', 'Core+', '', '', 3 , null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(43, 'VAL_ADD', 'Value Added', '', '', 3, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(44, 'OPPORTUN', 'Opportunistic', '', '', 3 , null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(45, 'RE_MULTI', 'Multi', '', '' , 3, null);
  INSERT INTO strategy(id, code, name_en, name_ru, name_kz, group_type, parent_id) VALUES(46, 'RE_OTHER', 'Other', '', '' , 3, null);