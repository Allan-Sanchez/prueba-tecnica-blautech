export interface ApiResponse<T = any> {
  success: boolean
  httpStatus: number
  appCode: string
  message: string
  data: T
  errors: ErrorDetail[]
  meta: Meta
}

export interface ErrorDetail {
  appCode: string
  message: string
}

export interface Meta {
  requestId: string
  timestamp: string
  service: string
  version: string
  durationMs: number
}

export interface ApiError {
  message: string
  status?: number
}

export interface Configuration {
  id: number
  key: string
  value: string
  createdAt: string
  updatedAt: string
}