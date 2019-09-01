-- DAI - 1
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (1, 'DAI1_H', 'Head', null, 'Директор', 1);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (2, 'DAI1_DH', 'Deputy Head', null, 'Заместитель директора', 1);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (3, 'DAI1_SA', 'Senior Analyst', null, 'Главный специалист-аналитик', 1);

-- DAI - 2
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (21, 'DAI2_H', 'Head', null, 'Директор', 2);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (22, 'DAI2_DH', 'Deputy Head', null, 'Заместитель директора', 2);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (23, 'DAI2_SA', 'Senior Analyst', null, 'Главный специалист-аналитик', 2);

-- DEV
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (31, 'DEV_H', 'Head', null, 'Начальник', 3);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (32, 'DEV_SA', 'Senior Developer/Data Analyst', null, 'Главный специалист-аналитик', 3);


-- LEGAL
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (41, 'LEGAL_H', 'Head', null, 'Начальник', 4);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (42, 'LEGAL_L', 'Lawyer', null, 'Юрист', 4);

-- TREASURY
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (51, 'TRSRY_H', 'Head', null, 'Казначей', 5);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (52, 'TRSRY_DH', 'Deputy Head', null, 'Заместитель начальника', 5);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (52, 'TRSRY_SA', 'Senior dealer', null, 'Главный специалист – дилер', 5);

-- STRATEGY
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (61, 'STRTGY_H', 'Head', null, 'Начальник', 6);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (62, 'STRTGY_SA', 'Senior Analyst', null, 'Главный специалист-аналитик', 6);

-- REPORTING
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (71, 'REP_H', 'Head', null, 'Начальник', 7);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (72, 'REP_DH', 'Deputy Head', null, 'Заместитель начальника', 7);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (73, 'REP_SA', 'Senior Analyst', null, 'Главный специалист-аналитик', 7);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (74, 'REP_LA', 'Leading Analyst', null, 'Ведущий специалист-аналитик', 7);

-- RISK MANAGEMENT
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (81, 'RISK_H', 'Head', null, 'Директор', 8);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (82, 'RISK_DH', 'Deputy Head', null, 'Заместитель директора', 8);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (83, 'RISK_SA', 'Senior Analyst', null, 'Главный специалист-аналитик', 8);

-- OPERATIONS
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (91, 'OPERS_H', 'Head', null, 'Начальник', 9);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (92, 'OPERS_DH', 'Deputy Head', null, 'Заместитель начальника', 9);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (93, 'OPERS_SYSA', 'System Administrator', null, 'Системный администратор', 9);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (94, 'OPERS_PROC', 'Procurement manager', null, 'Менеджер по закупкам', 9);

-- ACCOUNTING
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (101, 'ACC_H', 'Chief Accountant', null, 'Главный бухгалтер', 10);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (102, 'ACC_DH', 'Deputy Chief Accountant', null, 'Заместитель главного бухгалтера', 10);

-- HUMAN RESOURCES
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (111, 'HR_H', 'Head', null, 'Начальник', 11);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (112, 'HR_CEOPA', 'CEO Personal Assistant', null, 'Помощник Председателя Правления', 11);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (113, 'HR_LA', 'Leading Analyst', null, 'Ведущий специалист-аналитик', 11);

-- EXEC
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (121, 'CEO', 'CEO', null, 'Председатель Правления', null);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (122, 'DEP_CEO', 'Deputy CEO', null, 'Заместитель Председателя Правления', null);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (123, 'MNG_DIR', 'Managing Director', null, 'Управляющий Директор - член Правления', null);

--
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (131, 'IN_AUDIT', 'Internal Auditor', null, 'Внутренний аудитор', null);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (132, 'CORP_SEC', 'Corporate Secretary', null, 'Корпоративный секретарь', null);
INSERT INTO public.position(id, code, name_en, name_kz, name_ru, department_id) VALUES (133, 'COMPLNC', 'Compliance Controller', null, 'Комплаенс контроллер', null);