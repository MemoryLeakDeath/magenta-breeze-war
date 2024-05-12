--liquibase formatted sql

--changeset memoryleakdeath:04202024001645 dbms:h2
create table if not exists alertsettings
(
	id bigint generated always as identity(start with 1) not null primary key,
	service varchar(30) not null,
	type varchar(30) not null,
	active boolean not null default true,
	settings json(50000) null,
	created timestamp with time zone not null,
	updated timestamp with time zone not null
)
--rollback drop table if exists alertsettings