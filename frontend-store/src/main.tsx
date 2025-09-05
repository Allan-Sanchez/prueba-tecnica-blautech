import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Provider } from 'react-redux'
import './scss/index.scss'
import { store } from './store'
import {AppRouter} from './routes/AppRouter.tsx'
import { AlertProvider } from './contexts/AlertContext'
import AlertContainer from './components/Alert'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <AlertProvider> 
        <AppRouter />
        <AlertContainer />
      </AlertProvider>
    </Provider>
  </StrictMode>
)
