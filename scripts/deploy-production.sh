#!/usr/bin/env bash
set -euo pipefail
umask 077

cd /home/ubuntu/buildlog
exec 9>/tmp/buildlog-deploy.lock
flock -n 9 || { echo 'Another deployment is already running'; exit 1; }

docker compose build backend web
docker compose up -d db
docker compose stop backend

schema_count=$(docker compose exec -T db sh -c \
  'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -N -B -uroot "$MYSQL_DATABASE" -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = '\''career_positions'\''"')
if [[ "$schema_count" == 0 ]]; then
  mkdir -p ../buildlog-db-backups
  backup_file="../buildlog-db-backups/before-career-positions-$(date -u +%Y%m%dT%H%M%SZ).sql"
  docker compose exec -T db sh -c \
    'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysqldump -uroot --single-transaction --routines --triggers "$MYSQL_DATABASE"' > "$backup_file"
  echo "Database backup saved: $backup_file"
fi

docker compose exec -T db sh -c \
  'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot "$MYSQL_DATABASE"' \
  < db/migrations/V20260929__career_positions.sql

docker compose up -d
docker compose ps
