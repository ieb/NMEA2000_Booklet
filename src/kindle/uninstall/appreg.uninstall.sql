DELETE FROM "handlerIds" WHERE handlerId='uk.co.tfd.kindle.nmea2000';
DELETE FROM "properties" WHERE handlerId='uk.co.tfd.kindle.nmea2000';
DELETE FROM "associations" WHERE handlerId='uk.co.tfd.kindle.nmea2000';

DELETE FROM "mimetypes" WHERE ext='nmea2000';
DELETE FROM "extenstions" WHERE ext='nmea2000';
DELETE FROM "properties" WHERE value='nmea2000';
DELETE FROM "associations" WHERE contentId='GL:*.nmea2000';
