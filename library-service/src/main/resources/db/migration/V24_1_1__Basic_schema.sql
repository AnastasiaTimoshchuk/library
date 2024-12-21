create schema if not exists library;

create table library.reader
(
    id          serial primary key,
    first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    middle_name varchar(100),
    email       varchar(100) unique not null
);

create unique index idx_readers_email on library.reader (email);


create table library.author (
    id          serial primary key,
    first_name  varchar(100) not null,
    last_name   varchar(100) not null,
    middle_name varchar(100),
    birth_date  date not null
);


create table library.book (
    id          serial primary key,
    author_id   integer not null references library.author(id),
    title       varchar(200) not null,
    is_borrowed boolean default false,
    borrow_date date,
    reader_id   integer references library.reader(id)
);

create index idx_book_author_id on library.book (author_id);
create index idx_book_reader_id on library.book (reader_id);
create unique index idx_author_id_title on library.book (author_id, title);