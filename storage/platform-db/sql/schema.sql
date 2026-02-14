CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       PRIMARY KEY,
    login_id   VARCHAR(20)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(10)  NOT NULL,
    user_tag   VARCHAR(30)  NOT NULL UNIQUE,
    role       VARCHAR(10)  NOT NULL DEFAULT 'USER',
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS login_sessions (
    id               BIGINT       PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    access_jti       VARCHAR(255) NOT NULL COMMENT '매칭된 Access Token의 JTI',
    refresh_jti      VARCHAR(255) NOT NULL COMMENT '매칭된 Refresh Token의 JTI',
    revoked_at       TIMESTAMP    NULL,
    remote_ip        VARCHAR(50),
    ua_raw_value     VARCHAR(500)  COMMENT 'User-Agent 원본 문자열',
    ua_os_name       VARCHAR(50)   COMMENT 'User-Agent OS 이름',
    ua_device_name   VARCHAR(50)   COMMENT 'User-Agent 디바이스 이름',
    ua_agent_name    VARCHAR(50)   COMMENT 'User-Agent 브라우저/앱 이름',
    ua_agent_version VARCHAR(20)   COMMENT 'User-Agent 브라우저/앱 버전',
    ua_device_class  VARCHAR(30)   COMMENT 'User-Agent 디바이스 분류',
    expires_at       TIMESTAMP    NOT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_id (user_id)
);