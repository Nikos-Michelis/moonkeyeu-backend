# Makefile
.PHONY: ping deploy-prod deploy-staging lint check-prod

ping:
    ansible all -m ping -i inventories/production/hosts.yml

deploy-prod:
    ansible-playbook playbooks/site.yml -i inventories/production/hosts.yml

deploy-staging:
    ansible-playbook playbooks/site.yml -i inventories/staging/hosts.yml

lint:
    ansible-lint playbooks/ roles/

check-prod:
    ansible-playbook playbooks/site.yml -i inventories/production/hosts.yml --check --diff

ansible-playbook playbook/provision.yml --syntax-check

create-vault:
	ansible-vault create inventories/production/group_vars/vault.yml

encrypt-vault:
	ansible-vault encrypt inventories/production/group_vars/secrets.yml

edit-vault:
	ansible-vault edit --vault-password-file pass_file/ansible-vault.pass group_vars/vault.yml

decrypt-vault:
	ansible-vault decrypt inventories/production/group_vars/vault.yml

view-vault:
	ansible-vault view inventories/production/group_vars/vault.yml