
INSERT INTO foglalas(id, asztal_szama, email, keresztnev, vezeteknev, foglalas_idopont, statusz, telefon)
    VALUES (1, 1, 'zbal1977@gmail.com', 'Zoltán', 'Balogh', '2020-01-06 14:30', 1, '+36-20-556-8595');

INSERT INTO foglalas(id, asztal_szama, email, keresztnev, vezeteknev, foglalas_idopont, statusz, telefon)
    VALUES (2, 3, 'horvath.tamas@gmail.com', 'Tamás', 'Horváth', '2019-12-29 18:43', 1, '+36305608999');

INSERT INTO foglalas(id, asztal_szama, email, keresztnev, vezeteknev, foglalas_idopont, statusz, telefon)
    VALUES (3, 8, 'erno.kiss@gmail.com', 'Ernő', 'Kiss', '2020-01-04 14:16', 1, '06-70-234-9088');

ALTER SEQUENCE foglalas_id_seq RESTART WITH 4;
