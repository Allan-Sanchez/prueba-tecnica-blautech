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
  // useGetCartSummaryQuery,
  // useValidateCartQuery,
  // useMergeCartMutation,
  // useSaveForLaterMutation,
  // useMoveToCartMutation,
  // useGetSavedItemsQuery,
} = cartApiEndpoints

export { baseCartApi as cartApi }