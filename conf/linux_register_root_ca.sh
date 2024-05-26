#!/bin/bash
# Backup existing certificate
cp /usr/local/share/ca-certificates/localhost_root_ca.crt /usr/local/share/ca-certificates/localhost_root_ca.crt.bak
# Copy new certificate
sudo cp localhost_root_ca.crt /usr/local/share/ca-certificates/
# Update certificates
sudo update-ca-certificates
