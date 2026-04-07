# COP DMS Project

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Running the Application

```bash
docker compose up --build
```

This starts three services:
- **MySQL** on port `3306`
- **Backend** (Spring Boot) on port `8080`
- **Frontend** (Next.js) on port `3000`

## MySQL Root Password

The MySQL container uses `MYSQL_RANDOM_ROOT_PASSWORD`, so a random root password is generated on first startup. To find it:

**macOS/Linux:**
```bash
docker compose logs mysql | grep "GENERATED ROOT PASSWORD"
```

**Windows (PowerShell):**
```powershell
docker compose logs mysql | Select-String "GENERATED ROOT PASSWORD"
```

## Stopping the Application

```bash
docker compose down
```
