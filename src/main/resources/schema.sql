CREATE TABLE sessions (
    telegram_user_id integer PRIMARY KEY,
    state varchar not null
);

CREATE TABLE participants (
    id serial primary key,
    name varchar(255) not null,
    session_id integer not null references sessions
);

CREATE TABLE party_spends (
    id serial primary key,
    name varchar(255) not null,
    session_id integer references sessions
);

CREATE TABLE payments (
    id serial primary key,
    who integer not null references participants,
    what integer not null references party_spends,
    how_much numeric not null
);

CREATE TABLE uses (
    id serial primary key,
    who integer not null references participants,
    what integer not null references party_spends
);

CREATE TABLE debts (

);

