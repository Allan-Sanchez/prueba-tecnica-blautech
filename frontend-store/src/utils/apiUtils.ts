import { ApiResponse, ApiError } from '../types';

/**
 * Utility class for handling API responses
 */
export class ApiUtils {
  /**
   * Checks if the API response is successful
   */
  static isSuccess<T>(response: ApiResponse<T>): boolean {
    return response.success && response.httpStatus >= 200 && response.httpStatus < 300;
  }

  /**
   * Extracts data from a successful API response
   */
  static getData<T>(response: ApiResponse<T>): T | null {
    return this.isSuccess(response) ? response.data : null;
  }

  /**
   * Extracts error information from a failed API response
   */
  static getError<T>(response: ApiResponse<T>): ApiError {
    return {
      message: response.message || 'Error desconocido',
      status: response.httpStatus
    };
  }

  /**
   * Gets the first error detail from the errors array
   */
  static getFirstErrorDetail<T>(response: ApiResponse<T>): string {
    if (response.errors && response.errors.length > 0) {
      return response.errors[0].message;
    }
    return response.message || 'Error desconocido';
  }

  /**
   * Gets all error messages as an array
   */
  static getAllErrorMessages<T>(response: ApiResponse<T>): string[] {
    if (response.errors && response.errors.length > 0) {
      return response.errors.map(error => error.message);
    }
    return [response.message || 'Error desconocido'];
  }

  /**
   * Formats the response meta information for debugging
   */
  static formatMetaInfo<T>(response: ApiResponse<T>): string {
    const { requestId, timestamp, service, version, durationMs } = response.meta;
    return `Request ID: ${requestId}, Service: ${service} v${version}, Duration: ${durationMs}ms, Timestamp: ${timestamp}`;
  }

  /**
   * Creates a standardized error object for failed requests
   */
  static createRequestError(message: string, status?: number): ApiError {
    return {
      message,
      status: status || 500
    };
  }

  /**
   * Handles common API response scenarios
   */
  static handleResponse<T>(
    response: ApiResponse<T>,
    onSuccess: (data: T) => void,
    onError: (error: ApiError) => void
  ): void {
    if (this.isSuccess(response)) {
      onSuccess(response.data);
    } else {
      onError(this.getError(response));
    }
  }
}