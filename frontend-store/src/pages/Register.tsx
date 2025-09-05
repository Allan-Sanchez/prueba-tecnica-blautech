import React, { useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { useRegisterMutation } from '../store/apis'
import { loginSuccess } from '../store/authSlice'
import { useAlert } from '../contexts/AlertContext'
import type { RegisterRequest } from '../types'

interface RegisterFormData {
  email: string
  password: string
  confirmPassword: string
  firstName: string
  lastName: string
}

const Register: React.FC = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const { showAlert } = useAlert()
  const { isAuthenticated } = useAppSelector(state => state.auth)
  
  const [register, { isLoading: registerLoading }] = useRegisterMutation()
  
  const {
    register: registerField,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting }
  } = useForm<RegisterFormData>({
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: ''
    }
  })

  const watchedPassword = watch('password')

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/', { replace: true })
    }
  }, [isAuthenticated, navigate])

  const onSubmit = async (data: RegisterFormData) => {
    try {
      const registerData: RegisterRequest = {
        email: data.email,
        password: data.password,
        firstName: data.firstName.trim(),
        lastName: data.lastName.trim()
      }

      const response = await register(registerData).unwrap()
      
      if (response.success && response.data) {
        dispatch(loginSuccess(response.data))
        showAlert({
          type: 'success',
          title: '¡Cuenta creada exitosamente!',
          message: `Bienvenido, ${response.data.firstName}!`
        })
        navigate('/', { replace: true })
      } else {
        throw new Error(response.message || 'Error en el registro')
      }
    } catch (error: any) {
      showAlert({
        type: 'error',
        title: 'Error al crear la cuenta',
        message: error?.data?.message || error.message || 'Error al crear la cuenta. Intente nuevamente.'
      })
    }
  }

  if (isAuthenticated) {
    return null // Will redirect via useEffect
  }

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-card">
          {/* Header */}
          <div className="auth-header">
            <h1>Crear Cuenta</h1>
            <p>Únete a Frontend Store y comienza a comprar</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="firstName">Nombre</label>
                <input
                  {...registerField('firstName', {
                    required: 'El nombre es requerido',
                    minLength: {
                      value: 2,
                      message: 'El nombre debe tener al menos 2 caracteres'
                    },
                    validate: value => value.trim() !== '' || 'El nombre no puede estar vacío'
                  })}
                  type="text"
                  id="firstName"
                  className={errors.firstName ? 'error' : ''}
                  placeholder="Tu nombre"
                  disabled={registerLoading || isSubmitting}
                  autoComplete="given-name"
                />
                {errors.firstName && (
                  <span className="field-error">{errors.firstName.message}</span>
                )}
              </div>

              <div className="form-group">
                <label htmlFor="lastName">Apellido</label>
                <input
                  {...registerField('lastName', {
                    required: 'El apellido es requerido',
                    minLength: {
                      value: 2,
                      message: 'El apellido debe tener al menos 2 caracteres'
                    },
                    validate: value => value.trim() !== '' || 'El apellido no puede estar vacío'
                  })}
                  type="text"
                  id="lastName"
                  className={errors.lastName ? 'error' : ''}
                  placeholder="Tu apellido"
                  disabled={registerLoading || isSubmitting}
                  autoComplete="family-name"
                />
                {errors.lastName && (
                  <span className="field-error">{errors.lastName.message}</span>
                )}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                {...registerField('email', {
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
                disabled={registerLoading || isSubmitting}
                autoComplete="email"
              />
              {errors.email && (
                <span className="field-error">{errors.email.message}</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>
              <input
                {...registerField('password', {
                  required: 'La contraseña es requerida',
                  minLength: {
                    value: 6,
                    message: 'La contraseña debe tener al menos 6 caracteres'
                  },
                  pattern: {
                    value: /(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                    message: 'La contraseña debe contener al menos una mayúscula, una minúscula y un número'
                  }
                })}
                type="password"
                id="password"
                className={errors.password ? 'error' : ''}
                placeholder="Mínimo 6 caracteres"
                disabled={registerLoading || isSubmitting}
                autoComplete="new-password"
              />
              {errors.password && (
                <span className="field-error">{errors.password.message}</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Confirmar Contraseña</label>
              <input
                {...registerField('confirmPassword', {
                  required: 'Confirme su contraseña',
                  validate: value => value === watchedPassword || 'Las contraseñas no coinciden'
                })}
                type="password"
                id="confirmPassword"
                className={errors.confirmPassword ? 'error' : ''}
                placeholder="Repite tu contraseña"
                disabled={registerLoading || isSubmitting}
                autoComplete="new-password"
              />
              {errors.confirmPassword && (
                <span className="field-error">{errors.confirmPassword.message}</span>
              )}
            </div>

            <button
              type="submit"
              className="btn-primary auth-submit"
              disabled={registerLoading || isSubmitting}
            >
              {registerLoading || isSubmitting ? (
                <>
                  <span className="spinner"></span>
                  Creando cuenta...
                </>
              ) : (
                <>
                  Crear Cuenta
                </>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="auth-footer">
            <p>
              ¿Ya tienes una cuenta?{' '}
              <Link to="/login" className="auth-link">
                Inicia sesión aquí
              </Link>
            </p>
          </div>
        </div>

        {/* Registration Info */}
        <div className="registration-info">
          <h4>Beneficios de crear una cuenta</h4>
          <ul>
            <li>Guarda productos en tu carrito</li>
            <li>Historial de órdenes</li>
            <li>Checkout más rápido</li>
            <li>Ofertas personalizadas</li>
            <li>Sincronización entre dispositivos</li>
          </ul>
        </div>
      </div>
    </div>
  )
}

export default Register