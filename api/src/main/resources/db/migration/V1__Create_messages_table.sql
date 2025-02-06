create table messages (
    id int not null,
    conversationId int not null,
    author varchar(128) not null,
    content varchar(4096) not null
)