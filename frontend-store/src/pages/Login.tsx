import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { useLoginMutation } from '../store/apis'
import { loginStart, loginSuccess, loginFailure } from '../store/authSlice'
import { useAlert } from '../contexts/AlertContext'

interface LoginFormData {
  email: string
  password: string
}

const Login: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useAppDispatch()
  const { showAlert } = useAlert()
  const { isAuthenticated, isLoading } = useAppSelector(state => state.auth)
  
  const [login, { isLoading: loginLoading }] = useLoginMutation()
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting }
  } = useForm<LoginFormData>({
    defaultValues: {
      email: '',
      password: ''
    }
  })

  const from = (location.state as any)?.from?.pathname || '/'

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, from])

  const onSubmit = async (data: LoginFormData) => {
    try {
      dispatch(loginStart())
      const response = await login(data).unwrap()
      
      if (response.success && response.data) {
        dispatch(loginSuccess(response.data))
        showAlert({
          type: 'success',
          message: `¡Bienvenido, ${response.data.user.firstName}!`
        })
        navigate(from, { replace: true })
      } else {
        throw new Error(response.message || 'Error en el login')
      }
    } catch (error: any) {
      dispatch(loginFailure())
      showAlert({
        type: 'error',
        title: 'Error de autenticación',
        message: error?.data?.message || error.message || 'Error al iniciar sesión. Verifique sus credenciales.'
      })
    }
  }

  if (isAuthenticated) {
    return null 
  }

  const [showPassword, setShowPassword] = useState(false)

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-card">
          {/* Header */}
          <div className="auth-header">
            <h1>Iniciar Sesión</h1>
            <p>Accede a tu cuenta en Mi Tienda</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                {...register('email', {
                  required: 'El email es requerido',
                  pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: 'Ingrese un email válido'
                  }
                })}
                type="email"
                id="email"
                className={errors.email ? 'error' : ''}
                placeholder="tu@email.com"
                disabled={loginLoading || isLoading || isSubmitting}
                autoComplete="email"
              />
              {errors.email && (
                <span className="field-error">{errors.email.message}</span>
              )}
            </div>

            <div className="form-group" style={{ position: 'relative' }}>
              <label htmlFor="password">Contraseña</label>
              <input
                {...register('password', {
                  required: 'La contraseña es requerida',
                  minLength: {
                    value: 6,
                    message: 'La contraseña debe tener al menos 6 caracteres'
                  }
                })}
                type={showPassword ? 'text' : 'password'}
                id="password"
                className={errors.password ? 'error' : ''}
                placeholder="Tu contraseña"
                disabled={loginLoading || isLoading || isSubmitting}
                autoComplete="current-password"
              />
              <button
                type="button"
                tabIndex={-1}
                aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                onClick={() => setShowPassword((prev) => !prev)}
                style={{
                  position: 'absolute',
                  right: 10,
                  top: 42,
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  padding: 0
                }}
              >
                {showPassword ? (
                  // Ojo abierto SVG
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                ) : (
                  // Ojo cerrado SVG
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a21.81 21.81 0 0 1 5.06-6.06M1 1l22 22"></path><path d="M9.53 9.53A3.5 3.5 0 0 0 12 15.5a3.5 3.5 0 0 0 2.47-5.97"></path></svg>
                )}
              </button>
              {errors.password && (
                <span className="field-error">{errors.password.message}</span>
              )}
            </div>

            <button
              type="submit"
              className="btn-primary auth-submit"
              disabled={loginLoading || isLoading || isSubmitting}
            >
              {loginLoading || isLoading || isSubmitting ? (
                <>
                  <span className="spinner"></span>
                  Iniciando sesión...
                </>
              ) : (
                <>
                  Iniciar Sesión
                </>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="auth-footer">
            <p>
              ¿No tienes una cuenta?{' '}
              <Link to="/register" className="auth-link">
                Regístrate aquí
              </Link>
            </p>
          </div>
        </div>

      </div>
    </div>
  )
}

export default Login