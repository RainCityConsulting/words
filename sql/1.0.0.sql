CREATE TABLE applications (
  id INTEGER UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  url VARCHAR(256) NOT NULL,
  version INTEGER UNSIGNED NOT NULL DEFAULT 0,
  creation_date DATETIME NOT NULL DEFAULT 0,
  last_updated_date DATETIME NOT NULL DEFAULT 0,
  UNIQUE KEY (name)
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

DELIMITER ;
