# Prueba Técnica - Arquitectura de Microservicios

## Descripción del Proyecto

Este proyecto implementa una aplicación de e-commerce utilizando arquitectura de microservicios con Spring Boot 3.5.5, Java 21, y Spring Cloud.

## Arquitectura de Microservicios

### Servicios Implementados

1. **Discovery Server (Eureka)** - Puerto 8761
   - Servidor de descubrimiento de servicios
   - Permite el registro y descubrimiento automático de microservicios

2. **API Gateway** - Puerto 8080  
   - Punto de entrada único para todos los servicios
   - Enrutamiento inteligente a microservicios
   - Autenticación JWT centralizada
   - Configuración CORS

3. **Auth Service** - Puerto 8081
   - Gestión de usuarios (registro, login, logout)  
   - Autenticación JWT con refresh tokens
   - Base de datos: `prueba_tecnica_auth`

4. **Product Service** - Puerto 8082
   - Gestión de productos (CRUD)
   - Base de datos: `prueba_tecnica_products`

5. **Cart Service** - Puerto 8083 (En desarrollo)
   - Gestión de carritos de compra
   - Base de datos: `prueba_tecnica_carts`

6. **Order Service** - Puerto 8084 (En desarrollo)  
   - Gestión de órdenes
   - Base de datos: `prueba_tecnica_orders`

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Cloud** (Gateway, Eureka, LoadBalancer)
- **Spring Security** con JWT
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**
- **Lombok**

## Bases de Datos

Cada microservicio tiene su propia base de datos MySQL:

- `prueba_tecnica_auth` - Auth Service
- `prueba_tecnica_products` - Product Service  
- `prueba_tecnica_carts` - Cart Service
- `prueba_tecnica_orders` - Order Service

## Cómo Ejecutar

### **RECOMENDADO: Usar IDE** (Eclipse, IntelliJ, VSCode)

**Orden obligatorio de ejecución:**

1. **Discovery Server** (Puerto 8761) - PRIMERO
   - Clase: `com.pruebatecnica.discoveryserver.DiscoveryServerApplication`
   - Esperar 30-45 segundos

2. **Auth Service** (Puerto 8081) - SEGUNDO  
   - Clase: `com.pruebatecnica.authservice.AuthServiceApplication`
   - Esperar 20-30 segundos

3. **Product Service** (Puerto 8082) - TERCERO
   - Clase: `com.pruebatecnica.productservice.ProductServiceApplication`
   - Esperar 20-30 segundos

4. **API Gateway** (Puerto 8080) - ÚLTIMO
   - Clase: `com.pruebatecnica.apigateway.ApiGatewayApplication`

📋 **Ver GUIA-EJECUTAR-IDE.md** para instrucciones detalladas por IDE.

### Alternativa: Línea de comandos (requiere Maven instalado)

```bash
# Script automático
start-with-maven.bat

# O manual:
cd microservices/discovery-server && mvn spring-boot:run
cd microservices/auth-service && mvn spring-boot:run  
cd microservices/product-service && mvn spring-boot:run
cd microservices/api-gateway && mvn spring-boot:run
```

### Verificación

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Frontend**: http://localhost:3000

## Endpoints Principales

### Autenticación (a través del API Gateway)
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/refresh` - Renovar token
- `POST /api/users/register` - Registrar usuario  
- `GET /api/users/me` - Obtener perfil
- `PATCH /api/users/me` - Actualizar perfil
- `POST /api/users/logout` - Cerrar sesión

### Productos
- `GET /api/products` - Listar productos
- `POST /api/products` - Crear producto
- `GET /api/products/{id}` - Obtener producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

## Comandos Útiles

### Compilar todos los microservicios
```bash
cd microservices
mvn clean install
```

### Ver servicios registrados en Eureka
```bash
curl http://localhost:8761/eureka/apps
```

### Verificar salud del API Gateway
```bash
curl http://localhost:8080/actuator/health
```

## Estructura del Proyecto

```
Prueba-tecnica/
├── frontend-store/           # React frontend
├── microservices/
│   ├── pom.xml              # Parent POM
│   ├── discovery-server/    # Eureka Server
│   ├── api-gateway/         # Spring Cloud Gateway  
│   ├── auth-service/        # Authentication Service
│   ├── product-service/     # Product Management
│   ├── cart-service/        # Cart Management (En desarrollo)
│   └── order-service/       # Order Management (En desarrollo)
├── documentation/           # Documentación y SQL scripts
└── README.md
```

## Seguridad

- **JWT Tokens** para autenticación
- **Refresh Token Rotation** para mayor seguridad  
- **API Gateway** filtra requests con JWT
- **Headers X-User-Id** y **X-User-Email** para microservicios downstream

## Base de Datos

Ejecutar scripts SQL en documentation/DB/ para crear las estructuras necesarias en cada base de datos de microservicio.

## Estado del Desarrollo

- ✅ Discovery Server
- ✅ API Gateway  
- ✅ Auth Service
- 🔄 Product Service (En progreso)
- ⏳ Cart Service (Pendiente)
- ⏳ Order Service (Pendiente)