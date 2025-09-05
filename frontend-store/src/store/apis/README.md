# Microservices APIs Documentation

Esta carpeta contiene las APIs separadas para cada microservicio de la arquitectura. Cada API está diseñada para manejar un dominio específico de la aplicación.

## Estructura de APIs

### 🔐 Auth API (Puerto 8081)
**Archivo:** `authApi.ts`

Maneja autenticación y gestión de usuarios:
- Login/Logout
- Registro de usuarios
- Gestión de perfil
- Refresh de tokens
- Administración de usuarios (admin)

**Uso:**
```typescript
import { useLoginMutation, useGetProfileQuery } from '../store/apis'

// En componente
const [login] = useLoginMutation()
const { data: profile } = useGetProfileQuery()
```

### 📦 Product API (Puerto 8082)
**Archivo:** `productApi.ts`

Gestión completa de productos:
- CRUD de productos
- Búsqueda y filtros
- Categorías
- Control de stock
- Productos destacados

**Uso:**
```typescript
import { useGetProductsQuery, useCreateProductMutation } from '../store/apis'

// En componente
const { data: products } = useGetProductsQuery()
const [createProduct] = useCreateProductMutation()
```

### 🛒 Cart API (Puerto 8083)
**Archivo:** `cartApi.ts`

Manejo del carrito de compras:
- Agregar/eliminar productos
- Actualizar cantidades
- Aplicar cupones
- Validación de carrito
- Guardar para después

**Uso:**
```typescript
import { useGetCartQuery, useAddToCartMutation } from '../store/apis'

// En componente
const { data: cart } = useGetCartQuery()
const [addToCart] = useAddToCartMutation()
```

### 📋 Order API (Puerto 8084)
**Archivo:** `orderApi.ts`

Gestión de órdenes:
- Crear órdenes
- Seguimiento de estado
- Historial de órdenes
- Procesamiento de pagos
- Estadísticas (admin)

**Uso:**
```typescript
import { useCreateOrderMutation, useGetMyOrdersQuery } from '../store/apis'

// En componente
const [createOrder] = useCreateOrderMutation()
const { data: orders } = useGetMyOrdersQuery()
```

## Configuración Base

### Base API (`baseApi.ts`)
Contiene la configuración común para todos los servicios:
- **URLs específicas de microservicios** usando variables de entorno
- Headers de autenticación automática (JWT)
- Tags para cache invalidation
- Instancias API independientes para cada servicio

### Variables de Entorno
Cada microservicio tiene su propia URL configurada:
```env
VITE_URL_AUTH=http://localhost:8081      # Auth Service
VITE_URL_PRODUCTS=http://localhost:8082  # Product Service  
VITE_URL_CART=http://localhost:8083      # Cart Service
VITE_URL_ORDERS=http://localhost:8084    # Order Service
```

### Características Principales

1. **Direct Microservice Connection**: Cada API se conecta directamente a su microservicio específico
2. **Environment Configuration**: URLs configurables mediante variables de entorno
3. **Automatic Authentication**: Headers JWT se agregan automáticamente  
4. **Independent Cache**: Cada servicio tiene su propio cache y tags
5. **Type Safety**: Completamente tipado con TypeScript
6. **Error Handling**: Manejo consistente de errores a través de la ApiResponse

## Patrones de Uso

### 1. Consultas Básicas
```typescript
// Obtener datos
const { data, isLoading, error } = useGetProductsQuery()

// Con parámetros
const { data } = useGetProductsQuery({ 
  search: 'laptop', 
  category: 'electronics' 
})
```

### 2. Mutaciones
```typescript
// Crear/actualizar datos
const [createProduct, { isLoading }] = useCreateProductMutation()

const handleCreate = async () => {
  try {
    const result = await createProduct(productData).unwrap()
    // Éxito
  } catch (error) {
    // Error
  }
}
```

### 3. Invalidación de Cache
Las mutaciones automáticamente invalidan el cache relacionado usando tags:
- `Auth`: Datos de autenticación
- `User`: Información de usuario
- `Product`: Catálogo de productos
- `Cart`: Carrito de compras
- `Order`: Órdenes

### 4. Conditional Queries
```typescript
// Solo ejecutar query si hay token
const { data } = useGetProfileQuery(undefined, {
  skip: !isAuthenticated
})
```

## Migración desde API Única

Si tienes componentes usando la API anterior, actualiza las importaciones:

```typescript
// Antes
import { useGetProductsQuery } from '../store/api'

// Después  
import { useGetProductsQuery } from '../store/apis'
```

## Desarrollo y Testing

### Llamadas Directas a Microservicios
Para desarrollo o testing, puedes crear queries que llamen directamente a los microservicios:

```typescript
import { createMicroserviceQuery } from './baseApi'

// Query directa al Product Service (puerto 8082)
const productServiceQuery = createMicroserviceQuery(8082)
```

### Debug
Para debuggear las llamadas a la API, puedes usar las herramientas de desarrollador de Redux Toolkit:
- Redux DevTools
- Network tab del navegador
- RTK Query DevTools

## Mejores Prácticas

1. **Use Typed Hooks**: Siempre usa los hooks tipados exportados
2. **Handle Loading States**: Maneja estados de carga en la UI
3. **Error Boundaries**: Implementa manejo de errores apropiado
4. **Cache Optimization**: Usa `providesTags` e `invalidatesTags` correctamente
5. **Conditional Queries**: Usa `skip` para queries condicionales

## Endpoints por Microservicio

### Auth Service (8081)
- `POST /auth/login` - Iniciar sesión
- `POST /auth/refresh` - Renovar token
- `POST /users/register` - Registro
- `GET /users/me` - Perfil
- `POST /users/logout` - Cerrar sesión

### Product Service (8082)
- `GET /products` - Listar productos
- `POST /products` - Crear producto
- `PUT /products/:id` - Actualizar producto
- `DELETE /products/:id` - Eliminar producto
- `GET /products/search` - Buscar productos

### Cart Service (8083)
- `GET /cart` - Obtener carrito
- `POST /cart/items` - Agregar al carrito
- `PUT /cart/items/:id` - Actualizar cantidad
- `DELETE /cart/items/:id` - Eliminar del carrito
- `POST /cart/clear` - Vaciar carrito

### Order Service (8084)
- `POST /orders` - Crear orden
- `GET /orders/my-orders` - Mis órdenes
- `GET /orders/:id` - Detalles de orden
- `POST /orders/:id/cancel` - Cancelar orden
- `GET /orders/:id/tracking` - Seguimiento

## Troubleshooting

### Error de Conexión
- Verificar que el API Gateway esté corriendo (puerto 8080)
- Verificar que los microservicios estén registrados en Eureka

### Error de Autenticación
- Verificar que el token JWT sea válido
- Revisar headers de autorización

### Cache Issues
- Usar `refetch()` para forzar actualización
- Revisar tags de invalidación