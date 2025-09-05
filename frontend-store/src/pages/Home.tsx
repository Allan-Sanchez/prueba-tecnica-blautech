import React, { useState, useEffect } from 'react'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { initializeAuth } from '../store/authSlice'
import { 
  useGetProductsQuery, 
} from '../store/apis'
import { ProductCard } from '../components'
import type { Product } from '../types'
import '../scss/pages/_home.scss'

const Home: React.FC = () => {
  const dispatch = useAppDispatch()
  const { isAuthenticated } = useAppSelector(state => state.auth)
  const { totalItems } = useAppSelector(state => state.cart)
  
  const { data: productsResponse, isLoading, error } = useGetProductsQuery()

  useEffect(() => {
    dispatch(initializeAuth())
  }, [dispatch])

  const products = productsResponse?.data || []
  console.log("🚀 ~ Home ~ products:", products)


  if (isLoading) {
    return (
      <div className="container py-8">
        <div className="text-center">
          <div className="loading-spinner">Cargando productos...</div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container py-8">
        <div className="text-center">
          <div className="error-message">Error al cargar productos</div>
        </div>
      </div>
    )
  }

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title">Mi Tienda</h1>
          <p className="hero-subtitle">
            Descubre los mejores productos con la mejor tecnología
          </p>
          {totalItems > 0 && (
            <div className="hero-cart-status">
              🛒 Tienes {totalItems} producto{totalItems !== 1 ? 's' : ''} en tu carrito
            </div>
          )}
        </div>
      </section>



      {/* Products Section */}
      <section className="products-section">
        <div className="container">
          <h2 className="section-title">Nuestros Productos</h2>
            <div className="products-grid">
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  // onEdit={isAuthenticated ? handleEdit : undefined}
                  // onDelete={isAuthenticated ? handleDelete : undefined}
                  showActions={isAuthenticated}
                />
              ))}
            </div>
        </div>
      </section>
    </div>
  )
}

export default Home