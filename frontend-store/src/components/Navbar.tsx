import React, { useState, useRef, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { toggleCart } from '../store/cartSlice'
import { logout as authLogout } from '../store/authSlice'
import { clearCart } from '../store/cartSlice'
import { useLogoutMutation } from '../store/apis'
import { useAlert } from '../contexts/AlertContext'

interface NavbarProps {
  className?: string
}

const Navbar: React.FC<NavbarProps> = ({ className = '' }) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const { isAuthenticated, user } = useAppSelector(state => state.auth)
  const { totalItems } = useAppSelector(state => state.cart)
  const [logoutMutation] = useLogoutMutation()
  const [showUserMenu, setShowUserMenu] = useState(false)
  const userMenuRef = useRef<HTMLDivElement>(null)
  const { showConfirm } = useAlert()
  const handleCartToggle = () => {
    dispatch(toggleCart())
  }

  const handleLogout = async () => {

          showConfirm(
            '¿Estás seguro de que quieres cerrar sesión?',
            async () => {
              await logoutMutation().unwrap()
                    dispatch(authLogout())
                    dispatch(clearCart())
                    setShowUserMenu(false)
                    navigate('/', { replace: true })

                        },
                        'Cerrar Sesión'
          )
  }

  const toggleUserMenu = () => {
    setShowUserMenu(!showUserMenu)
  }

  const handleLoginClick = () => {
    navigate('/login')
  }

  const handleProfileClick = () => {
    setShowUserMenu(false)
    navigate('/profile')
  }

  const handleOrdersClick = () => {
    setShowUserMenu(false)
    // send en searchParams userId and userEmail
    navigate('/orders')
  }

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target as Node)) {
        setShowUserMenu(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  return (
    <nav className={`navbar ${className}`}>
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/" className="brand-link">
            <h2>Mi Tienda </h2>
          </Link>
        </div>

        <div className="navbar-actions">
          {/* Cart Icon */}
          <button 
            className="navbar-cart-btn"
            onClick={handleCartToggle}
            aria-label={`Carrito con ${totalItems} productos`}
          >
            <div className="cart-icon">
              🛒
              {totalItems > 0 && (
                <span className="cart-badge">{totalItems}</span>
              )}
            </div>
          </button>

          {/* User Section */}
          <div className="navbar-user" ref={userMenuRef}>
            {isAuthenticated && user ? (
              <div className="user-menu-container">
                <button 
                  className="user-greeting"
                  onClick={toggleUserMenu}
                  aria-expanded={showUserMenu}
                  aria-haspopup="true"
                >
                  <span className="user-icon">👤</span>
                  <span className="user-text">
                    Hola, {user.firstName}
                  </span>
                  <span className="dropdown-arrow">
                    {showUserMenu ? '⌄' : '⌄'}
                  </span>
                </button>

                {showUserMenu && (
                  <div className="user-dropdown">
                    <button 
                      className="dropdown-item"
                      onClick={handleProfileClick}
                    >
                      Mi Perfil
                    </button>
                    <button 
                      className="dropdown-item"
                      onClick={handleOrdersClick}
                    >
                      Mis Compras
                    </button>
                    <hr className="dropdown-divider" />
                    <button 
                      className="dropdown-item logout-item"
                      onClick={handleLogout}
                    >
                      Cerrar Sesión
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <button className="login-btn" onClick={handleLoginClick}>
                <span className="user-icon">👤</span>
                <span>Iniciar Sesión</span>
              </button>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}

export default Navbar