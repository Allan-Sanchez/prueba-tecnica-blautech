# Prueba Técnica - E-Commerce con Microservicios

Sistema completo de e-commerce implementado con arquitectura de microservicios, incluyendo frontend React, backend Spring Boot, y pipeline completo de CI/CD con despliegue en AWS.

## 🚀 Características Principales

- ✅ **Arquitectura de Microservicios** con Spring Cloud
- ✅ **Frontend moderno** con React + TypeScript + Redux
- ✅ **Autenticación JWT** con refresh tokens
- ✅ **CI/CD completo** con GitHub Actions
- ✅ **Despliegue en AWS** (ECS, RDS, S3, CloudFront)
- ✅ **Infraestructura como código** con Terraform
- ✅ **Docker** con multi-stage builds optimizados
- ✅ **Tests automatizados** y coverage
- ✅ **Escaneo de vulnerabilidades** (OWASP, Trivy, SonarCloud)

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Quick Start](#quick-start)
- [CI/CD](#cicd)
- [Documentación](#documentación)
- [Estructura del Proyecto](#estructura-del-proyecto)

---

## 🏗️ Arquitectura

### Microservicios

```
                                    ┌─────────────────┐
                                    │     Frontend    │
                                    │  (React + TS)   │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   API Gateway   │
                                    │   (Port 8080)   │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
           ┌────────▼───────┐      ┌────────▼───────┐      ┌────────▼───────┐
           │  Auth Service  │      │Product Service │      │  Cart Service  │
           │   (Port 8081)  │      │   (Port 8082)  │      │   (Port 8083)  │
           └────────┬───────┘      └────────┬───────┘      └────────┬───────┘
                    │                        │                        │
                    └────────────────────────┼────────────────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │ Discovery Server│
                                    │     (Eureka)    │
                                    │   (Port 8761)   │
                                    └─────────────────┘

                    ┌─────────────────────────────────────────────┐
                    │              MySQL Databases                 │
                    │  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
                    │  │ auth_db  │  │ prod_db  │  │ cart_db  │  │
                    │  └──────────┘  └──────────┘  └──────────┘  │
                    └─────────────────────────────────────────────┘
```

### Servicios Implementados

| Servicio           | Puerto | Estado | Descripción                              |
|--------------------|--------|--------|------------------------------------------|
| Discovery Server   | 8761   | ✅     | Eureka - Service Discovery               |
| API Gateway        | 8080   | ✅     | Punto de entrada único + Autenticación   |
| Auth Service       | 8081   | ✅     | Gestión de usuarios y JWT                |
| Product Service    | 8082   | 🔄     | CRUD de productos                        |
| Cart Service       | 8083   | ⏳     | Gestión de carritos                      |
| Order Service      | 8084   | ⏳     | Gestión de órdenes                       |
| Frontend Store     | 3000   | ✅     | React SPA                                |

---

## 🛠️ Tecnologías

### Backend
- **Java 21** con **Spring Boot 3.5.5**
- **Spring Cloud** (Gateway, Eureka, LoadBalancer)
- **Spring Security** con JWT
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**

### Frontend
- **React 18** con **TypeScript**
- **Redux Toolkit Query**
- **React Router DOM**
- **Vite**
- **SCSS**

### DevOps
- **Docker** con multi-stage builds
- **GitHub Actions** para CI/CD
- **Terraform** para IaC
- **AWS** (ECS, RDS, S3, CloudFront, ECR, ALB)

### Seguridad y Testing
- **OWASP Dependency Check**
- **Trivy Scanner**
- **SonarCloud** (opcional)
- **JUnit** + **JaCoCo** para coverage
- **ESLint** para frontend

---

## 🚀 Quick Start

### Desarrollo Local

#### 1. Backend (Microservicios)

```bash
# Clonar repositorio
git clone <repo-url>
cd Prueba-tecnica

# Configurar bases de datos MySQL
mysql -u root -p < documentation/DB/create-databases.sql
mysql -u root -p < documentation/DB/auth-schema.sql
mysql -u root -p < documentation/DB/products-schema.sql

# Compilar todos los microservicios
cd microservices
mvn clean install

# Ejecutar servicios en orden (ver CLAUDE.md para detalles)
# Opción 1: Usar IDE (IntelliJ, Eclipse, VSCode)
# Opción 2: Scripts automáticos
./start-microservices.bat  # Windows
```

**Orden de ejecución obligatorio:**
1. Discovery Server (8761) - Esperar 30s
2. Auth Service (8081) - Esperar 20s
3. Product Service (8082) - Esperar 20s
4. API Gateway (8080)

#### 2. Frontend

```bash
cd frontend-store

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev

# Abrir http://localhost:3000
```

#### Verificación

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Frontend**: http://localhost:3000

---

## 🔄 CI/CD

### Pipeline Automatizado

El proyecto incluye un pipeline completo que se ejecuta en cada push/PR:

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    Tests     │────▶│  Vulnerab.   │────▶│    Build     │────▶│   Deploy     │
│  Automáticos │     │   Scanning   │     │    Docker    │     │     AWS      │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
```

### Stages del Pipeline

#### 1. Tests Automatizados
- ✅ Backend: JUnit + Integration tests + JaCoCo coverage
- ✅ Frontend: Jest/Vitest + ESLint
- ✅ Coverage reports → Codecov

#### 2. Escaneo de Vulnerabilidades
- ✅ **OWASP Dependency Check** - CVEs en dependencias Java
- ✅ **Trivy** - Vulnerabilidades en filesystem e imágenes
- ✅ **NPM Audit** - Vulnerabilidades en Node.js
- ✅ **SonarCloud** - Análisis estático (opcional)

#### 3. Construcción
- ✅ Docker multi-stage builds
- ✅ Push a Amazon ECR
- ✅ Tag con SHA y latest
- ✅ Escaneo de imágenes

#### 4. Despliegue
- ✅ **Staging** (branch `develop`) - Automático
- ✅ **Production** (branch `main`) - Manual con aprobación
- ✅ Smoke tests post-deploy
- ✅ Rollback automático si fallan tests

### Branches Strategy

| Branch      | CI Actions                          | Deploy Target | Auto-Deploy |
|-------------|-------------------------------------|---------------|-------------|
| `feature/*` | Tests + Security scans              | -             | ❌          |
| `develop`   | Tests + Scans + Build + Deploy      | Staging       | ✅          |
| `main`      | Tests + Scans + Build + Deploy      | Production    | ✅ (manual) |

### Configuración del Pipeline

#### Paso 1: Configurar AWS Infrastructure

```bash
cd aws-infrastructure/terraform
terraform init
terraform workspace new staging
terraform apply -var="environment=staging" -var="db_password=YOUR_PASSWORD"
```

📖 **Guía completa**: [documentation/CI-CD/AWS-SETUP.md](documentation/CI-CD/AWS-SETUP.md)

#### Paso 2: Configurar GitHub Secrets

Ve a **Settings → Secrets and variables → Actions** y agrega:

```yaml
# AWS Credentials
AWS_ACCESS_KEY_ID: "AKIA..."
AWS_SECRET_ACCESS_KEY: "wJalr..."
AWS_ACCOUNT_ID: "123456789012"

# Database
DB_PASSWORD: "your-secure-password"

# CloudFront
CLOUDFRONT_DISTRIBUTION_ID_STAGING: "E1234..."
CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION: "E5678..."

# SonarCloud (Optional)
SONAR_TOKEN: "..."
SONAR_PROJECT_KEY: "..."
SONAR_ORGANIZATION: "..."
```

📖 **Guía completa**: [documentation/CI-CD/SECRETOS-GITHUB.md](documentation/CI-CD/SECRETOS-GITHUB.md)

#### Paso 3: Trigger Pipeline

```bash
# Crear feature branch
git checkout -b feature/my-feature

# Hacer cambios y commit
git add .
git commit -m "feat: Add new feature"
git push origin feature/my-feature

# Pipeline se ejecutará automáticamente
```

---

## 📚 Documentación

### Guías Principales

- **[CLAUDE.md](CLAUDE.md)** - Descripción general del proyecto
- **[documentation/CI-CD/GUIA-CICD.md](documentation/CI-CD/GUIA-CICD.md)** - Guía completa del pipeline
- **[documentation/CI-CD/SECRETOS-GITHUB.md](documentation/CI-CD/SECRETOS-GITHUB.md)** - Configuración de secretos
- **[documentation/CI-CD/AWS-SETUP.md](documentation/CI-CD/AWS-SETUP.md)** - Setup AWS con Terraform

### Diagramas

- **[documentation/flowcharts/](documentation/flowcharts/)** - Diagramas de flujo
- **[documentation/sequences/](documentation/sequences/)** - Diagramas de secuencia
- **[documentation/DB/](documentation/DB/)** - Esquemas de base de datos

---

## 📁 Estructura del Proyecto

```
Prueba-tecnica/
├── .github/workflows/
│   └── ci-cd-pipeline.yml         # Pipeline principal
├── frontend-store/                # React Frontend
├── microservices/                 # Spring Boot Microservices
├── aws-infrastructure/            # Terraform IaC
├── documentation/CI-CD/           # Documentación CI/CD
├── scripts/smoke-tests.sh         # Tests post-deploy
├── CLAUDE.md                      # Guía del proyecto
└── README-CICD.md                 # Este archivo
```

---

## 📊 Archivos Creados

### GitHub Actions
- `.github/workflows/ci-cd-pipeline.yml` - Pipeline completo de CI/CD

### Dockerfiles
- `microservices/discovery-server/Dockerfile`
- `microservices/api-gateway/Dockerfile`
- `microservices/auth-service/Dockerfile`
- `microservices/product-service/Dockerfile`
- `microservices/cart-service/Dockerfile`
- `microservices/order-service/Dockerfile`
- `frontend-store/Dockerfile`
- `frontend-store/nginx.conf`
- `.dockerignore`

### Infraestructura AWS (Terraform)
- `aws-infrastructure/terraform/main.tf` - VPC, ECS, RDS, ALB, S3, CloudFront
- `aws-infrastructure/terraform/ecs-services.tf` - Task definitions y servicios

### Scripts
- `scripts/smoke-tests.sh` - Tests automáticos post-deploy

### Documentación
- `documentation/CI-CD/README.md` - Índice de documentación CI/CD
- `documentation/CI-CD/GUIA-CICD.md` - Guía completa del pipeline (20+ páginas)
- `documentation/CI-CD/SECRETOS-GITHUB.md` - Setup de secretos (15+ páginas)
- `documentation/CI-CD/AWS-SETUP.md` - Setup de infraestructura (25+ páginas)

---

## 💰 Costos Estimados

### Staging: ~$132 USD/mes
- ECS Fargate (4 tareas): $30
- RDS MySQL (2x db.t3.micro): $30
- ALB: $20
- NAT Gateway: $32
- S3 + CloudFront: $13
- CloudWatch + ECR: $7

### Production: ~$500 USD/mes
- ECS Fargate (7 tareas, 2x replicas): $52
- RDS MySQL Multi-AZ (2x db.t3.medium): $240
- ALB: $20
- NAT Gateway (2x): $64
- S3 + CloudFront: $88
- CloudWatch + ECR: $36

**💡 Optimización:**
- Reserved Instances para RDS: hasta 60% descuento
- Savings Plans para ECS: hasta 50% descuento
- Apagar staging fuera de horario laboral: 40% ahorro

---

## 🎯 Próximos Pasos

### Para ejecutar el CI/CD:

1. ✅ **Configurar AWS Infrastructure**
   ```bash
   cd aws-infrastructure/terraform
   terraform init
   terraform apply -var="environment=staging" -var="db_password=PASSWORD"
   ```

2. ✅ **Configurar GitHub Secrets**
   - Ver guía: `documentation/CI-CD/SECRETOS-GITHUB.md`

3. ✅ **Push a develop para testing**
   ```bash
   git checkout -b develop
   git add .
   git commit -m "ci: Setup CI/CD pipeline"
   git push origin develop
   ```

4. ✅ **Verificar despliegue en Staging**
   - GitHub Actions: Ver logs del pipeline
   - AWS Console: Verificar servicios en ECS

5. ✅ **Merge a main para Production**
   ```bash
   git checkout main
   git merge develop
   git push origin main
   # Aprobar despliegue manual en GitHub
   ```

---

## 📧 Soporte

Para preguntas o problemas:
1. Revisar documentación en `documentation/CI-CD/`
2. Ver troubleshooting en `GUIA-CICD.md`
3. Consultar logs en CloudWatch y GitHub Actions

---

**Última actualización**: 2025-10-21

**Pipeline incluye**: Tests automáticos | Escaneo de vulnerabilidades | Build Docker | Deploy AWS | Smoke tests
