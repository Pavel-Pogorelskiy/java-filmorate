drop table if exists mpa, films, likes,users,friends,genre,genre_link, DIRECTORS, FILMS_DIRECTORS, reviews,like_review;

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id integer PRIMARY KEY,
  name varchar(255) NOT null unique
);
CREATE TABLE IF NOT EXISTS films (
  film_id integer generated by default as identity not null PRIMARY key,
  name varchar(255) NOT NULL,
  description varchar(200),
  releaseDate date NOT null,
  duration integer NOT NULL,
  mpa integer NOT null REFERENCES mpa (mpa_id) on delete cascade
);

CREATE TABLE IF NOT EXISTS users (
  user_id integer generated by default as identity not null PRIMARY key,
  email varchar UNIQUE NOT NULL,
  login varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS likes (
film_id integer REFERENCES films (film_id) on delete cascade,
user_id integer REFERENCES users (user_id) on delete cascade,
  PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer references users (user_id) on delete cascade,
  friends_id integer references users (user_id) on delete cascade,
  PRIMARY KEY (user_id, friends_id)
);

CREATE TABLE IF NOT EXISTS genre (
  genre_id integer PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre_link (
  film_id integer REFERENCES films (film_id) on delete cascade,
  genre_id integer REFERENCES genre (genre_id) on delete cascade,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name CHARACTER VARYING(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS films_directors
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
    film_id INTEGER NOT NULL REFERENCES films (film_id),
    director_id INTEGER NOT NULL REFERENCES directors (director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
  review_id integer generated by default as identity not null PRIMARY key,
  content varchar(255) NOT NULL,
  isPositive boolean,
  user_id integer references users (user_id) on delete cascade,
  film_id integer REFERENCES films (film_id) on delete cascade,
  useful integer DEFAULT 0
);

CREATE TABLE IF NOT EXISTS like_review (
  review_id integer REFERENCES reviews (review_id) on delete cascade,
  user_id integer REFERENCES users (user_id) on delete cascade,
  isLike boolean,
  PRIMARY KEY (review_id, user_id)
);


