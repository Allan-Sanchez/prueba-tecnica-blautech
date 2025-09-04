# Postman Collection - Prueba Técnica API

Esta collection de Postman contiene todos los endpoints de la API REST de la aplicación Shop App, organizados por funcionalidades y con ejemplos completos de uso.

## 📁 Estructura de la Collection

### 🔐 **Authentication**
- **Login**: Autenticación con email/password
- **Refresh Token**: Renovación automática de tokens JWT

### 👤 **Users**
- **Register User**: Registro de nuevos usuarios
- **Get Current User Profile**: Obtener perfil del usuario autenticado
- **Update User Profile**: Actualizar información del usuario
- **Logout**: Cerrar sesión y revocar tokens

### 🛍️ **Products**
- **Get All Products**: Listar productos con paginación
- **Get All Products (No Pagination)**: Listar todos los productos
- **Get Product by ID**: Obtener producto específico
- **Search Products**: Buscar productos por texto
- **Filter Products by Price**: Filtrar por rango de precios
- **Create Product**: Crear nuevo producto
- **Update Product**: Actualizar producto existente
- **Deactivate Product**: Desactivar producto (soft delete)
- **Get Products Count**: Contar productos activos

### ⚙️ **Configuration**
- **Get App Info**: Información general de la aplicación

### 🛒 **Carts** (Endpoints futuros)
- **Get User Cart**: Obtener carrito del usuario
- **Add Item to Cart**: Agregar productos al carrito
- **Update Cart Item**: Modificar cantidad de items
- **Remove Item from Cart**: Eliminar items del carrito

### 📦 **Orders** (Endpoints futuros)
- **Checkout**: Realizar checkout y crear orden
- **Get User Orders**: Listar órdenes del usuario
- **Get Order by ID**: Obtener orden específica

## 🚀 Cómo usar la Collection

### 1. **Importar la Collection**
```bash
# Importar en Postman
File > Import > Seleccionar collection.json
```

### 2. **Configurar Variables**
La collection incluye variables predefinidas:
- `base_url`: http://localhost:8080/api
- `access_token`: Se actualiza automáticamente tras login
- `refresh_token`: Se actualiza automáticamente tras login
- `user_id`: ID del usuario autenticado

### 3. **Flujo de Uso Recomendado**

#### **Paso 1: Registrar Usuario**
```
POST /users/register
```
Crea una nueva cuenta de usuario.

#### **Paso 2: Iniciar Sesión**
```
POST /auth/login
```
- Los tokens se guardan automáticamente en las variables
- El access_token se usa para endpoints protegidos

#### **Paso 3: Usar Endpoints Protegidos**
Los endpoints que requieren autenticación usan automáticamente el `access_token` guardado.

#### **Paso 4: Renovar Token (cuando expire)**
```
POST /auth/refresh
```
Renueva automáticamente los tokens usando el refresh_token.

## 🔧 Scripts Automáticos

### **Login Automático**
Después del login exitoso, se ejecuta automáticamente:
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    if (response.success && response.data) {
        pm.collectionVariables.set('access_token', response.data.accessToken);
        pm.collectionVariables.set('refresh_token', response.data.refreshToken);
        pm.collectionVariables.set('user_id', response.data.user.id);
    }
}
```

### **Refresh Token Automático**
Similar al login, actualiza automáticamente las variables tras renovar tokens.

## 📋 Códigos de Respuesta

### **Códigos de Aplicación (appCode)**
- `LOGIN_OK`: Login exitoso
- `USER_REGISTERED`: Usuario registrado
- `PROFILE_FETCHED`: Perfil obtenido
- `PROFILE_UPDATED`: Perfil actualizado
- `PRODUCT_CREATED`: Producto creado
- `PRODUCT_UPDATED`: Producto actualizado
- `PRODUCT_DEACTIVATED`: Producto desactivado
- `AUTH_INVALID_CREDENTIALS`: Credenciales inválidas
- `AUTH_TOKEN_EXPIRED`: Token expirado
- `VALIDATION_ERROR`: Error de validación

### **HTTP Status Codes**
- `200`: Operación exitosa
- `201`: Recurso creado
- `400`: Bad Request (validación)
- `401`: No autorizado
- `403`: Prohibido
- `404`: No encontrado
- `409`: Conflicto (email duplicado)
- `500`: Error interno del servidor

## 🛡️ Seguridad

- Todos los endpoints protegidos usan **Bearer Token Authentication**
- Los tokens tienen expiración configurada
- Implementa **refresh token rotation** para mayor seguridad
- Passwords se almacenan hasheados con **bcrypt**

## 📝 Notas Importantes

1. **Base URL**: Asegúrate de que la API esté ejecutándose en `http://localhost:8080`
2. **CORS**: Configurado para `http://localhost:3000` (frontend React)
3. **Database**: Requiere MySQL con base de datos `prueba-tecnica`
4. **Validation**: Todos los endpoints validan datos de entrada
5. **Error Handling**: Respuestas estructuradas con códigos específicos

## 🔍 Testing

### **Tests Básicos Incluidos**
- Validación de status codes
- Extracción automática de tokens
- Verificación de estructura de respuestas

### **Variables de Entorno**
Para diferentes entornos (dev, staging, prod), puedes crear environments en Postman con diferentes valores para `base_url`.

## 📚 Documentación Adicional

- **API Responses**: Todas siguen el formato `ApiResponse<T>`
- **JWT Tokens**: Access tokens (15 min) y Refresh tokens (7 días)
- **Pagination**: Formato estándar con `page`, `size`, `sortBy`, `sortDir`
- **Search**: Búsqueda por texto en nombre y descripción de productos
- **Filters**: Filtrado por rangos de precio

---

**Creado para**: Prueba Técnica - Shop App API  
**Versión**: 1.0.0  
**Fecha**: Septiembre 2024