create table if not exists question
(
    id       bigserial not null
        constraint question_pkey
            primary key,
    answer   varchar(255),
    question varchar(500)
);

create table if not exists game
(
    id                  bigserial not null
        constraint game_pkey
            primary key,
    players_amount      integer,
    max_question        integer,
    players_to_start    integer,
    question_iterator   integer   not null,
    state               varchar(255),
    current_question_id bigint
        constraint fkbfmocguestowyqhw90u7f04ba
            references question,
    lobby_id            bigint
);

create table if not exists lobby
(
    id           bigserial not null
        constraint lobby_pkey
            primary key,
    chat_id      integer
        constraint uk_lsj041gasn8p8i1wgro9fsool
            unique,
    invited_date timestamp,
    peer_id      integer
        constraint uk_m88fgpk37dhsqgo039ap6hu4r
            unique,
    game_id      bigint
        constraint fkkmh1t34n86iwvgwb95mxy8iwa
            references game
);

create table if not exists users
(
    user_id         bigint  not null
        constraint users_pkey
            primary key,
    elo             integer not null,
    name            varchar(255),
    role            varchar(255),
    surname         varchar(255),
    current_game_id bigint
        constraint fkso2vlcvk3rgesf3afaik9m61m
            references game
);
