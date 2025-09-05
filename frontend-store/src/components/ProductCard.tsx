import React from 'react'
import { useAppDispatch } from '../store/hooks'
import { addToCart } from '../store/cartSlice'
import type { Product } from '../types'

interface ProductCardProps {
  product: Product
  onEdit?: (product: Product) => void
  onDelete?: (id: number) => void
  showActions?: boolean
}

const ProductCard: React.FC<ProductCardProps> = ({ 
  product, 
  onEdit, 
  onDelete, 
  showActions = false 
}) => {
  const dispatch = useAppDispatch()

  const handleAddToCart = () => {
    dispatch(addToCart(product))
  }

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-GT', {
      style: 'currency',
      currency: 'GTQ',
    }).format(price)
  }

  return (
    <div className="product-card">
      <div className="product-image">
        {product.imageUrl ? (
          <img src={product.imageUrl} alt={product.name} />
        ) : (
          <div className="product-placeholder">
            📦
          </div>
        )}
      </div>
      
      <div className="product-info">
        <h3 className="product-name">{product.name}</h3>
        <p className="product-description">{product.description}</p>
        
        <div className="product-details">
          <span className="product-price">{formatPrice(product.priceInCurrency)}</span>
        </div>

        {product.category && (
          <span className="product-category badge badge-info">
            {product.category}
          </span>
        )}
      </div>

      <div className="product-actions">
          <button 
            className="btn-primary add-to-cart-btn"
            onClick={handleAddToCart}
            // disabled={product.stock === 0}
          >
            🛒 Agregar al Carrito
          </button>

      </div>
    </div>
  )
}

export default ProductCard