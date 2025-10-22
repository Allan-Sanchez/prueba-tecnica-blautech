# Guía de CI/CD - Pipeline Completo

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Arquitectura del Pipeline](#arquitectura-del-pipeline)
3. [Requisitos Previos](#requisitos-previos)
4. [Configuración Inicial](#configuración-inicial)
5. [Etapas del Pipeline](#etapas-del-pipeline)
6. [Despliegue en AWS](#despliegue-en-aws)
7. [Monitoreo y Troubleshooting](#monitoreo-y-troubleshooting)

---

## Descripción General

Este proyecto implementa un pipeline completo de CI/CD utilizando **GitHub Actions** para automatizar:

- ✅ **Pruebas automatizadas** (unitarias e integración)
- 🔒 **Escaneo de vulnerabilidades** (OWASP, Trivy, SonarCloud)
- 🏗️ **Construcción de imágenes Docker**
- 🚀 **Despliegue automático en AWS ECS**
- 📊 **Monitoreo y reportes de calidad**

### Flujo de Trabajo

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐     ┌─────────────┐
│   Commit    │────▶│  Tests &     │────▶│   Build     │────▶│   Deploy    │
│  to GitHub  │     │  Security    │     │   Docker    │     │   to AWS    │
└─────────────┘     └──────────────┘     └─────────────┘     └─────────────┘
```

### Branches y Ambientes

| Branch      | Ambiente   | URL                           | Auto-Deploy |
|-------------|------------|-------------------------------|-------------|
| `feature/*` | -          | Solo tests                    | ❌          |
| `develop`   | Staging    | https://staging.mitienda.com  | ✅          |
| `main`      | Production | https://mitienda.com          | ✅          |

---

## Arquitectura del Pipeline

### Pipeline Principal: `ci-cd-pipeline.yml`

El pipeline se divide en 4 jobs principales:

#### 1. **test-backend**
   - Tests unitarios e integración
   - Generación de coverage con JaCoCo
   - OWASP Dependency Check
   - Trivy vulnerability scanner
   - SonarCloud análisis de código

#### 2. **test-frontend**
   - Linting con ESLint
   - Tests con Jest/Vitest
   - NPM Audit
   - Trivy scanner para dependencias

#### 3. **build-and-push**
   - Build multi-arquitectura Docker
   - Push a Amazon ECR
   - Escaneo de imágenes con Trivy
   - Tag con commit SHA y 'latest'

#### 4. **deploy-staging / deploy-production**
   - Despliegue a ECS con rolling updates
   - Despliegue de frontend a S3/CloudFront
   - Smoke tests automáticos
   - Creación de releases (solo producción)

---

## Requisitos Previos

### 1. Cuenta de AWS

Necesitarás:
- Una cuenta de AWS activa
- Permisos para crear recursos (ECS, RDS, S3, CloudFront, ECR)
- AWS CLI configurado localmente (para setup inicial)

### 2. Terraform (Opcional pero Recomendado)

```bash
# Instalar Terraform
# Windows (con Chocolatey)
choco install terraform

# macOS
brew install terraform

# Linux
wget https://releases.hashicorp.com/terraform/1.6.0/terraform_1.6.0_linux_amd64.zip
unzip terraform_1.6.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/
```

### 3. GitHub Repository Secrets

Debes configurar los siguientes secretos en tu repositorio de GitHub:

**Settings → Secrets and variables → Actions → New repository secret**

---

## Configuración Inicial

### Paso 1: Configurar Secretos de GitHub

#### Secretos Obligatorios:

```yaml
# AWS Credentials
AWS_ACCESS_KEY_ID          # Access Key ID de IAM user con permisos necesarios
AWS_SECRET_ACCESS_KEY      # Secret Access Key del IAM user
AWS_ACCOUNT_ID             # ID de tu cuenta AWS (12 dígitos)

# Database
DB_PASSWORD                # Password para RDS MySQL (mínimo 16 caracteres)

# SonarCloud (Opcional)
SONAR_TOKEN               # Token de SonarCloud para análisis de código
SONAR_PROJECT_KEY         # Key del proyecto en SonarCloud
SONAR_ORGANIZATION        # Nombre de tu organización en SonarCloud

# CloudFront
CLOUDFRONT_DISTRIBUTION_ID_STAGING     # ID de distribución CloudFront para staging
CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION  # ID de distribución CloudFront para producción
```

#### Cómo obtener AWS Credentials:

```bash
# 1. Crear IAM User con permisos necesarios
aws iam create-user --user-name github-actions-deployer

# 2. Crear Access Key
aws iam create-access-key --user-name github-actions-deployer

# 3. Adjuntar políticas necesarias (ejemplo simplificado, ajustar según necesidades)
aws iam attach-user-policy --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::aws:policy/AmazonECS_FullAccess

aws iam attach-user-policy --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess
```

### Paso 2: Desplegar Infraestructura AWS con Terraform

```bash
# 1. Navegar al directorio de Terraform
cd aws-infrastructure/terraform

# 2. Inicializar Terraform
terraform init

# 3. Crear workspace para staging
terraform workspace new staging
terraform workspace select staging

# 4. Revisar el plan de ejecución
terraform plan -var="environment=staging" -var="db_password=TU_PASSWORD_AQUI"

# 5. Aplicar la configuración (ESTO CREARÁ RECURSOS EN AWS)
terraform apply -var="environment=staging" -var="db_password=TU_PASSWORD_AQUI"

# 6. Repetir para producción
terraform workspace new production
terraform workspace select production
terraform apply -var="environment=production" -var="db_password=TU_PASSWORD_AQUI"
```

**Nota:** Terraform creará:
- VPC con subnets públicas y privadas
- ECS Cluster con servicios
- RDS MySQL instances
- Application Load Balancer
- ECR Repositories
- S3 buckets y CloudFront distributions
- Security Groups y IAM roles

### Paso 3: Crear Repositorios ECR (si no usas Terraform)

```bash
# Lista de servicios
services=(
  "discovery-server"
  "api-gateway"
  "auth-service"
  "product-service"
  "cart-service"
  "order-service"
  "frontend-store"
)

# Crear repositorios
for service in "${services[@]}"; do
  aws ecr create-repository \
    --repository-name prueba-tecnica/$service \
    --region us-east-1 \
    --image-scanning-configuration scanOnPush=true
done
```

### Paso 4: Configurar Base de Datos

```bash
# Conectar a RDS y ejecutar scripts de inicialización
mysql -h <RDS_ENDPOINT> -u admin -p < documentation/DB/auth-schema.sql
mysql -h <RDS_ENDPOINT> -u admin -p < documentation/DB/products-schema.sql
```

---

## Etapas del Pipeline

### 1. Tests Automatizados

#### Backend Tests

El pipeline ejecuta:

```bash
# Tests unitarios
mvn clean test -B

# Tests de integración
mvn verify -B -DskipUnitTests=true

# Coverage report
mvn jacoco:report
```

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:33-75`

**Métricas generadas:**
- Coverage de código (JaCoCo)
- Resultados de tests (JUnit)
- Reportes subidos a Codecov

#### Frontend Tests

```bash
# Linting
npm run lint

# Tests con coverage
npm test -- --coverage --watchAll=false

# Audit de dependencias
npm audit --audit-level=moderate
```

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:77-124`

### 2. Escaneo de Vulnerabilidades

#### OWASP Dependency Check

Analiza dependencias de Java en busca de CVEs conocidos:

```bash
mvn org.owasp:dependency-check-maven:check -B
```

**Configuración:**
- Severity: CRITICAL, HIGH
- Formato: XML report
- Falla el build si encuentra vulnerabilidades críticas

#### Trivy Scanner

Escanea:
- Sistema de archivos (dependencias)
- Imágenes Docker construidas
- Configuraciones de infraestructura

```bash
trivy fs ./microservices --severity CRITICAL,HIGH
trivy image <ECR_IMAGE>:latest --severity CRITICAL,HIGH
```

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:60-73` (Backend)
- `.github/workflows/ci-cd-pipeline.yml:106-116` (Frontend)
- `.github/workflows/ci-cd-pipeline.yml:178-186` (Docker images)

#### SonarCloud (Opcional)

Análisis estático de código:
- Code smells
- Bugs
- Security hotspots
- Technical debt
- Coverage metrics

**Setup en SonarCloud:**
1. Crear cuenta en https://sonarcloud.io
2. Importar proyecto desde GitHub
3. Obtener token y configurar secrets
4. Ejecutar análisis en el pipeline

### 3. Construcción de Aplicación

#### Docker Build Multi-Stage

Todas las imágenes usan multi-stage builds:

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s CMD wget --spider http://localhost:8081/actuator/health
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
```

**Ventajas:**
- Imágenes optimizadas (solo runtime en producción)
- Menor tamaño (50-70% reducción)
- Sin herramientas de build en producción
- Usuario no-root por seguridad

#### Push a Amazon ECR

```bash
# Login
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin $ECR_REGISTRY

# Build y push
docker build -t $ECR_REGISTRY/auth-service:$GITHUB_SHA .
docker push $ECR_REGISTRY/auth-service:$GITHUB_SHA
docker tag $ECR_REGISTRY/auth-service:$GITHUB_SHA $ECR_REGISTRY/auth-service:latest
docker push $ECR_REGISTRY/auth-service:latest
```

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:126-195`
- `microservices/*/Dockerfile`

### 4. Despliegue en Ambientes

#### Staging (branch `develop`)

```bash
# 1. Desplegar Discovery Server (primero)
aws ecs update-service \
  --cluster prueba-tecnica-staging \
  --service discovery-server \
  --force-new-deployment

# 2. Esperar a que esté estable
aws ecs wait services-stable \
  --cluster prueba-tecnica-staging \
  --services discovery-server

# 3. Desplegar servicios dependientes (Auth, Product)
aws ecs update-service \
  --cluster prueba-tecnica-staging \
  --service auth-service \
  --force-new-deployment

# 4. Finalmente API Gateway
aws ecs update-service \
  --cluster prueba-tecnica-staging \
  --service api-gateway \
  --force-new-deployment

# 5. Deploy Frontend a S3/CloudFront
aws s3 sync frontend-store/dist/ s3://prueba-tecnica-frontend-staging --delete
aws cloudfront create-invalidation \
  --distribution-id $CLOUDFRONT_ID \
  --paths "/*"
```

**Orden de despliegue (IMPORTANTE):**
1. Discovery Server (Eureka)
2. Auth Service + Product Service (en paralelo)
3. API Gateway
4. Frontend

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:197-248`

#### Production (branch `main`)

Mismo proceso que staging pero con aprobación manual requerida.

**Configuración de Protection Rules:**

1. Ir a **Settings → Environments → production**
2. Habilitar **Required reviewers**
3. Añadir reviewers autorizados
4. Configurar **Wait timer** (opcional, ej: 5 minutos)

**Archivos relacionados:**
- `.github/workflows/ci-cd-pipeline.yml:250-306`

---

## Despliegue en AWS

### Arquitectura AWS

```
                                    ┌─────────────────┐
                                    │   CloudFront    │
                                    │   (Frontend)    │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┴────────────────────────┐
                    │                                                  │
            ┌───────▼────────┐                              ┌─────────▼────────┐
            │   S3 Bucket    │                              │  Application     │
            │   (Static)     │                              │  Load Balancer   │
            └────────────────┘                              └─────────┬────────┘
                                                                      │
                                             ┌────────────────────────┼────────────────────────┐
                                             │                        │                        │
                                    ┌────────▼───────┐      ┌────────▼───────┐      ┌────────▼───────┐
                                    │  API Gateway   │      │  Auth Service  │      │ Product Service│
                                    │  (ECS Fargate) │      │  (ECS Fargate) │      │  (ECS Fargate) │
                                    └────────┬───────┘      └────────┬───────┘      └────────┬───────┘
                                             │                        │                        │
                                             └────────────────────────┼────────────────────────┘
                                                                      │
                                                         ┌────────────▼────────────┐
                                                         │   Discovery Server      │
                                                         │   (Eureka - Fargate)    │
                                                         └─────────────────────────┘

                    ┌────────────────────────────────────────────────────────────────┐
                    │                         AWS RDS MySQL                           │
                    │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
                    │  │   auth_db    │  │ products_db  │  │   carts_db   │         │
                    │  └──────────────┘  └──────────────┘  └──────────────┘         │
                    └────────────────────────────────────────────────────────────────┘
```

### Recursos AWS Creados

#### Compute:
- **ECS Cluster** (Fargate)
  - Discovery Server: 1 instancia
  - Auth Service: 2 instancias (prod), 1 (staging)
  - Product Service: 2 instancias (prod), 1 (staging)
  - API Gateway: 2 instancias (prod), 1 (staging)

#### Networking:
- **VPC** con subnets públicas y privadas
- **Application Load Balancer**
- **Security Groups** (ALB, ECS, RDS)
- **Service Discovery** (AWS Cloud Map)

#### Storage:
- **RDS MySQL**
  - auth-db: db.t3.micro (staging) / db.t3.medium (prod)
  - products-db: db.t3.micro (staging) / db.t3.medium (prod)
- **S3 Buckets** (frontend estático)
- **ECR Repositories** (imágenes Docker)

#### CDN:
- **CloudFront Distribution** (frontend)

#### Monitoring:
- **CloudWatch Logs** (logs de todos los servicios)
- **CloudWatch Metrics** (ECS, RDS, ALB)

### Costos Estimados (Staging)

| Servicio          | Tipo              | Costo/Mes (USD) |
|-------------------|-------------------|-----------------|
| ECS Fargate       | 4 tareas x 0.5GB  | ~$30            |
| RDS MySQL         | 2x db.t3.micro    | ~$30            |
| ALB               | 1 instancia       | ~$20            |
| S3 + CloudFront   | <1TB transfer     | ~$5             |
| ECR               | <500GB storage    | ~$2             |
| **TOTAL**         |                   | **~$87/mes**    |

**Nota:** Costos de producción serán mayores por instancias más grandes y alta disponibilidad.

---

## Monitoreo y Troubleshooting

### Logs de GitHub Actions

1. Ir a **Actions** en GitHub
2. Seleccionar workflow run
3. Ver logs de cada job

**Comandos útiles en logs:**
```bash
# Ver logs del pipeline
gh run list
gh run view <run-id>
gh run watch <run-id>
```

### Logs de ECS en CloudWatch

```bash
# Ver logs de un servicio
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow

# Buscar errores
aws logs filter-log-events \
  --log-group-name /ecs/prueba-tecnica-staging \
  --filter-pattern "ERROR"
```

### Troubleshooting Común

#### 1. Tests Fallan

**Síntoma:** Job `test-backend` o `test-frontend` falla

**Solución:**
```bash
# Ejecutar tests localmente
cd microservices
mvn clean test

cd frontend-store
npm test
```

#### 2. Docker Build Falla

**Síntoma:** Job `build-and-push` falla al construir imagen

**Solución:**
```bash
# Verificar Dockerfile localmente
docker build -t test-image -f microservices/auth-service/Dockerfile microservices/auth-service

# Ver logs de build
docker build --progress=plain -t test-image .
```

#### 3. Despliegue a ECS Falla

**Síntoma:** Servicio no arranca o health check falla

**Solución:**
```bash
# Ver estado del servicio
aws ecs describe-services \
  --cluster prueba-tecnica-staging \
  --services auth-service

# Ver tareas fallidas
aws ecs list-tasks \
  --cluster prueba-tecnica-staging \
  --service-name auth-service \
  --desired-status STOPPED

# Ver logs de la tarea
aws ecs describe-tasks \
  --cluster prueba-tecnica-staging \
  --tasks <task-arn>
```

#### 4. Smoke Tests Fallan

**Síntoma:** Tests de smoke fallan después del deploy

**Solución:**
```bash
# Ejecutar smoke tests manualmente
chmod +x scripts/smoke-tests.sh
./scripts/smoke-tests.sh https://staging-api.mitienda.com

# Verificar conectividad
curl -v https://staging-api.mitienda.com/actuator/health
```

#### 5. Error de Autenticación AWS

**Síntoma:** `Error: Unable to locate credentials`

**Solución:**
1. Verificar que los secretos estén configurados en GitHub
2. Verificar que el IAM user tenga permisos necesarios
3. Regenerar Access Keys si es necesario

---

## Scripts de Utilidad

### Rollback Manual

```bash
# Listar task definitions anteriores
aws ecs list-task-definitions \
  --family-prefix prueba-tecnica-staging-auth-service \
  --sort DESC

# Actualizar servicio a versión anterior
aws ecs update-service \
  --cluster prueba-tecnica-staging \
  --service auth-service \
  --task-definition prueba-tecnica-staging-auth-service:PREVIOUS_VERSION
```

### Escalar Servicios

```bash
# Escalar auth-service a 3 instancias
aws ecs update-service \
  --cluster prueba-tecnica-production \
  --service auth-service \
  --desired-count 3
```

### Limpiar Recursos

```bash
# Eliminar imágenes antiguas de ECR
aws ecr list-images \
  --repository-name prueba-tecnica/auth-service \
  --filter tagStatus=UNTAGGED \
  --query 'imageIds[*]' \
  --output json | \
  jq -r '.[] | .imageDigest' | \
  xargs -I {} aws ecr batch-delete-image \
    --repository-name prueba-tecnica/auth-service \
    --image-ids imageDigest={}
```

---

## Próximos Pasos

1. ✅ **Configurar secretos de GitHub**
2. ✅ **Desplegar infraestructura con Terraform**
3. ✅ **Hacer primer push a `develop` para probar staging**
4. ✅ **Revisar logs y métricas en CloudWatch**
5. ✅ **Configurar alertas en CloudWatch**
6. ✅ **Hacer merge a `main` para desplegar a producción**

---

## Referencias

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [Trivy Scanner](https://github.com/aquasecurity/trivy)
