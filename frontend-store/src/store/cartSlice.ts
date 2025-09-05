import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { Cart, CartItem, Product } from '../types'

interface CartState extends Cart {
  isOpen: boolean
}

const initialState: CartState = {
  items: [],
  totalItems: 0,
  totalPrice: 0,
  isOpen: false,
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addToCart: (state, action: PayloadAction<Product>) => {
      const product = action.payload
      const existingItem = state.items.find(item => item.productId === product.id)

      if (existingItem) {
        // if (existingItem.quantity < product.stock) {
        //   existingItem.quantity += 1
        // }
      } else {
        const newItem: CartItem = {
          id: `${product.id}-${Date.now()}`,
          productId: product.id,
          quantity: 1,
          product,
        }
        state.items.push(newItem)
      }

      state.totalItems = state.items.reduce((sum, item) => sum + item.quantity, 0)
      state.totalPrice = state.items.reduce((sum, item) => sum + (item.quantity * item.product.priceInCurrency), 0)
    },

    removeFromCart: (state, action: PayloadAction<string>) => {
      const itemId = action.payload
      state.items = state.items.filter(item => item.id !== itemId)
      
      state.totalItems = state.items.reduce((sum, item) => sum + item.quantity, 0)
      state.totalPrice = state.items.reduce((sum, item) => sum + (item.quantity * item.product.priceInCurrency), 0)
    },

    updateQuantity: (state, action: PayloadAction<{ itemId: string; quantity: number }>) => {
      const { itemId, quantity } = action.payload
      const item = state.items.find(item => item.id === itemId)

      if (item && quantity > 0 && quantity ) {
        item.quantity = quantity
        state.totalItems = state.items.reduce((sum, item) => sum + item.quantity, 0)
        state.totalPrice = state.items.reduce((sum, item) => sum + (item.quantity * item.product.priceInCurrency), 0)
      }
    },

    clearCart: (state) => {
      state.items = []
      state.totalItems = 0
      state.totalPrice = 0
    },

    toggleCart: (state) => {
      state.isOpen = !state.isOpen
    },

    setCartOpen: (state, action: PayloadAction<boolean>) => {
      state.isOpen = action.payload
    },
  },
})

export const {
  addToCart,
  removeFromCart,
  updateQuantity,
  clearCart,
  toggleCart,
  setCartOpen,
} = cartSlice.actions

export default cartSlice.reducer