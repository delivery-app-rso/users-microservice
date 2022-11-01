# RSO: Image metadata microservice

## How to run

1. Create a .env copy from .env.example and fill it.`DB_HOST` and `DB_NAME` properties must match the ones in api/src/main/resources/config.yaml (eq. `jdbc:postgresql://[DB_HOST]:5432/[DB_NAME]`). Same goes for `POSTGRES_USER` and `LOCAL_PORT` property defines on which port the api will be accessible.

2. Run `docker-compose --env-file [created .env file name] up --build -d`

3. Microservice is now accessible on http://127.0.0.1:[defined PORT]/v1/
