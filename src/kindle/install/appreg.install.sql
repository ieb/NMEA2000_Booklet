INSERT OR IGNORE INTO "handlerIds" VALUES('uk.co.tfd.kindle.nmea2000');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','lipcId','uk.co.tfd.kindle.nmea2000');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','jar','/opt/amazon/ebook/booklet/nmea2000_booklet.jar');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','maxUnloadTime','45');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','maxGoTime','60');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','maxPauseTime','60');

INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','default-chrome-style','NH');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','unloadPolicy','unloadOnPause');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','extend-start','Y');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','searchbar-mode','transient');
INSERT OR IGNORE INTO "properties" VALUES('uk.co.tfd.kindle.nmea2000','supportedOrientation','U');

INSERT OR IGNORE INTO "mimetypes" VALUES('nmea2000','MT:image/x.nmea2000');
INSERT OR IGNORE INTO "extenstions" VALUES('nmea2000','MT:image/x.nmea2000');
INSERT OR IGNORE INTO "properties" VALUES('archive.displaytags.mimetypes','image/x.nmea2000','NMEA2000');
INSERT OR IGNORE INTO "associations" VALUES('com.lab126.generic.extractor','extractor','GL:*.nmea2000','true');
INSERT OR IGNORE INTO "associations" VALUES('uk.co.tfd.kindle.nmea2000','application','MT:image/x.nmea2000','true');
