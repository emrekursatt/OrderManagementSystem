CREATE SCHEMA IF NOT EXISTS customer_service
    AUTHORIZATION postgres;


create table if not exists customer_service.tiers
(
    id              bigint generated always as identity,
    name            varchar(50)      not null,
    required_orders integer          not null,
    discount_rate   double precision not null,
    constraint tiers_pkey1
    primary key (id),
    constraint ui_name
    unique (name)
    );

comment on constraint ui_name on customer_service.tiers is 'name';

alter table customer_service.tiers
    owner to postgres;

create table if not exists customer_service.customer
(
    id               bigint generated always as identity,
    username         varchar(100) not null,
    name             varchar      not null,
    email            varchar(255) not null,
    tier_id          integer      not null,
    order_count      integer,
    created_at       bigint       not null,
    updated_at       bigint,
    password         varchar(255) not null,
    enabled          boolean      not null,
    status           smallint     not null,
    fail_login_count integer      not null,
    constraint tiers_pkey
    primary key (id),
    constraint ui_username
    unique (username),
    constraint ui_email
    unique (email),
    constraint fk_c_tier_id
    foreign key (tier_id) references customer_service.tiers
    on update cascade on delete restrict
    );

alter table customer_service.customer
    owner to postgres;

create table if not exists customer_service.tiers_history
(
    id               bigint generated always as identity,
    customer_id      integer not null,
    previous_tier_id integer not null,
    new_tier_id      integer not null,
    change_date      time with time zone default CURRENT_TIMESTAMP,
    primary key (id),
    constraint fk_th_customer_id
    foreign key (customer_id) references customer_service.customer
    on update cascade on delete restrict,
    constraint fk_th_new_tier_id
    foreign key (new_tier_id) references customer_service.tiers
    on update cascade on delete restrict,
    constraint fk_th_previous_tier_id
    foreign key (previous_tier_id) references customer_service.tiers
    );

alter table customer_service.tiers_history
    owner to postgres;



INSERT INTO customer_service.tiers (name, required_orders, discount_rate)
VALUES ('Regular', 0, 0);

INSERT INTO customer_service.tiers (name, required_orders, discount_rate)
VALUES ('Gold', 10, 10);

INSERT INTO customer_service.tiers (name, required_orders, discount_rate)
VALUES ('Platinum', 20, 20);



CREATE SCHEMA IF NOT EXISTS order_service
    AUTHORIZATION postgres;



create table if not exists order_service.products
(
    id     bigint generated always as identity,
    name   varchar(75)      not null,
    price  double precision not null,
    stocks integer          not null,
    primary key (id),
    constraint iu_name
    unique (name)
    );

alter table order_service.products
    owner to postgres;

create table if not exists order_service.orders
(
    id             bigint generated always as identity,
    customer_id    integer             not null,
    order_date     time with time zone not null,
    total_amount   double precision    not null,
    customer_email varchar(255),
    primary key (id)
    );

alter table order_service.orders
    owner to postgres;

create table if not exists order_service.payments
(
    id             bigint generated always as identity,
    order_id       integer             not null,
    payment_method varchar             not null,
    amount         double precision    not null,
    payment_date   time with time zone not null,
    primary key (id),
    constraint fk_p_order_id
    foreign key (order_id) references order_service.orders
    );

alter table order_service.payments
    owner to postgres;

create table if not exists order_service.order_products
(
    id            bigint generated always as identity,
    order_id      integer          not null,
    product_id    integer          not null,
    quantity      integer          not null,
    price         double precision not null,
    discount_rate integer,
    total_price   double precision,
    primary key (id),
    constraint fk_op_order_id
    foreign key (order_id) references order_service.orders,
    constraint fk_op_products_id
    foreign key (product_id) references order_service.products
    );

alter table order_service.order_products
    owner to postgres;

