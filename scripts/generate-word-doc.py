#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Script para generar documentación Word del CI/CD Pipeline
Requiere: pip install python-docx
"""

try:
    from docx import Document
    from docx.shared import Inches, Pt, RGBColor
    from docx.enum.text import WD_ALIGN_PARAGRAPH
    from docx.enum.style import WD_STYLE_TYPE
except ImportError:
    print("ERROR: python-docx no está instalado")
    print("Instalar con: pip install python-docx")
    exit(1)

import os
from datetime import datetime

def add_heading_with_style(doc, text, level=1):
    """Agregar encabezado con estilo personalizado"""
    heading = doc.add_heading(text, level=level)
    heading.alignment = WD_ALIGN_PARAGRAPH.LEFT
    return heading

def add_table_of_contents(doc):
    """Agregar tabla de contenidos"""
    doc.add_heading('Tabla de Contenidos', level=1)

    toc_items = [
        "1. Introducción",
        "2. Descripción General del Pipeline",
        "3. Arquitectura del Sistema",
        "4. Tecnologías Utilizadas",
        "5. Etapas del Pipeline CI/CD",
        "   5.1. Tests Automatizados",
        "   5.2. Escaneo de Vulnerabilidades",
        "   5.3. Construcción de Imágenes Docker",
        "   5.4. Despliegue en AWS",
        "6. Configuración del Pipeline",
        "   6.1. Secretos de GitHub",
        "   6.2. Infraestructura AWS",
        "7. Estrategia de Branches",
        "8. Monitoreo y Logs",
        "9. Estimación de Costos",
        "10. Troubleshooting",
        "11. Anexos"
    ]

    for item in toc_items:
        p = doc.add_paragraph(item, style='List Number' if not item.startswith('   ') else 'List Bullet')

    doc.add_page_break()

def add_code_block(doc, code, language=""):
    """Agregar bloque de código con formato"""
    p = doc.add_paragraph()
    run = p.add_run(code)
    run.font.name = 'Consolas'
    run.font.size = Pt(9)
    run.font.color.rgb = RGBColor(0, 0, 128)
    p.paragraph_format.left_indent = Inches(0.5)
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(6)

    # Fondo gris claro simulado con borde
    p.paragraph_format.line_spacing = 1.0

def add_info_box(doc, title, content):
    """Agregar caja de información destacada"""
    p = doc.add_paragraph()
    run_title = p.add_run(f"📌 {title}\n")
    run_title.bold = True
    run_title.font.size = Pt(11)

    run_content = p.add_run(content)
    run_content.font.size = Pt(10)

    p.paragraph_format.left_indent = Inches(0.3)
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(6)

def create_cicd_documentation():
    """Crear documento Word completo"""
    doc = Document()

    # Configurar estilos del documento
    style = doc.styles['Normal']
    font = style.font
    font.name = 'Calibri'
    font.size = Pt(11)

    # ========== PORTADA ==========
    title = doc.add_heading('Documentación CI/CD Pipeline', level=0)
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER

    subtitle = doc.add_heading('Sistema de E-Commerce con Microservicios', level=2)
    subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER

    doc.add_paragraph()
    doc.add_paragraph()

    info_table = doc.add_table(rows=5, cols=2)
    info_table.style = 'Light Grid Accent 1'

    info_data = [
        ("Proyecto:", "Prueba Técnica - Microservicios"),
        ("Tecnologías:", "Spring Boot 3.5.5, React 18, AWS, Docker, Terraform"),
        ("Fecha:", datetime.now().strftime("%d/%m/%Y")),
        ("Versión:", "1.0"),
        ("Autor:", "Sistema Automatizado")
    ]

    for i, (label, value) in enumerate(info_data):
        info_table.rows[i].cells[0].text = label
        info_table.rows[i].cells[1].text = value
        info_table.rows[i].cells[0].paragraphs[0].runs[0].font.bold = True

    doc.add_page_break()

    # ========== TABLA DE CONTENIDOS ==========
    add_table_of_contents(doc)

    # ========== 1. INTRODUCCIÓN ==========
    doc.add_heading('1. Introducción', level=1)

    doc.add_paragraph(
        "Este documento describe la implementación completa del pipeline de CI/CD "
        "(Continuous Integration / Continuous Deployment) para el sistema de e-commerce "
        "basado en arquitectura de microservicios."
    )

    add_info_box(
        doc,
        "Objetivo del Pipeline",
        "Automatizar completamente el proceso de desarrollo, testing, construcción y "
        "despliegue de la aplicación, garantizando calidad, seguridad y consistencia "
        "en cada release."
    )

    doc.add_paragraph("El pipeline implementa las siguientes capacidades:")

    capabilities = [
        "Tests automatizados (unitarios e integración)",
        "Escaneo de vulnerabilidades de seguridad",
        "Construcción automática de imágenes Docker",
        "Despliegue automático en entornos de staging y producción",
        "Monitoreo y alertas en tiempo real",
        "Rollback automático en caso de fallos"
    ]

    for cap in capabilities:
        doc.add_paragraph(cap, style='List Bullet')

    doc.add_page_break()

    # ========== 2. DESCRIPCIÓN GENERAL ==========
    doc.add_heading('2. Descripción General del Pipeline', level=1)

    doc.add_paragraph(
        "El pipeline de CI/CD está implementado con GitHub Actions y se ejecuta "
        "automáticamente en cada push o pull request al repositorio."
    )

    doc.add_heading('2.1. Flujo del Pipeline', level=2)

    doc.add_paragraph("El flujo completo del pipeline consta de 4 etapas principales:")

    # Tabla de flujo
    flow_table = doc.add_table(rows=5, cols=3)
    flow_table.style = 'Light List Accent 1'

    # Headers
    headers = ['Etapa', 'Descripción', 'Duración Aprox.']
    for i, header in enumerate(headers):
        cell = flow_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    # Data
    flow_data = [
        ("1. Tests", "Ejecución de tests unitarios e integración", "5-10 min"),
        ("2. Security", "Escaneo de vulnerabilidades (OWASP, Trivy)", "8-15 min"),
        ("3. Build", "Construcción de imágenes Docker", "10-15 min"),
        ("4. Deploy", "Despliegue a AWS ECS", "5-10 min")
    ]

    for i, (stage, desc, duration) in enumerate(flow_data, start=1):
        flow_table.rows[i].cells[0].text = stage
        flow_table.rows[i].cells[1].text = desc
        flow_table.rows[i].cells[2].text = duration

    doc.add_paragraph()
    add_info_box(
        doc,
        "Tiempo Total",
        "El pipeline completo toma aproximadamente 30-50 minutos en ejecutarse "
        "de principio a fin, dependiendo de la cantidad de cambios."
    )

    doc.add_page_break()

    # ========== 3. ARQUITECTURA ==========
    doc.add_heading('3. Arquitectura del Sistema', level=1)

    doc.add_heading('3.1. Microservicios', level=2)

    # Tabla de microservicios
    services_table = doc.add_table(rows=8, cols=4)
    services_table.style = 'Light Grid Accent 1'

    headers = ['Servicio', 'Puerto', 'Estado', 'Descripción']
    for i, header in enumerate(headers):
        cell = services_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    services_data = [
        ("Discovery Server", "8761", "✅ Completo", "Eureka - Service Discovery"),
        ("API Gateway", "8080", "✅ Completo", "Punto de entrada único"),
        ("Auth Service", "8081", "✅ Completo", "Autenticación JWT"),
        ("Product Service", "8082", "🔄 En progreso", "Gestión de productos"),
        ("Cart Service", "8083", "⏳ Pendiente", "Carrito de compras"),
        ("Order Service", "8084", "⏳ Pendiente", "Gestión de órdenes"),
        ("Frontend", "3000", "✅ Completo", "React SPA")
    ]

    for i, (service, port, status, desc) in enumerate(services_data, start=1):
        services_table.rows[i].cells[0].text = service
        services_table.rows[i].cells[1].text = port
        services_table.rows[i].cells[2].text = status
        services_table.rows[i].cells[3].text = desc

    doc.add_paragraph()

    doc.add_heading('3.2. Bases de Datos', level=2)
    doc.add_paragraph(
        "Cada microservicio tiene su propia base de datos MySQL independiente, "
        "siguiendo el patrón de Database per Service:"
    )

    dbs = [
        "prueba_tecnica_auth - Auth Service",
        "prueba_tecnica_products - Product Service",
        "prueba_tecnica_carts - Cart Service",
        "prueba_tecnica_orders - Order Service"
    ]

    for db in dbs:
        doc.add_paragraph(db, style='List Bullet')

    doc.add_page_break()

    # ========== 4. TECNOLOGÍAS ==========
    doc.add_heading('4. Tecnologías Utilizadas', level=1)

    doc.add_heading('4.1. Backend', level=2)
    backend_tech = [
        "Java 21 - Lenguaje de programación",
        "Spring Boot 3.5.5 - Framework principal",
        "Spring Cloud (Gateway, Eureka, LoadBalancer) - Microservicios",
        "Spring Security - Autenticación y autorización",
        "Spring Data JPA - Persistencia de datos",
        "MySQL 8.0 - Base de datos relacional",
        "Maven - Gestión de dependencias"
    ]
    for tech in backend_tech:
        doc.add_paragraph(tech, style='List Bullet')

    doc.add_heading('4.2. Frontend', level=2)
    frontend_tech = [
        "React 18 - Librería de UI",
        "TypeScript - Tipado estático",
        "Redux Toolkit Query - State management y API calls",
        "React Router DOM - Navegación",
        "Vite - Build tool y dev server",
        "SCSS - Estilos modulares"
    ]
    for tech in frontend_tech:
        doc.add_paragraph(tech, style='List Bullet')

    doc.add_heading('4.3. DevOps y CI/CD', level=2)
    devops_tech = [
        "Docker - Containerización",
        "GitHub Actions - CI/CD pipeline",
        "Terraform - Infrastructure as Code",
        "AWS ECS (Fargate) - Container orchestration",
        "AWS RDS - Bases de datos gestionadas",
        "AWS ECR - Docker registry",
        "AWS S3 + CloudFront - Hosting frontend",
        "AWS Application Load Balancer - Balanceo de carga"
    ]
    for tech in devops_tech:
        doc.add_paragraph(tech, style='List Bullet')

    doc.add_heading('4.4. Seguridad y Testing', level=2)
    security_tech = [
        "OWASP Dependency Check - Análisis de vulnerabilidades",
        "Trivy Scanner - Escaneo de imágenes Docker",
        "SonarCloud - Análisis estático de código",
        "JUnit 5 - Tests unitarios",
        "JaCoCo - Coverage de código",
        "ESLint - Linting de JavaScript/TypeScript"
    ]
    for tech in security_tech:
        doc.add_paragraph(tech, style='List Bullet')

    doc.add_page_break()

    # ========== 5. ETAPAS DEL PIPELINE ==========
    doc.add_heading('5. Etapas del Pipeline CI/CD', level=1)

    doc.add_heading('5.1. Tests Automatizados', level=2)

    doc.add_paragraph("Backend Tests:")
    backend_tests = [
        "Tests unitarios con JUnit 5",
        "Tests de integración con Spring Boot Test",
        "Generación de reportes de coverage con JaCoCo",
        "Upload de métricas a Codecov"
    ]
    for test in backend_tests:
        doc.add_paragraph(test, style='List Bullet')

    doc.add_paragraph()
    doc.add_paragraph("Frontend Tests:")
    frontend_tests = [
        "Linting con ESLint",
        "Tests unitarios con Jest/Vitest",
        "Generación de reportes de coverage"
    ]
    for test in frontend_tests:
        doc.add_paragraph(test, style='List Bullet')

    add_code_block(doc, """# Comando para ejecutar tests backend
cd microservices
mvn clean test

# Comando para ejecutar tests frontend
cd frontend-store
npm test -- --coverage""")

    doc.add_heading('5.2. Escaneo de Vulnerabilidades', level=2)

    doc.add_paragraph(
        "El pipeline implementa múltiples capas de análisis de seguridad:"
    )

    # Tabla de herramientas de seguridad
    security_table = doc.add_table(rows=5, cols=3)
    security_table.style = 'Light List Accent 1'

    sec_headers = ['Herramienta', 'Objetivo', 'Severidad']
    for i, header in enumerate(sec_headers):
        cell = security_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    sec_data = [
        ("OWASP Dependency Check", "CVEs en dependencias Java", "CRITICAL, HIGH"),
        ("Trivy", "Vulnerabilidades en filesystem e imágenes", "CRITICAL, HIGH"),
        ("NPM Audit", "Vulnerabilidades en Node.js", "MODERATE+"),
        ("SonarCloud", "Code smells, bugs, security hotspots", "Todos")
    ]

    for i, (tool, target, severity) in enumerate(sec_data, start=1):
        security_table.rows[i].cells[0].text = tool
        security_table.rows[i].cells[1].text = target
        security_table.rows[i].cells[2].text = severity

    doc.add_paragraph()
    add_info_box(
        doc,
        "Integración con GitHub Security",
        "Todos los resultados de los escaneos se reportan automáticamente a "
        "GitHub Security tab en formato SARIF, permitiendo tracking y remediation."
    )

    doc.add_heading('5.3. Construcción de Imágenes Docker', level=2)

    doc.add_paragraph(
        "Todas las imágenes Docker utilizan multi-stage builds para optimización:"
    )

    doc.add_paragraph("Características de las imágenes:")
    docker_features = [
        "Multi-stage builds (builder + runtime)",
        "Imágenes base Alpine Linux (menor tamaño)",
        "Usuario no-root para seguridad",
        "Health checks configurados",
        "Layers cacheados para builds rápidos"
    ]
    for feature in docker_features:
        doc.add_paragraph(feature, style='List Bullet')

    doc.add_paragraph()
    add_code_block(doc, """# Ejemplo de build
docker build -t auth-service:latest \\
  -f microservices/auth-service/Dockerfile \\
  microservices/auth-service

# Push a ECR
docker push $ECR_REGISTRY/auth-service:latest""")

    doc.add_heading('5.4. Despliegue en AWS', level=2)

    doc.add_paragraph(
        "El despliegue se realiza automáticamente a AWS ECS con Fargate:"
    )

    doc.add_paragraph("Orden de despliegue (crítico):")
    deploy_order = [
        "1. Discovery Server (Eureka) - Esperar hasta que esté healthy",
        "2. Auth Service + Product Service (paralelo)",
        "3. API Gateway - Después de que los servicios estén registrados",
        "4. Frontend - Deploy a S3 e invalidación de CloudFront"
    ]
    for step in deploy_order:
        doc.add_paragraph(step, style='List Number')

    doc.add_paragraph()
    add_info_box(
        doc,
        "Smoke Tests",
        "Después de cada despliegue, se ejecutan smoke tests automáticos para "
        "verificar que los servicios estén funcionando correctamente. Si fallan, "
        "se puede hacer rollback manual."
    )

    doc.add_page_break()

    # ========== 6. CONFIGURACIÓN ==========
    doc.add_heading('6. Configuración del Pipeline', level=1)

    doc.add_heading('6.1. Secretos de GitHub', level=2)

    doc.add_paragraph(
        "Los siguientes secretos deben configurarse en GitHub "
        "(Settings → Secrets and variables → Actions):"
    )

    # Tabla de secretos
    secrets_table = doc.add_table(rows=8, cols=3)
    secrets_table.style = 'Medium Grid 1 Accent 1'

    sec_headers = ['Secreto', 'Descripción', 'Ejemplo']
    for i, header in enumerate(sec_headers):
        cell = secrets_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    secrets_data = [
        ("AWS_ACCESS_KEY_ID", "Access Key del IAM user", "AKIAIOSFODNN7EXAMPLE"),
        ("AWS_SECRET_ACCESS_KEY", "Secret Access Key", "wJalrXUtnFEMI/K7MDENG/..."),
        ("AWS_ACCOUNT_ID", "ID de cuenta AWS (12 dígitos)", "123456789012"),
        ("DB_PASSWORD", "Password para RDS MySQL", "(16+ caracteres)"),
        ("CLOUDFRONT_DISTRIBUTION_ID_STAGING", "ID de CloudFront staging", "E1234EXAMPLE"),
        ("CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION", "ID de CloudFront prod", "E5678EXAMPLE"),
        ("SONAR_TOKEN", "Token de SonarCloud (opcional)", "sqp_...")
    ]

    for i, (secret, desc, example) in enumerate(secrets_data, start=1):
        secrets_table.rows[i].cells[0].text = secret
        secrets_table.rows[i].cells[1].text = desc
        secrets_table.rows[i].cells[2].text = example

    doc.add_paragraph()

    doc.add_heading('6.2. Infraestructura AWS', level=2)

    doc.add_paragraph(
        "La infraestructura se despliega usando Terraform (Infrastructure as Code):"
    )

    add_code_block(doc, """# Inicializar Terraform
cd aws-infrastructure/terraform
terraform init

# Crear workspace para staging
terraform workspace new staging
terraform workspace select staging

# Aplicar configuración
terraform apply \\
  -var="environment=staging" \\
  -var="db_password=YOUR_SECURE_PASSWORD"

# Repetir para production
terraform workspace new production
terraform apply \\
  -var="environment=production" \\
  -var="db_password=YOUR_SECURE_PASSWORD" """)

    doc.add_paragraph()
    doc.add_paragraph("Recursos creados por Terraform:")

    aws_resources = [
        "VPC con subnets públicas y privadas (3 AZs)",
        "ECS Cluster con service discovery",
        "Task definitions para cada microservicio",
        "RDS MySQL instances (una por microservicio)",
        "Application Load Balancer + Target Groups",
        "ECR Repositories para imágenes Docker",
        "S3 bucket + CloudFront distribution (frontend)",
        "Security Groups configurados",
        "IAM roles y policies",
        "CloudWatch Log Groups"
    ]

    for resource in aws_resources:
        doc.add_paragraph(resource, style='List Bullet')

    doc.add_page_break()

    # ========== 7. ESTRATEGIA DE BRANCHES ==========
    doc.add_heading('7. Estrategia de Branches y Ambientes', level=1)

    # Tabla de estrategia
    branch_table = doc.add_table(rows=4, cols=4)
    branch_table.style = 'Colorful List Accent 1'

    branch_headers = ['Branch', 'Acciones CI/CD', 'Ambiente', 'Auto-Deploy']
    for i, header in enumerate(branch_headers):
        cell = branch_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    branch_data = [
        ("feature/*", "Tests + Security scans", "N/A", "❌"),
        ("develop", "Tests + Scans + Build + Deploy", "Staging", "✅"),
        ("main", "Tests + Scans + Build + Deploy", "Production", "✅ (manual)")
    ]

    for i, (branch, actions, env, auto) in enumerate(branch_data, start=1):
        branch_table.rows[i].cells[0].text = branch
        branch_table.rows[i].cells[1].text = actions
        branch_table.rows[i].cells[2].text = env
        branch_table.rows[i].cells[3].text = auto

    doc.add_paragraph()

    doc.add_heading('Workflow Recomendado', level=2)

    workflow_steps = [
        "Crear feature branch desde develop",
        "Desarrollar funcionalidad con commits frecuentes",
        "Push a GitHub - Pipeline ejecuta tests y scans",
        "Crear Pull Request hacia develop",
        "Code review y aprobación",
        "Merge a develop - Deploy automático a Staging",
        "Testing en Staging",
        "Cuando esté listo, crear PR de develop → main",
        "Aprobación de equipo senior",
        "Merge a main - Aprobar deploy manual a Production"
    ]

    for i, step in enumerate(workflow_steps, start=1):
        doc.add_paragraph(f"{i}. {step}")

    doc.add_page_break()

    # ========== 8. MONITOREO ==========
    doc.add_heading('8. Monitoreo y Logs', level=1)

    doc.add_heading('8.1. CloudWatch Logs', level=2)

    doc.add_paragraph(
        "Todos los servicios envían logs a CloudWatch. Para acceder a los logs:"
    )

    add_code_block(doc, """# Ver logs en tiempo real
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow

# Buscar errores
aws logs filter-log-events \\
  --log-group-name /ecs/prueba-tecnica-staging \\
  --filter-pattern "ERROR"

# Logs de un periodo específico
aws logs filter-log-events \\
  --log-group-name /ecs/prueba-tecnica-staging \\
  --start-time 1609459200000 \\
  --end-time 1609545600000""")

    doc.add_heading('8.2. CloudWatch Metrics', level=2)

    doc.add_paragraph("Métricas disponibles automáticamente:")

    metrics = [
        "ECS: CPU Utilization, Memory Utilization, Network I/O",
        "RDS: Connections, CPU, Free Storage, Read/Write IOPS",
        "ALB: Request Count, Target Response Time, HTTP Errors",
        "CloudFront: Requests, Bytes Downloaded, Error Rate"
    ]

    for metric in metrics:
        doc.add_paragraph(metric, style='List Bullet')

    doc.add_heading('8.3. GitHub Actions Logs', level=2)

    doc.add_paragraph("Para ver logs del pipeline:")

    add_code_block(doc, """# Listar ejecuciones
gh run list

# Ver detalles de una ejecución
gh run view <run-id>

# Ver logs completos
gh run view <run-id> --log

# Watch en tiempo real
gh run watch""")

    doc.add_page_break()

    # ========== 9. COSTOS ==========
    doc.add_heading('9. Estimación de Costos', level=1)

    doc.add_heading('9.1. Ambiente de Staging', level=2)

    # Tabla de costos staging
    cost_staging_table = doc.add_table(rows=9, cols=3)
    cost_staging_table.style = 'Light Grid Accent 1'

    cost_headers = ['Servicio', 'Configuración', 'Costo Mensual (USD)']
    for i, header in enumerate(cost_headers):
        cell = cost_staging_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    staging_costs = [
        ("ECS Fargate (4 tareas)", "0.5 vCPU, 1GB RAM cada una", "$30.00"),
        ("RDS MySQL (2 instancias)", "db.t3.micro, 20GB cada una", "$30.00"),
        ("Application Load Balancer", "1 instancia", "$20.00"),
        ("NAT Gateway", "1 instancia", "$32.00"),
        ("S3 + CloudFront", "<100GB transfer", "$10.00"),
        ("ECR + CloudWatch", "Logs y registry", "$10.00"),
        ("", "TOTAL STAGING", "$132.00")
    ]

    for i, (service, config, cost) in enumerate(staging_costs, start=1):
        cost_staging_table.rows[i].cells[0].text = service
        cost_staging_table.rows[i].cells[1].text = config
        cost_staging_table.rows[i].cells[2].text = cost
        if "TOTAL" in service:
            for j in range(3):
                cost_staging_table.rows[i].cells[j].paragraphs[0].runs[0].font.bold = True

    doc.add_paragraph()

    doc.add_heading('9.2. Ambiente de Production', level=2)

    # Tabla de costos production
    cost_prod_table = doc.add_table(rows=9, cols=3)
    cost_prod_table.style = 'Light Grid Accent 1'

    for i, header in enumerate(cost_headers):
        cell = cost_prod_table.rows[0].cells[i]
        cell.text = header
        cell.paragraphs[0].runs[0].font.bold = True

    prod_costs = [
        ("ECS Fargate (7 tareas)", "0.5 vCPU, 1GB RAM, 2x replicas", "$52.50"),
        ("RDS MySQL Multi-AZ (2)", "db.t3.medium, 100GB cada una", "$240.00"),
        ("Application Load Balancer", "1 instancia", "$20.00"),
        ("NAT Gateway (2 AZs)", "2 instancias", "$64.00"),
        ("S3 + CloudFront", "<1TB transfer", "$88.00"),
        ("ECR + CloudWatch", "Logs y registry", "$35.50"),
        ("", "TOTAL PRODUCTION", "$500.00")
    ]

    for i, (service, config, cost) in enumerate(prod_costs, start=1):
        cost_prod_table.rows[i].cells[0].text = service
        cost_prod_table.rows[i].cells[1].text = config
        cost_prod_table.rows[i].cells[2].text = cost
        if "TOTAL" in service:
            for j in range(3):
                cost_prod_table.rows[i].cells[j].paragraphs[0].runs[0].font.bold = True

    doc.add_paragraph()

    doc.add_heading('9.3. Optimización de Costos', level=2)

    optimization = [
        "Reserved Instances para RDS: Hasta 60% de descuento",
        "Savings Plans para ECS: Hasta 50% de descuento",
        "Apagar ambiente de staging fuera de horario laboral: ~40% ahorro",
        "Usar instancias ARM (Graviton) para ECS: ~20% ahorro",
        "Optimizar imágenes Docker para reducir storage en ECR"
    ]

    for opt in optimization:
        doc.add_paragraph(opt, style='List Bullet')

    doc.add_page_break()

    # ========== 10. TROUBLESHOOTING ==========
    doc.add_heading('10. Troubleshooting', level=1)

    doc.add_heading('10.1. Pipeline Falla en Tests', level=2)

    doc.add_paragraph("Síntoma: Job 'test-backend' o 'test-frontend' falla")
    doc.add_paragraph()
    doc.add_paragraph("Solución:")

    add_code_block(doc, """# Ejecutar tests localmente - Backend
cd microservices
mvn clean test

# Ejecutar tests localmente - Frontend
cd frontend-store
npm test

# Ver logs detallados del pipeline en GitHub Actions""")

    doc.add_heading('10.2. Docker Build Falla', level=2)

    doc.add_paragraph("Síntoma: Job 'build-and-push' falla al construir imagen")
    doc.add_paragraph()
    doc.add_paragraph("Solución:")

    add_code_block(doc, """# Verificar Dockerfile localmente
docker build -t test-image \\
  -f microservices/auth-service/Dockerfile \\
  microservices/auth-service

# Ver logs detallados
docker build --progress=plain -t test-image .""")

    doc.add_heading('10.3. Despliegue a ECS Falla', level=2)

    doc.add_paragraph("Síntoma: Servicio no arranca o health check falla")
    doc.add_paragraph()
    doc.add_paragraph("Solución:")

    add_code_block(doc, """# Ver estado del servicio
aws ecs describe-services \\
  --cluster prueba-tecnica-staging \\
  --services auth-service

# Ver tareas fallidas
aws ecs list-tasks \\
  --cluster prueba-tecnica-staging \\
  --service-name auth-service \\
  --desired-status STOPPED

# Ver logs de la tarea
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow""")

    doc.add_heading('10.4. Smoke Tests Fallan', level=2)

    doc.add_paragraph("Síntoma: Tests de smoke fallan después del deploy")
    doc.add_paragraph()
    doc.add_paragraph("Solución:")

    add_code_block(doc, """# Ejecutar smoke tests manualmente
chmod +x scripts/smoke-tests.sh
./scripts/smoke-tests.sh https://staging-api.mitienda.com

# Verificar conectividad
curl -v https://staging-api.mitienda.com/actuator/health

# Verificar logs del API Gateway
aws logs tail /ecs/prueba-tecnica-staging/api-gateway --follow""")

    doc.add_page_break()

    # ========== 11. ANEXOS ==========
    doc.add_heading('11. Anexos', level=1)

    doc.add_heading('11.1. Estructura de Archivos del Proyecto', level=2)

    add_code_block(doc, """Prueba-tecnica/
├── .github/workflows/
│   └── ci-cd-pipeline.yml         # Pipeline principal
│
├── frontend-store/
│   ├── src/
│   ├── Dockerfile
│   └── nginx.conf
│
├── microservices/
│   ├── discovery-server/
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── api-gateway/
│   ├── auth-service/
│   ├── product-service/
│   ├── cart-service/
│   └── order-service/
│
├── aws-infrastructure/terraform/
│   ├── main.tf
│   └── ecs-services.tf
│
├── documentation/CI-CD/
│   ├── GUIA-CICD.md
│   ├── SECRETOS-GITHUB.md
│   └── AWS-SETUP.md
│
└── scripts/
    └── smoke-tests.sh""")

    doc.add_heading('11.2. Comandos Útiles', level=2)

    doc.add_paragraph("Comandos frecuentemente utilizados:")

    add_code_block(doc, """# Ver estado de servicios en ECS
aws ecs list-services --cluster prueba-tecnica-staging

# Escalar un servicio
aws ecs update-service \\
  --cluster prueba-tecnica-staging \\
  --service auth-service \\
  --desired-count 2

# Rollback manual a versión anterior
aws ecs update-service \\
  --cluster prueba-tecnica-staging \\
  --service auth-service \\
  --task-definition prueba-tecnica-staging-auth-service:5

# Limpiar imágenes antiguas de ECR
aws ecr list-images \\
  --repository-name prueba-tecnica/auth-service \\
  --filter tagStatus=UNTAGGED

# Invalidar caché de CloudFront
aws cloudfront create-invalidation \\
  --distribution-id E1234EXAMPLE \\
  --paths "/*" """)

    doc.add_heading('11.3. Enlaces de Referencia', level=2)

    references = [
        "GitHub Actions Documentation: https://docs.github.com/en/actions",
        "AWS ECS Best Practices: https://docs.aws.amazon.com/ecs/",
        "Terraform AWS Provider: https://registry.terraform.io/providers/hashicorp/aws/",
        "Docker Best Practices: https://docs.docker.com/develop/dev-best-practices/",
        "OWASP Dependency Check: https://owasp.org/www-project-dependency-check/",
        "Trivy Scanner: https://github.com/aquasecurity/trivy"
    ]

    for ref in references:
        doc.add_paragraph(ref, style='List Bullet')

    doc.add_page_break()

    # ========== CONCLUSIÓN ==========
    doc.add_heading('Conclusión', level=1)

    doc.add_paragraph(
        "Este pipeline de CI/CD proporciona una solución completa y automatizada para "
        "el desarrollo, testing, seguridad y despliegue de la aplicación de e-commerce "
        "basada en microservicios."
    )

    doc.add_paragraph()

    doc.add_paragraph("Beneficios clave:")

    benefits = [
        "Automatización completa del ciclo de vida del software",
        "Detección temprana de vulnerabilidades y bugs",
        "Despliegues consistentes y reproducibles",
        "Rollback rápido en caso de problemas",
        "Monitoreo y observabilidad integrados",
        "Infraestructura como código para reproducibilidad",
        "Escalabilidad automática en AWS",
        "Separación de ambientes (staging/production)"
    ]

    for benefit in benefits:
        doc.add_paragraph(benefit, style='List Bullet')

    doc.add_paragraph()
    doc.add_paragraph()

    # Footer
    footer_para = doc.add_paragraph()
    footer_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
    footer_run = footer_para.add_run(
        f"\n\nDocumento generado automáticamente - {datetime.now().strftime('%d/%m/%Y %H:%M')}\n"
        "Versión 1.0"
    )
    footer_run.font.size = Pt(9)
    footer_run.font.color.rgb = RGBColor(128, 128, 128)

    # Guardar documento
    output_path = os.path.join(
        os.path.dirname(os.path.dirname(__file__)),
        'documentation',
        'CI-CD',
        'Documentacion_CICD_Pipeline.docx'
    )

    doc.save(output_path)
    print(f"✅ Documento creado exitosamente: {output_path}")
    return output_path

if __name__ == "__main__":
    try:
        create_cicd_documentation()
    except Exception as e:
        print(f"❌ Error al crear documento: {str(e)}")
        import traceback
        traceback.print_exc()
        exit(1)
