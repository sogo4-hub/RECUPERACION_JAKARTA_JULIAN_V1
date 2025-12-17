package es.daw.jakarta.pedidosexamen.controller;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.daw.jakarta.pedidosexamen.model.Cliente;
import es.daw.jakarta.pedidosexamen.model.Pedido;
import es.daw.jakarta.pedidosexamen.repository.ClienteDAO;
import es.daw.jakarta.pedidosexamen.dao.GenericDAO;
import es.daw.jakarta.pedidosexamen.repository.PedidoDAO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@WebServlet(name = "ListarPedidosServlet", value = "/pedidos/listar")
public class ListarPedidosServlet extends HttpServlet {

    private static final Logger log =  Logger.getLogger(ListarPedidosServlet.class.getName());

    //private GenericDAO<Pedido,Long> pedidoDAO = null;
    private PedidoDAO pedidoDAO = null;//Para tener en cuenta la nueva funcion findByClienteId creada en PedidoDAO;
    private GenericDAO<Cliente,Long>  clienteDAO = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            pedidoDAO = new PedidoDAO();
            clienteDAO = new ClienteDAO();
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            log.severe(e.getMessage());
            throw new ServletException("Error inicializando DAOs",e);
        }


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Pedido> pedidos= new ArrayList<>();
        List<Cliente> clientes = new ArrayList<>();

        try{
            // RECUPERAMOS DATOS DE LA BASE DE DATOS
            // --- LÓGICA  QUE INCLUYE EL FILTRADO POR CLIENTE ---
            String clienteIdParam = request.getParameter("clienteId");
            Long clienteIdSeleccionado = null;

            if (clienteIdParam != null && !clienteIdParam.isEmpty()) {
                // Si viene un ID, lo convertimos y filtramos
                try {
                    clienteIdSeleccionado = Long.parseLong(clienteIdParam);
                    pedidos = pedidoDAO.findByClienteId(clienteIdSeleccionado);
                } catch (NumberFormatException e) {
                    pedidos = pedidoDAO.findAll(); // Si el ID no es válido, mostramos todos
                }
            } else {
                // Si no hay parámetro, mostramos todos
                pedidos = pedidoDAO.findAll();
            }
            // --- NUEVO BLOQUE: ORDENACIÓN POR ESTADO ---
            String orden = request.getParameter("orden");

            if (orden != null) {
                if ("asc".equals(orden)) {
                    // Ordenamos de la A a la Z basándonos en el nombre del estado
                    pedidos.sort(Comparator.comparing(p -> p.getEstado().name()));
                } else if ("desc".equals(orden)) {
                    // Ordenamos de la Z a la A
                    pedidos.sort((p1, p2) -> p2.getEstado().name().compareTo(p1.getEstado().name()));
                }
            }

            // Creamos el mapa (diccionario): Clave=ID del cliente, Valor=Nombre del cliente
            clientes = clienteDAO.findAll();
            Map<Long, String> mapaClientes = clientes.stream()
                    .collect(Collectors.toMap(Cliente::getId, Cliente::getNombre));

            // --- NUEVO BLOQUE: LIMITAR NÚMERO DE PEDIDOS (COOKIE) ---
            int limite = 0; // 0 significa "sin límite" por defecto

            // 1. Buscamos la cookie "pageSize"
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("pageSize".equals(c.getName())) {
                        try {
                            limite = Integer.parseInt(c.getValue());
                        } catch (NumberFormatException e) {
                            limite = 0; // Si hay error, asumimos sin límite
                        }
                        break;
                    }
                }
            }

            // 2. Aplicamos el límite si es mayor que 0 y menor que el total de pedidos
            if (limite > 0 && limite < pedidos.size()) {
                pedidos = pedidos.subList(0, limite);
            }

            // Enviamos los datos a la página JSP
            request.setAttribute("mapaClientes", mapaClientes);
            request.setAttribute("pedidos", pedidos);
            request.setAttribute("clientes", clientes);
            request.setAttribute("clienteIdSeleccionado", clienteIdSeleccionado); // Enviamos el ID seleccionado de vuelta a la JSP para mantener el desplegable

            getServletContext().getRequestDispatcher("/pedidos.jsp").forward(request, response);
        }catch(SQLException e){
            request.setAttribute("mensajeError",
                    "Error al acceder a la base de datos: " + e.getMessage());

            // Redirigir con ruta absoluta dentro del contexto
            getServletContext()
                    .getRequestDispatcher("/error.jsp")
                    .forward(request, response);
            return;
        }
    }

}