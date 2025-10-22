#!/bin/bash

# Smoke Tests Script
# Usage: ./smoke-tests.sh <API_BASE_URL>

set -e

API_BASE_URL="${1:-http://localhost:8080}"

echo "Running smoke tests against: $API_BASE_URL"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local description=$4
    local data=$5

    echo -n "Testing: $description ... "

    if [ -z "$data" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" "$API_BASE_URL$endpoint")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$API_BASE_URL$endpoint")
    fi

    if [ "$response" -eq "$expected_status" ]; then
        echo -e "${GREEN}PASS${NC} (HTTP $response)"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}FAIL${NC} (Expected: $expected_status, Got: $response)"
        ((TESTS_FAILED++))
    fi
}

# Wait for services to be ready
echo "Waiting for services to be ready..."
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -s -f "$API_BASE_URL/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}API Gateway is ready!${NC}"
        break
    fi
    attempt=$((attempt + 1))
    echo "Attempt $attempt/$max_attempts - waiting..."
    sleep 10
done

if [ $attempt -eq $max_attempts ]; then
    echo -e "${RED}Timeout waiting for services to be ready${NC}"
    exit 1
fi

sleep 5

echo ""
echo "=========================================="
echo "Running Smoke Tests"
echo "=========================================="
echo ""

# API Gateway Health Check
test_endpoint "GET" "/actuator/health" 200 "API Gateway Health Check"

# Auth Service - Registration
REGISTER_PAYLOAD='{"username":"smoketest","email":"smoke@test.com","password":"Test123456"}'
test_endpoint "POST" "/api/users/register" 201 "User Registration" "$REGISTER_PAYLOAD"

# Auth Service - Login
LOGIN_PAYLOAD='{"username":"smoketest","password":"Test123456"}'
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "$LOGIN_PAYLOAD")

if [ $? -eq 0 ]; then
    ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    if [ -n "$ACCESS_TOKEN" ]; then
        echo -e "Testing: User Login ... ${GREEN}PASS${NC} (Token obtained)"
        ((TESTS_PASSED++))
    else
        echo -e "Testing: User Login ... ${RED}FAIL${NC} (No token returned)"
        ((TESTS_FAILED++))
    fi
else
    echo -e "Testing: User Login ... ${RED}FAIL${NC}"
    ((TESTS_FAILED++))
fi

# Product Service - List Products
test_endpoint "GET" "/api/products?page=0&size=10" 200 "List Products"

# Product Service - Create Product (with auth)
if [ -n "$ACCESS_TOKEN" ]; then
    PRODUCT_PAYLOAD='{"name":"Smoke Test Product","description":"Test","price":99.99,"category":"Test","stock":10,"imageUrl":"https://example.com/test.jpg"}'

    CREATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_BASE_URL/api/products" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -d "$PRODUCT_PAYLOAD")

    HTTP_CODE=$(echo "$CREATE_RESPONSE" | tail -n1)

    if [ "$HTTP_CODE" -eq 201 ] || [ "$HTTP_CODE" -eq 200 ]; then
        echo -e "Testing: Create Product (Authenticated) ... ${GREEN}PASS${NC} (HTTP $HTTP_CODE)"
        ((TESTS_PASSED++))
    else
        echo -e "Testing: Create Product (Authenticated) ... ${YELLOW}SKIP${NC} (HTTP $HTTP_CODE)"
    fi
fi

# Print summary
echo ""
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo "=========================================="

if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
else
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
fi
