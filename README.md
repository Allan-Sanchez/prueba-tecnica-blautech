# Prueba Técnica - E-commerce con Arquitectura de Microservicios

## Descripción

Aplicación de e-commerce desarrollada con arquitectura de microservicios utilizando Spring Boot 3.5.5, Java 21, Spring Cloud y React como frontend.

## Screenshots

### Página Principal
<!-- Agregar captura de pantalla aquí -->

### Catálogo de Productos
<!-- Agregar captura de pantalla aquí -->

### Carrito de Compras
<!-- Agregar captura de pantalla aquí -->

### Proceso de Registro/Login
<!-- Agregar captura de pantalla aquí -->

### Dashboard de Eureka
<!-- Agregar captura de pantalla aquí -->

## Arquitectura

### Microservicios Implementados

- **Discovery Server (Eureka)** - Puerto 8761
- **API Gateway** - Puerto 8080
- **Auth Service** - Puerto 8081
- **Product Service** - Puerto 8082
- **Cart Service** - Puerto 8083 (En desarrollo)
- **Order Service** - Puerto 8084 (En desarrollo)

### Frontend

- **React Application** - Puerto 3000

## Tecnologías

### Backend
- Java 21
- Spring Boot 3.5.5
- Spring Cloud (Gateway, Eureka, LoadBalancer)
- Spring Security con JWT
- Spring Data JPA
- MySQL 8.0
- Maven

### Frontend
- React
- TypeScript
- Redux Toolkit
- React Router
- SCSS

## Instalación y Ejecución

### Prerrequisitos

- Java 21
- Maven
- MySQL 8.0
- Node.js y npm/pnpm

### Base de Datos

1. Crear las bases de datos necesarias:
   - `prueba_tecnica_auth`
   - `prueba_tecnica_products`
   - `prueba_tecnica_carts`
   - `prueba_tecnica_orders`

2. Ejecutar scripts SQL desde `documentation/DB/`

### Backend (Microservicios)

**Orden obligatorio de ejecución:**

1. **Discovery Server** (Puerto 8761)
```bash
cd microservices/discovery-server
mvn spring-boot:run
```

2. **Auth Service** (Puerto 8081)
```bash
cd microservices/auth-service
mvn spring-boot:run
```

3. **Product Service** (Puerto 8082)
```bash
cd microservices/product-service
mvn spring-boot:run
```

4. **API Gateway** (Puerto 8080)
```bash
cd microservices/api-gateway
mvn spring-boot:run
```

### Frontend

```bash
cd frontend-store
npm install
npm run dev
```

## Endpoints Principales

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/refresh` - Renovar token
- `POST /api/users/register` - Registrar usuario
- `GET /api/users/me` - Obtener perfil
- `POST /api/users/logout` - Cerrar sesión

### Productos
- `GET /api/products` - Listar productos
- `POST /api/products` - Crear producto
- `GET /api/products/{id}` - Obtener producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

## Verificación

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Frontend**: http://localhost:3000

## Estructura del Proyecto

```
Prueba-tecnica/
├── frontend-store/           # Aplicación React
├── microservices/
│   ├── pom.xml              # Parent POM
│   ├── discovery-server/    # Servidor Eureka
│   ├── api-gateway/         # API Gateway
│   ├── auth-service/        # Servicio de Autenticación
│   ├── product-service/     # Servicio de Productos
│   ├── cart-service/        # Servicio de Carritos (En desarrollo)
│   └── order-service/       # Servicio de Órdenes (En desarrollo)
├── documentation/           # Documentación y scripts SQL
└── README.md
```

## Funcionalidades Implementadas

- ✅ Registro y autenticación de usuarios
- ✅ Gestión de productos (CRUD)
- ✅ Carrito de compras
- ✅ Gestión de órdenes
- ✅ Interface responsive
- ✅ Lazy loading de imágenes
- ✅ Validación de formularios
- ✅ Sistema de alertas

## Estado del Desarrollo

- ✅ Discovery Server
- ✅ API Gateway
- ✅ Auth Service
- ✅ Product Service
- 🔄 Cart Service (En progreso)
- 🔄 Order Service (En progreso)
- ✅ Frontend React

## Autor

Desarrollado como prueba técnica demostrando conocimientos en:
- Arquitectura de microservicios
- Spring Boot y Spring Cloud
- React y TypeScript
- Bases de datos relacionales
- Patrones de diseño
- Seguridad con JWT
- 
- Incluye los siguientes pasos en tu pipeline:
Pruebas automatizadas
Escaneo de vulnerabilidades
Construcción de la aplicación
Despliegue en el entorno de pruebas