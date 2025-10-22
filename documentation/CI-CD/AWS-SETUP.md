# Guía de Setup de AWS Infrastructure

Esta guía te ayudará a configurar toda la infraestructura AWS necesaria para ejecutar los microservicios.

## Tabla de Contenidos

1. [Opciones de Despliegue](#opciones-de-despliegue)
2. [Opción 1: Terraform (Recomendado)](#opción-1-terraform-recomendado)
3. [Opción 2: AWS Console Manual](#opción-2-aws-console-manual)
4. [Opción 3: AWS CLI Scripts](#opción-3-aws-cli-scripts)
5. [Configuración Post-Despliegue](#configuración-post-despliegue)
6. [Estimación de Costos](#estimación-de-costos)

---

## Opciones de Despliegue

Tienes 3 opciones para desplegar la infraestructura:

| Opción   | Dificultad | Tiempo | Reproducible | Recomendado |
|----------|------------|--------|--------------|-------------|
| Terraform| Media      | 15 min | ✅ Sí        | ✅ Sí       |
| Console  | Baja       | 60 min | ❌ No        | ❌ No       |
| CLI      | Alta       | 30 min | ⚠️ Parcial   | ⚠️ Parcial  |

---

## Opción 1: Terraform (Recomendado)

### Prerrequisitos

```bash
# 1. Instalar Terraform
# Windows (Chocolatey)
choco install terraform

# macOS (Homebrew)
brew install terraform

# Linux
wget https://releases.hashicorp.com/terraform/1.6.0/terraform_1.6.0_linux_amd64.zip
unzip terraform_1.6.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/

# 2. Verificar instalación
terraform --version

# 3. Configurar AWS CLI
aws configure
# AWS Access Key ID: TU_ACCESS_KEY
# AWS Secret Access Key: TU_SECRET_KEY
# Default region: us-east-1
# Default output format: json
```

### Paso 1: Crear S3 Bucket para Terraform State

```bash
# Crear bucket para state files (cambiar nombre si ya existe)
aws s3api create-bucket \
  --bucket prueba-tecnica-terraform-state \
  --region us-east-1

# Habilitar versionado
aws s3api put-bucket-versioning \
  --bucket prueba-tecnica-terraform-state \
  --versioning-configuration Status=Enabled

# Habilitar encriptación
aws s3api put-bucket-encryption \
  --bucket prueba-tecnica-terraform-state \
  --server-side-encryption-configuration '{
    "Rules": [{
      "ApplyServerSideEncryptionByDefault": {
        "SSEAlgorithm": "AES256"
      }
    }]
  }'

# Crear DynamoDB table para state locking
aws dynamodb create-table \
  --table-name terraform-state-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1
```

### Paso 2: Inicializar Terraform

```bash
cd aws-infrastructure/terraform

# Inicializar Terraform (descarga providers)
terraform init

# Debería ver:
# Terraform has been successfully initialized!
```

### Paso 3: Desplegar Staging

```bash
# Crear workspace para staging
terraform workspace new staging
terraform workspace select staging

# Ver plan de ejecución (no hace cambios aún)
terraform plan \
  -var="environment=staging" \
  -var="db_password=TU_PASSWORD_SEGURO_AQUI"

# Revisar el output. Debería mostrar:
# - VPC con subnets
# - ECS Cluster
# - 4 task definitions
# - RDS instances
# - Load balancer
# - S3 y CloudFront
# - etc.

# Si todo se ve bien, aplicar
terraform apply \
  -var="environment=staging" \
  -var="db_password=TU_PASSWORD_SEGURO_AQUI"

# Escribir "yes" cuando pregunte
# ⏱️ Esto tomará ~15-20 minutos
```

### Paso 4: Desplegar Production

```bash
# Crear workspace para production
terraform workspace new production
terraform workspace select production

# Aplicar configuración de producción
terraform apply \
  -var="environment=production" \
  -var="db_password=TU_PASSWORD_SEGURO_AQUI_DIFERENTE"

# ⏱️ Esto tomará ~15-20 minutos
```

### Paso 5: Obtener Outputs

```bash
# Ver todos los outputs
terraform output

# Outputs específicos
terraform output alb_dns_name
terraform output ecs_cluster_name
terraform output cloudfront_domain_name
terraform output ecr_repositories

# Guardar outputs en archivo
terraform output -json > ../outputs.json
```

### Paso 6: Guardar Información Importante

```bash
# Crear archivo con información importante
cat > ../DEPLOYMENT-INFO.md <<EOF
# Deployment Information - Staging

## Infrastructure
- **ECS Cluster**: $(terraform output -raw ecs_cluster_name)
- **ALB DNS**: $(terraform output -raw alb_dns_name)
- **CloudFront**: $(terraform output -raw cloudfront_domain_name)

## RDS Endpoints
- **Auth DB**: $(terraform output -raw auth_db_endpoint)
- **Products DB**: $(terraform output -raw products_db_endpoint)

## Generated on
$(date)
EOF
```

---

## Opción 2: AWS Console Manual

Si prefieres usar la interfaz gráfica de AWS Console:

### 1. Crear VPC

1. Ir a **VPC Console**
2. Click en **Create VPC**
3. Configurar:
   - Name: `prueba-tecnica-staging-vpc`
   - IPv4 CIDR: `10.1.0.0/16`
4. Crear **3 subnets públicas** en diferentes AZs
5. Crear **3 subnets privadas** en diferentes AZs
6. Crear **Internet Gateway** y adjuntar a VPC
7. Crear **NAT Gateway** en subnet pública
8. Configurar **Route Tables**

### 2. Crear Security Groups

#### ECS Tasks SG
- Name: `prueba-tecnica-staging-ecs-tasks-sg`
- VPC: La VPC creada
- Inbound: Todo el tráfico desde ALB SG
- Outbound: Todo

#### ALB SG
- Name: `prueba-tecnica-staging-alb-sg`
- Inbound:
  - HTTP (80) desde 0.0.0.0/0
  - HTTPS (443) desde 0.0.0.0/0
- Outbound: Todo

#### RDS SG
- Name: `prueba-tecnica-staging-rds-sg`
- Inbound: MySQL (3306) desde ECS Tasks SG
- Outbound: Todo

### 3. Crear RDS Instances

1. Ir a **RDS Console** → **Create database**
2. **Auth Database:**
   - Engine: MySQL 8.0
   - Template: Dev/Test
   - DB instance: db.t3.micro
   - DB name: `prueba_tecnica_auth`
   - Master username: `admin`
   - Master password: Tu password seguro
   - VPC: La VPC creada
   - Subnet group: Crear nuevo con subnets privadas
   - Security group: RDS SG
   - Initial database: `prueba_tecnica_auth`
3. Repetir para **Products Database** (`prueba_tecnica_products`)

### 4. Crear ECR Repositories

```bash
# Más fácil con CLI
services=(
  "discovery-server"
  "api-gateway"
  "auth-service"
  "product-service"
  "cart-service"
  "order-service"
  "frontend-store"
)

for service in "${services[@]}"; do
  aws ecr create-repository \
    --repository-name prueba-tecnica/$service \
    --region us-east-1 \
    --image-scanning-configuration scanOnPush=true
done
```

### 5. Crear ECS Cluster

1. Ir a **ECS Console** → **Create cluster**
2. Nombre: `prueba-tecnica-staging`
3. Infrastructure: AWS Fargate
4. Container insights: Enabled

### 6. Crear Application Load Balancer

1. Ir a **EC2 Console** → **Load Balancers** → **Create**
2. Type: Application Load Balancer
3. Name: `prueba-tecnica-staging-alb`
4. Scheme: Internet-facing
5. VPC: La VPC creada
6. Subnets: Las 3 subnets públicas
7. Security group: ALB SG
8. Crear Target Group:
   - Type: IP
   - Name: `prueba-tecnica-staging-api-gw-tg`
   - Port: 8080
   - Health check: `/actuator/health`

### 7. Crear S3 Bucket y CloudFront

#### S3:
1. Ir a **S3 Console** → **Create bucket**
2. Name: `prueba-tecnica-frontend-staging`
3. Region: us-east-1
4. Block all public access: Enabled
5. Versioning: Enabled

#### CloudFront:
1. Ir a **CloudFront Console** → **Create distribution**
2. Origin domain: Tu bucket S3
3. Origin access: Origin access control
4. Viewer protocol: Redirect HTTP to HTTPS
5. Default root object: `index.html`
6. Custom error response: 404 → 200 → `/index.html`

**Nota:** Este proceso es tedioso y propenso a errores. Terraform es mucho mejor.

---

## Opción 3: AWS CLI Scripts

Hemos incluido scripts automatizados:

```bash
# Ver scripts disponibles
ls aws-infrastructure/scripts/

# Ejecutar setup completo
cd aws-infrastructure/scripts
chmod +x setup-staging.sh
./setup-staging.sh

# Esto ejecutará todos los pasos automáticamente
```

---

## Configuración Post-Despliegue

### 1. Inicializar Bases de Datos

```bash
# Obtener endpoints de RDS
RDS_AUTH_ENDPOINT=$(terraform output -raw auth_db_endpoint)
RDS_PRODUCTS_ENDPOINT=$(terraform output -raw products_db_endpoint)

# Conectar y ejecutar scripts SQL
mysql -h $RDS_AUTH_ENDPOINT -u admin -p < ../../documentation/DB/auth-schema.sql
mysql -h $RDS_PRODUCTS_ENDPOINT -u admin -p < ../../documentation/DB/products-schema.sql
```

### 2. Configurar DNS (Opcional)

Si tienes un dominio:

```bash
# Obtener DNS del ALB
ALB_DNS=$(terraform output -raw alb_dns_name)
CLOUDFRONT_DNS=$(terraform output -raw cloudfront_domain_name)

# Crear registros en Route 53 o tu proveedor DNS
# CNAME: api.mitienda.com → $ALB_DNS
# CNAME: www.mitienda.com → $CLOUDFRONT_DNS
```

### 3. Crear Task Definitions en ECS

Las task definitions se crean automáticamente con Terraform. Para verificar:

```bash
# Listar task definitions
aws ecs list-task-definitions \
  --family-prefix prueba-tecnica-staging

# Ver detalles de una task definition
aws ecs describe-task-definition \
  --task-definition prueba-tecnica-staging-auth-service
```

### 4. Configurar CloudWatch Alarms

```bash
# Crear alarm para ECS CPU
aws cloudwatch put-metric-alarm \
  --alarm-name "prueba-tecnica-staging-auth-service-high-cpu" \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --dimensions Name=ServiceName,Value=auth-service Name=ClusterName,Value=prueba-tecnica-staging

# Crear alarm para RDS connections
aws cloudwatch put-metric-alarm \
  --alarm-name "prueba-tecnica-staging-rds-high-connections" \
  --alarm-description "Alert when DB connections exceed 80" \
  --metric-name DatabaseConnections \
  --namespace AWS/RDS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --dimensions Name=DBInstanceIdentifier,Value=prueba-tecnica-staging-auth-db
```

### 5. Configurar CloudWatch Logs Insights

Queries útiles:

```sql
-- Errores en los servicios
fields @timestamp, @message
| filter @message like /ERROR/
| sort @timestamp desc
| limit 100

-- Latencia de requests
fields @timestamp, @message
| filter @message like /request/
| parse @message /duration=(?<duration>\d+)ms/
| stats avg(duration), max(duration), min(duration) by bin(5m)

-- Top IPs con más requests
fields @timestamp, @message
| parse @message /ip=(?<ip>\d+\.\d+\.\d+\.\d+)/
| stats count() as requests by ip
| sort requests desc
| limit 20
```

---

## Estimación de Costos

### Staging Environment

| Servicio                  | Configuración           | Costo Mensual (USD) |
|---------------------------|-------------------------|---------------------|
| **ECS Fargate**           |                         |                     |
| - Discovery Server        | 0.5 vCPU, 1GB RAM       | $7.50               |
| - Auth Service            | 0.5 vCPU, 1GB RAM       | $7.50               |
| - Product Service         | 0.5 vCPU, 1GB RAM       | $7.50               |
| - API Gateway             | 0.5 vCPU, 1GB RAM       | $7.50               |
| **Subtotal ECS**          |                         | **$30.00**          |
|                           |                         |                     |
| **RDS MySQL**             |                         |                     |
| - Auth DB                 | db.t3.micro, 20GB       | $15.00              |
| - Products DB             | db.t3.micro, 20GB       | $15.00              |
| **Subtotal RDS**          |                         | **$30.00**          |
|                           |                         |                     |
| **Application Load Balancer** | 1 instancia         | $20.00              |
| **NAT Gateway**           | 1 instancia             | $32.00              |
| **S3**                    | <5GB storage, <100GB transfer | $2.00     |
| **CloudFront**            | <100GB data transfer    | $8.00               |
| **ECR**                   | <50GB storage           | $5.00               |
| **CloudWatch Logs**       | <10GB logs/mes          | $5.00               |
|                           |                         |                     |
| **TOTAL MENSUAL STAGING** |                         | **~$132.00**        |

### Production Environment

| Servicio                  | Configuración           | Costo Mensual (USD) |
|---------------------------|-------------------------|---------------------|
| **ECS Fargate**           |                         |                     |
| - Discovery Server (x1)   | 0.5 vCPU, 1GB RAM       | $7.50               |
| - Auth Service (x2)       | 0.5 vCPU, 1GB RAM       | $15.00              |
| - Product Service (x2)    | 0.5 vCPU, 1GB RAM       | $15.00              |
| - API Gateway (x2)        | 0.5 vCPU, 1GB RAM       | $15.00              |
| **Subtotal ECS**          |                         | **$52.50**          |
|                           |                         |                     |
| **RDS MySQL**             |                         |                     |
| - Auth DB (Multi-AZ)      | db.t3.medium, 100GB     | $120.00             |
| - Products DB (Multi-AZ)  | db.t3.medium, 100GB     | $120.00             |
| **Subtotal RDS**          |                         | **$240.00**         |
|                           |                         |                     |
| **Application Load Balancer** | 1 instancia         | $20.00              |
| **NAT Gateway (x2)**      | 2 instancias            | $64.00              |
| **S3**                    | <10GB storage           | $3.00               |
| **CloudFront**            | <1TB data transfer      | $85.00              |
| **ECR**                   | <100GB storage          | $10.00              |
| **CloudWatch Logs**       | <50GB logs/mes          | $25.00              |
|                           |                         |                     |
| **TOTAL MENSUAL PRODUCTION** |                      | **~$499.50**        |

**Notas:**
- Precios aproximados basados en región us-east-1
- No incluye data transfer out (variable según uso)
- Asume tráfico bajo-medio
- Para reducir costos en staging, considera apagar recursos fuera de horario laboral

### Optimización de Costos

#### Para Staging:
```bash
# Detener servicios ECS cuando no se usen (ahorra ~$30/mes)
aws ecs update-service --cluster prueba-tecnica-staging \
  --service auth-service --desired-count 0

# Cambiar RDS a menor capacidad
# db.t3.micro → db.t4g.micro (ARM, más barato)

# Usar Spot Instances para ECS (hasta 70% descuento)
# Configurar en task definition: capacityProviderStrategy
```

#### Para Production:
```bash
# Usar Reserved Instances para RDS (hasta 60% descuento)
aws rds purchase-reserved-db-instances-offering \
  --reserved-db-instances-offering-id <offering-id> \
  --reserved-db-instance-id prueba-tecnica-prod-rds-ri

# Usar Savings Plans para ECS (hasta 50% descuento)
# Configurar en AWS Cost Management

# Habilitar Auto Scaling para ECS
# Escalar down en horas de bajo tráfico
```

---

## Verificación del Setup

### Checklist Post-Despliegue

- [ ] VPC creada con subnets públicas y privadas
- [ ] Security Groups configurados correctamente
- [ ] RDS instances corriendo y accesibles
- [ ] ECS Cluster creado
- [ ] ECR Repositories creados
- [ ] Application Load Balancer funcionando
- [ ] S3 bucket y CloudFront distribution creados
- [ ] IAM roles para ECS task execution creados
- [ ] CloudWatch Logs groups creados
- [ ] Bases de datos inicializadas con schemas

### Tests de Conectividad

```bash
# Test 1: Verificar RDS
mysql -h <RDS_ENDPOINT> -u admin -p -e "SHOW DATABASES;"

# Test 2: Verificar ECR
aws ecr describe-repositories --query 'repositories[*].repositoryName'

# Test 3: Verificar ECS Cluster
aws ecs list-clusters

# Test 4: Verificar ALB
curl -v http://<ALB_DNS>/

# Test 5: Verificar CloudFront
curl -v https://<CLOUDFRONT_DNS>/
```

---

## Troubleshooting

### Terraform Errors

#### Error: "Bucket already exists"
```bash
# Cambiar nombre del bucket en main.tf
# o eliminar bucket existente:
aws s3 rb s3://prueba-tecnica-terraform-state --force
```

#### Error: "VPC Limit Exceeded"
```bash
# Listar VPCs existentes
aws ec2 describe-vpcs --query 'Vpcs[*].[VpcId,Tags[?Key==`Name`].Value|[0]]'

# Eliminar VPCs no usadas
aws ec2 delete-vpc --vpc-id vpc-xxxxx
```

### RDS Connection Issues

```bash
# Verificar security group
aws ec2 describe-security-groups --group-ids sg-xxxxx

# Verificar subnet group
aws rds describe-db-subnet-groups

# Test de conectividad desde EC2 en misma VPC
# Crear EC2 temporal en subnet privada y probar:
mysql -h <RDS_ENDPOINT> -u admin -p
```

### ECS Service Won't Start

```bash
# Ver eventos del servicio
aws ecs describe-services \
  --cluster prueba-tecnica-staging \
  --services auth-service

# Ver logs de tareas fallidas
aws ecs describe-tasks \
  --cluster prueba-tecnica-staging \
  --tasks <task-arn>

# Ver logs en CloudWatch
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow
```

---

## Limpieza de Recursos

### Destruir con Terraform

```bash
# Staging
terraform workspace select staging
terraform destroy -var="environment=staging" -var="db_password=dummy"

# Production
terraform workspace select production
terraform destroy -var="environment=production" -var="db_password=dummy"

# Eliminar workspaces
terraform workspace select default
terraform workspace delete staging
terraform workspace delete production
```

### Eliminación Manual

Si algo falla con Terraform:

```bash
# 1. Eliminar ECS Services
aws ecs update-service --cluster prueba-tecnica-staging \
  --service auth-service --desired-count 0
aws ecs delete-service --cluster prueba-tecnica-staging \
  --service auth-service --force

# 2. Eliminar ECS Cluster
aws ecs delete-cluster --cluster prueba-tecnica-staging

# 3. Eliminar RDS
aws rds delete-db-instance \
  --db-instance-identifier prueba-tecnica-staging-auth-db \
  --skip-final-snapshot

# 4. Eliminar ALB
aws elbv2 delete-load-balancer --load-balancer-arn <arn>

# 5. Eliminar VPC (requiere eliminar todos los recursos dentro primero)
aws ec2 delete-vpc --vpc-id vpc-xxxxx
```

---

## Próximos Pasos

1. ✅ Completar este setup
2. Configurar secretos de GitHub (ver `SECRETOS-GITHUB.md`)
3. Hacer push a `develop` para probar CI/CD
4. Verificar despliegue en staging
5. Hacer merge a `main` para desplegar a producción

---

## Recursos Adicionales

- [AWS ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS Pricing Calculator](https://calculator.aws/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
