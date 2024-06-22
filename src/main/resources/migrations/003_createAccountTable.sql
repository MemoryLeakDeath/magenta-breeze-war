--liquibase formatted sql

--changeset memoryleakdeath:06152024172529 dbms:h2
create table if not exists accountalertsettings
(
	accountid bigint not null,
	alertsettingsid bigint not null,
	created timestamp with time zone not null,
	updated timestamp with time zone not null,
	primary key(accountid, alertsettingsid)
)
--rollback drop table if exists accountalertsettings

--changeset memoryleakdeath:06152024225610 dbms:h2
create table if not exists accounts
(
	id bigint generated always as identity(start with 1) not null primary key,
	service varchar(30) not null,
	displayname varchar(100) not null,
	profileurl varchar(500) not null,
	chatonly boolean not null default false,
	statekey varchar(50) not null,
	statekeyexpired boolean not null default false,
	created timestamp with time zone not null,
	updated timestamp with time zone not null
)
--rollback alter table if exists accounts
--rollback  drop constraint if exists accounts_id_accountalertsettings_fk;
--rollback drop table if exists accounts

--changeset memoryleakdeath:06152024230520 dbms:h2
alter table if exists alertsettings 
	drop column if exists service;
delete from alertsettings;	
alter table if exists accountalertsettings
	add constraint accountalertsettings_alertsettingsid_alertsettings_fk foreign key(alertsettingsid) references alertsettings(id) on delete cascade;
alter table if exists accountalertsettings
	add constraint accountalertsettings_accountid_accounts_fk foreign key(accountid) references accounts(id) on delete cascade;	

--rollback alter table if exists alertsettings
--rollback  drop constraint if exists alertsettings_accountid_accounts_fk;
--rollback alter table if exists alertsettings
--rollback  drop constraint if exists alertsettings_id_accountalertsettings_fk;
--rollback delete from alertsettings;
--rollback alter table if exists alertsettings
--rollback  drop column if exists accountid;
--rollback alter table if exists alertsettings
--rollback  add column if not exists service varchar(30) not null;
--rollback alter table if exists accountalertsettings
--rollback  drop constraint if exists accountalertsettings_alertsettingsid_alertsettings_fk;
--rollback alter table if exists accountalertsettings
--rollback  drop constraint if exists accountalertsettings_accountid_accounts_fk;

