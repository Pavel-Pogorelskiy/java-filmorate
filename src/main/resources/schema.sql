drop table if exists mpa, films, likes,users,friends,genre,genre_link;

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id integer PRIMARY KEY,
  name varchar(255) NOT null unique
);
CREATE TABLE IF NOT EXISTS films (
  film_id integer generated by default as identity not null PRIMARY key,
  name varchar(255) NOT NULL,
  description varchar(255),
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