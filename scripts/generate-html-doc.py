#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Script para generar documentación HTML del CI/CD Pipeline
Compatible con Python 2.7+
El HTML generado puede abrirse en Word y guardarse como .docx
"""

import os
from datetime import datetime

def create_html_documentation():
    """Crear documento HTML completo con estilos profesionales"""

    html_content = u"""<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documentación CI/CD Pipeline - Microservicios E-Commerce</title>
    <style>
        @page {
            margin: 2.5cm;
        }

        body {
            font-family: 'Calibri', 'Arial', sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 21cm;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
        }

        h1 {
            color: #2E75B6;
            border-bottom: 3px solid #2E75B6;
            padding-bottom: 10px;
            margin-top: 30px;
            page-break-before: always;
        }

        h1:first-of-type {
            page-break-before: avoid;
        }

        h2 {
            color: #0070C0;
            border-left: 4px solid #0070C0;
            padding-left: 10px;
            margin-top: 25px;
        }

        h3 {
            color: #4472C4;
            margin-top: 20px;
        }

        .cover {
            text-align: center;
            padding: 100px 0;
            page-break-after: always;
        }

        .cover h1 {
            font-size: 42px;
            color: #2E75B6;
            border-bottom: none;
            page-break-before: avoid;
        }

        .cover h2 {
            font-size: 28px;
            color: #0070C0;
            border-left: none;
            padding-left: 0;
        }

        .info-table {
            width: 60%;
            margin: 50px auto;
            border-collapse: collapse;
        }

        .info-table td {
            padding: 12px;
            border: 1px solid #ddd;
        }

        .info-table td:first-child {
            font-weight: bold;
            background-color: #E7E6E6;
            width: 40%;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        th {
            background-color: #2E75B6;
            color: white;
            padding: 12px;
            text-align: left;
            font-weight: bold;
        }

        td {
            padding: 10px;
            border: 1px solid #ddd;
        }

        tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .info-box {
            background-color: #E7F3FF;
            border-left: 4px solid #0070C0;
            padding: 15px;
            margin: 20px 0;
        }

        .info-box strong {
            color: #0070C0;
            font-size: 16px;
        }

        .warning-box {
            background-color: #FFF3CD;
            border-left: 4px solid #FFC107;
            padding: 15px;
            margin: 20px 0;
        }

        .success-box {
            background-color: #D4EDDA;
            border-left: 4px solid #28A745;
            padding: 15px;
            margin: 20px 0;
        }

        code {
            background-color: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Consolas', 'Monaco', monospace;
            font-size: 14px;
            color: #c7254e;
        }

        pre {
            background-color: #2D2D2D;
            color: #F8F8F2;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            font-family: 'Consolas', 'Monaco', monospace;
            font-size: 13px;
            line-height: 1.4;
        }

        pre code {
            background-color: transparent;
            color: #F8F8F2;
            padding: 0;
        }

        ul, ol {
            margin: 10px 0;
            padding-left: 30px;
        }

        li {
            margin: 8px 0;
        }

        .toc {
            background-color: #f9f9f9;
            padding: 20px;
            border: 1px solid #ddd;
            margin: 30px 0;
        }

        .toc h2 {
            margin-top: 0;
            border-left: none;
        }

        .toc ul {
            list-style-type: none;
        }

        .toc li {
            margin: 10px 0;
        }

        .toc a {
            color: #0070C0;
            text-decoration: none;
        }

        .toc a:hover {
            text-decoration: underline;
        }

        .page-break {
            page-break-after: always;
        }

        footer {
            text-align: center;
            margin-top: 50px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            color: #666;
            font-size: 12px;
        }

        @media print {
            body {
                background-color: white;
            }

            h1 {
                page-break-after: avoid;
            }

            table {
                page-break-inside: avoid;
            }

            pre {
                page-break-inside: avoid;
            }
        }
    </style>
</head>
<body>

<!-- PORTADA -->
<div class="cover">
    <h1>Documentación CI/CD Pipeline</h1>
    <h2>Sistema de E-Commerce con Microservicios</h2>

    <table class="info-table">
        <tr>
            <td>Proyecto:</td>
            <td>Prueba Técnica - Microservicios</td>
        </tr>
        <tr>
            <td>Tecnologías:</td>
            <td>Spring Boot 3.5.5, React 18, AWS, Docker, Terraform</td>
        </tr>
        <tr>
            <td>Fecha:</td>
            <td>""" + datetime.now().strftime("%d/%m/%Y") + """</td>
        </tr>
        <tr>
            <td>Versión:</td>
            <td>1.0</td>
        </tr>
        <tr>
            <td>Autor:</td>
            <td>Sistema Automatizado</td>
        </tr>
    </table>
</div>

<!-- TABLA DE CONTENIDOS -->
<div class="toc">
    <h2>📋 Tabla de Contenidos</h2>
    <ul>
        <li><a href="#introduccion">1. Introducción</a></li>
        <li><a href="#descripcion">2. Descripción General del Pipeline</a></li>
        <li><a href="#arquitectura">3. Arquitectura del Sistema</a></li>
        <li><a href="#tecnologias">4. Tecnologías Utilizadas</a></li>
        <li><a href="#etapas">5. Etapas del Pipeline CI/CD</a>
            <ul>
                <li><a href="#tests">5.1. Tests Automatizados</a></li>
                <li><a href="#security">5.2. Escaneo de Vulnerabilidades</a></li>
                <li><a href="#build">5.3. Construcción de Imágenes Docker</a></li>
                <li><a href="#deploy">5.4. Despliegue en AWS</a></li>
            </ul>
        </li>
        <li><a href="#configuracion">6. Configuración del Pipeline</a></li>
        <li><a href="#branches">7. Estrategia de Branches</a></li>
        <li><a href="#monitoreo">8. Monitoreo y Logs</a></li>
        <li><a href="#costos">9. Estimación de Costos</a></li>
        <li><a href="#troubleshooting">10. Troubleshooting</a></li>
        <li><a href="#anexos">11. Anexos</a></li>
    </ul>
</div>

<div class="page-break"></div>

<!-- 1. INTRODUCCIÓN -->
<h1 id="introduccion">1. Introducción</h1>

<p>Este documento describe la implementación completa del pipeline de <strong>CI/CD (Continuous Integration / Continuous Deployment)</strong> para el sistema de e-commerce basado en arquitectura de microservicios.</p>

<div class="info-box">
    <strong>📌 Objetivo del Pipeline</strong><br>
    Automatizar completamente el proceso de desarrollo, testing, construcción y despliegue de la aplicación, garantizando calidad, seguridad y consistencia en cada release.
</div>

<p>El pipeline implementa las siguientes capacidades:</p>

<ul>
    <li>✅ Tests automatizados (unitarios e integración)</li>
    <li>✅ Escaneo de vulnerabilidades de seguridad</li>
    <li>✅ Construcción automática de imágenes Docker</li>
    <li>✅ Despliegue automático en entornos de staging y producción</li>
    <li>✅ Monitoreo y alertas en tiempo real</li>
    <li>✅ Rollback automático en caso de fallos</li>
</ul>

<!-- 2. DESCRIPCIÓN GENERAL -->
<h1 id="descripcion">2. Descripción General del Pipeline</h1>

<p>El pipeline de CI/CD está implementado con <strong>GitHub Actions</strong> y se ejecuta automáticamente en cada push o pull request al repositorio.</p>

<h2>2.1. Flujo del Pipeline</h2>

<p>El flujo completo del pipeline consta de 4 etapas principales:</p>

<table>
    <thead>
        <tr>
            <th>Etapa</th>
            <th>Descripción</th>
            <th>Duración Aprox.</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>1. Tests</strong></td>
            <td>Ejecución de tests unitarios e integración</td>
            <td>5-10 min</td>
        </tr>
        <tr>
            <td><strong>2. Security</strong></td>
            <td>Escaneo de vulnerabilidades (OWASP, Trivy)</td>
            <td>8-15 min</td>
        </tr>
        <tr>
            <td><strong>3. Build</strong></td>
            <td>Construcción de imágenes Docker</td>
            <td>10-15 min</td>
        </tr>
        <tr>
            <td><strong>4. Deploy</strong></td>
            <td>Despliegue a AWS ECS</td>
            <td>5-10 min</td>
        </tr>
    </tbody>
</table>

<div class="success-box">
    <strong>⏱️ Tiempo Total:</strong> El pipeline completo toma aproximadamente 30-50 minutos en ejecutarse de principio a fin, dependiendo de la cantidad de cambios.
</div>

<!-- 3. ARQUITECTURA -->
<h1 id="arquitectura">3. Arquitectura del Sistema</h1>

<h2>3.1. Microservicios</h2>

<table>
    <thead>
        <tr>
            <th>Servicio</th>
            <th>Puerto</th>
            <th>Estado</th>
            <th>Descripción</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>Discovery Server</strong></td>
            <td>8761</td>
            <td>✅ Completo</td>
            <td>Eureka - Service Discovery</td>
        </tr>
        <tr>
            <td><strong>API Gateway</strong></td>
            <td>8080</td>
            <td>✅ Completo</td>
            <td>Punto de entrada único + Auth</td>
        </tr>
        <tr>
            <td><strong>Auth Service</strong></td>
            <td>8081</td>
            <td>✅ Completo</td>
            <td>Autenticación JWT</td>
        </tr>
        <tr>
            <td><strong>Product Service</strong></td>
            <td>8082</td>
            <td>🔄 En progreso</td>
            <td>Gestión de productos CRUD</td>
        </tr>
        <tr>
            <td><strong>Cart Service</strong></td>
            <td>8083</td>
            <td>⏳ Pendiente</td>
            <td>Carrito de compras</td>
        </tr>
        <tr>
            <td><strong>Order Service</strong></td>
            <td>8084</td>
            <td>⏳ Pendiente</td>
            <td>Gestión de órdenes</td>
        </tr>
        <tr>
            <td><strong>Frontend</strong></td>
            <td>3000</td>
            <td>✅ Completo</td>
            <td>React SPA con TypeScript</td>
        </tr>
    </tbody>
</table>

<h2>3.2. Bases de Datos</h2>

<p>Cada microservicio tiene su propia base de datos MySQL independiente, siguiendo el patrón de <strong>Database per Service</strong>:</p>

<ul>
    <li><code>prueba_tecnica_auth</code> - Auth Service</li>
    <li><code>prueba_tecnica_products</code> - Product Service</li>
    <li><code>prueba_tecnica_carts</code> - Cart Service</li>
    <li><code>prueba_tecnica_orders</code> - Order Service</li>
</ul>

<!-- 4. TECNOLOGÍAS -->
<h1 id="tecnologias">4. Tecnologías Utilizadas</h1>

<h2>4.1. Backend</h2>
<ul>
    <li><strong>Java 21</strong> - Lenguaje de programación</li>
    <li><strong>Spring Boot 3.5.5</strong> - Framework principal</li>
    <li><strong>Spring Cloud</strong> (Gateway, Eureka, LoadBalancer) - Microservicios</li>
    <li><strong>Spring Security</strong> - Autenticación y autorización</li>
    <li><strong>Spring Data JPA</strong> - Persistencia de datos</li>
    <li><strong>MySQL 8.0</strong> - Base de datos relacional</li>
    <li><strong>Maven</strong> - Gestión de dependencias</li>
</ul>

<h2>4.2. Frontend</h2>
<ul>
    <li><strong>React 18</strong> - Librería de UI</li>
    <li><strong>TypeScript</strong> - Tipado estático</li>
    <li><strong>Redux Toolkit Query</strong> - State management y API calls</li>
    <li><strong>React Router DOM</strong> - Navegación</li>
    <li><strong>Vite</strong> - Build tool y dev server</li>
    <li><strong>SCSS</strong> - Estilos modulares</li>
</ul>

<h2>4.3. DevOps y CI/CD</h2>
<ul>
    <li><strong>Docker</strong> - Containerización</li>
    <li><strong>GitHub Actions</strong> - CI/CD pipeline</li>
    <li><strong>Terraform</strong> - Infrastructure as Code</li>
    <li><strong>AWS ECS (Fargate)</strong> - Container orchestration</li>
    <li><strong>AWS RDS</strong> - Bases de datos gestionadas</li>
    <li><strong>AWS ECR</strong> - Docker registry</li>
    <li><strong>AWS S3 + CloudFront</strong> - Hosting frontend</li>
    <li><strong>AWS Application Load Balancer</strong> - Balanceo de carga</li>
</ul>

<h2>4.4. Seguridad y Testing</h2>
<ul>
    <li><strong>OWASP Dependency Check</strong> - Análisis de vulnerabilidades</li>
    <li><strong>Trivy Scanner</strong> - Escaneo de imágenes Docker</li>
    <li><strong>SonarCloud</strong> - Análisis estático de código</li>
    <li><strong>JUnit 5</strong> - Tests unitarios</li>
    <li><strong>JaCoCo</strong> - Coverage de código</li>
    <li><strong>ESLint</strong> - Linting de JavaScript/TypeScript</li>
</ul>

<!-- 5. ETAPAS DEL PIPELINE -->
<h1 id="etapas">5. Etapas del Pipeline CI/CD</h1>

<h2 id="tests">5.1. Tests Automatizados</h2>

<h3>Backend Tests</h3>
<ul>
    <li>Tests unitarios con JUnit 5</li>
    <li>Tests de integración con Spring Boot Test</li>
    <li>Generación de reportes de coverage con JaCoCo</li>
    <li>Upload de métricas a Codecov</li>
</ul>

<h3>Frontend Tests</h3>
<ul>
    <li>Linting con ESLint</li>
    <li>Tests unitarios con Jest/Vitest</li>
    <li>Generación de reportes de coverage</li>
</ul>

<pre><code># Comando para ejecutar tests backend
cd microservices
mvn clean test

# Comando para ejecutar tests frontend
cd frontend-store
npm test -- --coverage</code></pre>

<h2 id="security">5.2. Escaneo de Vulnerabilidades</h2>

<p>El pipeline implementa <strong>múltiples capas de análisis de seguridad</strong>:</p>

<table>
    <thead>
        <tr>
            <th>Herramienta</th>
            <th>Objetivo</th>
            <th>Severidad</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>OWASP Dependency Check</strong></td>
            <td>CVEs en dependencias Java</td>
            <td>CRITICAL, HIGH</td>
        </tr>
        <tr>
            <td><strong>Trivy</strong></td>
            <td>Vulnerabilidades en filesystem e imágenes</td>
            <td>CRITICAL, HIGH</td>
        </tr>
        <tr>
            <td><strong>NPM Audit</strong></td>
            <td>Vulnerabilidades en Node.js</td>
            <td>MODERATE+</td>
        </tr>
        <tr>
            <td><strong>SonarCloud</strong></td>
            <td>Code smells, bugs, security hotspots</td>
            <td>Todos</td>
        </tr>
    </tbody>
</table>

<div class="info-box">
    <strong>📌 Integración con GitHub Security</strong><br>
    Todos los resultados de los escaneos se reportan automáticamente a GitHub Security tab en formato SARIF, permitiendo tracking y remediation.
</div>

<h2 id="build">5.3. Construcción de Imágenes Docker</h2>

<p>Todas las imágenes Docker utilizan <strong>multi-stage builds</strong> para optimización:</p>

<ul>
    <li>Multi-stage builds (builder + runtime)</li>
    <li>Imágenes base Alpine Linux (menor tamaño)</li>
    <li>Usuario no-root para seguridad</li>
    <li>Health checks configurados</li>
    <li>Layers cacheados para builds rápidos</li>
</ul>

<pre><code># Ejemplo de build
docker build -t auth-service:latest \\
  -f microservices/auth-service/Dockerfile \\
  microservices/auth-service

# Push a ECR
docker push $ECR_REGISTRY/auth-service:latest</code></pre>

<h2 id="deploy">5.4. Despliegue en AWS</h2>

<p>El despliegue se realiza automáticamente a <strong>AWS ECS con Fargate</strong>:</p>

<div class="warning-box">
    <strong>⚠️ Orden de despliegue (CRÍTICO):</strong>
    <ol>
        <li>Discovery Server (Eureka) - Esperar hasta que esté healthy</li>
        <li>Auth Service + Product Service (en paralelo)</li>
        <li>API Gateway - Después de que los servicios estén registrados</li>
        <li>Frontend - Deploy a S3 e invalidación de CloudFront</li>
    </ol>
</div>

<div class="success-box">
    <strong>✅ Smoke Tests:</strong> Después de cada despliegue, se ejecutan smoke tests automáticos para verificar que los servicios estén funcionando correctamente.
</div>

<!-- 6. CONFIGURACIÓN -->
<h1 id="configuracion">6. Configuración del Pipeline</h1>

<h2>6.1. Secretos de GitHub</h2>

<p>Los siguientes secretos deben configurarse en GitHub (<strong>Settings → Secrets and variables → Actions</strong>):</p>

<table>
    <thead>
        <tr>
            <th>Secreto</th>
            <th>Descripción</th>
            <th>Ejemplo</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>AWS_ACCESS_KEY_ID</code></td>
            <td>Access Key del IAM user</td>
            <td>AKIAIOSFODNN7EXAMPLE</td>
        </tr>
        <tr>
            <td><code>AWS_SECRET_ACCESS_KEY</code></td>
            <td>Secret Access Key</td>
            <td>wJalrXUtnFEMI/K7MDENG/...</td>
        </tr>
        <tr>
            <td><code>AWS_ACCOUNT_ID</code></td>
            <td>ID de cuenta AWS (12 dígitos)</td>
            <td>123456789012</td>
        </tr>
        <tr>
            <td><code>DB_PASSWORD</code></td>
            <td>Password para RDS MySQL</td>
            <td>(16+ caracteres)</td>
        </tr>
        <tr>
            <td><code>CLOUDFRONT_DISTRIBUTION_ID_STAGING</code></td>
            <td>ID de CloudFront staging</td>
            <td>E1234EXAMPLE</td>
        </tr>
        <tr>
            <td><code>CLOUDFRONT_DISTRIBUTION_ID_PRODUCTION</code></td>
            <td>ID de CloudFront production</td>
            <td>E5678EXAMPLE</td>
        </tr>
        <tr>
            <td><code>SONAR_TOKEN</code></td>
            <td>Token de SonarCloud (opcional)</td>
            <td>sqp_...</td>
        </tr>
    </tbody>
</table>

<h2>6.2. Infraestructura AWS</h2>

<p>La infraestructura se despliega usando <strong>Terraform (Infrastructure as Code)</strong>:</p>

<pre><code># Inicializar Terraform
cd aws-infrastructure/terraform
terraform init

# Crear workspace para staging
terraform workspace new staging
terraform workspace select staging

# Aplicar configuración
terraform apply \\
  -var="environment=staging" \\
  -var="db_password=YOUR_SECURE_PASSWORD"</code></pre>

<p><strong>Recursos creados por Terraform:</strong></p>
<ul>
    <li>VPC con subnets públicas y privadas (3 AZs)</li>
    <li>ECS Cluster con service discovery</li>
    <li>Task definitions para cada microservicio</li>
    <li>RDS MySQL instances (una por microservicio)</li>
    <li>Application Load Balancer + Target Groups</li>
    <li>ECR Repositories para imágenes Docker</li>
    <li>S3 bucket + CloudFront distribution (frontend)</li>
    <li>Security Groups configurados</li>
    <li>IAM roles y policies</li>
    <li>CloudWatch Log Groups</li>
</ul>

<!-- 7. ESTRATEGIA DE BRANCHES -->
<h1 id="branches">7. Estrategia de Branches y Ambientes</h1>

<table>
    <thead>
        <tr>
            <th>Branch</th>
            <th>Acciones CI/CD</th>
            <th>Ambiente</th>
            <th>Auto-Deploy</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>feature/*</code></td>
            <td>Tests + Security scans</td>
            <td>N/A</td>
            <td>❌</td>
        </tr>
        <tr>
            <td><code>develop</code></td>
            <td>Tests + Scans + Build + Deploy</td>
            <td>Staging</td>
            <td>✅</td>
        </tr>
        <tr>
            <td><code>main</code></td>
            <td>Tests + Scans + Build + Deploy</td>
            <td>Production</td>
            <td>✅ (manual)</td>
        </tr>
    </tbody>
</table>

<h2>Workflow Recomendado</h2>
<ol>
    <li>Crear feature branch desde develop</li>
    <li>Desarrollar funcionalidad con commits frecuentes</li>
    <li>Push a GitHub - Pipeline ejecuta tests y scans</li>
    <li>Crear Pull Request hacia develop</li>
    <li>Code review y aprobación</li>
    <li>Merge a develop - Deploy automático a Staging</li>
    <li>Testing en Staging</li>
    <li>Cuando esté listo, crear PR de develop → main</li>
    <li>Aprobación de equipo senior</li>
    <li>Merge a main - Aprobar deploy manual a Production</li>
</ol>

<!-- 8. MONITOREO -->
<h1 id="monitoreo">8. Monitoreo y Logs</h1>

<h2>8.1. CloudWatch Logs</h2>

<p>Todos los servicios envían logs a CloudWatch:</p>

<pre><code># Ver logs en tiempo real
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow

# Buscar errores
aws logs filter-log-events \\
  --log-group-name /ecs/prueba-tecnica-staging \\
  --filter-pattern "ERROR"</code></pre>

<h2>8.2. CloudWatch Metrics</h2>

<p>Métricas disponibles automáticamente:</p>
<ul>
    <li><strong>ECS:</strong> CPU Utilization, Memory Utilization, Network I/O</li>
    <li><strong>RDS:</strong> Connections, CPU, Free Storage, Read/Write IOPS</li>
    <li><strong>ALB:</strong> Request Count, Target Response Time, HTTP Errors</li>
    <li><strong>CloudFront:</strong> Requests, Bytes Downloaded, Error Rate</li>
</ul>

<!-- 9. COSTOS -->
<h1 id="costos">9. Estimación de Costos</h1>

<h2>9.1. Ambiente de Staging</h2>

<table>
    <thead>
        <tr>
            <th>Servicio</th>
            <th>Configuración</th>
            <th>Costo Mensual (USD)</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>ECS Fargate (4 tareas)</td>
            <td>0.5 vCPU, 1GB RAM cada una</td>
            <td>$30.00</td>
        </tr>
        <tr>
            <td>RDS MySQL (2 instancias)</td>
            <td>db.t3.micro, 20GB cada una</td>
            <td>$30.00</td>
        </tr>
        <tr>
            <td>Application Load Balancer</td>
            <td>1 instancia</td>
            <td>$20.00</td>
        </tr>
        <tr>
            <td>NAT Gateway</td>
            <td>1 instancia</td>
            <td>$32.00</td>
        </tr>
        <tr>
            <td>S3 + CloudFront</td>
            <td>&lt;100GB transfer</td>
            <td>$10.00</td>
        </tr>
        <tr>
            <td>ECR + CloudWatch</td>
            <td>Logs y registry</td>
            <td>$10.00</td>
        </tr>
        <tr style="font-weight: bold; background-color: #E7E6E6;">
            <td colspan="2">TOTAL STAGING</td>
            <td>$132.00</td>
        </tr>
    </tbody>
</table>

<h2>9.2. Ambiente de Production</h2>

<table>
    <thead>
        <tr>
            <th>Servicio</th>
            <th>Configuración</th>
            <th>Costo Mensual (USD)</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>ECS Fargate (7 tareas)</td>
            <td>0.5 vCPU, 1GB RAM, 2x replicas</td>
            <td>$52.50</td>
        </tr>
        <tr>
            <td>RDS MySQL Multi-AZ (2)</td>
            <td>db.t3.medium, 100GB cada una</td>
            <td>$240.00</td>
        </tr>
        <tr>
            <td>Application Load Balancer</td>
            <td>1 instancia</td>
            <td>$20.00</td>
        </tr>
        <tr>
            <td>NAT Gateway (2 AZs)</td>
            <td>2 instancias</td>
            <td>$64.00</td>
        </tr>
        <tr>
            <td>S3 + CloudFront</td>
            <td>&lt;1TB transfer</td>
            <td>$88.00</td>
        </tr>
        <tr>
            <td>ECR + CloudWatch</td>
            <td>Logs y registry</td>
            <td>$35.50</td>
        </tr>
        <tr style="font-weight: bold; background-color: #E7E6E6;">
            <td colspan="2">TOTAL PRODUCTION</td>
            <td>$500.00</td>
        </tr>
    </tbody>
</table>

<h2>9.3. Optimización de Costos</h2>
<ul>
    <li>✅ <strong>Reserved Instances para RDS:</strong> Hasta 60% de descuento</li>
    <li>✅ <strong>Savings Plans para ECS:</strong> Hasta 50% de descuento</li>
    <li>✅ <strong>Apagar staging fuera de horario:</strong> ~40% ahorro</li>
    <li>✅ <strong>Usar instancias ARM (Graviton):</strong> ~20% ahorro</li>
    <li>✅ <strong>Optimizar imágenes Docker:</strong> Reducir storage en ECR</li>
</ul>

<!-- 10. TROUBLESHOOTING -->
<h1 id="troubleshooting">10. Troubleshooting</h1>

<h2>10.1. Pipeline Falla en Tests</h2>
<p><strong>Síntoma:</strong> Job 'test-backend' o 'test-frontend' falla</p>
<p><strong>Solución:</strong></p>
<pre><code># Ejecutar tests localmente - Backend
cd microservices
mvn clean test

# Ejecutar tests localmente - Frontend
cd frontend-store
npm test</code></pre>

<h2>10.2. Docker Build Falla</h2>
<p><strong>Síntoma:</strong> Job 'build-and-push' falla al construir imagen</p>
<p><strong>Solución:</strong></p>
<pre><code># Verificar Dockerfile localmente
docker build -t test-image \\
  -f microservices/auth-service/Dockerfile \\
  microservices/auth-service</code></pre>

<h2>10.3. Despliegue a ECS Falla</h2>
<p><strong>Síntoma:</strong> Servicio no arranca o health check falla</p>
<p><strong>Solución:</strong></p>
<pre><code># Ver estado del servicio
aws ecs describe-services \\
  --cluster prueba-tecnica-staging \\
  --services auth-service

# Ver logs
aws logs tail /ecs/prueba-tecnica-staging/auth-service --follow</code></pre>

<!-- 11. ANEXOS -->
<h1 id="anexos">11. Anexos</h1>

<h2>11.1. Estructura de Archivos del Proyecto</h2>
<pre><code>Prueba-tecnica/
├── .github/workflows/
│   └── ci-cd-pipeline.yml         # Pipeline principal
├── frontend-store/
│   ├── src/
│   ├── Dockerfile
│   └── nginx.conf
├── microservices/
│   ├── discovery-server/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── product-service/
│   ├── cart-service/
│   └── order-service/
├── aws-infrastructure/terraform/
│   ├── main.tf
│   └── ecs-services.tf
├── documentation/CI-CD/
│   ├── GUIA-CICD.md
│   ├── SECRETOS-GITHUB.md
│   └── AWS-SETUP.md
└── scripts/
    └── smoke-tests.sh</code></pre>

<h2>11.2. Enlaces de Referencia</h2>
<ul>
    <li>GitHub Actions Documentation: <a href="https://docs.github.com/en/actions">https://docs.github.com/en/actions</a></li>
    <li>AWS ECS Best Practices: <a href="https://docs.aws.amazon.com/ecs/">https://docs.aws.amazon.com/ecs/</a></li>
    <li>Terraform AWS Provider: <a href="https://registry.terraform.io/providers/hashicorp/aws/">https://registry.terraform.io/providers/hashicorp/aws/</a></li>
    <li>Docker Best Practices: <a href="https://docs.docker.com/develop/dev-best-practices/">https://docs.docker.com/develop/dev-best-practices/</a></li>
    <li>OWASP Dependency Check: <a href="https://owasp.org/www-project-dependency-check/">https://owasp.org/www-project-dependency-check/</a></li>
    <li>Trivy Scanner: <a href="https://github.com/aquasecurity/trivy">https://github.com/aquasecurity/trivy</a></li>
</ul>

<!-- CONCLUSIÓN -->
<h1>Conclusión</h1>

<p>Este pipeline de CI/CD proporciona una <strong>solución completa y automatizada</strong> para el desarrollo, testing, seguridad y despliegue de la aplicación de e-commerce basada en microservicios.</p>

<div class="success-box">
    <strong>✅ Beneficios clave:</strong>
    <ul>
        <li>Automatización completa del ciclo de vida del software</li>
        <li>Detección temprana de vulnerabilidades y bugs</li>
        <li>Despliegues consistentes y reproducibles</li>
        <li>Rollback rápido en caso de problemas</li>
        <li>Monitoreo y observabilidad integrados</li>
        <li>Infraestructura como código para reproducibilidad</li>
        <li>Escalabilidad automática en AWS</li>
        <li>Separación de ambientes (staging/production)</li>
    </ul>
</div>

<footer>
    <p>Documento generado automáticamente - """ + datetime.now().strftime("%d/%m/%Y %H:%M") + """</p>
    <p>Versión 1.0 | Sistema CI/CD Pipeline - Microservicios E-Commerce</p>
</footer>

</body>
</html>
"""

    # Guardar HTML
    output_path = os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
        'documentation',
        'CI-CD',
        'Documentacion_CICD_Pipeline.html'
    )

    try:
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(html_content.encode('utf-8'))

        print(u"✅ Documento HTML creado exitosamente!")
        print(u"📄 Ubicación: " + output_path)
        print(u"")
        print(u"📖 Para convertir a Word:")
        print(u"   1. Abre el archivo HTML en Microsoft Word")
        print(u"   2. Ve a Archivo → Guardar como")
        print(u"   3. Selecciona formato: Documento de Word (.docx)")
        print(u"   4. Guarda el archivo")
        print(u"")
        print(u"💡 El documento HTML se puede compartir directamente")
        print(u"   y se verá profesional en cualquier navegador.")

        return output_path

    except Exception as e:
        print(u"❌ Error al guardar archivo: " + str(e))
        return None

if __name__ == "__main__":
    try:
        create_html_documentation()
    except Exception as e:
        print(u"❌ Error: " + str(e))
        import traceback
        traceback.print_exc()
