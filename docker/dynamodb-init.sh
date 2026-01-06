#!/bin/sh
set -e

ENDPOINT="${DYNAMODB_ENDPOINT:-http://dynamodb:8000}"
TABLE="${DYNAMODB_TABLE:-kitchen_orders}"
INDEX="${DYNAMODB_STATUS_INDEX:-gsi_status_createdAt}"

echo "Waiting for DynamoDB Local at ${ENDPOINT} ..."
until aws dynamodb list-tables --endpoint-url "${ENDPOINT}" >/dev/null 2>&1; do
  sleep 2
done

echo "DynamoDB Local is up. Ensuring table '${TABLE}' exists..."

# se a tabela já existir, sai com sucesso
if aws dynamodb describe-table \
  --table-name "${TABLE}" \
  --endpoint-url "${ENDPOINT}" >/dev/null 2>&1; then
  echo "Table '${TABLE}' already exists. Skipping creation."
  exit 0
fi

echo "Creating table '${TABLE}' with GSI '${INDEX}'..."

aws dynamodb create-table \
  --table-name "${TABLE}" \
  --attribute-definitions \
      AttributeName=orderId,AttributeType=N \
      AttributeName=status,AttributeType=S \
      AttributeName=createdAt,AttributeType=S \
  --key-schema \
      AttributeName=orderId,KeyType=HASH \
  --global-secondary-indexes "[
    {
      \"IndexName\": \"${INDEX}\",
      \"KeySchema\": [
        {\"AttributeName\": \"status\", \"KeyType\": \"HASH\"},
        {\"AttributeName\": \"createdAt\", \"KeyType\": \"RANGE\"}
      ],
      \"Projection\": {\"ProjectionType\": \"ALL\"},
      \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 5, \"WriteCapacityUnits\": 5}
    }
  ]" \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url "${ENDPOINT}"

echo "Waiting for table '${TABLE}' to become ACTIVE..."
aws dynamodb wait table-exists \
  --table-name "${TABLE}" \
  --endpoint-url "${ENDPOINT}"

echo "✅ Table '${TABLE}' created."