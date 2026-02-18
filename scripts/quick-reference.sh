#!/bin/bash
# Quick Reference - JWT Authentication

cat << 'EOF'
╔══════════════════════════════════════════════════════════════════════╗
║                   JWT AUTHENTICATION QUICK REFERENCE                  ║
╚══════════════════════════════════════════════════════════════════════╝

📦 START SERVICES
  make run                    # Start Postgres, Keycloak, and App

🔐 SETUP KEYCLOAK (First Time Only)
  make setup-keycloak         # Create realm, client, and test user

🎫 GET JWT TOKEN
  make get-token              # Generate and save token to token.txt

🚀 USE API
  TOKEN=$(cat token.txt)
  curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/recipes

🧪 RUN TESTS
  make unit-test              # Run unit tests
  make integration-test       # Run integration tests

🌐 URLs
  App:            http://localhost:8080
  Swagger:        http://localhost:8080/swagger-ui.html
  Keycloak:       http://localhost:8090
  Health Check:   http://localhost:8080/actuator/health

🔑 DEFAULT CREDENTIALS
  Keycloak Admin: admin / admin
  Test User:      testuser / testpass
  Client ID:      recipes-app
  Realm:          recipes

⚡ QUICK START
  1. make run
  2. Wait 30 seconds for Keycloak to start
  3. make setup-keycloak
  4. make get-token
  5. Use the API with TOKEN=$(cat token.txt)

🆘 TROUBLESHOOTING
  - Token expired? → make get-token
  - Keycloak not ready? → Wait 30 seconds, check http://localhost:8090/health
  - Need fresh setup? → docker-compose down -v && make run && make setup-keycloak

📋 API ENDPOINTS (All require JWT except /actuator/health)
  POST   /api/v1/recipes         Create recipe
  GET    /api/v1/recipes         List recipes (with filters)
  GET    /api/v1/recipes/{id}    Get recipe by ID
  PUT    /api/v1/recipes/{id}    Update recipe
  PATCH  /api/v1/recipes/{id}    Partial update recipe
  DELETE /api/v1/recipes/{id}    Delete recipe
  GET    /actuator/health        Health check (public)

✨ EXAMPLE REQUEST
  TOKEN=$(cat token.txt)
  curl -H "Authorization: Bearer $TOKEN" \
       -H "Content-Type: application/json" \
       -X POST http://localhost:8080/api/v1/recipes \
       -d '{"name":"Pizza","servings":4,"vegetarian":true,
            "ingredients":"dough, tomato, cheese",
            "instructions":"Bake at 220C for 15 minutes"}'

╔══════════════════════════════════════════════════════════════════════╗
║                   JWT IMPLEMENTATION COMPLETE ✅                      ║
╚══════════════════════════════════════════════════════════════════════╝
EOF
