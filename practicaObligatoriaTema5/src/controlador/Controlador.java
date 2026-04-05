package controlador;

import models.*;
import models.GestionAPP;
import models.Producto;
import models.Trato;
import models.Usuario;
import vista.Vista;

import java.util.ArrayList;

// Coordina el modelo y la vista
// Recoge la entrada del usuario (a través de Vista), llama a GestionAPP y muestra el resultado
// No hace lógica de negocio, eso es trabajo del modelo
public class Controlador {

    private GestionAPP app;          // El modelo — aquí están los datos y la lógica
    private Usuario usuarioActivo;   // El usuario que tiene la sesión abierta, null si no hay ninguno

    public Controlador() {
        this.app = new GestionAPP();
        this.usuarioActivo = null;
    }

    // Punto de entrada de la app — arranca el bucle principal
    public void iniciar() {
        cargarDatosPrueba();
        boolean salir = false;

        while (!salir) {
            // Dependiendo de si hay sesión abierta o no, se muestra un menú u otro
            if (usuarioActivo == null) {
                salir = menuSinLogin();
            } else {
                salir = menuConLogin();
            }
        }
        Vista.mostrarMensaje("¡Hasta pronto!");
    }

    // ===================== MENÚ SIN LOGIN =====================

    private boolean menuSinLogin() {
        Vista.mostrarMenuSinLogin();
        int opcion = Vista.pedirEntero("Introduzca la opción deseada: ");

        switch (opcion) {
            case 1: menuBusquedaProductos(); break;
            case 2: iniciarSesion(); break;
            case 3: registrarse(); break;
            case 4: return true; // Indica al bucle principal que hay que salir
            default: Vista.mostrarError("Opción no válida.");
        }
        return false;
    }

    // ===================== MENÚ CON LOGIN =====================

    private boolean menuConLogin() {
        Vista.mostrarMenuConLogin(usuarioActivo.getNombre(), usuarioActivo.valoracionesPendientes());
        int opcion = Vista.pedirEntero("Introduzca la opción deseada: ");

        switch (opcion) {
            case 1:  mostrarPerfil(); break;
            case 2:  cambiarDatosPersonales(); break;
            case 3:  menuMisProductos(); break;
            case 4:  introducirProducto(); break;
            case 5:  menuBusquedaProductos(); break;
            case 6:  menuValoraciones(); break;
            case 7:  verHistorialTratos(); break;
            case 8:  borrarPerfil(); return false; // Tras borrar el perfil volvemos al menú sin login
            case 9:  cerrarSesion(); break;
            case 10: return true;
            default: Vista.mostrarError("Opción no válida.");
        }
        return false;
    }

    // ===================== ACCIONES SIN LOGIN =====================

    private void iniciarSesion() {
        try {
            String email = Vista.pedirTexto("Email: ");
            String clave = Vista.pedirTexto("Contraseña: ");
            Usuario u = app.login(email, clave);

            if (u == null) {
                Vista.mostrarError("Email o contraseña incorrectos.");
            } else {
                usuarioActivo = u;
                Vista.mostrarNotificacion("Bienvenido, " + u.getNombre() + "!");
                // Notificación de valoraciones pendientes nada más hacer login
                if (u.valoracionesPendientes() > 0) {
                    Vista.mostrarNotificacion("Tienes " + u.valoracionesPendientes() + " valoraciones pendientes.");
                }
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    private void registrarse() {
        try {
            String nombre = Vista.pedirTexto("Nombre: ");
            String apel   = Vista.pedirTexto("Apellidos: ");
            String email  = Vista.pedirTexto("Email: ");

            // Comprobamos que el email no esté ya en uso antes de pedir el resto de datos
            if (app.buscaMail(email) != null) {
                Vista.mostrarError("Ya existe un usuario con ese email.");
                return;
            }

            String clave = Vista.pedirTexto("Contraseña: ");
            int movil    = Vista.pedirEntero("Móvil: ");

            int id = app.generaIdUser();
            Usuario nuevo = new Usuario(id, nombre, apel, email, clave, movil);

            if (app.addUsuario(nuevo)) {
                Vista.mostrarNotificacion("Usuario registrado correctamente. ID asignado: " + id);
            } else {
                Vista.mostrarError("No se pudo registrar el usuario.");
            }
        } catch (Exception e) {
            Vista.mostrarError("Error en el registro: " + e.getMessage());
        }
    }

    // ===================== BÚSQUEDA DE PRODUCTOS =====================

    // Submenú de búsqueda — accesible tanto con login como sin él
    private void menuBusquedaProductos() {
        boolean volver = false;
        while (!volver) {
            Vista.mostrarMenuBusquedaProductos();
            int opcion = Vista.pedirEntero("Introduzca la opción deseada: ");

            switch (opcion) {
                case 1: mostrarTodosProductos(); break;
                case 2: buscarProductoPorId(); break;
                case 3: buscarProductoPorTexto(); break;
                case 4: volver = true; break;
                default: Vista.mostrarError("Opción no válida.");
            }
        }
    }

    private void mostrarTodosProductos() {
        ArrayList<Producto> lista = app.getAllProductos();
        if (lista.isEmpty()) {
            Vista.mostrarMensaje("No hay productos disponibles.");
        } else {
            for (Producto p : lista) {
                Vista.mostrarMensaje(p.toString());
            }
        }
        Vista.pausar();
    }

    private void buscarProductoPorId() {
        try {
            long id = Vista.pedirLong("ID del producto: ");
            Producto p = app.buscaProductoId(id);
            if (p == null) {
                Vista.mostrarError("No se encontró ningún producto con ese ID.");
            } else {
                Vista.mostrarMensaje(p.toString());
            }
        } catch (Exception e) {
            Vista.mostrarError("Error en la búsqueda: " + e.getMessage());
        }
        Vista.pausar();
    }

    private void buscarProductoPorTexto() {
        try {
            String texto = Vista.pedirTexto("Texto a buscar en el nombre: ");
            ArrayList<Producto> lista = app.buscaProductosTexto(texto);
            if (lista.isEmpty()) {
                Vista.mostrarMensaje("No se encontraron productos con ese texto.");
            } else {
                for (Producto p : lista) {
                    Vista.mostrarMensaje(p.toString());
                }
            }
        } catch (Exception e) {
            Vista.mostrarError("Error en la búsqueda: " + e.getMessage());
        }
        Vista.pausar();
    }

    // ===================== ACCIONES CON LOGIN =====================

    private void mostrarPerfil() {
        Vista.mostrarMensaje(usuarioActivo.toString());
        Vista.pausar();
    }

    // Permite modificar los datos del perfil campo por campo
    // Si el usuario deja un campo vacío, se mantiene el valor anterior
    private void cambiarDatosPersonales() {
        try {
            Vista.mostrarMensaje("Deja en blanco para mantener el valor actual.");

            String nombre = Vista.pedirTexto("Nuevo nombre (" + usuarioActivo.getNombre() + "): ");
            if (!nombre.isEmpty()) usuarioActivo.setNombre(nombre);

            String apel = Vista.pedirTexto("Nuevos apellidos (" + usuarioActivo.getApel() + "): ");
            if (!apel.isEmpty()) usuarioActivo.setApel(apel);

            String email = Vista.pedirTexto("Nuevo email (" + usuarioActivo.getEmail() + "): ");
            if (!email.isEmpty()) {
                // No se puede poner un email que ya usa otro usuario
                if (!email.equals(usuarioActivo.getEmail()) && app.buscaMail(email) != null) {
                    Vista.mostrarError("Ese email ya está en uso.");
                } else {
                    usuarioActivo.setEmail(email);
                }
            }

            String clave = Vista.pedirTexto("Nueva contraseña (vacío para no cambiar): ");
            if (!clave.isEmpty()) usuarioActivo.setClave(clave);

            String movilStr = Vista.pedirTexto("Nuevo móvil (" + usuarioActivo.getMovil() + "): ");
            if (!movilStr.isEmpty()) {
                try {
                    usuarioActivo.setMovil(Integer.parseInt(movilStr));
                } catch (NumberFormatException e) {
                    Vista.mostrarError("Número de móvil no válido, se mantiene el anterior.");
                }
            }

            Vista.mostrarNotificacion("Datos actualizados correctamente.");
        } catch (Exception e) {
            Vista.mostrarError("Error al cambiar datos: " + e.getMessage());
        }
    }

    private void introducirProducto() {
        try {
            String titulo      = Vista.pedirTexto("Título del producto: ");
            String descripcion = Vista.pedirTexto("Descripción: ");
            double precio      = Vista.pedirDouble("Precio: ");
            String estado      = Vista.pedirTexto("Estado (Nuevo / Como nuevo / Poco uso / Usado / Muy usado): ");

            long id = app.generaIdProducto();
            Producto p = new Producto(id, titulo, descripcion, precio, estado);

            if (app.addProducto(usuarioActivo, p)) {
                Vista.mostrarNotificacion("Producto añadido con éxito. ID: " + id);
            } else {
                Vista.mostrarError("No se pudo añadir el producto.");
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al añadir producto: " + e.getMessage());
        }
    }

    // ===================== MIS PRODUCTOS =====================

    private void menuMisProductos() {
        boolean volver = false;
        while (!volver) {
            Vista.mostrarMenuMisProductos();
            int opcion = Vista.pedirEntero("Introduzca la opción deseada: ");

            switch (opcion) {
                case 1: mostrarMisProductos(); break;
                case 2: borrarProducto(); break;
                case 3: venderProducto(); break;
                case 4: volver = true; break;
                default: Vista.mostrarError("Opción no válida.");
            }
        }
    }

    private void mostrarMisProductos() {
        ArrayList<Producto> lista = usuarioActivo.getProductosOrdenados();
        if (lista.isEmpty()) {
            Vista.mostrarMensaje("No tienes productos en venta.");
        } else {
            for (Producto p : lista) {
                Vista.mostrarMensaje(p.toString());
            }
        }
        Vista.pausar();
    }

    private void borrarProducto() {
        try {
            mostrarMisProductos();
            long id = Vista.pedirLong("ID del producto a borrar: ");
            if (usuarioActivo.quitaProducto(id)) {
                Vista.mostrarNotificacion("Producto eliminado correctamente.");
            } else {
                Vista.mostrarError("No se encontró ese producto en tu lista.");
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al borrar producto: " + e.getMessage());
        }
    }

    // El vendedor cierra la venta indicando el ID del producto y el email del comprador
    private void venderProducto() {
        try {
            mostrarMisProductos();
            if (usuarioActivo.productosEnVenta() == 0) return;

            long idProducto      = Vista.pedirLong("ID del producto vendido: ");
            Producto prod        = usuarioActivo.getProducto(idProducto);
            if (prod == null) {
                Vista.mostrarError("No tienes ese producto.");
                return;
            }

            String emailComprador = Vista.pedirTexto("Email del comprador: ");
            if (emailComprador.equalsIgnoreCase(usuarioActivo.getEmail())) {
                Vista.mostrarError("No puedes venderte a ti mismo.");
                return;
            }

            double precio = Vista.pedirDouble("Precio final de la venta: ");

            // GestionAPP se encarga de todo: crear los tratos, notificar, etc.
            if (app.cerrarVenta(usuarioActivo, idProducto, emailComprador, precio)) {
                Vista.mostrarNotificacion("Venta cerrada correctamente.");
            } else {
                Vista.mostrarError("No se pudo cerrar la venta. Comprueba que el email del comprador existe.");
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al vender producto: " + e.getMessage());
        }
    }

    // ===================== VALORACIONES =====================

    private void menuValoraciones() {
        boolean volver = false;
        while (!volver) {
            Vista.mostrarMenuValoraciones();
            int opcion = Vista.pedirEntero("Introduzca la opción deseada: ");

            switch (opcion) {
                case 1: mostrarValoracionesPendientes(); break;
                case 2: valorarCompra(); break;
                case 3: volver = true; break;
                default: Vista.mostrarError("Opción no válida.");
            }
        }
    }

    private void mostrarValoracionesPendientes() {
        Vista.mostrarMensaje("Tratos pendientes de valorar:");
        ArrayList<Trato> pendientes = app.getValoracionesPendientes(usuarioActivo);
        if (pendientes.isEmpty()) {
            Vista.mostrarMensaje("No tienes valoraciones pendientes.");
        } else {
            for (Trato t : pendientes) {
                Vista.mostrarMensaje(t.toString());
            }
        }
        Vista.pausar();
    }

    private void valorarCompra() {
        try {
            ArrayList<Trato> pendientes = app.getValoracionesPendientes(usuarioActivo);
            if (pendientes.isEmpty()) {
                Vista.mostrarMensaje("No tienes valoraciones pendientes.");
                return;
            }

            mostrarValoracionesPendientes();

            int idTrato = Vista.pedirEntero("ID del trato a valorar: ");
            Trato trato = app.buscaTratoId(idTrato);

            // Comprobamos que ese trato realmente está pendiente para este usuario
            if (trato == null || !usuarioActivo.getValoracionesPendientesIds().contains(idTrato)) {
                Vista.mostrarError("No tienes ese trato pendiente de valorar.");
                return;
            }

            // Forzamos que la puntuación esté entre 1 y 5
            int puntuacion = 0;
            while (puntuacion < 1 || puntuacion > 5) {
                puntuacion = Vista.pedirEntero("Puntuación (1-5): ");
                if (puntuacion < 1 || puntuacion > 5) {
                    Vista.mostrarError("La puntuación debe estar entre 1 y 5.");
                }
            }

            String comentario = Vista.pedirTexto("Comentario: ");

            // Actualizamos el trato en el vendedor (es quien recibe la valoración)
            Usuario vendedor = app.buscaMail(trato.getEmailOtroUser());
            if (vendedor != null) {
                Trato tratoVendedor = vendedor.getTrato(idTrato);
                if (tratoVendedor != null) {
                    tratoVendedor.setPuntuacion(puntuacion);
                    tratoVendedor.setComentario(comentario);
                }
            }

            // También actualizamos la copia del trato que tiene el comprador
            trato.setPuntuacion(puntuacion);
            trato.setComentario(comentario);

            // Quitamos de la lista de pendientes
            app.borraValoracionPendiente(usuarioActivo, idTrato);
            Vista.mostrarNotificacion("Valoración guardada correctamente. ¡Gracias!");

        } catch (Exception e) {
            Vista.mostrarError("Error al valorar: " + e.getMessage());
        }
    }

    // ===================== HISTORIAL =====================

    private void verHistorialTratos() {
        try {
            Vista.mostrarSeparador();
            Vista.mostrarMensaje("--- Historial de ventas ---");
            ArrayList<Trato> ventas = usuarioActivo.getVentas();
            if (ventas.isEmpty()) {
                Vista.mostrarMensaje("No tienes ventas registradas.");
            } else {
                for (Trato t : ventas) {
                    Vista.mostrarMensaje(t.toString());
                }
            }

            Vista.mostrarMensaje("\n--- Historial de compras ---");
            ArrayList<Trato> compras = usuarioActivo.getCompras();
            if (compras.isEmpty()) {
                Vista.mostrarMensaje("No tienes compras registradas.");
            } else {
                for (Trato t : compras) {
                    Vista.mostrarMensaje(t.toString());
                }
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al mostrar historial: " + e.getMessage());
        }
        Vista.pausar();
    }

    // ===================== GESTIÓN DE CUENTA =====================

    private void borrarPerfil() {
        try {
            String confirmacion = Vista.pedirTexto("¿Seguro que deseas borrar tu perfil? (s/n): ");
            if (confirmacion.equalsIgnoreCase("s")) {
                if (app.borrarUsuario(usuarioActivo)) {
                    Vista.mostrarNotificacion("Perfil eliminado correctamente. Hasta pronto.");
                    usuarioActivo = null;
                } else {
                    Vista.mostrarError("No se pudo eliminar el perfil.");
                }
            } else {
                Vista.mostrarMensaje("Operación cancelada.");
            }
        } catch (Exception e) {
            Vista.mostrarError("Error al borrar perfil: " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        Vista.mostrarNotificacion("Sesión cerrada. ¡Hasta pronto, " + usuarioActivo.getNombre() + "!");
        usuarioActivo = null; // Al poner null, el bucle principal vuelve al menú sin login
    }

    // ===================== DATOS DE PRUEBA =====================

    // Se llama al iniciar la app para no tener que introducir todo a mano en cada prueba
    private void cargarDatosPrueba() {
        Usuario pepe = new Usuario(1, "Pepe", "García",   "pepe@test.com", "1234", 600111222);
        Usuario ana  = new Usuario(2, "Ana",  "López",    "ana@test.com",  "1234", 600333444);
        Usuario luis = new Usuario(3, "Luis", "Martínez", "luis@test.com", "1234", 600555666);

        pepe.addProducto(new Producto(1, "Iphone 11",            "Iphone 11 256GB, comprado en Abril de 2020",           550.0,   "Como nuevo"));
        pepe.addProducto(new Producto(3, "BMX X3 20d",           "BMW X3 con 45.000 km, todos los extras",               29000.0, "bien cuidado"));
        ana.addProducto (new Producto(2, "Zapatillas Adidas UltraBoost", "ZApatillas Adidas con poco uso, blancas y rojas", 90.0, "Poco uso"));
        ana.addProducto (new Producto(4, "PlayStation 4 pro + juegos",   "Consola sin marcas de uso, dos mandos y 20 juegos", 300.0, "Usado"));

        app.addUsuario(pepe);
        app.addUsuario(ana);
        app.addUsuario(luis);

        // Simulamos que Luis ya acordó la compra del BMW con Pepe
        app.cerrarVenta(pepe, 3, "luis@test.com", 15000.0);

        Vista.mostrarMensaje(">>> Datos de prueba cargados.\n");
    }
}
