#!/bin/bash
# =============================================================================
# Other Tales Backend - Google Cloud Run Deployment Script
# =============================================================================

set -e

# -----------------------------------------------------------------------------
# Configuration (EDIT THESE VALUES)
# -----------------------------------------------------------------------------
PROJECT_ID="${GCP_PROJECT_ID:-your-gcp-project-id}"
REGION="${GCP_REGION:-us-central1}"
SERVICE_NAME="other-tales-api"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Supabase Configuration (from environment or secrets)
SUPABASE_DB_HOST="${SUPABASE_DB_HOST:-db.gsslwdruiqtlztupekcd.supabase.co}"
SUPABASE_DB_NAME="${SUPABASE_DB_NAME:-postgres}"
SUPABASE_DB_USER="${SUPABASE_DB_USER:-postgres}"
SUPABASE_DB_PASSWORD="${SUPABASE_DB_PASSWORD}"
SUPABASE_PROJECT_REF="${SUPABASE_PROJECT_REF:-gsslwdruiqtlztupekcd}"

# -----------------------------------------------------------------------------
# Validation
# -----------------------------------------------------------------------------
if [ -z "$SUPABASE_DB_PASSWORD" ]; then
    echo "ERROR: SUPABASE_DB_PASSWORD environment variable is required"
    echo "Usage: SUPABASE_DB_PASSWORD='your-password' ./deploy.sh"
    exit 1
fi

if [ "$PROJECT_ID" == "your-gcp-project-id" ]; then
    echo "ERROR: Set GCP_PROJECT_ID environment variable"
    echo "Usage: GCP_PROJECT_ID='my-project' SUPABASE_DB_PASSWORD='xxx' ./deploy.sh"
    exit 1
fi

echo "=========================================="
echo "Deploying Other Tales Backend to Cloud Run"
echo "=========================================="
echo "Project:  ${PROJECT_ID}"
echo "Region:   ${REGION}"
echo "Service:  ${SERVICE_NAME}"
echo "=========================================="

# -----------------------------------------------------------------------------
# Step 1: Build the container image using Cloud Build
# -----------------------------------------------------------------------------
echo ""
echo "[1/3] Building container image with Cloud Build..."
echo ""

gcloud builds submit \
    --project="${PROJECT_ID}" \
    --tag="${IMAGE_NAME}:latest" \
    --timeout=15m \
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
    --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
    --set-env-vars="SPRING_DATASOURCE_URL=jdbc:postgresql://${SUPABASE_DB_HOST}:5432/${SUPABASE_DB_NAME}?sslmode=require" \
    --set-env-vars="SPRING_DATASOURCE_USERNAME=${SUPABASE_DB_USER}" \
    --set-env-vars="SPRING_DATASOURCE_PASSWORD=${SUPABASE_DB_PASSWORD}" \
    --set-env-vars="SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://${SUPABASE_PROJECT_REF}.supabase.co/auth/v1/.well-known/jwks.json"

# -----------------------------------------------------------------------------
# Step 3: Get the service URL
# -----------------------------------------------------------------------------
echo ""
echo "[3/3] Deployment complete!"
echo ""

SERVICE_URL=$(gcloud run services describe "${SERVICE_NAME}" \
    --project="${PROJECT_ID}" \
    --region="${REGION}" \
    --format="value(status.url)")

echo "=========================================="
echo "Service URL: ${SERVICE_URL}"
echo "Health Check: ${SERVICE_URL}/api/health"
echo "=========================================="
echo ""
echo "Test with:"
echo "  curl ${SERVICE_URL}/api/health"
echo ""
