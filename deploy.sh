#!/bin/bash
# =============================================================================
# Other Tales Backend - Google Cloud Run Deployment Script
#
# Usage:
#   export GCP_PROJECT_ID="other-tales-backend"
#   export SUPABASE_DB_PASSWORD="your-password"
#   ./deploy.sh
# =============================================================================

set -euo pipefail

# -----------------------------------------------------------------------------
# Configuration
# -----------------------------------------------------------------------------
PROJECT_ID="${GCP_PROJECT_ID:?ERROR: Set GCP_PROJECT_ID environment variable}"
REGION="${GCP_REGION:-us-central1}"
SERVICE_NAME="other-tales-api"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Supabase Configuration
SUPABASE_PROJECT_REF="${SUPABASE_PROJECT_REF:-gsslwdruiqtlztupekcd}"
SUPABASE_DB_HOST="${SUPABASE_DB_HOST:-db.${SUPABASE_PROJECT_REF}.supabase.co}"
SUPABASE_DB_PORT="${SUPABASE_DB_PORT:-5432}"
SUPABASE_DB_NAME="${SUPABASE_DB_NAME:-postgres}"
SUPABASE_DB_USER="${SUPABASE_DB_USER:-postgres}"
SUPABASE_DB_PASSWORD="${SUPABASE_DB_PASSWORD:?ERROR: Set SUPABASE_DB_PASSWORD environment variable}"

# Derived URLs
DATASOURCE_URL="jdbc:postgresql://${SUPABASE_DB_HOST}:${SUPABASE_DB_PORT}/${SUPABASE_DB_NAME}?sslmode=require"
JWKS_URI="https://${SUPABASE_PROJECT_REF}.supabase.co/auth/v1/.well-known/jwks.json"
JWT_ISSUER="https://${SUPABASE_PROJECT_REF}.supabase.co/auth/v1"

echo "=========================================="
echo " Other Tales API → Cloud Run"
echo "=========================================="
echo " Project:  ${PROJECT_ID}"
echo " Region:   ${REGION}"
echo " Service:  ${SERVICE_NAME}"
echo " DB Host:  ${SUPABASE_DB_HOST}"
echo " JWKS URI: ${JWKS_URI}"
echo "=========================================="
echo ""

# -----------------------------------------------------------------------------
# Step 1: Build container image with Cloud Build
# -----------------------------------------------------------------------------
echo "[1/3] Building container image..."
echo ""

gcloud builds submit \
    --project="${PROJECT_ID}" \
    --tag="${IMAGE_NAME}:latest" \
    --timeout=15m \
    --quiet \
    .

# -----------------------------------------------------------------------------
# Step 2: Deploy to Cloud Run
# -----------------------------------------------------------------------------
echo ""
echo "[2/3] Deploying to Cloud Run..."
echo ""

gcloud run deploy "${SERVICE_NAME}" \
    --project="${PROJECT_ID}" \
    --region="${REGION}" \
    --image="${IMAGE_NAME}:latest" \
    --platform=managed \
    --allow-unauthenticated \
    --port=8080 \
    --memory=512Mi \
    --cpu=1 \
    --min-instances=0 \
    --max-instances=10 \
    --timeout=300 \
    --concurrency=80 \
    --cpu-throttling \
    --set-env-vars="^##^SPRING_DATASOURCE_URL=${DATASOURCE_URL}##SPRING_DATASOURCE_USERNAME=${SUPABASE_DB_USER}##SPRING_DATASOURCE_PASSWORD=${SUPABASE_DB_PASSWORD}##SUPABASE_JWKS_URI=${JWKS_URI}##JWT_ISSUER=${JWT_ISSUER}##JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+UseStringDeduplication" \
    --clear-env-vars="SUPABASE_JWT_SECRET"

# -----------------------------------------------------------------------------
# Step 3: Verify deployment
# -----------------------------------------------------------------------------
echo ""
echo "[3/3] Verifying deployment..."
echo ""

SERVICE_URL=$(gcloud run services describe "${SERVICE_NAME}" \
    --project="${PROJECT_ID}" \
    --region="${REGION}" \
    --format="value(status.url)")

echo "=========================================="
echo " Deployment complete!"
echo "=========================================="
echo " Service URL:  ${SERVICE_URL}"
echo " Health Check: ${SERVICE_URL}/api/health"
echo " Swagger UI:   ${SERVICE_URL}/swagger-ui.html"
echo "=========================================="
echo ""
echo " Quick test:"
echo "   curl -s ${SERVICE_URL}/api/health | jq ."
echo ""
