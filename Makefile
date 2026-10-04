# Makefile
# SSL certificate management through the certbot container
.PHONY: help cert-obtain cert-renew cert-renew-dry-run cert-renew-force cert-list nginx-reload renew

-include .env

COMPOSE_FILE ?= docker-compose.prod.yml
COMPOSE      := docker compose -f $(COMPOSE_FILE)
CERTBOT      := $(COMPOSE) run --rm --no-deps certbot

help:
	@echo "cert-obtain         Obtain a new certificate (certonly --webroot)"
	@echo "cert-renew          Renew certificates close to expiry"
	@echo "cert-renew-dry-run  Test renewal without saving certificates"
	@echo "cert-renew-force    Force renewal of all certificates"
	@echo "cert-list           List managed certificates"
	@echo "nginx-reload        Reload the nginx container"
	@echo "renew               Run renew_cert.sh (renew + reload nginx)"

cert-obtain:
	$(CERTBOT) certonly --webroot -w /var/www/certbot --keep-until-expiring --email $(CERTBOT_EMAIL) -d $(CERTBOT_DOMAIN_BACKEND) --agree-tos

cert-renew:
	$(CERTBOT) renew

cert-renew-dry-run:
	$(CERTBOT) renew --dry-run

cert-renew-force:
	$(CERTBOT) renew --force-renewal

cert-list:
	$(CERTBOT) certificates

nginx-reload:
	$(COMPOSE) exec -T nginx nginx -s reload

renew:
	./renew_cert.sh
