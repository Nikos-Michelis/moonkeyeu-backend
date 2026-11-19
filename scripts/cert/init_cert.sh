#!/bin/sh
set -e

CERT="/etc/letsencrypt/live/${CERTBOT_DOMAIN_BACKEND}/fullchain.pem"
# uid/gid of the unprivileged nginx user, which needs read access to the private key
NGINX_UID=101

if [ -f "$CERT" ]; then
  echo "[certbot-init] Certificate for ${CERTBOT_DOMAIN_BACKEND} exists, skipping"
else
  echo "[certbot-init] No certificate found, requesting one for ${CERTBOT_DOMAIN_BACKEND}"
  certbot certonly --standalone -n --agree-tos \
    --email "${CERTBOT_EMAIL}" \
    -d "${CERTBOT_DOMAIN_BACKEND}"
fi

chown -R "${NGINX_UID}:${NGINX_UID}" /etc/letsencrypt/live /etc/letsencrypt/archive
