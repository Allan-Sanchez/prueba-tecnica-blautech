import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

// Environment URLs for each microservice
const MICROSERVICE_URLS = {
  AUTH: import.meta.env.VITE_URL_AUTH || 'http://localhost:8081',
  PRODUCTS: import.meta.env.VITE_URL_PRODUCTS || 'http://localhost:8082', 
  CART: import.meta.env.VITE_URL_CART || 'http://localhost:8083',
  ORDERS: import.meta.env.VITE_URL_ORDERS || 'http://localhost:8084',
  API_GATEWAY: 'http://localhost:8080/api' // Fallback option
}

// Common headers preparation
const prepareCommonHeaders = (headers: Headers) => {
  const token = localStorage.getItem('accessToken')
  console.log("🚀 ~ prepareCommonHeaders ~ token:", token)
  if (token) {
    headers.set('authorization', `Bearer ${token}`)
  }
  headers.set('Content-Type', 'application/json')
  return headers
}

// Headers preparation with user context for orders
const prepareOrderHeaders = (headers: Headers, { getState }: any) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    headers.set('authorization', `Bearer ${token}`)
  }
  headers.set('Content-Type', 'application/json')
  
  // Get user info from state
  const state = getState()
  const user = state.auth?.user
  console.log("🚀 ~ prepareOrderHeaders ~ user:", user)
  
  if (user) {
    headers.set('X-User-Id', user.id.toString())
    headers.set('X-User-Email', user.email)
  }
  
  return headers
}

// Base query factory for each microservice
export const createMicroserviceQuery = (baseUrl: string) => 
  fetchBaseQuery({
    baseUrl,
    prepareHeaders: prepareCommonHeaders,
  })

// Individual base queries for each microservice
export const authBaseQuery = createMicroserviceQuery(MICROSERVICE_URLS.AUTH)
export const productBaseQuery = createMicroserviceQuery(MICROSERVICE_URLS.PRODUCTS)  
export const cartBaseQuery = createMicroserviceQuery(MICROSERVICE_URLS.CART)
export const orderBaseQuery = createMicroserviceQuery(MICROSERVICE_URLS.ORDERS)

// API Gateway base query (for backwards compatibility)
export const apiGatewayBaseQuery = fetchBaseQuery({
  baseUrl: MICROSERVICE_URLS.API_GATEWAY,
  prepareHeaders: prepareCommonHeaders,
})

// Main API instance using API Gateway (legacy support)
export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: apiGatewayBaseQuery,
  tagTypes: ['Auth', 'User', 'Product', 'Cart', 'Order'],
  endpoints: () => ({}),
})

// Individual API instances for each microservice
export const authApi = createApi({
  reducerPath: 'authApi',
  baseQuery: authBaseQuery,
  tagTypes: ['Auth', 'User'],
  endpoints: () => ({}),
})

export const productApi = createApi({
  reducerPath: 'productApi', 
  baseQuery: productBaseQuery,
  tagTypes: ['Product'],
  endpoints: () => ({}),
})

export const cartApi = createApi({
  reducerPath: 'cartApi',
  baseQuery: cartBaseQuery,
  tagTypes: ['Cart'],
  endpoints: () => ({}),
})

// Order base query with user headers
export const orderBaseQueryWithUserHeaders = fetchBaseQuery({
  baseUrl: MICROSERVICE_URLS.ORDERS,
  prepareHeaders: prepareOrderHeaders,
})

export const orderApi = createApi({
  reducerPath: 'orderApi',
  baseQuery: orderBaseQueryWithUserHeaders,
  tagTypes: ['Order'],
  endpoints: () => ({}),
})