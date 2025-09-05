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

    // Create new product
    createProduct: builder.mutation<ApiResponse<Product>, Omit<Product, 'id' | 'createdAt' | 'updatedAt'>>({
      query: (product) => ({
        url: '/products',
        method: 'POST',
        body: product,
      }),
      invalidatesTags: ['Product'],
    }),

    // Update product
    updateProduct: builder.mutation<ApiResponse<Product>, { id: number; product: Partial<Product> }>({
      query: ({ id, product }) => ({
        url: `/products/${id}`,
        method: 'PUT',
        body: product,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Product', id }],
    }),

    // Delete product
    deleteProduct: builder.mutation<ApiResponse<void>, number>({
      query: (id) => ({
        url: `/products/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Product'],
    }),

    // Search products
    searchProducts: builder.query<ApiResponse<Product[]>, string>({
      query: (searchTerm) => ({
        url: '/products/search',
        params: { q: searchTerm },
      }),
      providesTags: ['Product'],
    }),

    // Get products by category
    getProductsByCategory: builder.query<ApiResponse<Product[]>, string>({
      query: (category) => `/products/category/${category}`,
      providesTags: ['Product'],
    }),

    // Get product categories
    getProductCategories: builder.query<ApiResponse<string[]>, void>({
      query: () => '/products/categories',
      providesTags: ['Product'],
    }),

    // Update product stock
    updateProductStock: builder.mutation<ApiResponse<Product>, { id: number; stock: number }>({
      query: ({ id, stock }) => ({
        url: `/products/${id}/stock`,
        method: 'PATCH',
        body: { stock },
      }),
      invalidatesTags: (result, error, { id }) => [{ type: 'Product', id }],
    }),

    // Toggle product active status
    toggleProductStatus: builder.mutation<ApiResponse<Product>, number>({
      query: (id) => ({
        url: `/products/${id}/toggle-status`,
        method: 'PATCH',
      }),
      invalidatesTags: (result, error, id) => [{ type: 'Product', id }],
    }),

    // Get featured products
    getFeaturedProducts: builder.query<ApiResponse<Product[]>, void>({
      query: () => '/products/featured',
      providesTags: ['Product'],
    }),

    // Get products with low stock
    getLowStockProducts: builder.query<ApiResponse<Product[]>, { threshold?: number }>({
      query: ({ threshold = 5 } = {}) => ({
        url: '/products/low-stock',
        params: { threshold },
      }),
      providesTags: ['Product'],
    }),
  }),
})

// Export the hooks for Product Service
export const {
  useGetProductsQuery,
  useGetProductQuery,
  useCreateProductMutation,
  useUpdateProductMutation,
  useDeleteProductMutation,
  useSearchProductsQuery,
  useGetProductsByCategoryQuery,
  useGetProductCategoriesQuery,
  useUpdateProductStockMutation,
  useToggleProductStatusMutation,
  useGetFeaturedProductsQuery,
  useGetLowStockProductsQuery,
} = productApiEndpoints

// Export the base product API instance for store configuration
export { baseProductApi as productApi }