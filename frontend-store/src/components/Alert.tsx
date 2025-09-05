import React, { useEffect } from 'react'
import { createPortal } from 'react-dom'
import { useAlert, type Alert } from '../contexts/AlertContext'

interface AlertItemProps {
  alert: Alert
}

const AlertItem: React.FC<AlertItemProps> = ({ alert }) => {
  const { removeAlert } = useAlert()

  useEffect(() => {
    if (alert.duration && alert.duration > 0) {
      const timer = setTimeout(() => {
        removeAlert(alert.id)
      }, alert.duration)

      return () => clearTimeout(timer)
    }
  }, [alert.id, alert.duration, removeAlert])

  const getAlertIcon = (type: Alert['type']) => {
    switch (type) {
      case 'success':
        return '✅'
      case 'error':
        return '❌'
      case 'warning':
        return '⚠️'
      case 'info':
        return 'ℹ️'
      default:
        return '📢'
    }
  }

  return (
    <div className={`alert alert--${alert.type}`}>
      <div className="alert__icon">
        {getAlertIcon(alert.type)}
      </div>
      
      <div className="alert__content">
        {alert.title && (
          <h4 className="alert__title">{alert.title}</h4>
        )}
        <p className="alert__message">{alert.message}</p>
      </div>

      <div className="alert__actions">
        {alert.action && (
          <button
            className="alert__action-btn"
            onClick={alert.action.onClick}
          >
            {alert.action.label}
          </button>
        )}
        
        <button
          className="alert__close"
          onClick={() => removeAlert(alert.id)}
          aria-label="Cerrar alerta"
        >
          ×
        </button>
      </div>
    </div>
  )
}

const AlertContainer: React.FC = () => {
  const { alerts } = useAlert()

  if (alerts.length === 0) {
    return null
  }

  const alertRoot = document.getElementById('alert-root')
  if (!alertRoot) {
    console.warn('Alert root element not found. Make sure to add <div id="alert-root"></div> to your HTML.')
    return null
  }

  return createPortal(
    <div className="alert-container">
      {alerts.map(alert => (
        <AlertItem key={alert.id} alert={alert} />
      ))}
    </div>,
    alertRoot
  )
}

export default AlertContainer