import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Home, Login, Register, Profile } from '../pages';
import { Navbar, Cart, ProtectedRoute } from '../components';

export const AppRouter = () => {
	return (
		<BrowserRouter>
			<div className="App">
				<Navbar />
				<main className="main-content">
					<Routes>
						{/* Public Routes */}
						<Route path='/' element={<Home />} />
						<Route path='/login' element={<Login />} />
						<Route path='/register' element={<Register />} />
						
						{/* Protected Routes */}
						<Route path='/profile' element={
							<ProtectedRoute>
								<Profile />
							</ProtectedRoute>
						} />
					</Routes>
				</main>
				<Cart />
			</div>
		</BrowserRouter>
	);
};
