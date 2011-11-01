CREATE TABLE applications (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  url VARCHAR(256) NOT NULL,
  version INTEGER UNSIGNED NOT NULL DEFAULT 0,
  creation_date DATETIME NOT NULL DEFAULT 0,
  last_updated_date DATETIME NOT NULL DEFAULT 0,
  UNIQUE KEY (name)
) ENGINE = InnoDB;

CREATE TABLE application_keys (
  application_id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  security_key CHAR(64) NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT 0
) ENGINE = InnoDB;

CREATE TABLE application_attributes (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  application_id INTEGER UNSIGNED NOT NULL,
  name VARCHAR(128) NOT NULL,
  value TEXT NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT 0,
  FOREIGN KEY (application_id) REFERENCES applications (id)
) ENGINE = InnoDB;

CREATE TABLE players (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  application_id INTEGER UNSIGNED NOT NULL,
  name VARCHAR(128) NOT NULL,
  version INTEGER UNSIGNED NOT NULL DEFAULT 0,
  creation_date DATETIME NOT NULL DEFAULT 0,
  last_updated_date DATETIME NOT NULL DEFAULT 0,
  UNIQUE KEY (application_id, name),
  FOREIGN KEY (application_id) REFERENCES applications (id)
) ENGINE = InnoDB;

CREATE TABLE player_attributes (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  player_id INTEGER UNSIGNED NOT NULL,
  name VARCHAR(128) NOT NULL,
  value TEXT NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT 0,
  FOREIGN KEY (player_id) REFERENCES players (id)
) ENGINE = InnoDB;

CREATE TABLE rules (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  description TEXT NOT NULL,
  version INTEGER UNSIGNED NOT NULL DEFAULT 0,
  creation_date DATETIME NOT NULL DEFAULT 0,
  last_updated_date DATETIME NOT NULL DEFAULT 0
) ENGINE = InnoDB;

CREATE TABLE games (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  application_id INTEGER UNSIGNED NOT NULL,
  rule_id INTEGER UNSIGNED NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT 0,
  FOREIGN KEY (application_id) REFERENCES applications (id),
  FOREIGN KEY (rule_id) REFERENCES rules (id)
) ENGINE = InnoDB;

CREATE TABLE game_players (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  game_id INTEGER UNSIGNED NOT NULL,
  player_id INTEGER UNSIGNED NOT NULL,
  FOREIGN KEY (game_id) REFERENCES games (id),
  FOREIGN KEY (player_id) REFERENCES players (id)
) ENGINE = InnoDB;

CREATE TABLE plays (
  game_player_id INTEGER UNSIGNED NOT NULL,
  play VARCHAR(255) NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT 0,
  FOREIGN KEY (game_player_id) REFERENCES game_players (id)
) ENGINE = InnoDB;




DELIMITER ;;

CREATE TRIGGER applications_bi_trig
BEFORE INSERT ON applications FOR EACH ROW BEGIN
SET NEW.version = 0;
SET NEW.creation_date = NOW();
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER applications_bu_trig
BEFORE UPDATE ON applications FOR EACH ROW BEGIN
SET NEW.version = OLD.version + 1;
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER application_keys_bi_trig
BEFORE INSERT ON application_keys FOR EACH ROW BEGIN
SET NEW.creation_date = NOW();
END ;;

CREATE TRIGGER application_attributes_bi_trig
BEFORE INSERT ON application_attributes FOR EACH ROW BEGIN
SET NEW.creation_date = NOW();
END ;;

CREATE TRIGGER players_bi_trig
BEFORE INSERT ON players FOR EACH ROW BEGIN
SET NEW.version = 0;
SET NEW.creation_date = NOW();
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER players_bu_trig
BEFORE UPDATE ON players FOR EACH ROW BEGIN
SET NEW.version = OLD.version + 1;
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER player_attributes_bi_trig
BEFORE INSERT ON player_attributes FOR EACH ROW BEGIN
SET NEW.creation_date = NOW();
END ;;

CREATE TRIGGER rules_bi_trig
BEFORE INSERT ON rules FOR EACH ROW BEGIN
SET NEW.version = 0;
SET NEW.creation_date = NOW();
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER rules_bu_trig
BEFORE UPDATE ON rules FOR EACH ROW BEGIN
SET NEW.version = OLD.version + 1;
SET NEW.last_updated_date = NOW();
END ;;

CREATE TRIGGER games_bi_trig
BEFORE INSERT ON games FOR EACH ROW BEGIN
SET NEW.creation_date = NOW();
END ;;

CREATE TRIGGER plays_bi_trig
BEFORE INSERT ON plays FOR EACH ROW BEGIN
SET NEW.creation_date = NOW();
END ;;

DELIMITER ;
