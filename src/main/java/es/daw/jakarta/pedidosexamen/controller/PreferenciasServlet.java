package es.daw.jakarta.pedidosexamen.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/preferencias")
public class PreferenciasServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Recibir el valor del formulario (3, 5, 8, 10 o 0 para "sin límite")
        String pageSizeParam = request.getParameter("pageSize");

        // 2. Crear la cookie
        Cookie cookie = new Cookie("pageSize", pageSizeParam);

        // 3. Configuración según enunciado:
        cookie.setMaxAge(60 * 60 * 24); // Duración de 1 día (en segundos)
        cookie.setPath("/");            // Disponible para toda la aplicación

        // 4. Añadir la cookie a la respuesta
        response.addCookie(cookie);

        // 5. Redirigir al usuario.
        // Lo mandamos directamente al listado para que vea el cambio aplicado.
        response.sendRedirect(request.getContextPath() + "/pedidos/listar");
    }
}
