#!/bin/bash

# Keycloak Setup and Token Generation Script
# This script helps set up Keycloak and generate JWT tokens for testing

KEYCLOAK_URL="http://localhost:8090"
REALM="recipes"
CLIENT_ID="recipes-app"
CLIENT_SECRET="your-client-secret"
USERNAME="testuser"
PASSWORD="testpass"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Keycloak JWT Token Generator ===${NC}\n"

# Check if Keycloak is running
if ! curl -s "${KEYCLOAK_URL}/health" > /dev/null 2>&1; then
    echo -e "${RED}Error: Keycloak is not running at ${KEYCLOAK_URL}${NC}"
    echo -e "${YELLOW}Start Keycloak with: make run${NC}"
    exit 1
fi

echo -e "${GREEN}Keycloak is running${NC}\n"

# Function to get admin token
get_admin_token() {
    TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=admin" \
        -d "password=admin" \
        -d "grant_type=password" \
        -d "client_id=admin-cli")

    echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | sed 's/"access_token":"//'
}

# Function to create realm
create_realm() {
    ADMIN_TOKEN=$1
    echo -e "${YELLOW}Creating realm '${REALM}'...${NC}"

    curl -s -X POST "${KEYCLOAK_URL}/admin/realms" \
        -H "Authorization: Bearer ${ADMIN_TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "realm": "'${REALM}'",
            "enabled": true,
            "sslRequired": "none",
            "registrationAllowed": true
        }' > /dev/null

    echo -e "${GREEN}Realm created${NC}"
}

# Function to create client
create_client() {
    ADMIN_TOKEN=$1
    echo -e "${YELLOW}Creating client '${CLIENT_ID}'...${NC}"

    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/clients" \
        -H "Authorization: Bearer ${ADMIN_TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "clientId": "'${CLIENT_ID}'",
            "enabled": true,
            "publicClient": true,
            "directAccessGrantsEnabled": true,
            "standardFlowEnabled": true,
            "redirectUris": ["*"],
            "webOrigins": ["*"]
        }' > /dev/null

    echo -e "${GREEN}Client created${NC}"
}

# Function to create user
create_user() {
    ADMIN_TOKEN=$1
    echo -e "${YELLOW}Creating user '${USERNAME}'...${NC}"

    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users" \
        -H "Authorization: Bearer ${ADMIN_TOKEN}" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "'${USERNAME}'",
            "enabled": true,
            "emailVerified": true,
            "email": "'${USERNAME}'@example.com",
            "firstName": "Test",
            "lastName": "User",
            "credentials": [{
                "type": "password",
                "value": "'${PASSWORD}'",
                "temporary": false
            }]
        }' > /dev/null

    echo -e "${GREEN}User created${NC}"

    # Get the user ID and update to ensure account is fully set up
    sleep 1
    USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=${USERNAME}" \
        -H "Authorization: Bearer ${ADMIN_TOKEN}" | grep -o '"id":"[^"]*' | head -1 | sed 's/"id":"//')

    if [ -n "$USER_ID" ]; then
        echo -e "${YELLOW}Configuring user account...${NC}"
        # Update user to clear required actions and verify email
        curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}" \
            -H "Authorization: Bearer ${ADMIN_TOKEN}" \
            -H "Content-Type: application/json" \
            -d '{
                "email": "'${USERNAME}'@example.com",
                "firstName": "Test",
                "lastName": "User",
                "emailVerified": true,
                "enabled": true,
                "requiredActions": []
            }' > /dev/null

        # Reset password to ensure it's properly set
        curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/reset-password" \
            -H "Authorization: Bearer ${ADMIN_TOKEN}" \
            -H "Content-Type: application/json" \
            -d '{
                "type": "password",
                "value": "'${PASSWORD}'",
                "temporary": false
            }' > /dev/null

        echo -e "${GREEN}User account fully configured${NC}"
    fi
}

# Function to get user token
get_user_token() {
    echo -e "\n${YELLOW}Getting JWT token for user '${USERNAME}'...${NC}\n"

    TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=${USERNAME}" \
        -d "password=${PASSWORD}" \
        -d "grant_type=password" \
        -d "client_id=${CLIENT_ID}")

    ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | sed 's/"access_token":"//')

    if [ -z "$ACCESS_TOKEN" ]; then
        # Check if error is "Account is not fully set up"
        if echo "$TOKEN_RESPONSE" | grep -q "Account is not fully set up"; then
            echo -e "${YELLOW}Account needs configuration. Fixing...${NC}\n"

            # Get admin token
            ADMIN_TOKEN=$(get_admin_token)
            if [ -z "$ADMIN_TOKEN" ]; then
                echo -e "${RED}Failed to get admin token${NC}"
                return 1
            fi

            # Get user ID
            USER_ID=$(curl -s -X GET "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=${USERNAME}" \
                -H "Authorization: Bearer ${ADMIN_TOKEN}" | grep -o '"id":"[^"]*' | head -1 | sed 's/"id":"//')

            if [ -n "$USER_ID" ]; then
                # Update user to clear required actions and verify email
                curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}" \
                    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
                    -H "Content-Type: application/json" \
                    -d '{
                        "email": "'${USERNAME}'@example.com",
                        "firstName": "Test",
                        "lastName": "User",
                        "emailVerified": true,
                        "enabled": true,
                        "requiredActions": []
                    }' > /dev/null

                # Reset password
                curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/reset-password" \
                    -H "Authorization: Bearer ${ADMIN_TOKEN}" \
                    -H "Content-Type: application/json" \
                    -d '{
                        "type": "password",
                        "value": "'${PASSWORD}'",
                        "temporary": false
                    }' > /dev/null

                echo -e "${GREEN}Account fixed. Retrying token request...${NC}\n"

                # Try again
                TOKEN_RESPONSE=$(curl -s -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
                    -H "Content-Type: application/x-www-form-urlencoded" \
                    -d "username=${USERNAME}" \
                    -d "password=${PASSWORD}" \
                    -d "grant_type=password" \
                    -d "client_id=${CLIENT_ID}")

                ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"access_token":"[^"]*' | sed 's/"access_token":"//')
            fi
        fi

        if [ -z "$ACCESS_TOKEN" ]; then
            echo -e "${RED}Failed to get token. Response:${NC}"
            echo "$TOKEN_RESPONSE"
            return 1
        fi
    fi

    echo -e "${GREEN}=== JWT Token ===${NC}"
    echo "$ACCESS_TOKEN"
    echo ""
    echo -e "${GREEN}=== Use with curl ===${NC}"
    echo "curl -H \"Authorization: Bearer ${ACCESS_TOKEN}\" http://localhost:8080/api/v1/recipes"
    echo ""
    echo -e "${GREEN}=== Token saved to token.txt ===${NC}"
    echo "$ACCESS_TOKEN" > token.txt
}

# Main setup flow
if [ "$1" == "setup" ]; then
    echo -e "${YELLOW}Setting up Keycloak realm, client, and user...${NC}\n"

    ADMIN_TOKEN=$(get_admin_token)
    if [ -z "$ADMIN_TOKEN" ]; then
        echo -e "${RED}Failed to get admin token${NC}"
        exit 1
    fi

    create_realm "$ADMIN_TOKEN"
    sleep 1
    create_client "$ADMIN_TOKEN"
    sleep 1
    create_user "$ADMIN_TOKEN"

    echo -e "\n${GREEN}=== Setup Complete ===${NC}"
    echo -e "Realm: ${REALM}"
    echo -e "Client ID: ${CLIENT_ID}"
    echo -e "Client Secret: ${CLIENT_SECRET}"
    echo -e "Username: ${USERNAME}"
    echo -e "Password: ${PASSWORD}"
    echo -e "\nRun './scripts/get-token.sh' to get a JWT token"
else
    get_user_token
fi
