create table messages (
    id UUID PRIMARY KEY,
    conversation_id INTEGER not null,
    author varchar(128) not null,
    content varchar(4096) not null,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)