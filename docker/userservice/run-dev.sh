#!/usr/bin/env bash
set -euo pipefail

# Helper to start userservice with the dev profile using a local .env file.
# Usage: bash run-dev.sh  (or chmod +x run-dev.sh && ./run-dev.sh)

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

if [ -f .env ]; then
  echo "Sourcing .env"
  # Export environment variables from .env for child processes
  # shellcheck disable=SC1091
  set -a
  source .env
  set +a
else
  echo "Warning: .env not found in $ROOT_DIR — ensure env vars are set: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD"
fi

echo "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL-}" >&2 || true

echo "Starting userservice with profile 'dev'"
./mvnw -Dspring-boot.run.profiles=dev -DskipTests spring-boot:run
