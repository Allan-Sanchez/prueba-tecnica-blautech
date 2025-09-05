import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { removeFromCart, updateQuantity, clearCart, setCartOpen } from '../store/cartSlice'
import { useAlert } from '../contexts/AlertContext'
import { useCreateOrderMutation } from '../store/apis/orderApi'

const Cart: React.FC = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const { items, totalItems, totalPrice, isOpen } = useAppSelector(state => state.cart)
  const { isAuthenticated, user } = useAppSelector(state => state.auth)
  const { showConfirm, showAlert } = useAlert()
  const [createOrder, { isLoading: isCreatingOrder }] = useCreateOrderMutation()

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-GT', {
      style: 'currency',
      currency: 'GTQ'
    }).format(price)
  }

  const handleQuantityChange = (itemId: string, quantity: number) => {
    if (quantity <= 0) {
      dispatch(removeFromCart(itemId))
    } else {
      dispatch(updateQuantity({ itemId, quantity }))
    }
  }

  const handleClearCart = () => {
    console.log("🚀 ~ handleClearCart ~ items: LLEGO A ELIMINAR EL CARRITO")
    showConfirm(
      '¿Estás seguro de que quieres vaciar el carrito? Esta acción no se puede deshacer.',
      () => {
        dispatch(clearCart())
      },
      'Vaciar Carrito'
    )
  }

  const handleCheckout = async () => {
    // Validar que hay items en el carrito
    if (items.length === 0) {
      showAlert({
        type: 'warning',
        title: 'Carrito vacío',
        message: 'No hay productos en tu carrito para procesar.',
      })
      return
    }

    // Verificar autenticación
    if (!isAuthenticated) {
      showConfirm(
        'Para procesar tu compra necesitas iniciar sesión. ¿Deseas ir al login? Tu carrito se mantendrá guardado.',
        () => {
          dispatch(setCartOpen(false))
          navigate('/login')
        },
        'Iniciar Sesión Requerida'
      )
      return
    }

    try {
      // Por ahora crear una orden básica - en el futuro se pueden agregar datos de dirección y pago
      const orderRequest = {
        items: items.map(item => ({
          productId: item.product.id,
          quantity: item.quantity,
          pricePerUnit: item.product.priceInCurrency
        })),
        // Dirección y método de pago por defecto - se pueden obtener del perfil del usuario o un formulario
        shippingAddress: user?.shippingAddress || 'Dirección por defecto',
        billingAddress: user?.shippingAddress || 'Dirección por defecto',
        userEmail: user?.email || 'temporal@ejemplo.com',
        userId: user?.id || 0,
        paymentMethod: 'CASH_ON_DELIVERY',
        paymentReference: 'Referencia de pago',
        notes: 'pago al recibir'
      }

      const response = await createOrder(orderRequest).unwrap()
      
      if (response.success) {
        showAlert({
          type: 'success',
          title: '¡Orden creada exitosamente!',
          message: `Tu orden #${response.data.id} ha sido procesada. Total: ${formatPrice(response.data.totalAmount)}`,
          duration: 8000
        })
        
        // Limpiar el carrito después de crear la orden exitosamente
        dispatch(clearCart())
        dispatch(setCartOpen(false))
      }
    } catch (error: any) {
      console.error('Error creating order:', error)
      showAlert({
        type: 'error',
        title: 'Error al procesar la orden',
        message: error?.data?.message || 'Hubo un problema al procesar tu orden. Inténtalo nuevamente.',
        duration: 8000
      })
    }
  }

  const closeCart = () => {
    dispatch(setCartOpen(false))
  }

  if (!isOpen) return null

  return (
    <div className="cart-overlay" onClick={closeCart}>
      <div className="cart-sidebar" onClick={(e) => e.stopPropagation()}>
        <div className="cart-header">
          <h3>🛒 Mi Carrito ({totalItems})</h3>
          <button className="cart-close-btn" onClick={closeCart}>
            ✕
          </button>
        </div>

        <div className="cart-content">
          {items.length === 0 ? (
            <div className="cart-empty">
              <p>Tu carrito está vacío</p>
              <p>¡Agrega algunos productos increíbles!</p>
            </div>
          ) : (
            <>
              <div className="cart-items">
                {items.map((item) => (
                  <div key={item.id} className="cart-item">
                    <div className="cart-item-image">
                      {item.product.imageUrl ? (
                        <img src={item.product.imageUrl} alt={item.product.name} />
                      ) : (
                        <div className="cart-item-placeholder">📦</div>
                      )}
                    </div>
                    
                    <div className="cart-item-details">
                      <h4 className="cart-item-name">{item.product.name}</h4>
                      <p className="cart-item-price">{formatPrice(item.product.priceInCurrency)}</p>
                      
                      <div className="cart-item-quantity">
                        <button 
                          className="quantity-btn"
                          onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                        >
                          −
                        </button>
                        <span className="quantity-display">{item.quantity}</span>
                        <button 
                          className="quantity-btn"
                          onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                          // disabled={item.quantity >= item.product.stock}
                        >
                          +
                        </button>
                      </div>

                      <p className="cart-item-subtotal">
                        Subtotal: {formatPrice(item.quantity * item.product.priceInCurrency)}
                      </p>
                    </div>

                    <button 
                      className="cart-item-remove"
                      onClick={() => dispatch(removeFromCart(item.id))}
                      title="Eliminar del carrito"
                    >
                      X
                    </button>
                  </div>
                ))}
              </div>

              <div className="cart-footer">
                <div className="cart-total">
                  <h4>Total: {formatPrice(totalPrice)}</h4>
                </div>
                
                <div className="cart-actions">
                  <button 
                    className="btn-secondary btn-small"
                    onClick={handleClearCart}
                  >
                    Vaciar Carrito
                  </button>
                  <button 
                    className="btn-primary"
                    onClick={handleCheckout}
                    disabled={isCreatingOrder}
                  >
                    {isCreatingOrder ? 'Procesando...' : 'Pagar'}
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  )
}

export default Cart