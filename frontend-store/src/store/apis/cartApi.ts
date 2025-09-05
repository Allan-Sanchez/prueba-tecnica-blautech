import { cartApi as baseCartApi } from './baseApi'
import type { ApiResponse, CartItem, CartItemRequest, Product } from '../../types'

// Server-side cart representation
export interface ServerCart {
  id: number
  userId: number
  items: ServerCartItem[]
  totalItems: number
  totalPrice: number
  createdAt: string
  updatedAt: string
}

export interface ServerCartItem {
  id: number
  cartId: number
  productId: number
  quantity: number
  unitPrice: number
  totalPrice: number
  product: Product
  createdAt: string
  updatedAt: string
}

// Cart Service API (Puerto 8083) - Direct connection to VITE_URL_CART
export const cartApiEndpoints = baseCartApi.injectEndpoints({
  endpoints: (builder) => ({
    // Get user's cart
    getCart: builder.query<ApiResponse<ServerCart>, void>({
      query: () => 'api/cart',
      providesTags: ['Cart'],
    }),

    // Add item to cart
    addToCart: builder.mutation<ApiResponse<ServerCartItem>, CartItemRequest>({
      query: (item) => ({
        url: 'api/cart/items',
        method: 'POST',
        body: item,
      }),
      invalidatesTags: ['Cart'],
    }),

    // Update cart item quantity
    updateCartItem: builder.mutation<ApiResponse<ServerCartItem>, { itemId: number; quantity: number }>({
      query: ({ itemId, quantity }) => ({
        url: `api/cart/items/${itemId}`,
        method: 'PUT',
        body: { quantity },
      }),
      invalidatesTags: ['Cart'],
    }),

    // Remove item from cart
    removeCartItem: builder.mutation<ApiResponse<void>, number>({
      query: (itemId) => ({
        url: `api/cart/items/${itemId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
    }),

    // Clear entire cart
    clearCart: builder.mutation<ApiResponse<void>, void>({
      query: () => ({
        url: 'api/cart/clear',
        method: 'POST',
      }),
      invalidatesTags: ['Cart'],
    }),

    // Apply coupon to cart
    applyCoupon: builder.mutation<ApiResponse<ServerCart>, { couponCode: string }>({
      query: ({ couponCode }) => ({
        url: 'api/cart/coupon',
        method: 'POST',
        body: { couponCode },
      }),
      invalidatesTags: ['Cart'],
    }),

    // Remove coupon from cart
    removeCoupon: builder.mutation<ApiResponse<ServerCart>, void>({
      query: () => ({
        url: 'api/cart/coupon',
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
    }),

    // Get cart summary (totals, taxes, shipping)
    getCartSummary: builder.query<ApiResponse<{
      subtotal: number;
      tax: number;
      shipping: number;
      discount: number;
      total: number;
      itemCount: number;
    }>, void>({
      query: () => 'api/cart/summary',
      providesTags: ['Cart'],
    }),

    // Validate cart (check stock availability, prices, etc.)
    validateCart: builder.query<ApiResponse<{
      isValid: boolean;
      issues: Array<{
        itemId: number;
        productId: number;
        issue: 'OUT_OF_STOCK' | 'INSUFFICIENT_STOCK' | 'PRICE_CHANGED' | 'PRODUCT_INACTIVE';
        message: string;
      }>;
    }>, void>({
      query: () => 'api/cart/validate',
      providesTags: ['Cart'],
    }),

    // Merge guest cart with user cart (after login)
    mergeCart: builder.mutation<ApiResponse<ServerCart>, { guestCartItems: CartItemRequest[] }>({
      query: ({ guestCartItems }) => ({
        url: '/cart/merge',
        method: 'POST',
        body: { items: guestCartItems },
      }),
      invalidatesTags: ['Cart'],
    }),

    // Save cart for later (wishlist functionality)
    saveForLater: builder.mutation<ApiResponse<void>, number>({
      query: (itemId) => ({
        url: `api/cart/items/${itemId}/save-for-later`,
        method: 'POST',
      }),
      invalidatesTags: ['Cart'],
    }),

    // Move item from saved for later back to cart
    moveToCart: builder.mutation<ApiResponse<ServerCartItem>, number>({
      query: (itemId) => ({
        url: `api/cart/saved-items/${itemId}/move-to-cart`,
        method: 'POST',
      }),
      invalidatesTags: ['Cart'],
    }),

    // Get saved for later items
    getSavedItems: builder.query<ApiResponse<ServerCartItem[]>, void>({
      query: () => 'api/cart/saved-items',
      providesTags: ['Cart'],
    }),
  }),
})

// Export the hooks for Cart Service
export const {
  useGetCartQuery,
  useAddToCartMutation,
  useUpdateCartItemMutation,
  useRemoveCartItemMutation,
  useClearCartMutation,
  useApplyCouponMutation,
  useRemoveCouponMutation,
  useGetCartSummaryQuery,
  useValidateCartQuery,
  useMergeCartMutation,
  useSaveForLaterMutation,
  useMoveToCartMutation,
  useGetSavedItemsQuery,
} = cartApiEndpoints

// Export the base cart API instance for store configuration
export { baseCartApi as cartApi }