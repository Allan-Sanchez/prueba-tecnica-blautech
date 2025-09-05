import React, { useState, useEffect } from 'react'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { initializeAuth } from '../store/authSlice'
import { 
  useGetProductsQuery, 
  useCreateProductMutation, 
  useUpdateProductMutation, 
  useDeleteProductMutation 
} from '../store/apis'
import { ProductCard } from '../components'
import type { Product } from '../types'

const Home: React.FC = () => {
  const dispatch = useAppDispatch()
  const { isAuthenticated, user } = useAppSelector(state => state.auth)
  const { totalItems } = useAppSelector(state => state.cart)
  
  const { data: productsResponse, isLoading, error } = useGetProductsQuery()
  const [createProduct] = useCreateProductMutation()
  const [updateProduct] = useUpdateProductMutation()
  const [deleteProduct] = useDeleteProductMutation()

  const [showForm, setShowForm] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    priceInCurrency: 0,
    // stock: 0,
    category: '',
    imageUrl: '',
    isActive: true
  })

  useEffect(() => {
    dispatch(initializeAuth())
  }, [dispatch])

  const products = productsResponse?.data || []
  console.log("🚀 ~ Home ~ products:", products)

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      price: 0,
      stock: 0,
      category: '',
      imageUrl: '',
      isActive: true
    })
    setEditingProduct(null)
    setShowForm(false)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    try {
      if (editingProduct) {
        await updateProduct({
          id: editingProduct.id,
          product: formData
        }).unwrap()
      } else {
        await createProduct(formData).unwrap()
      }
      resetForm()
    } catch (error) {
      console.error('Error saving product:', error)
    }
  }

  const handleEdit = (product: Product) => {
    setFormData({
      name: product.name,
      description: product.description,
      priceInCurrency: product.priceInCurrency,
      // stock: product.stock,
      category: product.category || '',
      imageUrl: product.imageUrl || '',
      isActive: product.isActive
    })
    setEditingProduct(product)
    setShowForm(true)
  }

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Estás seguro de que quieres eliminar este producto?')) {
      try {
        await deleteProduct(id).unwrap()
      } catch (error) {
        console.error('Error deleting product:', error)
      }
    }
  }

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
          <h1 className="hero-title">Frontend Store</h1>
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
                  onEdit={isAuthenticated ? handleEdit : undefined}
                  onDelete={isAuthenticated ? handleDelete : undefined}
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