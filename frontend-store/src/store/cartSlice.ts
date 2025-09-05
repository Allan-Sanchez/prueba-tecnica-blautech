import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { Cart, CartItem, Product } from '../types'

interface CartState extends Cart {
  isOpen: boolean
}

// Helper functions for localStorage persistence
const loadCartFromStorage = (): Omit<CartState, 'isOpen'> => {
  try {
    const savedCart = localStorage.getItem('cart')
    if (savedCart) {
      const parsedCart = JSON.parse(savedCart)
      return {
        items: parsedCart.items || [],
        totalItems: parsedCart.totalItems || 0,
        totalPrice: parsedCart.totalPrice || 0,
      }
    }
  } catch (error) {
    console.error('Error loading cart from localStorage:', error)
  }
  
  return {
    items: [],
    totalItems: 0,
    totalPrice: 0,
  }
}

const saveCartToStorage = (cartData: Omit<CartState, 'isOpen'>) => {
  try {
    localStorage.setItem('cart', JSON.stringify(cartData))
  } catch (error) {
    console.error('Error saving cart to localStorage:', error)
  }
}

const savedCart = loadCartFromStorage()

const initialState: CartState = {
  ...savedCart,
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
        existingItem.quantity += 1
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
      
      // Persist to localStorage
      saveCartToStorage({
        items: state.items,
        totalItems: state.totalItems,
        totalPrice: state.totalPrice,
      })
    },

    removeFromCart: (state, action: PayloadAction<string>) => {
      const itemId = action.payload
      state.items = state.items.filter(item => item.id !== itemId)
      
      state.totalItems = state.items.reduce((sum, item) => sum + item.quantity, 0)
      state.totalPrice = state.items.reduce((sum, item) => sum + (item.quantity * item.product.priceInCurrency), 0)
      
      // Persist to localStorage
      saveCartToStorage({
        items: state.items,
        totalItems: state.totalItems,
        totalPrice: state.totalPrice,
      })
    },

    updateQuantity: (state, action: PayloadAction<{ itemId: string; quantity: number }>) => {
      const { itemId, quantity } = action.payload
      const item = state.items.find(item => item.id === itemId)

      if (item && quantity > 0) {
        item.quantity = quantity
        state.totalItems = state.items.reduce((sum, item) => sum + item.quantity, 0)
        state.totalPrice = state.items.reduce((sum, item) => sum + (item.quantity * item.product.priceInCurrency), 0)
        
        // Persist to localStorage
        saveCartToStorage({
          items: state.items,
          totalItems: state.totalItems,
          totalPrice: state.totalPrice,
        })
      }
    },

    clearCart: (state) => {
      state.items = []
      state.totalItems = 0
      state.totalPrice = 0
      
      // Persist to localStorage
      saveCartToStorage({
        items: state.items,
        totalItems: state.totalItems,
        totalPrice: state.totalPrice,
      })
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