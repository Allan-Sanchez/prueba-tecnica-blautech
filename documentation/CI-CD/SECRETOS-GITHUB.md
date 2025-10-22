# Configuración de Secretos de GitHub

Esta guía detalla todos los secretos necesarios para configurar el pipeline de CI/CD.

## Tabla de Contenidos

1. [Acceso a Configuración de Secretos](#acceso-a-configuración-de-secretos)
2. [Secretos Obligatorios](#secretos-obligatorios)
3. [Secretos Opcionales](#secretos-opcionales)
4. [Cómo Obtener Cada Secreto](#cómo-obtener-cada-secreto)
5. [Variables de Entorno](#variables-de-entorno)
6. [Verificación](#verificación)

---

## Acceso a Configuración de Secretos

1. Ve a tu repositorio en GitHub
2. Click en **Settings** (Configuración)
3. En el menú lateral, click en **Secrets and variables** → **Actions**
4. Click en **New repository secret**

---

## Secretos Obligatorios

Estos secretos son necesarios para que el pipeline funcione:

### AWS Credentials

| Nombre del Secreto      | Descripción                                    | Ejemplo                      |
|-------------------------|------------------------------------------------|------------------------------|
| `AWS_ACCESS_KEY_ID`     | Access Key ID del IAM user                     | `AKIAIOSFODNN7EXAMPLE`       |
| `AWS_SECRET_ACCESS_KEY` | Secret Access Key del IAM user                 | `wJalrXUtnFEMI/K7MDENG/...`  |
| `AWS_ACCOUNT_ID`        | ID de tu cuenta AWS (12 dígitos)              | `123456789012`               |

### Base de Datos

| Nombre del Secreto | Descripción                                      | Requisitos                    |
|--------------------|--------------------------------------------------|-------------------------------|
| `DB_PASSWORD`      | Password para todas las instancias RDS MySQL     | Min. 16 caracteres, complejo  |

### CloudFront

| Nombre del Secreto                       | Descripción                               | Ejemplo                    |
|------------------------------------------|-------------------------------------------|----------------------------|
| `CLOUDFRONT_DISTRIBUTION_ID_STAGING`     | ID de CloudFront Distribution para staging| `E1234EXAMPLE`             |
| `CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION`  | ID de CloudFront Distribution para prod   | `E5678EXAMPLE`             |

---

## Secretos Opcionales

Estos secretos mejoran el pipeline pero no son estrictamente necesarios:

### SonarCloud (Análisis de Calidad de Código)

| Nombre del Secreto   | Descripción                        | Dónde obtenerlo              |
|----------------------|------------------------------------|------------------------------|
| `SONAR_TOKEN`        | Token de autenticación SonarCloud  | https://sonarcloud.io        |
| `SONAR_PROJECT_KEY`  | Key del proyecto en SonarCloud     | Panel de SonarCloud          |
| `SONAR_ORGANIZATION` | Nombre de tu organización          | Panel de SonarCloud          |

**Nota:** Si no configuras SonarCloud, ese paso del pipeline se saltará automáticamente (`continue-on-error: true`).

---

## Cómo Obtener Cada Secreto

### 1. AWS Access Keys

#### Opción A: Crear un nuevo IAM User (Recomendado)

```bash
# 1. Crear IAM user
aws iam create-user --user-name github-actions-deployer

# 2. Crear Access Key
aws iam create-access-key --user-name github-actions-deployer

# Salida (guarda estos valores):
# {
#     "AccessKey": {
#         "AccessKeyId": "AKIAIOSFODNN7EXAMPLE",
#         "SecretAccessKey": "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
#         ...
#     }
# }
```

#### Opción B: Usar AWS Console

1. Ir a **IAM Console** → **Users** → **Add users**
2. Nombre: `github-actions-deployer`
3. Seleccionar: **Access key - Programmatic access**
4. Adjuntar políticas (siguiente sección)
5. Crear usuario y **guardar Access Key y Secret**

#### Políticas Necesarias

El IAM user necesita estas políticas:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "ecr:PutImage",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ecs:UpdateService",
        "ecs:DescribeServices",
        "ecs:DescribeTasks",
        "ecs:ListTasks"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:ListBucket",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::prueba-tecnica-frontend-*",
        "arn:aws:s3:::prueba-tecnica-frontend-*/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "cloudfront:CreateInvalidation"
      ],
      "Resource": "*"
    }
  ]
}
```

**Crear y adjuntar la política:**

```bash
# Crear archivo policy.json con el contenido de arriba, luego:

# Crear la política
aws iam create-policy \
  --policy-name GitHubActionsDeployerPolicy \
  --policy-document file://policy.json

# Adjuntar al usuario
aws iam attach-user-policy \
  --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::YOUR_ACCOUNT_ID:policy/GitHubActionsDeployerPolicy
```

### 2. AWS Account ID

Tu Account ID es un número de 12 dígitos.

**Obtenerlo:**

```bash
# Opción 1: AWS CLI
aws sts get-caller-identity --query Account --output text

# Opción 2: AWS Console
# Aparece en la esquina superior derecha al hacer click en tu nombre
```

### 3. Database Password

Genera un password seguro para RDS MySQL:

```bash
# Generar password aleatorio (Linux/Mac)
openssl rand -base64 32

# Generar password aleatorio (Windows PowerShell)
Add-Type -AssemblyName System.Web
[System.Web.Security.Membership]::GeneratePassword(32, 10)
```

**Requisitos:**
- Mínimo 16 caracteres
- Incluir mayúsculas, minúsculas, números y símbolos
- NO incluir caracteres especiales problemáticos: `@`, `/`, `"`, `'`

**Ejemplo válido:**
```
Kj8mN2pLq9RtYu3VwXz4Aa1Bb5Cc7Dd
```

### 4. CloudFront Distribution IDs

Después de crear la infraestructura con Terraform, obtén los IDs:

```bash
# Ver outputs de Terraform
cd aws-infrastructure/terraform

# Staging
terraform workspace select staging
terraform output cloudfront_domain_name

# Production
terraform workspace select production
terraform output cloudfront_domain_name
```

**O desde AWS Console:**
1. Ir a **CloudFront Console**
2. Ver lista de distributions
3. Copiar el ID (formato: `E1234ABCD5678`)

**O con AWS CLI:**
```bash
aws cloudfront list-distributions --query 'DistributionList.Items[*].[Id,Comment]' --output table
```

### 5. SonarCloud (Opcional)

#### Paso 1: Crear cuenta en SonarCloud

1. Ir a https://sonarcloud.io
2. Sign in con GitHub
3. Autorizar SonarCloud

#### Paso 2: Crear organización

1. Click en **+** (arriba derecha) → **Create new organization**
2. Elegir tu cuenta de GitHub
3. Crear organización

#### Paso 3: Importar proyecto

1. Click en **+** → **Analyze new project**
2. Seleccionar tu repositorio
3. Click en **Set Up**

#### Paso 4: Obtener token

1. Ir a **My Account** → **Security**
2. Generar nuevo token
3. Nombre: `github-actions`
4. Copiar el token (solo se muestra una vez)

#### Paso 5: Obtener Project Key y Organization

- **Project Key**: Aparece en la página del proyecto (formato: `organization_repository`)
- **Organization**: Tu nombre de organización en SonarCloud

---

## Variables de Entorno

Además de secretos, puedes configurar variables de entorno no sensibles:

**Settings → Secrets and variables → Actions → Variables**

| Nombre            | Valor         | Descripción                    |
|-------------------|---------------|--------------------------------|
| `AWS_REGION`      | `us-east-1`   | Región AWS por defecto         |
| `NODE_VERSION`    | `20`          | Versión de Node.js             |
| `JAVA_VERSION`    | `21`          | Versión de Java                |

**Nota:** Estas variables ya están configuradas en el workflow con valores por defecto.

---

## Verificación

### Checklist de Secretos

Antes de hacer push, verifica que hayas configurado:

- [ ] `AWS_ACCESS_KEY_ID`
- [ ] `AWS_SECRET_ACCESS_KEY`
- [ ] `AWS_ACCOUNT_ID`
- [ ] `DB_PASSWORD`
- [ ] `CLOUDFRONT_DISTRIBUTION_ID_STAGING`
- [ ] `CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION`

Opcionales:
- [ ] `SONAR_TOKEN`
- [ ] `SONAR_PROJECT_KEY`
- [ ] `SONAR_ORGANIZATION`

### Probar Configuración

#### Test 1: Verificar AWS Credentials

Crea un workflow temporal para probar:

```yaml
# .github/workflows/test-aws.yml
name: Test AWS Credentials

on: workflow_dispatch

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Test AWS CLI
        run: |
          aws sts get-caller-identity
          aws ecr describe-repositories --query 'repositories[*].repositoryName'
```

Ejecutar desde GitHub:
1. **Actions** → **Test AWS Credentials** → **Run workflow**

#### Test 2: Verificar ECR Access

```bash
# Localmente con las mismas credenciales
export AWS_ACCESS_KEY_ID="tu_access_key"
export AWS_SECRET_ACCESS_KEY="tu_secret_key"
export AWS_REGION="us-east-1"

# Intentar login a ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com

# Si funciona, las credenciales son correctas
```

---

## Seguridad y Buenas Prácticas

### 1. Rotación de Secretos

Rotar Access Keys cada 90 días:

```bash
# Crear nueva Access Key
aws iam create-access-key --user-name github-actions-deployer

# Actualizar secretos en GitHub

# Eliminar Access Key antigua
aws iam delete-access-key \
  --user-name github-actions-deployer \
  --access-key-id OLD_ACCESS_KEY_ID
```

### 2. Principio de Mínimo Privilegio

- Usar IAM user dedicado para CI/CD (no tu usuario personal)
- Adjuntar solo las políticas necesarias
- Revisar periódicamente permisos

### 3. Monitoreo

Configurar alertas de CloudTrail para:
- Uso de Access Keys
- Cambios en políticas IAM
- Despliegues a producción

### 4. Auditoría

Revisar logs de GitHub Actions:
- ¿Quién ejecutó el workflow?
- ¿Qué cambios se desplegaron?
- ¿Hubo errores de autenticación?

---

## Troubleshooting

### Error: "Unable to locate credentials"

**Causa:** Secretos no configurados o mal escritos

**Solución:**
1. Verificar nombres exactos de secretos (case-sensitive)
2. Verificar que no haya espacios en blanco al final
3. Re-crear secretos si es necesario

### Error: "Access Denied" al push a ECR

**Causa:** IAM user no tiene permisos ECR

**Solución:**
```bash
# Verificar permisos
aws iam list-attached-user-policies --user-name github-actions-deployer

# Adjuntar política necesaria
aws iam attach-user-policy \
  --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess
```

### Error: "AccessDenied" al invalidar CloudFront

**Causa:** IAM user no tiene permisos CloudFront

**Solución:**
```bash
# Crear política inline
aws iam put-user-policy \
  --user-name github-actions-deployer \
  --policy-name CloudFrontInvalidation \
  --policy-document '{
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Action": "cloudfront:CreateInvalidation",
      "Resource": "*"
    }]
  }'
```

---

## Recursos Adicionales

- [GitHub Actions Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [AWS IAM Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [AWS Access Keys Management](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)
