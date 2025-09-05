import React from 'react'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { removeFromCart, updateQuantity, clearCart, setCartOpen } from '../store/cartSlice'
import { useAlert } from '../contexts/AlertContext'

const Cart: React.FC = () => {
  const dispatch = useAppDispatch()
  const { items, totalItems, totalPrice, isOpen } = useAppSelector(state => state.cart)
  const { showConfirm } = useAlert()

  const formatPrice = (price: number) => {
    console.log("🚀 ~ formatPrice ~ number:", price)
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

  const handleCheckout = () => {
    alert('Funcionalidad de checkout en desarrollo')
    // TODO: Implement checkout functionality
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
                  >
                    Pagar
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