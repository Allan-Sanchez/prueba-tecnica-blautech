import { orderApi as baseOrderApi } from './baseApi'
import type { ApiResponse, Order, CreateOrderRequest, OrderStatus } from '../../types'

// Order Service API (Puerto 8084) - Direct connection to VITE_URL_ORDERS
export const orderApiEndpoints = baseOrderApi.injectEndpoints({
  endpoints: (builder) => ({
    // Create new order
    createOrder: builder.mutation<ApiResponse<Order>, CreateOrderRequest>({
      query: (orderData) => (
        {
          url: 'api/orders',
          method: 'POST',
          body: orderData,
          headers: {
            'X-User-Id': orderData.userId?.toString() || '',
            'X-User-Email': orderData.userEmail || '',
          }
        }
      ),
      invalidatesTags: ['Order', 'Cart'],
    }),

    // Get user's orders
    getMyOrders: builder.query<ApiResponse<Order[]>, {
      page?: number;
      size?: number;
      status?: OrderStatus;
      startDate?: string;
      endDate?: string;
    } | void>({
      query: (params) => {
        const searchParams = new URLSearchParams()
        
        if (params) {
          Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
              searchParams.append(key, value.toString())
            }
          })
        }

        return {
          url: '/orders/my-orders',
          params: searchParams,
        }
      },
      providesTags: ['Order'],
    }),

    // Get order by ID
    getOrder: builder.query<ApiResponse<Order>, number>({
      query: (id) => `api/orders/${id}`,
      providesTags: (result, error, id) => [{ type: 'Order', id }],
    }),

    // Update order status (admin/system)
    updateOrderStatus: builder.mutation<ApiResponse<Order>, { id: number; status: OrderStatus; reason?: string }>({
      query: ({ id, status, reason }) => ({
        url: `api/orders/${id}/status`,
        method: 'PATCH',
        body: { status, reason },
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Order', id }],
    }),

    // Cancel order
    cancelOrder: builder.mutation<ApiResponse<Order>, { id: number; reason: string }>({
      query: ({ id, reason }) => ({
        url: `api/orders/${id}/cancel`,
        method: 'POST',
        body: { reason },
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Order', id }],
    }),

    // Get order tracking
    getOrderTracking: builder.query<ApiResponse<{
      orderId: number;
      status: OrderStatus;
      timeline: Array<{
        status: OrderStatus;
        timestamp: string;
        location?: string;
        notes?: string;
      }>;
      estimatedDelivery?: string;
      trackingNumber?: string;
    }>, number>({
      query: (orderId) => `api/orders/${orderId}/tracking`,
      providesTags: (result, error, orderId) => [{ type: 'Order', id: orderId }],
    }),

    // Get all orders (admin only)
    getAllOrders: builder.query<ApiResponse<Order[]>, {
      page?: number;
      size?: number;
      status?: OrderStatus;
      userId?: number;
      startDate?: string;
      endDate?: string;
      sortBy?: string;
      sortDirection?: 'asc' | 'desc';
    } | void>({
      query: (params) => {
        const searchParams = new URLSearchParams()
        
        if (params) {
          Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
              searchParams.append(key, value.toString())
            }
          })
        }

        return {
          url: '/orders',
          params: searchParams,
        }
      },
      providesTags: ['Order'],
    }),

    // Get order statistics (admin only)
    getOrderStatistics: builder.query<ApiResponse<{
      totalOrders: number;
      totalRevenue: number;
      averageOrderValue: number;
      ordersByStatus: Record<OrderStatus, number>;
      recentOrders: Order[];
      topProducts: Array<{
        productId: number;
        productName: string;
        quantity: number;
        revenue: number;
      }>;
    }>, {
      period?: 'day' | 'week' | 'month' | 'year';
      startDate?: string;
      endDate?: string;
    } | void>({
      query: (params) => {
        const searchParams = new URLSearchParams()
        
        if (params) {
          Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
              searchParams.append(key, value.toString())
            }
          })
        }

        return {
          url: '/orders/statistics',
          params: searchParams,
        }
      },
      providesTags: ['Order'],
    }),

    // Process payment for order
    processPayment: builder.mutation<ApiResponse<{
      success: boolean;
      transactionId?: string;
      paymentMethod: string;
      amount: number;
    }>, {
      orderId: number;
      paymentDetails: {
        method: string;
        token?: string;
        cardNumber?: string;
        expiryDate?: string;
        cvv?: string;
      };
    }>({
      query: ({ orderId, paymentDetails }) => ({
        url: `api/orders/${orderId}/payment`,
        method: 'POST',
        body: paymentDetails,
      }),
      invalidatesTags: (result, error, { orderId }) => [{ type: 'Order', id: orderId }],
    }),

    // Request refund
    requestRefund: builder.mutation<ApiResponse<{
      refundId: string;
      status: 'PENDING' | 'APPROVED' | 'REJECTED';
      amount: number;
    }>, {
      orderId: number;
      reason: string;
      items?: number[];
    }>({
      query: ({ orderId, reason, items }) => ({
        url: `api/orders/${orderId}/refund`,
        method: 'POST',
        body: { reason, items },
      }),
      invalidatesTags: (result, error, { orderId }) => [{ type: 'Order', id: orderId }],
    }),

    // Reorder (create new order from existing order)
    reorder: builder.mutation<ApiResponse<Order>, number>({
      query: (orderId) => ({
        url: `api/orders/${orderId}/reorder`,
        method: 'POST',
      }),
      invalidatesTags: ['Order'],
    }),
  }),
})

// Export the hooks for Order Service
export const {
  useCreateOrderMutation,
  useGetMyOrdersQuery,
  useGetOrderQuery,
  useUpdateOrderStatusMutation,
  useCancelOrderMutation,
  useGetOrderTrackingQuery,
  useGetAllOrdersQuery,
  useGetOrderStatisticsQuery,
  useProcessPaymentMutation,
  useRequestRefundMutation,
  useReorderMutation,
} = orderApiEndpoints

// Export the base order API instance for store configuration
export { baseOrderApi as orderApi }