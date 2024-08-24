--liquibase formatted sql

--changeset memoryleakdeath:08232024213808 dbms:h2
create table if not exists chatsettings
(
	id bigint generated always as identity(start with 1) not null primary key,
	active boolean not null default true,
	name varchar(100) not null,
	settings json(50000) null,
	created timestamp with time zone not null,
	updated timestamp with time zone not null
)
--rollback drop table if exists chatsettings