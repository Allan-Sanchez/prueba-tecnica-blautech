import React, { createContext, useContext, useState, useCallback } from 'react'

export type AlertType = 'success' | 'error' | 'warning' | 'info'

export interface Alert {
  id: string
  type: AlertType
  title?: string
  message: string
  duration?: number
  action?: {
    label: string
    onClick: () => void
  }
}

interface AlertContextType {
  alerts: Alert[]
  showAlert: (alert: Omit<Alert, 'id'>) => string
  removeAlert: (id: string) => void
  clearAlerts: () => void
}

const AlertContext = createContext<AlertContextType | undefined>(undefined)

export const useAlert = () => {
  const context = useContext(AlertContext)
  if (!context) {
    throw new Error('useAlert must be used within an AlertProvider')
  }
  return context
}

interface AlertProviderProps {
  children: React.ReactNode
}

export const AlertProvider: React.FC<AlertProviderProps> = ({ children }) => {
  const [alerts, setAlerts] = useState<Alert[]>([])

  const showAlert = useCallback((alertData: Omit<Alert, 'id'>) => {
    const id = Math.random().toString(36).substr(2, 9)
    const newAlert: Alert = {
      ...alertData,
      id,
      duration: alertData.duration ?? 5000, // Default 5 seconds
    }

    setAlerts(prev => [...prev, newAlert])

    // Auto remove after duration
    if (newAlert.duration && newAlert.duration > 0) {
      setTimeout(() => {
        removeAlert(id)
      }, newAlert.duration)
    }

    return id
  }, [])

  const removeAlert = useCallback((id: string) => {
    setAlerts(prev => prev.filter(alert => alert.id !== id))
  }, [])

  const clearAlerts = useCallback(() => {
    setAlerts([])
  }, [])

  const value = {
    alerts,
    showAlert,
    removeAlert,
    clearAlerts,
  }

  return (
    <AlertContext.Provider value={value}>
      {children}
    </AlertContext.Provider>
  )
}