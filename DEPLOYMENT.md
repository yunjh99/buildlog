# BuildLog Docker deployment

## Requirements

- Docker Engine
- Docker Compose plugin
- At least 2 GB RAM recommended

## Configuration

Copy the example environment file and replace every `replace_with_...` value.

```bash
cp .env.example .env
```

Generate the JWT secret without committing its output:

```bash
openssl rand -base64 64
```

Keep `JWT_COOKIE_SECURE=false` only while testing over HTTP. Change it to `true` after HTTPS is configured.

## Start

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f backend
```

Open `http://SERVER_PUBLIC_IP`. The admin page is available at `/admin`.

## Update

```bash
git pull
docker compose up -d --build
```

## Database backup

```bash
docker compose exec -T db mysqldump -uroot -p"$DB_ROOT_PASSWORD" buildlog > buildlog-backup.sql
```

The database uses the named volume `mysql-data`. Do not run `docker compose down -v` unless the database should be permanently deleted.
