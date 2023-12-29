drop table if exists record;
drop table if exists court;
drop table if exists club_member;
-- 建立 court
create table if not exists court(
	court_id int primary key,
	court_name varchar(50) not null unique
);
-- 建立 club_member
create table if not exists club_member(
	club_member_id int auto_increment primary key,
    club_member_username varchar(50) not null unique,
    club_member_password varchar(50) not null,
	club_member_name varchar(50) not null,
    club_member_birth varchar(50) not null
);

-- 建立 record
create table if not exists record(
	record int auto_increment primary key,
	court_id int not null,
    club_member_id int not null,
    use_date varchar(50) not null,
    createDate timestamp default current_timestamp,
    foreign key (court_id) references court(court_id),
    foreign key (club_member_id) references club_member(club_member_id),
    constraint unique_court_id_and_use_date unique(court_id, use_date)
);

-- 建立預設資料
insert into court(court_id, court_name) values(1, '東側球場');
insert into court(court_id, court_name) values(2, '西側球場');
insert into court(court_id, court_name) values(3, '南側球場');
insert into court(court_id, court_name) values(4, '北側球場');

insert into club_member(club_member_username, club_member_password, club_member_name, club_member_birth) values('john@gmail.com', '12345678', 'John', '1992-01-14');
insert into club_member(club_member_username, club_member_password, club_member_name, club_member_birth) values('mary@gmail.com', '12345678', 'mary', '1994-11-11');
insert into club_member(club_member_username, club_member_password, club_member_name, club_member_birth) values('ken@gmail.com', '12345678', 'ken', '1990-05-07');

insert into record(court_id, club_member_id, use_date) values('2', '1', '2023-12-31');
insert into record(court_id, club_member_id, use_date) values('3', '2', '2023-12-30');
insert into record(court_id, club_member_id, use_date) values('1', '3', '2023-12-29');