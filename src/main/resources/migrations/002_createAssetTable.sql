--liquibase formatted sql

--changeset memoryleakdeath:05312024225345 dbms:h2
create table if not exists assets
(
	id bigint generated always as identity(start with 1) not null primary key,
	originalfilename varchar(50) not null,
	contenttype varchar(50) not null,	
	description varchar(1000) null,
	created timestamp with time zone not null,
	updated timestamp with time zone not null
)
--rollback drop table if exists assets

--changeset memoryleakdeath:05312024225830 dbms:h2
alter table if exists alertsettings 
	add column if not exists imageid bigint null;
alter table if exists alertsettings 
	add column if not exists soundid bigint null;
alter table if exists alertsettings 
	add constraint alertsettings_imageid_assets_fk foreign key(imageid) references assets(id);
alter table if exists alertsettings 
	add constraint alertsettings_soundid_assets_fk foreign key(soundid) references assets(id);
--rollback alter table if exists alertsettings
--rollback  drop constraint if exists alertsettings_imageid_assets_fk;
--rollback alter table if exists alertsettings
--rollback drop constraint if exists alertsettings_soundid_assets_fk;
--rollback alter table if exists alertsettings
--rollback drop column if exists imageid;
--rollback alter table if exists alertsettings
--rollback drop column if exists soundid;
