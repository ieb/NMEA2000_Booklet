#!/bin/bash -e
mvn clean install
scp target/Update_NMEA2000_uk.co.tfd.kindle.nmea2000_*_install.bin root@192.168.15.244:/mnt/us/mrpackages
ssh root@192.168.15.244 /mnt/us/extensions/MRInstaller/bin/mrinstaller.sh launch_installer
