#!/bin/sh
set -e

ENDPOINT="${DYNAMODB_ENDPOINT:-http://dynamodb:8000}"
REGION="${AWS_DEFAULT_REGION:-us-east-1}"
TABLE="${DYNAMODB_TABLE:-kitchen_orders}"

echo "Waiting for DynamoDB Local at $ENDPOINT ..."

# espera o dynamodb responder
until aws dynamodb list-tables --endpoint-url "$ENDPOINT" --region "$REGION" >/dev/null 2>&1; do
  sleep 1
done

echo "DynamoDB is up. Checking table: $TABLE"

# se tabela existe, não faz nada
if aws dynamodb describe-table --table-name "$TABLE" --endpoint-url "$ENDPOINT" --region "$REGION" >/dev/null 2>&1; then
  echo "Table $TABLE already exists. Skipping create."
  exit 0
fi

echo "Creating table $TABLE ..."

aws dynamodb create-table \
  --table-name "$TABLE" \
  --attribute-definitions \
    AttributeName=orderId,AttributeType=N \
    AttributeName=status,AttributeType=S \
    AttributeName=createdAt,AttributeType=S \
  --key-schema AttributeName=orderId,KeyType=HASH \
  --global-secondary-indexes \
    "IndexName=gsi_status_createdAt,KeySchema=[{AttributeName=status,KeyType=HASH},{AttributeName=createdAt,KeyType=RANGE}],Projection={ProjectionType=ALL}" \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT" \
  --region "$REGION"

echo "Done. Table $TABLE created."