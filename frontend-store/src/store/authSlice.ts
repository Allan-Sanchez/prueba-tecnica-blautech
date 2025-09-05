import { createSlice, type PayloadAction,  } from '@reduxjs/toolkit'
import type { User, AuthResponse } from '../types'

interface AuthState {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
}

const initialState: AuthState = {
  user: null,
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
  isLoading: false,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginStart: (state) => {
      state.isLoading = true
    },

    loginSuccess: (state, action: PayloadAction<AuthResponse>) => {
      const { accessToken, refreshToken, user } = action.payload
      state.user = user
      state.accessToken = accessToken
      state.refreshToken = refreshToken
      state.isAuthenticated = true
      state.isLoading = false

      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      localStorage.setItem('user', JSON.stringify(user))
    },

    loginFailure: (state) => {
      state.isLoading = false
    },

    logout: (state) => {
      state.user = null
      state.accessToken = null
      state.refreshToken = null
      state.isAuthenticated = false
      state.isLoading = false
      
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
    },

    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload
      localStorage.setItem('user', JSON.stringify(action.payload))
    },

    updateTokens: (state, action: PayloadAction<{ accessToken: string; refreshToken: string }>) => {
      const { accessToken, refreshToken } = action.payload
      state.accessToken = accessToken
      state.refreshToken = refreshToken

      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
    },

    initializeAuth: (state) => {
      const storedUser = localStorage.getItem('user')
      if (storedUser && state.accessToken) {
        try {
          state.user = JSON.parse(storedUser)
          state.isAuthenticated = true
        } catch {
          state.user = null
          state.isAuthenticated = false
          localStorage.removeItem('user')
        }
      }
    },
  },
})

export const {
  loginStart,
  loginSuccess,
  loginFailure,
  logout,
  setUser,
  updateTokens,
  initializeAuth,
} = authSlice.actions

export default authSlice.reducer