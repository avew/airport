CREATE TABLE t_airport
(
    id                 varchar(37)  NOT NULL,
    city               varchar(255) not null,

    created_by         varchar(50)  NOT NULL,
    created_date       timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   varchar(50)           DEFAULT NULL:: character varying,
    last_modified_date timestamp(0)          DEFAULT NULL:: timestamp without time zone,
    CONSTRAINT t_airport_pkey PRIMARY KEY (id)
);

