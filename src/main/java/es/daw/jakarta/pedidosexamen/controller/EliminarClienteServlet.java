package es.daw.jakarta.pedidosexamen.controller;

import es.daw.jakarta.pedidosexamen.repository.ClienteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/clientes/eliminar")
public class EliminarClienteServlet extends HttpServlet {

    private ClienteDAO clienteDAO;

    @Override
    public void init() throws ServletException {
        try {
            clienteDAO = new ClienteDAO();
        } catch (SQLException e) {
            throw new ServletException("Error al iniciar DAO", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        try {
            if (idStr != null) {
                Long id = Long.parseLong(idStr);
                clienteDAO.delete(id); // Intentamos borrar en la BD
            }
            // Si funciona, volvemos a la lista (Patrón Post-Redirect-Get)
            response.sendRedirect(request.getContextPath() + "/clientes/listar");

        } catch (SQLException e) {
            // ¡AQUÍ ESTÁ LA CLAVE DEL EJERCICIO!
            // Si hay un error de integridad (FK), la BD lanza SQLException.
            // Nosotros la capturamos y mostramos la página de error bonita.
            e.printStackTrace();

            String mensaje = "Error al recuperar los clientes: Violación de una restricción de Integridad Referencial:";
            request.setAttribute("mensajeError", mensaje + e.getMessage());

            // Usamos forward() para mantener el atributo del mensaje
            getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // Si el ID no es un número válido, volvemos al listado
            response.sendRedirect(request.getContextPath() + "/clientes/listar");
        }
    }
}
