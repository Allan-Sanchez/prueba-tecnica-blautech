import React from 'react'

const Home: React.FC = () => {
  return (
    <div className="container py-8">
      <div className="flex flex-col items-center gap-8">
        <div className="text-center">
          <h1 className="mb-4">Frontend Store</h1>
          <p className="text-lg text-secondary mb-6">
            Bienvenido a nuestra tienda con React + TypeScript + Redux Toolkit
          </p>
        </div>

        <div className="flex gap-4">
          <button className="btn-primary">
            Botón Primario
          </button>
          <button className="btn-secondary">
            Botón Secundario
          </button>
          <button className="btn-accent">
            Botón Accent
          </button>
        </div>

        <div className="card w-full" style={{ maxWidth: '500px' }}>
          <h3 className="mb-4">Ejemplo de Card</h3>
          <p className="mb-4">
            Este es un ejemplo de card usando las variables CSS personalizadas 
            que configuraste con la paleta de colores de Blautech.
          </p>
          
          <div className="flex gap-2 mb-4">
            <span className="badge badge-success">Éxito</span>
            <span className="badge badge-warning">Advertencia</span>
            <span className="badge badge-danger">Error</span>
            <span className="badge badge-info">Info</span>
          </div>

          <div className="flex flex-col gap-3">
            <input type="text" placeholder="Ejemplo de input" />
            <textarea placeholder="Ejemplo de textarea" rows={3}></textarea>
          </div>
        </div>

        <div className="mt-8">
          <h4 className="mb-4 text-center">Prueba de Temas</h4>
          <div className="flex gap-4">
            <button 
              onClick={() => document.documentElement.removeAttribute('data-theme')}
              className="btn-secondary"
            >
              Tema Claro
            </button>
            <button 
              onClick={() => document.documentElement.setAttribute('data-theme', 'dark')}
              className="btn-secondary"
            >
              Tema Oscuro
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home