import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useAppSelector, useAppDispatch } from '../store/hooks'
import { useGetProfileQuery, useUpdateProfileMutation, useLogoutMutation } from '../store/apis'
import { setUser, logout } from '../store/authSlice'
import { clearCart } from '../store/cartSlice'
import { useAlert } from '../contexts/AlertContext'

interface ProfileFormData {
  firstName: string
  lastName: string
  email: string
}

const Profile: React.FC = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const { showAlert } = useAlert()
  const { user, isAuthenticated } = useAppSelector(state => state.auth)
  
  const { data: profileResponse, isLoading: profileLoading } = useGetProfileQuery(undefined, {
    skip: !isAuthenticated
  })
  const [updateProfile, { isLoading: updateLoading }] = useUpdateProfileMutation()
  const [logoutMutation] = useLogoutMutation()
  
  const [isEditing, setIsEditing] = useState(false)
  
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting, isDirty }
  } = useForm<ProfileFormData>({
    defaultValues: {
      firstName: '',
      lastName: '',
      email: ''
    }
  })

  // Redirect if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { replace: true })
    }
  }, [isAuthenticated, navigate])

  // Update form data when profile loads
  useEffect(() => {
    const profileData = profileResponse?.data || user
    if (profileData) {
      reset({
        firstName: profileData.firstName || '',
        lastName: profileData.lastName || '',
        email: profileData.email || ''
      })
    }
  }, [profileResponse, user, reset])

  const onSubmit = async (data: ProfileFormData) => {
    try {
      const response = await updateProfile({
        firstName: data.firstName.trim(),
        lastName: data.lastName.trim(),
        email: data.email.trim(),
      }).unwrap()
      
      if (response.success && response.data) {
        dispatch(setUser(response.data))
        setIsEditing(false)
        showAlert({
          type: 'success',
          message: 'Perfil actualizado correctamente'
        })
      } else {
        throw new Error(response.message || 'Error al actualizar el perfil')
      }
    } catch (error: any) {
      showAlert({
        type: 'error',
        title: 'Error al actualizar perfil',
        message: error?.data?.message || error.message || 'Error al actualizar el perfil'
      })
    }
  }

  const handleLogout = async () => {
    if (window.confirm('¿Estás seguro de que quieres cerrar sesión?')) {
      try {
        await logoutMutation().unwrap()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        dispatch(logout())
        dispatch(clearCart())
        showAlert({
          type: 'info',
          message: 'Sesión cerrada correctamente'
        })
        navigate('/login', { replace: true })
      }
    }
  }

  const cancelEdit = () => {
    const profileData = profileResponse?.data || user
    if (profileData) {
      reset({
        firstName: profileData.firstName || '',
        lastName: profileData.lastName || '',
        email: profileData.email || ''
      })
    }
    setIsEditing(false)
  }

  if (!isAuthenticated) {
    return null // Will redirect via useEffect
  }

  const profileData = profileResponse?.data || user

  if (profileLoading || !profileData) {
    return (
      <div className="profile-page">
        <div className="container">
          <div className="loading-spinner">Cargando perfil...</div>
        </div>
      </div>
    )
  }

  return (
    <div className="profile-page">
      <div className="container">
        <div className="profile-container">
          {/* Header */}
          <div className="profile-header">
            <div className="profile-avatar">
              <span className="avatar-icon">👤</span>
            </div>
            <div className="profile-info">
              <h1>Mi Perfil</h1>
              <p className="profile-subtitle">
                Gestiona tu información personal
              </p>
            </div>
          </div>

          {/* Profile Card */}
          <div className="profile-card">
            <div className="card-header">
              <h2>Información Personal</h2>
              {!isEditing ? (
                <button
                  className="btn-secondary"
                  onClick={() => setIsEditing(true)}
                >
                  Editar
                </button>
              ) : (
                <div className="edit-actions">
                  <button
                    type="button"
                    className="btn-secondary"
                    onClick={cancelEdit}
                    disabled={updateLoading || isSubmitting}
                  >
                    Cancelar
                  </button>
                </div>
              )}
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="profile-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">Nombre</label>
                  <input
                    {...register('firstName', {
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
                    disabled={!isEditing || updateLoading || isSubmitting}
                    readOnly={!isEditing}
                  />
                  {errors.firstName && (
                    <span className="field-error">{errors.firstName.message}</span>
                  )}
                </div>

                <div className="form-group">
                  <label htmlFor="lastName">Apellido</label>
                  <input
                    {...register('lastName', {
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
                    disabled={!isEditing || updateLoading || isSubmitting}
                    readOnly={!isEditing}
                  />
                  {errors.lastName && (
                    <span className="field-error">{errors.lastName.message}</span>
                  )}
                </div>
              </div>

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
                  disabled={!isEditing || updateLoading || isSubmitting}
                  readOnly={!isEditing}
                />
                {errors.email && (
                  <span className="field-error">{errors.email.message}</span>
                )}
              </div>

              {isEditing && (
                <button
                  type="submit"
                  className="btn-primary mt-4"
                  disabled={updateLoading || isSubmitting || !isDirty}
                >
                  {updateLoading || isSubmitting ? (
                    <>
                      <span className="spinner"></span>
                      Actualizando...
                    </>
                  ) : (
                    <>
                      Guardar Cambios
                    </>
                  )}
                </button>
              )}
            </form>
          </div>

          {/* Account Info */}
          <div className="account-info">
            <h3>Información de la Cuenta</h3>
            <div className="info-grid">
              <div className="info-item">
                <strong>Estado:</strong>
                <span className={`status ${profileData.isActive ? 'active' : 'inactive'}`}>
                  {profileData.isActive ? '🟢 Activa' : '🔴 Inactiva'}
                </span>
              </div>
              <div className="info-item">
                <strong>Registrado:</strong>
                <span>{new Date(profileData.createdAt).toLocaleDateString('es-ES')}</span>
              </div>
              <div className="info-item">
                <strong>Última actualización:</strong>
                <span>{new Date(profileData.updatedAt).toLocaleDateString('es-ES')}</span>
              </div>
              <div className="info-item">
                <strong>ID de Usuario:</strong>
                <span><code>#{profileData.id}</code></span>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="profile-actions">
            <button
              className="btn-danger"
              onClick={handleLogout}
            >
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Profile