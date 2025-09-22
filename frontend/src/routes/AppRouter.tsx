// src/routes/AppRouter.jsx
import { Routes, Route } from "react-router-dom";

// Public Pages
import Home from "../pages/public/Home";
import Products from "../pages/public/Products";
import ProductDetail from "../pages/public/ProductDetail";
import Login from "../pages/public/Login";
import Register from "../pages/public/Register";
import About from "../pages/public/About";

// Error Pages
import NotFound from "../pages/error/NotFound";

export default function AppRouter() {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/" element={<Home />} />
      <Route path="/products" element={<Products />} />
      <Route path="/products/:id" element={<ProductDetail />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/about" element={<About />} />

      {/* Catch-all */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}