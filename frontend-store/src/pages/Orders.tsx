import React from 'react'
import { useGetMyOrdersQuery } from '../store/apis/orderApi'
import { useAlert } from '../contexts/AlertContext'
import type { Order, OrderStatus } from '../types'

const Orders: React.FC = () => {
  const { showAlert } = useAlert()
  const { data, isLoading, error, refetch } = useGetMyOrdersQuery()

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-GT', {
      style: 'currency',
      currency: 'GTQ'
    }).format(price)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-GT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const getStatusBadge = (status: OrderStatus) => {
    const statusConfig = {
      PENDING: { label: 'Pendiente', className: 'status-pending' },
      CONFIRMED: { label: 'Confirmada', className: 'status-confirmed' },
      PROCESSING: { label: 'Procesando', className: 'status-processing' },
      SHIPPED: { label: 'Enviada', className: 'status-shipped' },
      DELIVERED: { label: 'Entregada', className: 'status-delivered' },
      CANCELLED: { label: 'Cancelada', className: 'status-cancelled' },
      REFUNDED: { label: 'Reembolsada', className: 'status-refunded' }
    }

    const config = statusConfig[status] || { label: status, className: 'status-default' }

    return (
      <span className={`order-status ${config.className}`}>
        {config.label}
      </span>
    )
  }

  const handleRetry = () => {
    refetch()
  }

  if (isLoading) {
    return (
      <div className="orders-page">
        <div className="page-header">
          <h1>Mi Historial de Compras</h1>
        </div>
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Cargando tus órdenes...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="orders-page">
        <div className="page-header">
          <h1>Mi Historial de Compras</h1>
        </div>
        <div className="error-container">
          <h3>Error al cargar las órdenes</h3>
          <p>Hubo un problema al cargar tu historial de compras.</p>
          <button className="btn-primary" onClick={handleRetry}>
            Reintentar
          </button>
        </div>
      </div>
    )
  }

  const orders = data?.data || []

  return (
    <div className="orders-page">
      <div className="page-header">
        <h1>Mi Historial de Compras</h1>
        <p className="page-subtitle">
          {orders.length > 0 
            ? `Tienes ${orders.length} orden${orders.length > 1 ? 'es' : ''} en tu historial`
            : 'No tienes órdenes registradas'
          }
        </p>
      </div>

      {orders.length === 0 ? (
        <div className="empty-orders">
          <div className="empty-orders-icon">📦</div>
          <h3>No tienes órdenes aún</h3>
          <p>Cuando realices tu primera compra, aparecerá aquí tu historial.</p>
          <button 
            className="btn-primary"
            onClick={() => window.location.href = '/'}
          >
            Explorar Productos
          </button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order: Order) => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <div className="order-info">
                  <h3 className="order-number">Orden #{order.id}</h3>
                  <p className="order-date">{formatDate(order.createdAt)}</p>
                </div>
                <div className="order-status-container">
                  {getStatusBadge(order.status)}
                </div>
              </div>

              <div className="order-body">
                <div className="order-items">
                  <h4>Productos ({order.items.length})</h4>
                  {order.items.map((item, index) => (
                    <div key={index} className="order-item">
                      <div className="order-item-info">
                        <span className="item-name">{item.productName}</span>
                        <span className="item-quantity">x{item.quantity}</span>
                      </div>
                      <span className="item-price">
                        {formatPrice(item.price * item.quantity)}
                      </span>
                    </div>
                  ))}
                </div>

                <div className="order-summary">
                  <div className="summary-row">
                    <span>Subtotal:</span>
                    <span>{formatPrice(order.subtotal)}</span>
                  </div>
                  {order.shippingCost > 0 && (
                    <div className="summary-row">
                      <span>Envío:</span>
                      <span>{formatPrice(order.shippingCost)}</span>
                    </div>
                  )}
                  {order.tax > 0 && (
                    <div className="summary-row">
                      <span>Impuestos:</span>
                      <span>{formatPrice(order.tax)}</span>
                    </div>
                  )}
                  <div className="summary-row summary-total">
                    <span>Total:</span>
                    <span>{formatPrice(order.total)}</span>
                  </div>
                </div>
              </div>

              <div className="order-footer">
                <div className="order-address">
                  <strong>Dirección de envío:</strong>
                  <p>
                    {order.shippingAddress}
                  </p>
                </div>
                
                <div className="order-actions">
                  <button className="btn-secondary btn-small">
                    Ver Detalles
                  </button>
                  {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                    <button 
                      className="btn-danger btn-small"
                      onClick={() => showAlert({
                        type: 'info',
                        title: 'Cancelar orden',
                        message: 'Para cancelar esta orden, contacta con atención al cliente.'
                      })}
                    >
                      Cancelar
                    </button>
                  )}
                  {order.status === 'DELIVERED' && (
                    <button 
                      className="btn-primary btn-small"
                      onClick={() => showAlert({
                        type: 'info', 
                        title: 'Volver a comprar',
                        message: 'Esta funcionalidad estará disponible pronto.'
                      })}
                    >
                      Volver a Comprar
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Orders