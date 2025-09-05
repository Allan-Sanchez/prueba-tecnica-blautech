export interface ApiResponse<T = any> {
  success: boolean
  httpStatus: number
  appCode: string
  message: string
  data: T
  errors: ErrorDetail[]
  meta: Meta
}

export interface ErrorDetail {
  appCode: string
  message: string
}

export interface Meta {
  requestId: string
  timestamp: string
  service: string
  version: string
  durationMs: number
}

export interface ApiError {
  message: string
  status?: number
}

export interface Configuration {
  id: number
  key: string
  value: string
  createdAt: string
  updatedAt: string
}

export interface Product {
  id: number
  name: string
  description: string
  priceInCurrency: number
  // stock: number
  imageUrl?: string
  category?: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface User {
  id: number
  email: string
  firstName: string
  lastName: string
  isActive: boolean
  createdAt: string
  updatedAt: string
  shippingAddress?: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: User
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  shippingAddress: string
  birthDate?: string
}

export interface CartItem {
  id: string
  productId: number
  quantity: number
  product: Product
}

export interface Cart {
  items: CartItem[]
  totalItems: number
  totalPrice: number
}

export interface CartItemRequest {
  productId: number
  quantity: number
}

export interface Order {
  id: number
  userId: number
  items: OrderItem[]
  totalAmount: number
  status: OrderStatus
  shippingAddress: string
  paymentMethod: string
  notes?: string
  createdAt: string
  updatedAt: string
  userEmail: string
}

export interface OrderItem {
  id: number
  productId: number
  quantity: number
  unitPrice: number
  totalPrice: number
  product: Product
}

export interface Address {
  street: string
  city: string
  state: string
  zipCode: string
  country: string
}

export interface PaymentMethod {
  type: 'CREDIT_CARD' | 'DEBIT_CARD' | 'PAYPAL' | 'BANK_TRANSFER'
  details?: string
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'

export interface CreateOrderRequest {
  items: CartItemRequest[]
  shippingAddress: string
  paymentMethod: string
  notes?: string