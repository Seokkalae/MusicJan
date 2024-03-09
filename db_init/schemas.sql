create table server (
    id text not null primary key unique,
    name text not null,
    allow boolean not null default false
)