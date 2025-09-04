import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Provider } from 'react-redux'
import './scss/index.scss'
import { store } from './store'
import {AppRouter} from './routes/AppRouter.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
        <AppRouter />
    </Provider>
  </StrictMode>
)
