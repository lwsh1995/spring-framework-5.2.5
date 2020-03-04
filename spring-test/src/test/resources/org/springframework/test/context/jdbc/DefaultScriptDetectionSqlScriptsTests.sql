DROP TABLE infoBean IF EXISTS;

CREATE TABLE infoBean (
  name VARCHAR(20) NOT NULL,
  PRIMARY KEY(name)
);

INSERT INTO infoBean VALUES('Dilbert');
INSERT INTO infoBean VALUES('Dogbert');
