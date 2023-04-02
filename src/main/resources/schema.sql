CREATE TABLE IF NOT EXISTS "genre" (
    "genre_id" int NOT NULL,
    "genre_name" varchar NOT NULL,
    CONSTRAINT "pk_genre" PRIMARY KEY ("genre_id")
);

CREATE TABLE IF NOT EXISTS "age_rate" (
    "age_rate_id" int,
    "rate_name" varchar NOT NULL,
    CONSTRAINT "pk_age_rate" PRIMARY KEY ("age_rate_id")
);

CREATE TABLE IF NOT EXISTS "film" (
    "film_id" int NOT NULL,
    "name" varchar   NOT NULL,
    "description" varchar(200)   NOT NULL,
    "release_date" date   NOT NULL,
    "duration" int   NOT NULL,
    "age_rate_id" int,
    CONSTRAINT "film_pk" PRIMARY KEY ("film_id"),
    CONSTRAINT "fk_film_age" FOREIGN KEY ("age_rate_id") REFERENCES "age_rate"("age_rate_id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "film_genre" (
    "film_id" long   NOT NULL,
    "genre_id" int   NOT NULL,
    CONSTRAINT "pk_film_genre" PRIMARY KEY ("film_id", "genre_id"),
    CONSTRAINT "fk_film_genre" FOREIGN KEY ("genre_id") REFERENCES "genre"("genre_id") ON DELETE CASCADE,
    CONSTRAINT "fk_film_id" FOREIGN KEY ("film_id") REFERENCES "film"("film_id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "user"(
    "user_id" int  NOT NULL,
    "login" varchar   NOT NULL,
    "name" varchar   NOT NULL,
    "email" varchar   NOT NULL,
    "birthday" date   NOT NULL,
    CONSTRAINT "pk_user" PRIMARY KEY ("user_id")
);

CREATE TABLE IF NOT EXISTS "film_like" (
    "film_id" int   NOT NULL,
    "user_id" int   NOT NULL,
    CONSTRAINT "pk_film_like" PRIMARY KEY ("film_id", "user_id"),
    CONSTRAINT "fk_film_like_to_film" FOREIGN KEY ("film_id") REFERENCES "film"("film_id") ON DELETE CASCADE,
    CONSTRAINT "fk_film_like_to_user" FOREIGN KEY ("user_id") REFERENCES "user"("user_id") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "friends_users" (
    "user_id" int NOT NULL,
    "friend_id" int NOT NULL,
    CONSTRAINT "pk_friends_users" PRIMARY KEY ("user_id", "friend_id"),
    CONSTRAINT "fk_friends_users" FOREIGN KEY ("user_id") REFERENCES "user"("user_id") ON DELETE CASCADE,
    CONSTRAINT "fk_friends_users_by_friend" FOREIGN KEY ("friend_id") REFERENCES "user"("user_id") ON DELETE CASCADE
);