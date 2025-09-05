import { productApi as baseProductApi } from './baseApi'
import type { ApiResponse, Product } from '../../types'

// Product Service API (Puerto 8082) - Direct connection to VITE_URL_PRODUCTS
export const productApiEndpoints = baseProductApi.injectEndpoints({
  endpoints: (builder) => ({
    // Get all products
    getProducts: builder.query<ApiResponse<Product[]>, { 
      search?: string;
      category?: string;
      minPrice?: number;
      maxPrice?: number;
      page?: number;
      size?: number;
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
          url: 'api/products',
          params: searchParams,
        }
      },
      providesTags: ['Product'],
      extraOptions: {
        maxRetries: 3, // Retry once on failure
      }
    }),

    // Get product by ID
    getProduct: builder.query<ApiResponse<Product>, number>({
      query: (id) => `/products/${id}`,
      providesTags: (result, error, id) => [{ type: 'Product', id }],
    }),

  }),
})

// Export the hooks for Product Service
export const {
  useGetProductsQuery,
  useGetProductQuery,
} = productApiEndpoints

// Export the base product API instance for store configuration
export { baseProductApi as productApi }