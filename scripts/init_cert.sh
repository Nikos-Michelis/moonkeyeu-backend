#!/bin/sh
set -e

CERT="/etc/letsencrypt/live/${CERTBOT_DOMAIN_BACKEND}/fullchain.pem"

if [ -f "$CERT" ]; then
  echo "[certbot-init] Certificate for ${CERTBOT_DOMAIN_BACKEND} exists, skipping"
  exit 0
fi

echo "[certbot-init] No certificate found, requesting one for ${CERTBOT_DOMAIN_BACKEND}"
certbot certonly --standalone -n --agree-tos \
  --email "${CERTBOT_EMAIL}" \
  -d "${CERTBOT_DOMAIN_BACKEND}"