CREATE SCHEMA IF NOT EXISTS customer_service
    AUTHORIZATION postgres;


CREATE TABLE IF NOT EXISTS customer_service.tiers
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    required_orders integer NOT NULL,
    discount_rate double precision NOT NULL,
    CONSTRAINT tiers_pkey1 PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS customer_service.tiers
    OWNER to postgres;



CREATE TABLE IF NOT EXISTS customer_service.customer
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    username character varying(100) COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    tier_id integer NOT NULL,
    order_count integer,
    created_at bigint NOT NULL,
    updated_at bigint,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    enabled boolean NOT NULL,
    status smallint NOT NULL,
    fail_login_count integer NOT NULL,
    CONSTRAINT tiers_pkey PRIMARY KEY (id),
    CONSTRAINT fk_c_tier_id FOREIGN KEY (tier_id)
        REFERENCES customer_service.tiers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        NOT VALID
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS customer_service.customer
    OWNER to postgres;






-- Table: customer_service.tiers_history

-- DROP TABLE IF EXISTS customer_service.tiers_history;

CREATE TABLE IF NOT EXISTS customer_service.tiers_history
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    customer_id integer NOT NULL,
    previous_tier_id integer NOT NULL,
    new_tier_id integer NOT NULL,
    change_date time with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tiers_history_pkey PRIMARY KEY (id),
    CONSTRAINT fk_th_customer_id FOREIGN KEY (customer_id)
        REFERENCES customer_service.customer (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        NOT VALID,
    CONSTRAINT fk_th_new_tier_id FOREIGN KEY (new_tier_id)
        REFERENCES customer_service.tiers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        NOT VALID,
    CONSTRAINT fk_th_previous_tier_id FOREIGN KEY (previous_tier_id)
        REFERENCES customer_service.tiers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS customer_service.tiers_history
    OWNER to postgres;



CREATE SCHEMA IF NOT EXISTS order_service
    AUTHORIZATION postgres;

-- Table: order_service.products

-- DROP TABLE IF EXISTS order_service.products;

CREATE TABLE IF NOT EXISTS order_service.products
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    name character varying(75) COLLATE pg_catalog."default" NOT NULL,
    price double precision NOT NULL,
    stocks integer NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS order_service.products
    OWNER to postgres;


CREATE TABLE IF NOT EXISTS order_service.orders
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    customer_id integer NOT NULL,
    order_date time with time zone NOT NULL,
    total_amount double precision NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS order_service.orders
    OWNER to postgres;


CREATE TABLE IF NOT EXISTS order_service.order_products
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    order_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL,
    price double precision NOT NULL,

    CONSTRAINT order_products_pkey PRIMARY KEY (id),
    CONSTRAINT fk_op_order_id FOREIGN KEY (order_id)
        REFERENCES order_service.orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT fk_op_products_id FOREIGN KEY (product_id)
        REFERENCES order_service.products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;



-- Table: order_service.payments

-- DROP TABLE IF EXISTS order_service.payments;

CREATE TABLE IF NOT EXISTS order_service.payments
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    order_id integer NOT NULL,
    payment_method character varying COLLATE pg_catalog."default" NOT NULL,
    amount double precision NOT NULL,
    payment_date time with time zone NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id),
    CONSTRAINT fk_p_order_id FOREIGN KEY (order_id)
        REFERENCES order_service.orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS order_service.payments
    OWNER to postgres;
