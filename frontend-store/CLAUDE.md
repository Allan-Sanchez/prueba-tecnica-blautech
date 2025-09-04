# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a React TypeScript frontend application using Vite, Redux Toolkit Query, and SCSS. It's part of a technical test project that includes a Spring Boot backend API. The frontend implements a store interface with routing, state management, and custom styling.

## Common Commands

### Development
- `npm run dev` - Start development server with hot reload
- `npm run build` - Build for production (compiles TypeScript and bundles with Vite)
- `npm run preview` - Preview production build locally
- `npm run lint` - Run ESLint to check code quality

### Package Management
- `npm install` - Install all dependencies
- `npm install <package>` - Add new dependency

## Architecture

### Core Technologies
- **React 18** with TypeScript for UI components
- **Redux Toolkit Query** for state management and API calls
- **React Router DOM** for client-side routing
- **Vite** for build tooling and development server
- **SCSS** with modular architecture for styling

### Project Structure
```
src/
├── routes/         # React Router configuration
├── pages/          # Page-level components (Home, etc.)
├── store/          # Redux store, API definitions, hooks
├── types/          # TypeScript type definitions
└── scss/           # Modular SCSS with variables, components, utilities
```

### State Management Architecture
- **Redux Store**: Configured with Redux Toolkit's configureStore
- **RTK Query API**: Centralized API slice with fetchBaseQuery
- **Typed Hooks**: useAppSelector and useAppDispatch for type safety
- **Middleware**: Includes RTK Query middleware for caching and automatic refetching

### Routing Architecture
- **AppRouter**: Main routing component using BrowserRouter
- **Route Configuration**: Centralized in src/routes/AppRouter.tsx
- Currently implements single route (Home) with room for expansion

### Styling Architecture
- **SCSS Modules**: Organized imports in src/scss/index.scss
  - `_variables.scss` - CSS custom properties and theme system
  - `_base.scss` - Base styles and typography
  - `_components.scss` - Reusable component styles
  - `_utilities.scss` - Utility classes
- **Theme System**: Supports light/dark themes via data-theme attribute
- **Color Palette**: Uses Blautech-inspired color scheme

### API Integration
- Base API configuration in src/store/api.ts
- Ready for endpoint injection using RTK Query's pattern
- Configured with base query and reducer path 'api'

## Development Notes

- **Path Alias**: `@` resolves to `src/` directory (configured in vite.config.ts)
- **TypeScript**: Strict mode enabled with separate app and node configs
- **ESLint**: Configured with React hooks and refresh plugins
- **Hot Reload**: Vite provides fast HMR for development

## Build Configuration

- **Vite Config**: Uses @vitejs/plugin-react for JSX transformation
- **TypeScript**: Project references pattern with tsconfig.app.json and tsconfig.node.json
- **Output**: Builds to `dist/` directory (ignored by ESLint)