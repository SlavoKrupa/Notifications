﻿SET search_path TO NOTIFICATIONS;
LISTEN "SIMPLE_NOTIFY_CHANNEL";
INSERT INTO SIMPLE_NOTIFICATIONS(message) VALUES ('a');
INSERT INTO SIMPLE_NOTIFICATIONS(message) VALUES ('a');
INSERT INTO SIMPLE_NOTIFICATIONS(message) VALUES ('a');
INSERT INTO SIMPLE_NOTIFICATIONS(message) VALUES ('a');
INSERT INTO SIMPLE_NOTIFICATIONS(message) VALUES ('a');

