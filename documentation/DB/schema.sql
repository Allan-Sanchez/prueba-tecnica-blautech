-- -------------------------------------------------------------------
-- Esquema
-- -------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS prueba-tecnica
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE prueba-tecnica;

-- -------------------------------------------------------------------
-- Tabla: users
-- -------------------------------------------------------------------
CREATE TABLE users (
  id              BIGINT  NOT NULL AUTO_INCREMENT,
  first_name      VARCHAR(100)    NOT NULL,
  last_name       VARCHAR(100)    NOT NULL,
  shipping_address TEXT           NOT NULL,
  email           VARCHAR(255)    NOT NULL,
  birth_date      DATE            NOT NULL,
  password_hash   VARCHAR(255)    NOT NULL, -- bcrypt
  created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: products
-- -------------------------------------------------------------------
CREATE TABLE products (
  id               BIGINT  NOT NULL AUTO_INCREMENT,
  name             VARCHAR(150)    NOT NULL,
  description      TEXT            NOT NULL,
  image_url        VARCHAR(500)    NOT NULL,  -- URL fija
  price_cents      INT     NOT NULL,  -- almacenar en centavos
  currency         CHAR(3)         NOT NULL DEFAULT 'GTQ',
  is_active        TINYINT(1)      NOT NULL DEFAULT 1,
  created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_products_active_created (is_active, created_at)
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: carts
-- Regla: un carrito ACTIVO por usuario.
-- Se implementa con una columna generada active_flag para clave única.
-- -------------------------------------------------------------------
CREATE TABLE carts (
  id              BIGINT  NOT NULL AUTO_INCREMENT,
  user_id         BIGINT  NOT NULL,
  status          ENUM('ACTIVE','CHECKED_OUT') NOT NULL DEFAULT 'ACTIVE',
  created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  -- Columna generada para asegurar un único carrito ACTIVE por usuario
  active_flag     TINYINT(1) GENERATED ALWAYS AS (status = 'ACTIVE') STORED,
  PRIMARY KEY (id),
  KEY fk_carts_user (user_id),
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  -- Garantiza como máximo un carrito ACTIVE (=1) por usuario.
  UNIQUE KEY uk_user_active_cart (user_id, active_flag)
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: cart_items
-- (precio unitario se congela al agregar al carrito)
-- -------------------------------------------------------------------
CREATE TABLE cart_items (
  id                 BIGINT  NOT NULL AUTO_INCREMENT,
  cart_id            BIGINT  NOT NULL,
  product_id         BIGINT  NOT NULL,
  quantity           INT     NOT NULL DEFAULT 1,
  unit_price_cents   INT     NOT NULL,
  line_total_cents   INT     NOT NULL,
  created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cart_product (cart_id, product_id),
  KEY fk_cart_items_cart (cart_id),
  KEY fk_cart_items_product (product_id),
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id)
    REFERENCES carts(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_cart_items_qty CHECK (quantity > 0),
  CONSTRAINT ck_cart_items_amounts CHECK (
    unit_price_cents > 0 AND line_total_cents = unit_price_cents * quantity
  )
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: orders
-- Se crea al hacer checkout y se devuelve el ID de la orden.
-- -------------------------------------------------------------------
CREATE TABLE orders (
  id              BIGINT  NOT NULL AUTO_INCREMENT,
  user_id         BIGINT  NOT NULL,
  cart_id         BIGINT  NOT NULL,
  status          ENUM('CREATED','PAID','CANCELLED') NOT NULL DEFAULT 'CREATED',
  total_cents     INT     NOT NULL,
  currency        CHAR(3)         NOT NULL DEFAULT 'GTQ',
  created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY fk_orders_user (user_id),
  KEY fk_orders_cart (cart_id),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id)
    REFERENCES carts(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: order_items
-- (ítems copiados desde el carrito al momento del checkout)
-- -------------------------------------------------------------------
CREATE TABLE order_items (
  id                 BIGINT  NOT NULL AUTO_INCREMENT,
  order_id           BIGINT  NOT NULL,
  product_id         BIGINT  NOT NULL,
  quantity           INT     NOT NULL,
  unit_price_cents   INT     NOT NULL,
  line_total_cents   INT     NOT NULL,
  PRIMARY KEY (id),
  KEY fk_order_items_order (order_id),
  KEY fk_order_items_product (product_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT ck_order_items_qty CHECK (quantity > 0),
  CONSTRAINT ck_order_items_amounts CHECK (
    unit_price_cents > 0 AND line_total_cents = unit_price_cents * quantity
  )
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: refresh_tokens (JWT refresh por sesión)
-- -------------------------------------------------------------------
CREATE TABLE refresh_tokens (
  id            BIGINT  NOT NULL AUTO_INCREMENT,
  user_id       BIGINT  NOT NULL,
  token_hash    VARCHAR(255)    NOT NULL, -- guarda hash del refresh token (no en claro)
  jti           VARCHAR(64)     NOT NULL, -- identificador único del token
  expires_at    DATETIME        NOT NULL,
  revoked       TINYINT(1)      NOT NULL DEFAULT 0,
  created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refresh_jti (jti),
  KEY fk_refresh_user (user_id),
  KEY idx_refresh_user_active (user_id, revoked, expires_at),
  CONSTRAINT fk_refresh_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Tabla: domain_events (auditoría/eventos de dominio)
-- -------------------------------------------------------------------
CREATE TABLE domain_events (
  id            BIGINT  NOT NULL AUTO_INCREMENT,
  event_type    VARCHAR(100)    NOT NULL,  -- ej. user.registered, order.created
  aggregate     VARCHAR(50)     NOT NULL,  -- users|orders|carts|products
  aggregate_id  BIGINT  NOT NULL,
  payload       JSON            NOT NULL,
  created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_events_type_created (event_type, created_at),
  KEY idx_events_aggregate (aggregate, aggregate_id, created_at)
) ENGINE=InnoDB;

-- -------------------------------------------------------------------
-- Opcional: disparadores para asegurar line_total_cents si prefieres calcular en BD
-- (Si ya calculas en aplicación, puedes omitirlos.)
-- -------------------------------------------------------------------
DELIMITER $$

CREATE TRIGGER bi_cart_items_compute BEFORE INSERT ON cart_items
FOR EACH ROW
BEGIN
  SET NEW.line_total_cents = NEW.unit_price_cents * NEW.quantity;
END$$

CREATE TRIGGER bu_cart_items_compute BEFORE UPDATE ON cart_items
FOR EACH ROW
BEGIN
  SET NEW.line_total_cents = NEW.unit_price_cents * NEW.quantity;
END$$

CREATE TRIGGER bi_order_items_compute BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
  SET NEW.line_total_cents = NEW.unit_price_cents * NEW.quantity;
END$$

CREATE TRIGGER bu_order_items_compute BEFORE UPDATE ON order_items
FOR EACH ROW
BEGIN
  SET NEW.line_total_cents = NEW.unit_price_cents * NEW.quantity;
END$$

DELIMITER ;

-- -------------------------------------------------------------------
-- Buenas prácticas adicionales:
-- - Considera roles/usuarios DB con privilegios mínimos para la app.
-- - Ajusta sql_mode para STRICT_TRANS_TABLES.
-- - Revisa tamaños de INT/INDEX según volumen esperado.
-- -------------------------------------------------------------------
