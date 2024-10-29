--changeset memoryleakdeath:10282024230745 dbms:h2
alter table if exists accounts 
	add column if not exists serviceuserid varchar(100) null;

--rollback alter table if exists accounts
--rollback  drop column if exists serviceuserid;
