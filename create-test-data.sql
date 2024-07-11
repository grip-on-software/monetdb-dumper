CREATE SCHEMA "gros";
SET SCHEMA "gros";

CREATE TABLE "gros"."test" (
    "flag" BOOLEAN NOT NULL,
    "count" BIGINT NOT NULL,
    "story_points" DECIMAL(5,2) NULL,
    "number" DOUBLE NOT NULL,
    "ieee" FLOAT NOT NULL,
    "attachments" INT NOT NULL,
    "duedate" DATE NULL,
    "reservation" TIME NULL,
    "start_date" TIMESTAMP NOT NULL,
    "key" VARCHAR(50) NOT NULL,
    "encryption" INTEGER NOT NULL DEFAULT 0
);

INSERT INTO "gros"."test" VALUES
(false, 12345, 5.25, 1.01, 2.22, 3, '2024-07-10', '12:00:00', '2024-07-10 12:34:56', 'TEST-42', 0),
(true, 6789, NULL, 2.02, 3.33, 4, NULL, NULL, '2024-07-10 13:57:00', 'TEST-43', 1);
