drop database if exists moviedb;
create database moviedb;

use moviedb;

create table movies (
    id varchar(10) not null,
    title varchar(100) not null,
    year int not null,
    director varchar(100) not null,

    fulltext (title),
    primary key (id)
);

create table stars (
    id varchar(10) not null,
    name varchar(100) not null,
    birthYear int,

    primary key (id)
);

create table stars_in_movies (
    starId varchar(10) not null,
    movieId varchar(10) not null,

    foreign key (starId) references stars(id),
    foreign key (movieId) references movies(id),
    primary key (starId, movieId)
);

create table genres (
    id int not null auto_increment,
    name varchar(32) not null,

    primary key (id)
);

create table genres_in_movies (
    genreId int not null,
    movieId varchar(10) not null,

    foreign key (genreId) references genres(id),
    foreign key (movieId) references movies(id),
    primary key (genreId, movieId)
);

create table creditcards (
    id varchar(20) not null,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    expiration date not null,

    primary key (id)
);

create table customers (
    id int not null auto_increment,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    ccId varchar(20) not null,
    address varchar(200) not null,
    email varchar(50) not null,
    password varchar(20) null null,

    foreign key (ccId) references creditcards(id),
    primary key (id)
);

create table sales (
    id int not null auto_increment,
    customerId int not null,
    movieId varchar(10) not null,
    saleDate date not null,

    foreign key (customerId) references customers(id),
    foreign key (movieId) references movies(id),
    primary key (id)
);

create table ratings (
    movieId varchar(10) not null,
    rating float not null,
    numVotes int not null,

    foreign key (movieId) references movies(id),
    primary key (movieId)
);

create table employees
(
  email    varchar(50) not null,
  password varchar(20) not null,
  fullname varchar(100),

  primary key (email)
);

insert into employees(email, password, fullname)
values ('classta@email.edu', 'classta', ' TA CS122B');

drop procedure if exists add_movie;

DELIMITER //
create procedure add_movie(in _movieId varchar(10),
                           in _title varchar(100),
                           in _year int,
                           in _director varchar(100),
                           in _genreId int,
                           in _starId varchar(10),
                           in _starName varchar(100),
                           in _starYear int)
begin
  insert into movies(id, title, year, director) value (_movieId, _title, _year, _director);

    # insert start if not exists
    if (_starName is not null) then
      insert into stars(id, name, birthYear) value (_starId, _starName, _starYear);
    end if;

    insert into stars_in_movies(starId, movieId) value (_starId, _movieId);
    insert into genres_in_movies(genreId, movieId) value (_genreId, _movieId);
end //
DELIMITER ;