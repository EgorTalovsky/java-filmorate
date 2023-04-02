DELETE
FROM "user" ;
DELETE
FROM "film" ;
DELETE
FROM "film_like" ;
DELETE
FROM "film_genre" ;
DELETE
FROM "friends_users" ;
DELETE
FROM "genre" ;
DELETE
FROM "age_rate" ;

INSERT  INTO "age_rate" ("age_rate_id", "rate_name")
VALUES ('1', 'G');
INSERT  INTO "age_rate" ("age_rate_id", "rate_name")
VALUES ('2', 'PG');
INSERT  INTO "age_rate" ("age_rate_id", "rate_name")
VALUES ('3', 'PG-13');
INSERT  INTO "age_rate" ("age_rate_id", "rate_name")
VALUES ('4', 'R');
INSERT  INTO "age_rate" ("age_rate_id", "rate_name")
VALUES ('5', 'NC-17');

INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('1', 'Комедия');
INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('2', 'Драма');
INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('3', 'Мультфильм');
INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('4', 'Триллер');
INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('5', 'Документальный');
INSERT  INTO "genre" ("genre_id", "genre_name")
VALUES ('6', 'Боевик');

