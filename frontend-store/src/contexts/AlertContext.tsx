import React, { createContext, useContext, useState, useCallback } from 'react'

export type AlertType = 'success' | 'error' | 'warning' | 'info' | 'confirm'

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
  onConfirm?: () => void
  onCancel?: () => void
}

interface AlertContextType {
  alerts: Alert[]
  showAlert: (alert: Omit<Alert, 'id'>) => string
  showConfirm: (message: string, onConfirm: () => void, title?: string) => string
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
    console.log("🚀 ~ AlertProvider ~ id:", id)
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

  const showConfirm = useCallback((message: string, onConfirm: () => void, title?: string) => {
    const id = Math.random().toString(36).substr(2, 9)
    const confirmAlert: Alert = {
      id,
      type: 'confirm',
      title: title ?? 'Confirmación',
      message,
      duration: 0, // No auto-remove for confirmation dialogs
      onConfirm: () => {
        onConfirm()
        removeAlert(id)
      },
      onCancel: () => {
        removeAlert(id)
      }
    }

    setAlerts(prev => [...prev, confirmAlert])
    return id
  }, [])

  const clearAlerts = useCallback(() => {
    setAlerts([])
  }, [])

  const value = {
    alerts,
    showAlert,
    showConfirm,
    removeAlert,
    clearAlerts,
  }

  return (
    <AlertContext.Provider value={value}>
      {children}
    </AlertContext.Provider>
  )
}