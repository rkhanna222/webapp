create table user_tbl
(
    id UUID not null,
    emailID varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    pass varchar(255) not null,
    primary key (id)
);
