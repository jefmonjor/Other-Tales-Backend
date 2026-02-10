#!/bin/bash
# =============================================================================
# Other Tales Backend - Script de Despliegue a Google Cloud Run
# =============================================================================

set -euo pipefail

# 1. Configuración y Variables
# -----------------------------------------------------------------------------
# Asegúrate de tener estas variables exportadas antes de ejecutar, o defínelas aquí por defecto.
PROJECT_ID="${GCP_PROJECT_ID:?ERROR: Falta la variable GCP_PROJECT_ID}"
DB_PASSWORD="${SUPABASE_DB_PASSWORD:?ERROR: Falta la variable SUPABASE_DB_PASSWORD}"

REGION="${GCP_REGION:-us-central1}"
SERVICE_NAME="other-tales-api"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Configuración de Supabase
SUPABASE_PROJECT_REF="gsslwdruiqtlztupekcd" # Tu ID de proyecto fijo
SUPABASE_DB_HOST="db.${SUPABASE_PROJECT_REF}.supabase.co"
SUPABASE_DB_PORT="5432"
SUPABASE_DB_NAME="postgres"

# Construcción de URLs
JDBC_URL="jdbc:postgresql://${SUPABASE_DB_HOST}:${SUPABASE_DB_PORT}/${SUPABASE_DB_NAME}?sslmode=require"
JWKS_URI="https://${SUPABASE_PROJECT_REF}.supabase.co/auth/v1/.well-known/jwks.json"
JWT_ISSUER="https://${SUPABASE_PROJECT_REF}.supabase.co/auth/v1"

echo "🚀 Iniciando despliegue para el proyecto: $PROJECT_ID"
echo "📍 Región: $REGION"
echo "🔧 Servicio: $SERVICE_NAME"

# 2. Construir y subir la imagen (Cloud Build)
# -----------------------------------------------------------------------------
echo "📦 Construyendo imagen con Cloud Build..."
gcloud builds submit --tag "$IMAGE_NAME" .

# 3. Desplegar en Cloud Run
# -----------------------------------------------------------------------------
echo "🚀 Desplegando en Cloud Run..."

gcloud run deploy "$SERVICE_NAME" \
  --image "$IMAGE_NAME" \
  --region "$REGION" \
  --platform "managed" \
  --allow-unauthenticated \
  --memory "1Gi" \
  --cpu "1" \
  --concurrency 80 \
  --remove-env-vars "SUPABASE_JWT_SECRET" \
  --set-env-vars "SPRING_DATASOURCE_URL=$JDBC_URL" \
  --set-env-vars "SPRING_DATASOURCE_USERNAME=postgres" \
  --set-env-vars "SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD" \
  --set-env-vars "SUPABASE_JWKS_URI=$JWKS_URI" \
  --set-env-vars "JWT_ISSUER=$JWT_ISSUER" \
  --set-env-vars "JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+UseStringDeduplication"

echo "✅ ¡Despliegue completado con éxito!"
