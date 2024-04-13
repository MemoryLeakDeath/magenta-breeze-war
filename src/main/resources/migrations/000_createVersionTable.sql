--liquibase formatted sql

--changeset memoryleakdeath:041220204213855 dbms:h2
create table if not exists version
(version varchar(20) not null)
--rollback drop table if exists version