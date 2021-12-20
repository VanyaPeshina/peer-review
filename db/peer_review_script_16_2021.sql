create table statuses
(
    id     int auto_increment
        primary key,
    status varchar(30) not null,
    constraint statuses_status_uindex
        unique (status)
);

create table teams
(
    id        int auto_increment
        primary key,
    name      varchar(30) not null,
    owner_id  int         not null,
    to_delete int         not null,
    constraint teams_name_uindex
        unique (name)
);

create table user_roles
(
    id   int auto_increment
        primary key,
    role varchar(30) not null,
    constraint user_roles_role_uindex
        unique (role)
);

create table users
(
    id         int auto_increment
        primary key,
    username   varchar(20)  not null,
    password   varchar(350) not null,
    email      varchar(350) not null,
    phone      varchar(10)  not null,
    team_id    int          null,
    role_id    int          null,
    photo_name varchar(255) null,
    to_delete  int          not null,
    enabled    tinyint(1)   null,
    locked     tinyint(1)   not null,
    constraint users_email_uindex
        unique (email),
    constraint users_phone_uindex
        unique (phone),
    constraint users_username_uindex
        unique (username),
    constraint users_role_fk
        foreign key (role_id) references user_roles (id),
    constraint users_team_fk
        foreign key (team_id) references teams (id)
);

create table invitations
(
    id                    int auto_increment
        primary key,
    invitation_creator_id int      not null,
    team_id               int      not null,
    invited_user_id       int      not null,
    date                  datetime not null,
    to_delete             int      not null,
    constraint invitations_creator_fk
        foreign key (invitation_creator_id) references users (id),
    constraint invitations_invited_user_fk
        foreign key (invited_user_id) references users (id),
    constraint invitations_team_fk
        foreign key (team_id) references teams (id)
);

alter table teams
    add constraint teams_owners_fk
        foreign key (owner_id) references users (id);

create table tokens
(
    id           int auto_increment
        primary key,
    token        varchar(250) not null,
    created_at   datetime     not null,
    expires_at   datetime     not null,
    confirmed_at datetime     null,
    user_id      int          not null,
    constraint tokens_user_id_fk
        foreign key (user_id) references users (id)
);

create table work_items
(
    id                int auto_increment
        primary key,
    title             varchar(80)  not null,
    description       text         not null,
    reviewer_id       int          null,
    status_id         int          null,
    creator_id        int          not null,
    team_id           int          not null,
    file_name         varchar(255) null,
    file_download_uri varchar(255) null,
    constraint work_items_creator_fk
        foreign key (creator_id) references users (id),
    constraint work_items_reviewer_fk
        foreign key (reviewer_id) references users (id),
    constraint work_items_status_fk
        foreign key (status_id) references statuses (id),
    constraint work_items_team_fk
        foreign key (team_id) references teams (id)
);

create table comments
(
    id           int auto_increment
        primary key,
    comment      varchar(2000) not null,
    creator_id   int           not null,
    work_item_id int           not null,
    constraint comments_user_fk
        foreign key (creator_id) references users (id),
    constraint comments_work_item_fk
        foreign key (work_item_id) references work_items (id)
);


