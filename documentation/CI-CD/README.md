# CI/CD Documentation

Esta carpeta contiene toda la documentación relacionada con el pipeline de CI/CD implementado para este proyecto.

## Contenido

### Guías Principales

1. **[GUIA-CICD.md](./GUIA-CICD.md)** - Guía completa del pipeline
   - Descripción general del pipeline
   - Arquitectura y flujo de trabajo
   - Etapas del pipeline (tests, security, build, deploy)
   - Monitoreo y troubleshooting
   - Scripts de utilidad

2. **[SECRETOS-GITHUB.md](./SECRETOS-GITHUB.md)** - Configuración de secretos
   - Lista completa de secretos requeridos
   - Cómo obtener cada secreto
   - Configuración paso a paso
   - Verificación y troubleshooting

3. **[AWS-SETUP.md](./AWS-SETUP.md)** - Setup de infraestructura AWS
   - 3 opciones de despliegue (Terraform, Console, CLI)
   - Configuración post-despliegue
   - Estimación de costos
   - Optimización y mejores prácticas

## Quick Start

### 1. Configurar Infraestructura AWS

```bash
# Opción recomendada: Terraform
cd aws-infrastructure/terraform
terraform init
terraform workspace new staging
terraform apply -var="environment=staging" -var="db_password=TU_PASSWORD"
```

📖 **Ver [AWS-SETUP.md](./AWS-SETUP.md) para más detalles**

### 2. Configurar Secretos de GitHub

1. Ir a **Settings** → **Secrets and variables** → **Actions**
2. Agregar secretos obligatorios:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `AWS_ACCOUNT_ID`
   - `DB_PASSWORD`
   - `CLOUDFRONT_DISTRIBUTION_ID_STAGING`
   - `CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION`

📖 **Ver [SECRETOS-GITHUB.md](./SECRETOS-GITHUB.md) para instrucciones detalladas**

### 3. Activar el Pipeline

```bash
# Crear branch de feature
git checkout -b feature/setup-cicd

# Hacer commit
git add .
git commit -m "feat: Configure CI/CD pipeline"

# Push a GitHub
git push origin feature/setup-cicd

# El pipeline se ejecutará automáticamente
```

📖 **Ver [GUIA-CICD.md](./GUIA-CICD.md) para entender el flujo completo**

## Estructura del Pipeline

```
┌─────────────────────────────────────────────────────────────────┐
│                         GitHub Actions                          │
└─────────────────────────────────────────────────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
            ┌───────▼────────┐       ┌───────▼────────┐
            │  test-backend  │       │ test-frontend  │
            │                │       │                │
            │ • Unit tests   │       │ • Linting      │
            │ • Integration  │       │ • Tests        │
            │ • Coverage     │       │ • NPM Audit    │
            │ • OWASP Check  │       │ • Trivy Scan   │
            │ • Trivy Scan   │       │                │
            │ • SonarCloud   │       │                │
            └───────┬────────┘       └───────┬────────┘
                    │                         │
                    └────────────┬────────────┘
                                 │
                        ┌────────▼────────┐
                        │ build-and-push  │
                        │                 │
                        │ • Docker build  │
                        │ • Push to ECR   │
                        │ • Image scan    │
                        └────────┬────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
        ┌───────────▼──────────┐   ┌─────────▼──────────┐
        │   deploy-staging     │   │ deploy-production  │
        │   (branch: develop)  │   │  (branch: main)    │
        │                      │   │                    │
        │ • Deploy to ECS      │   │ • Manual approval  │
        │ • Update services    │   │ • Deploy to ECS    │
        │ • Deploy frontend    │   │ • Deploy frontend  │
        │ • Smoke tests        │   │ • Smoke tests      │
        │                      │   │ • Create release   │
        └──────────────────────┘   └────────────────────┘
```

## Branches y Environments

| Branch        | Acción                                  | Ambiente   |
|---------------|-----------------------------------------|------------|
| `feature/*`   | Tests y security scans                  | -          |
| `develop`     | Tests, build, deploy automático         | Staging    |
| `main`        | Tests, build, deploy con aprobación     | Production |

## Archivos del Pipeline

### GitHub Actions Workflows

```
.github/workflows/
└── ci-cd-pipeline.yml         # Pipeline principal
```

### Dockerfiles

```
microservices/
├── discovery-server/Dockerfile
├── api-gateway/Dockerfile
├── auth-service/Dockerfile
├── product-service/Dockerfile
├── cart-service/Dockerfile
└── order-service/Dockerfile

frontend-store/
├── Dockerfile
└── nginx.conf
```

### Infraestructura

```
aws-infrastructure/
└── terraform/
    ├── main.tf              # Configuración principal
    ├── ecs-services.tf      # Definiciones de servicios ECS
    └── variables.tf         # Variables
```

### Scripts

```
scripts/
└── smoke-tests.sh           # Tests de humo post-deploy
```

## Etapas del Pipeline

### 1. Tests Automatizados

**Backend:**
- Tests unitarios con JUnit
- Tests de integración
- Generación de coverage (JaCoCo)
- Reporte a Codecov

**Frontend:**
- Linting con ESLint
- Tests con Jest/Vitest
- Coverage report

### 2. Escaneo de Vulnerabilidades

**Herramientas:**
- **OWASP Dependency Check** - Vulnerabilidades en dependencias Java
- **Trivy** - Vulnerabilidades en filesystems e imágenes Docker
- **SonarCloud** - Análisis estático de código (opcional)
- **NPM Audit** - Vulnerabilidades en dependencias Node.js

### 3. Construcción

**Docker Multi-stage builds:**
- Stage 1: Build de la aplicación
- Stage 2: Runtime optimizado
- Usuario no-root por seguridad
- Health checks configurados

**Push a ECR:**
- Tag con commit SHA
- Tag latest
- Escaneo automático de imágenes

### 4. Despliegue

**Staging (automático en `develop`):**
1. Discovery Server
2. Auth Service + Product Service
3. API Gateway
4. Frontend (S3 + CloudFront)
5. Smoke tests

**Production (manual en `main`):**
1. Aprobación requerida
2. Mismo proceso que staging
3. Creación de release en GitHub

## Monitoreo

### CloudWatch Logs

```bash
# Ver logs en tiempo real
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow

# Buscar errores
aws logs filter-log-events \
  --log-group-name /ecs/prueba-tecnica-staging \
  --filter-pattern "ERROR"
```

### CloudWatch Metrics

Métricas disponibles:
- **ECS**: CPU, memoria, network
- **RDS**: Connections, CPU, storage
- **ALB**: Request count, latency, errors
- **CloudFront**: Requests, data transfer

### GitHub Actions

```bash
# Ver runs del pipeline
gh run list

# Ver logs de un run
gh run view <run-id> --log

# Watch en tiempo real
gh run watch
```

## Costos

### Staging
- **Mensual**: ~$132 USD
- Incluye: ECS (4 tareas), RDS (2 instancias), ALB, NAT, S3, CloudFront

### Production
- **Mensual**: ~$500 USD
- Incluye: ECS (7 tareas), RDS Multi-AZ, ALB, NAT x2, S3, CloudFront

**💡 Tip:** Usa Reserved Instances para reducir costos hasta 60%

## Troubleshooting

### Pipeline falla en tests
```bash
# Ejecutar localmente
cd microservices
mvn clean test

cd frontend-store
npm test
```

### Pipeline falla en build
```bash
# Probar Docker build localmente
docker build -t test-image -f microservices/auth-service/Dockerfile microservices/auth-service
```

### Despliegue falla en ECS
```bash
# Ver estado del servicio
aws ecs describe-services \
  --cluster prueba-tecnica-staging \
  --services auth-service

# Ver logs
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow
```

### Smoke tests fallan
```bash
# Ejecutar manualmente
./scripts/smoke-tests.sh https://staging-api.mitienda.com

# Verificar health
curl https://staging-api.mitienda.com/actuator/health
```

## Mejores Prácticas

### Commits

```bash
# Usar conventional commits
git commit -m "feat: Add new feature"
git commit -m "fix: Fix bug in auth"
git commit -m "docs: Update CI/CD docs"
git commit -m "ci: Update pipeline configuration"
```

### Pull Requests

1. Crear feature branch desde `develop`
2. Desarrollar y hacer commits
3. Push y crear PR hacia `develop`
4. CI/CD ejecutará tests automáticamente
5. Review y merge
6. Despliegue automático a staging

### Releases

1. Cuando staging esté estable
2. Crear PR de `develop` → `main`
3. Review exhaustivo
4. Merge a `main`
5. Aprobar despliegue manual a producción
6. Se crea release automáticamente en GitHub

## Seguridad

### Secretos
- Nunca commitear secretos en código
- Usar GitHub Secrets
- Rotar Access Keys cada 90 días
- Usar IAM roles con mínimo privilegio

### Imágenes Docker
- Escaneo automático con Trivy
- Usar imágenes base oficiales
- Usuario no-root
- Multi-stage builds

### Vulnerabilidades
- OWASP Dependency Check en cada build
- NPM Audit para frontend
- SonarCloud para análisis estático
- Alertas automáticas en GitHub Security

## Recursos Adicionales

### Documentación Externa
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [AWS ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Terraform AWS](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)

### Herramientas
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [Trivy](https://github.com/aquasecurity/trivy)
- [SonarCloud](https://sonarcloud.io)

## Soporte

Si encuentras problemas:
1. Revisa la documentación detallada en este directorio
2. Verifica los logs en CloudWatch y GitHub Actions
3. Consulta la sección de Troubleshooting
4. Revisa los issues en GitHub

---

**Última actualización:** 2025-10-21

**Mantenedores:**
- CI/CD Pipeline: GitHub Actions
- Infraestructura: Terraform + AWS
- Monitoreo: CloudWatch + GitHub
