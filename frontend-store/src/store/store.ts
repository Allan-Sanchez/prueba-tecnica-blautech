import { configureStore } from '@reduxjs/toolkit'
import { 
  baseApi,
  authApi, 
  productApi, 
  cartApi as cartApiInstance, 
  orderApi 
} from './apis/baseApi'
import authReducer from './authSlice'
import cartReducer from './cartSlice'

export const store = configureStore({
  reducer: {
    // Main API (for backwards compatibility)
    [baseApi.reducerPath]: baseApi.reducer,
    
    // Individual microservice APIs
    [authApi.reducerPath]: authApi.reducer,
    [productApi.reducerPath]: productApi.reducer,
    [cartApiInstance.reducerPath]: cartApiInstance.reducer,
    [orderApi.reducerPath]: orderApi.reducer,
    
    // State slices
    auth: authReducer,
    cart: cartReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      baseApi.middleware,
      authApi.middleware,
      productApi.middleware,
      cartApiInstance.middleware,
      orderApi.middleware,
    ),
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch