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
    CONSTRAINT fk_th_previous_tier_id FOREIGN KEY (new_tier_id)
        REFERENCES customer_service.tiers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
        NOT VALID
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS customer_service.tiers_history
    OWNER to postgres;


INSERT INTO customer_service.tiers(
    name, required_orders, discount_rate)
VALUES ("Regular", ?, ?);