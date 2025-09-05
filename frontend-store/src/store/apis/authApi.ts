import { authApi as baseAuthApi } from './baseApi'
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, User } from '../../types'

// Auth Service API (Puerto 8081) - Direct connection to VITE_URL_AUTH
export const authApiEndpoints = baseAuthApi.injectEndpoints({
  endpoints: (builder) => ({
    login: builder.mutation<ApiResponse<AuthResponse>, LoginRequest>({
      query: (credentials) => ({
        url: 'api/auth/login',
        method: 'POST',
        body: credentials,
      }),
      invalidatesTags: ['Auth'],
    }),

    refreshToken: builder.mutation<ApiResponse<AuthResponse>, { refreshToken: string }>({
      query: (data) => ({
        url: 'api/auth/refresh',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['Auth'],
    }),

    // User Management endpoints - Direct to Auth Service
    register: builder.mutation<ApiResponse<AuthResponse>, RegisterRequest>({
      query: (userData) => ({
        url: 'api/users/register',
        method: 'POST',
        body: userData,
      }),
      invalidatesTags: ['User'],
    }),

    getProfile: builder.query<ApiResponse<User>, void>({
      query: () => 'api/users/me',
      providesTags: ['User'],
    }),

    updateProfile: builder.mutation<ApiResponse<User>, Partial<User>>({
      query: (userData) => ({
        url: 'api/users/me',
        method: 'PATCH',
        body: userData,
      }),
      invalidatesTags: ['User'],
    }),

    logout: builder.mutation<ApiResponse<void>, void>({
      query: () => ({
        url: 'api/users/logout',
        method: 'POST',
      }),
      invalidatesTags: ['Auth', 'User'],
    }),

    // // User list (admin only)
    // getAllUsers: builder.query<ApiResponse<User[]>, void>({
    //   query: () => 'api/users',
    //   providesTags: ['User'],
    // }),

    // // Get user by ID (admin only)
    // getUserById: builder.query<ApiResponse<User>, number>({
    //   query: (id) => `api/users/${id}`,
    //   providesTags: (result, error, id) => [{ type: 'User', id }],
    // }),

    // // Update user status (admin only)
    // updateUserStatus: builder.mutation<ApiResponse<User>, { id: number; isActive: boolean }>({
    //   query: ({ id, isActive }) => ({
    //     url: `api/users/${id}/status`,
    //     method: 'PATCH',
    //     body: { isActive },
    //   }),
    //   invalidatesTags: (result, error, { id }) => [{ type: 'User', id }],
    // }),
  }),
})

// Export the hooks for Auth Service
export const {
  useLoginMutation,
  useRefreshTokenMutation,
  useRegisterMutation,
  useGetProfileQuery,
  useUpdateProfileMutation,
  useLogoutMutation,
  // useGetAllUsersQuery,
  // useGetUserByIdQuery,
  // useUpdateUserStatusMutation,
} = authApiEndpoints

// Export the base auth API instance for store configuration
export { baseAuthApi as authApi }