# Career position migration (MySQL 8.4)

The production database is not available in this workspace. Before applying the migration, inspect the live schema and data with the backend stopped:

```sql
SHOW CREATE TABLE careers;
SHOW CREATE TABLE career_roles;
SHOW CREATE TABLE career_activities;
SELECT COUNT(*) FROM careers;
SELECT COUNT(*) FROM career_roles;
SELECT COUNT(*) FROM career_activities;
SELECT id, career_id, title, display_order FROM career_roles ORDER BY career_id, display_order;
SELECT COUNT(*) FROM career_activities a LEFT JOIN career_roles s ON s.id = a.career_role_id WHERE s.id IS NULL;
```

Confirm that `id` and `career_id` are signed `BIGINT`, dates are `DATE`, and `career_activities.career_role_id` points to `career_roles.id`. The SQL migration aborts for missing source tables, orphaned sections or activities, and sections that cannot be assigned to a position. If the live schema differs, adjust the SQL after inspection before deploying.

The production deployment script builds the new images, stops the backend, writes a `mysqldump` backup to `/home/ubuntu/buildlog-db-backups` before the initial migration, runs `V20260929__career_positions.sql`, and only then starts the new backend and web images. This same SQL can be run manually with:

```sh
docker compose exec -T db sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot "$MYSQL_DATABASE"' < db/migrations/V20260929__career_positions.sql
```

The migration adds `career_positions` and one position for every existing career, with dates copied from that career and `title = NULL`. It adds `career_roles.position_id` and links each existing section to its company's default position. The physical `career_roles` table and `career_activities.career_role_id` column remain in place so existing section and activity IDs stay unchanged. `career_roles.career_id` becomes nullable for new sections; old rows retain their original value. The SQL is designed to be rerunnable after a partial DDL failure, but a database backup is still necessary because MySQL DDL commits independently of the data transaction.

After the migration, compare the printed row counts with the backup and require `unlinked_sections = 0` and `mismatched_legacy_sections = 0`. Then check `GET /api/careers` for the migrated company, position, sections, and activities before opening editing to administrators. Do not start the new backend if migration or checks fail.
