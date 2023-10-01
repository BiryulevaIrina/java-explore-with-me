drop table IF EXISTS users, categories, events, requests, compilations, compilation_event cascade;

create TABLE IF NOT EXISTS users (
  id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name  VARCHAR(255)                            NOT NULL,
  email VARCHAR(512)                            NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create TABLE IF NOT EXISTS categories (
  id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name  VARCHAR(50)                             NOT NULL,
  CONSTRAINT pk_categories PRIMARY KEY (id),
  CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

create TABLE IF NOT EXISTS events (
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
    annotation         VARCHAR(2000)                             NOT NULL,
    category_id BIGINT                                           NOT NULL REFERENCES categories (id) ON delete CASCADE,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR(7000),
    event_date         TIMESTAMP WITHOUT TIME ZONE               NOT NULL,
    initiator_id       BIGINT                                    NOT NULL REFERENCES users (id) ON delete CASCADE,
    lat                REAL                                      NOT NULL,
    lon                REAL                                      NOT NULL,
    paid               BOOLEAN                                   NOT NULL,
    participant_limit  INT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(20),
    title              VARCHAR (120)                             NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS requests (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY    NOT NULL,
    created           TIMESTAMP WITHOUT TIME ZONE                NOT NULL,
    event_id          BIGINT                                     NOT NULL REFERENCES events(id) ON delete CASCADE,
    requester_id      BIGINT                                     NOT NULL REFERENCES users (id) ON delete CASCADE,
    status            VARCHAR(20)                                NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS compilations (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY    NOT NULL,
    pinned            BOOLEAN,
    title             VARCHAR (255)                              NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS compilation_event (
    event_id       BIGINT                                        NOT NULL REFERENCES events (id) ON delete CASCADE,
    compilation_id BIGINT                                        NOT NULL REFERENCES compilations (id) ON delete CASCADE,
    PRIMARY KEY (event_id, compilation_id)
);
