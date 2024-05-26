#!/bin/bash

### Script installs root.cert.pem to certificate trust store of applications using NSS
### (e.g. Firefox, Thunderbird, Chromium)
### Mozilla uses cert8, Chromium and Chrome use cert9

###
### Requirement: apt install libnss3-tools
###


###
### CA file to install (CUSTOMIZE!)
###

certfile="localhost_root_ca.crt"
certname="Root CA"


###
### For cert8 (legacy - DBM)
###

for certDB in $(find ~/ -name "cert8.db")
do
    certdir=$(dirname ${certDB});
    certutil -D -n "${certname}" -d dbm:${certdir}
    certutil -A -n "${certname}" -t "TCP,," -i ${certfile} -v 120 -d dbm:${certdir}
done


###
### For cert9 (SQL)
###

for certDB in $(find ~/ -name "cert9.db")
do
    certdir=$(dirname ${certDB});
    certutil -D -n "${certname}" -d sql:${certdir}
    certutil -A -n "${certname}" -t "TCP,," -i ${certfile} -v 120 -d sql:${certdir}
done
