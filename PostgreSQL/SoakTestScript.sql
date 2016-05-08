-- sad story that this doesn't work. The reason is lack of nested transactions :/ so the time is same

CREATE OR REPLACE FUNCTION INSERT_IN_CYCLE(INTEGER, INTEGER) RETURNS VOID AS $$
DECLARE
    loops ALIAS FOR $1;
    delay ALIAS FOR $2;
BEGIN
    FOR i IN 1..loops LOOP
        INSERT INTO NOTIFICATIONS.SIMPLE_NOTIFICATIONS(message) VALUES ('a');
	COMMIT;
	RAISE INFO 'INSERTING DATA @%', timeofday()::TIMESTAMP;
	PERFORM pg_sleep(delay);
    END LOOP;
END;
$$ LANGUAGE 'plpgsql' STRICT;
SELECT INSERT_IN_CYCLE(10,10);