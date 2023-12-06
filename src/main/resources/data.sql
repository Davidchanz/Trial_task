INSERT INTO users (username, password, email, created_on) VALUES ('admin', '$2a$10$2dnbN.uDbYTGj2RRb2TAn.M4DWPpp0C5uo/2QUDOD5Hfl8Jx6wj6C', 'admin@email.com', CURRENT_TIMESTAMP());

INSERT INTO quotes (text, author_id, b_archive, created_On, last_Updated_On) VALUES ('My first quote!', 1, false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());