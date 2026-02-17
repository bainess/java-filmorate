MERGE INTO ratings (mpa_name) KEY(mpa_name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

MERGE INTO genres (name) KEY(name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

MERGE INTO event_operations(operation_name) KEY(operation_name) VALUES('REMOVE'), ('ADD'), ('UPDATE');

MERGE INTO event_types(event_name) KEY(event_name) VALUES('LIKE'), ('REVIEW'), ('FRIEND');

