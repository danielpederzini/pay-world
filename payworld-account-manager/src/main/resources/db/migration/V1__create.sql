create table account (balance numeric(24,4), currency varchar(3), created_at timestamp(6), updated_at timestamp(6), key varchar(255), uuid varchar(255) not null, primary key (uuid));
create table transaction (converted_amount numeric(24,4), new_currency varchar(3), original_amount numeric(24,4), original_currency varchar(3), created_at timestamp(6), updated_at timestamp(6), receiver_fk varchar(255), sender_fk varchar(255), uuid varchar(255) not null, primary key (uuid));
alter table if exists transaction add constraint FK4t2u3gqb466w2xyybiov2usry foreign key (receiver_fk) references account;
alter table if exists transaction add constraint FKknax9xh1t81kj95xgeg2j487k foreign key (sender_fk) references account;
