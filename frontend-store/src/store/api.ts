// Compatibility layer - exports all APIs from the new modular structure
// This maintains backward compatibility while using the new microservices architecture

// Re-export everything from the new APIs structure
export * from './apis'

// For backward compatibility, also export the base API as 'api'
export { baseApi as api } from './apis/baseApi'