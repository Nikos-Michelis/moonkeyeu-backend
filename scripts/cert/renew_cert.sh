#!/bin/bash

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"
NGINX_SERVICE="nginx"
CERTBOT_SERVICE="certbot"

cd "$(dirname "$(readlink -f "$0")")/.." || exit 1

compose() {
  docker compose -f "$COMPOSE_FILE" "$@"
}

echo "[$(date '+%Y-%m-%d %H:%M:%S')] Starting certificate renewal"

compose run --rm --no-deps "$CERTBOT_SERVICE"
certbot_status=$?

reload_nginx() {
  if compose exec -T "$NGINX_SERVICE" nginx -s reload; then
    echo "Nginx container reloaded successfully."
    return 0
  fi

  echo "Nginx reload failed, restarting the nginx container..."
  if compose restart "$NGINX_SERVICE"; then
    echo "Nginx container restarted successfully."
    return 0
  fi

  echo "Nginx container restart failed."
  return 1
}

if [ "$certbot_status" -eq 0 ]; then
  echo "Certbot renewal successful, reloading nginx..."
  reload_nginx
else
  echo "Certbot renewal failed (exit code $certbot_status), nginx reload skipped."
  exit "$certbot_status"
fi
