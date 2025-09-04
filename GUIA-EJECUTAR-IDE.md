# 🚀 Guía para Ejecutar Microservicios desde IDE

## 📁 Estructura de Proyectos

```
microservices/
├── discovery-server/     # Puerto 8761 - Eureka Server
├── auth-service/         # Puerto 8081 - Autenticación
├── product-service/      # Puerto 8082 - Productos  
└── api-gateway/          # Puerto 8080 - Gateway
```

## 🎯 **ORDEN OBLIGATORIO DE EJECUCIÓN**

### **1️⃣ Discovery Server (PRIMERO - MUY IMPORTANTE)**
- **Proyecto**: `microservices/discovery-server/`
- **Clase Main**: `com.pruebatecnica.discoveryserver.DiscoveryServerApplication`
- **Puerto**: 8761
- **⏳ Esperar**: 30-45 segundos hasta que aparezca "Started DiscoveryServerApplication"

**✅ Verificación**: http://localhost:8761 debe mostrar el dashboard de Eureka

---

### **2️⃣ Auth Service (SEGUNDO)**
- **Proyecto**: `microservices/auth-service/`
- **Clase Main**: `com.pruebatecnica.authservice.AuthServiceApplication`
- **Puerto**: 8081
- **⏳ Esperar**: 20-30 segundos hasta ver "Started AuthServiceApplication"

**✅ Verificación**: En http://localhost:8761 debe aparecer "AUTH-SERVICE" registrado

---

### **3️⃣ Product Service (TERCERO)**
- **Proyecto**: `microservices/product-service/`
- **Clase Main**: `com.pruebatecnica.productservice.ProductServiceApplication`
- **Puerto**: 8082
- **⏳ Esperar**: 20-30 segundos hasta ver "Started ProductServiceApplication"

**✅ Verificación**: En http://localhost:8761 debe aparecer "PRODUCT-SERVICE" registrado

---

### **4️⃣ API Gateway (ÚLTIMO)**
- **Proyecto**: `microservices/api-gateway/`
- **Clase Main**: `com.pruebatecnica.apigateway.ApiGatewayApplication`
- **Puerto**: 8080
- **⏳ Esperar**: 15-20 segundos hasta ver "Started ApiGatewayApplication"

**✅ Verificación**: En http://localhost:8761 debe aparecer "API-GATEWAY" registrado

---

## 🔧 **Configuración por IDE**

### **Visual Studio Code**
1. Instala extensión "Extension Pack for Java"
2. Abre la carpeta `microservices/`
3. Ve a "Java Projects" en el sidebar
4. Para cada microservicio:
   - Haz clic derecho en la clase Application
   - "Run Java" o "Debug Java"

### **IntelliJ IDEA**
1. Abre proyecto: `File → Open → microservices/`
2. Espera a que importe todos los módulos
3. Para cada microservicio:
   - Ve a `src/main/java/com/pruebatecnica/.../Application.java`
   - Clic derecho → "Run Application" o Ctrl+Shift+F10

### **Eclipse**
1. `File → Import → Existing Maven Projects`
2. Selecciona carpeta `microservices/`
3. Importa todos los proyectos
4. Para cada microservicio:
   - Clic derecho en la clase Application
   - "Run As → Java Application"

---

## 📋 **Checklist de Verificación**

### **✅ Todos los servicios iniciados correctamente:**
- [ ] Discovery Server corriendo en puerto 8761
- [ ] Auth Service corriendo en puerto 8081
- [ ] Product Service corriendo en puerto 8082
- [ ] API Gateway corriendo en puerto 8080

### **✅ Servicios registrados en Eureka:**
Ve a http://localhost:8761 y verifica que aparezcan:
- [ ] AUTH-SERVICE (1 instancia)
- [ ] PRODUCT-SERVICE (1 instancia)  
- [ ] API-GATEWAY (1 instancia)

### **✅ API Gateway funcionando:**
- [ ] http://localhost:8080/actuator/health → {"status":"UP"}
- [ ] http://localhost:8080/api/products → Lista de productos

---

## 🐛 **Solución de Problemas**

### **Error: "Port already in use"**
```bash
# Windows - Matar proceso en puerto específico
netstat -ano | findstr :8080
taskkill /PID [NUMERO_PID] /F
```

### **Error: "Connection refused" en servicios**
- ✅ Verifica que Discovery Server esté corriendo PRIMERO
- ✅ Espera 30 segundos entre cada servicio
- ✅ Revisa logs para errores de base de datos

### **Servicios no aparecen en Eureka**
- ✅ Verifica que el puerto 8761 esté libre
- ✅ Espera 1-2 minutos para registro automático
- ✅ Revisa configuración de `application.yml`

### **Error de Base de Datos**
```sql
-- Crear bases de datos necesarias
CREATE DATABASE IF NOT EXISTS prueba_tecnica_auth;
CREATE DATABASE IF NOT EXISTS prueba_tecnica_products;
```

---

## 🎯 **URLs de Prueba**

Una vez que todos los servicios estén corriendo:

- **Dashboard Eureka**: http://localhost:8761
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Lista Productos**: http://localhost:8080/api/products
- **Auth Health**: http://localhost:8081/actuator/health (directo)
- **Product Health**: http://localhost:8082/products/health (directo)

---

## ⚡ **Tips para Desarrollo**

1. **Usa perfiles de Run Configuration** en tu IDE para guardar las configuraciones
2. **Configura JVM args** si necesitas más memoria: `-Xmx512m`
3. **Habilita hot reload** con Spring Boot DevTools
4. **Monitorea logs** de cada servicio en ventanas separadas

---

## 🔄 **Para Detener Todo**

1. Para cada aplicación en tu IDE: Botón "Stop" o Ctrl+C
2. O cierra las ventanas/tabs de ejecución
3. Verifica que no queden procesos: `netstat -ano | findstr :808`