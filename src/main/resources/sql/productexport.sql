INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_register',
         'Регистрация программной кассы (включая СКО)',
         'Регистрация Программной кассы (ПК) в АИС ПКС iKassa',
         53, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_register_only',
         'Регистрация программной кассы (включая СКО)',
         'Регистрация Программной кассы (ПК) в АИС ПКС iKassa',
         53, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 1 месяц)',
         15, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_3',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 3 месяца)',
         45, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_6',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 6 месяцев)',
         87, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_12',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 12 месяцев)',
         174, 0, '', 1, false, false);


INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_dusik',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 1 месяц)',
         20, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_3_dusik',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 3 месяца)',
         60, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_6_dusik',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 6 месяцев)',
         120, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('ikassa_license_12_dusik',
         'Абонентское обслуживание iKassa',
         'Абонентская плата (за 12 месяцев)',
         240, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('skko_register',
         'Регистрация программной кассы в СККО',
         '',
         2.50, 20, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('skko_register',
         'Регистрация программной кассы в СККО',
         '',
         2.50, 20, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('skko_license_6',
         'Оплата за обслуживание программной кассы в СККО',
         'Информационное обслуживание пользователя программной кассы за 6 месяцев',
         25, 20, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('skko_license_12',
         'Оплата за обслуживание программной кассы в СККО',
         'Информационное обслуживание пользователя программной кассы за 12 месяцев',
         50,  20, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('personal',
         'Личный Кабинет',
         '',
         0, 0, '', 1, false, false);

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('app',
         'Программная Касса',
         'само приложение',
         0, 0, '', 1, false, false);


INSERT INTO ikassa.accompanying_doc (path, name, raw)
VALUES ('docs/products/paymob_m1.docx',
        '05 Реквизиты для оплаты устройства PayMob-M1', false);
INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('fm_paymob',
         'Мобильный компьютер PayMob-M1',
         'iKassa FM',
         315, 20, '', 1, false, false);


INSERT INTO ikassa."product_accompanying_docs"("product_id", "accompanying_doc_id")
VALUES ((SELECT product_id FROM ikassa.product WHERE alias = 'fm_paymob'),
        (SELECT accompanying_doc_id FROM ikassa."accompanying_doc" WHERE path = 'docs/products/paymob_m1.docx'));

INSERT INTO ikassa.product (alias, name, comment,
                              price, tax, currency, units,
                              round_total, dual_docs)
VALUES  ('pax930',
         'POS-терминал PAX A930',
         'iKassa Smart&Card',
         715, 20, '', 1, false, true);




