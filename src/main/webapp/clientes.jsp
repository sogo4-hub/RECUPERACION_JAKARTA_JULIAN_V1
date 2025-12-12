<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="es.daw.jakarta.pedidosexamen.model.Cliente" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Clientes</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f8fafc;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        header {
            background-color: #198754;
            color: white;
            padding: 1.5rem 0;
            text-align: center;
            margin-bottom: 2rem;
        }
        .cliente-card {
            border-radius: 1rem;
            box-shadow: 0 3px 8px rgba(0,0,0,0.1);
            transition: transform 0.2s ease-in-out;
        }
        .cliente-card:hover {
            transform: translateY(-5px);
        }
        .icon-delete {
            color: #dc3545;
            cursor: pointer;
        }
        .icon-delete:hover {
            color: #a71d2a;
        }
        .email {
            font-size: 0.9rem;
            color: #6c757d;
        }
        footer {
            text-align: center;
            margin-top: 3rem;
            font-size: 0.9rem;
            color: #6c757d;
        }
    </style>
</head>

<body>

<header>
    <h1>👥 Administración de Clientes</h1>
    <p class="lead mb-0">Consulta, gestión y eliminación de clientes</p>
</header>

<div class="container">
    <div class="row g-4">
        <%
            List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");

            if (clientes == null || clientes.isEmpty()) {
        %>
        <div class="col-12 text-center">
            <div class="alert alert-warning mt-4">No hay clientes registrados.</div>
        </div>
        <%
        } else {
            for (Cliente c : clientes) {
        %>
        <div class="col-md-6 col-sm-12">
            <div class="card cliente-card h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title text-success fw-bold"><%= c.getNombre() %></h5>

                        <!-- Ícono de eliminación -->
                        <form action="clientes/eliminar" method="post"
                              onsubmit="return confirm('¿Seguro que deseas eliminar este cliente?');">
                            <button type="submit" class="btn btn-link p-0" title="Eliminar cliente">
                                <i class="bi bi-trash-fill icon-delete fs-5"></i>
                            </button>
                        </form>
                    </div>

                    <p><strong>NIF:</strong> <%= c.getNif() %></p>
                    <p class="email"><i class="bi bi-envelope-fill"></i> <%= c.getEmail() %></p>
                    <p><small>📅 Registrado el: <%= c.getFechaRegistro() %></small></p>
                </div>
            </div>
        </div>
        <%
                }
            }
        %>
    </div>

    <!-- Botón volver -->
    <div class="text-center mt-5">
        <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-outline-success">⬅ Volver al inicio</a>
    </div>
</div>

<footer>
    <p>Proyecto JAKARTAEE · Gestión de Pedidos · © 2025</p>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
