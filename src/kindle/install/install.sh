#!/bin/sh
#
# KUAL Booklet installer
#
# $Id$
#
##

# Pull libOTAUtils for logging & progress handling
[ -f ./libotautils5 ] && source ./libotautils5


otautils_update_progressbar

logmsg "I" "install" "" "installing booklet"
cp -f "HelloWorldBooklet.jar" "/opt/amazon/ebook/booklet/HelloWorldBooklet.jar"

otautils_update_progressbar

logmsg "I" "install" "" "registering booklet"
sqlite3 "/var/local/appreg.db" < "appreg.install.sql"

otautils_update_progressbar

# Enable WhisperTouch on the Voyage (c.f., https://github.com/koreader/koreader/issues/6038#issuecomment-612564693)
# Use the FBInk binary chosen by libOTAUtils
eval $(${FBINK_BIN} -e)
if [ "${deviceName}" = "Voyage" ] ; then
    logmsg "I" "install" "" "enabling whispertouch"
    sqlite3 "/var/local/appreg.db" < "whispertouch.install.sql"
fi

otautils_update_progressbar


logmsg "I" "install" "" "creating application"
touch "/mnt/us/documents/HelloWorld.hello"

otautils_update_progressbar

logmsg "I" "install" "" "cleaning up"
rm -f "HelloWorldBooklet.jar" "appreg.install.sql"

logmsg "I" "install" "" "done"

otautils_update_progressbar

return 0
