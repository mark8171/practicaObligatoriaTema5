package models;

import java.util.ArrayList;
import java.util.Collections;

// Clase central del modelo (actúa también como controlador de datos)
// Aquí está toda la lógica que afecta a más de un usuario a la vez
// El Controlador usa esta clase para cualquier operación sobre los datos
public class GestionAPP {

    private ArrayList<Usuario> usuarios; // Colección de todos los usuarios registrados

    public GestionAPP() {
        this.usuarios = new ArrayList<>();
    }

    // Getters / Setters
    public ArrayList<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(ArrayList<Usuario> usuarios) { this.usuarios = usuarios; }

    // Cuenta todos los productos en venta de todos los usuarios
    public int getTotalProductos() {
        int total = 0;
        for (Usuario u : usuarios) {
            total += u.productosEnVenta();
        }
        return total;
    }

    // Genera un ID para un nuevo usuario (coge el mayor ID existente y le suma 1)
    public int generaIdUser() {
        int maxId = 0;
        for (Usuario u : usuarios) {
            if (u.getId() > maxId) maxId = u.getId();
        }
        return maxId + 1;
    }

    // Comprueba si ya existe un usuario con ese ID
    public boolean buscaIdUser(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) return true;
        }
        return false;
    }

    // Busca un usuario por email — devuelve null si no existe
    // El email es el identificador único en la app (como el username)
    public Usuario buscaMail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    // Registra un nuevo usuario si el email no está ya en uso
    public boolean addUsuario(Usuario usuario) {
        if (usuario == null) return false;
        if (buscaMail(usuario.getEmail()) != null) return false; // Email duplicado
        usuarios.add(usuario);
        return true;
    }

    // Comprueba si un usuario ya está registrado (por email)
    public boolean buscaUsuario(Usuario usuario) {
        if (usuario == null) return false;
        return buscaMail(usuario.getEmail()) != null;
    }

    // Añade un producto al usuario indicado
    public boolean addProducto(Usuario usuario, Producto producto) {
        if (usuario == null || producto == null) return false;
        return usuario.addProducto(producto);
    }

    // Intenta hacer login con email y clave — devuelve el usuario si coincide, null si no
    public Usuario login(String email, String clave) {
        for (Usuario u : usuarios) {
            if (u.login(email, clave)) return u;
        }
        return null;
    }

    // Devuelve los productos en venta de un usuario concreto, ordenados por precio
    public ArrayList<Producto> getProductosUser(String email) {
        Usuario u = buscaMail(email);
        if (u == null) return new ArrayList<>();
        return u.getProductosOrdenados();
    }

    // Devuelve TODOS los productos del sistema ordenados por precio
    public ArrayList<Producto> getAllProductos() {
        ArrayList<Producto> todos = new ArrayList<>();
        for (Usuario u : usuarios) {
            todos.addAll(u.getEnVenta());
        }
        Collections.sort(todos);
        return todos;
    }

    // Busca un producto por su ID recorriendo todos los usuarios
    public Producto buscaProductoId(long id) {
        for (Usuario u : usuarios) {
            Producto p = u.getProducto(id);
            if (p != null) return p;
        }
        return null;
    }

    // Busca productos cuyo título contiene el texto dado (sin distinguir mayúsculas)
    // Devuelve la lista ordenada por precio
    public ArrayList<Producto> buscaProductosTexto(String texto) {
        ArrayList<Producto> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
            for (Producto p : u.getEnVenta()) {
                if (p.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                    resultado.add(p);
                }
            }
        }
        Collections.sort(resultado);
        return resultado;
    }

    // Elimina un usuario del sistema
    public boolean borrarUsuario(Usuario usuario) {
        return usuarios.remove(usuario);
    }

    // Busca un trato por ID recorriendo ventas y compras de todos los usuarios
    public Trato buscaTratoId(int id) {
        for (Usuario u : usuarios) {
            Trato t = u.getTrato(id);
            if (t != null) return t;
        }
        return null;
    }

    // Devuelve los tratos pendientes de valorar de un usuario
    // El orden es de más antiguo a más reciente (el orden en que se fueron añadiendo)
    public ArrayList<Trato> getValoracionesPendientes(Usuario usuario) {
        ArrayList<Trato> pendientes = new ArrayList<>();
        for (int idTrato : usuario.getValoracionesPendientesIds()) {
            Trato t = buscaTratoId(idTrato);
            if (t != null) pendientes.add(t);
        }
        return pendientes;
    }

    // Quita una valoración pendiente de un usuario (cuando ya ha valorado ese trato)
    public boolean borraValoracionPendiente(Usuario usuario, int id) {
        return usuario.quitaValoracionPendiente(id);
    }

    // Genera un ID único para un nuevo producto
    public long generaIdProducto() {
        long max = 0;
        for (Producto p : getAllProductos()) {
            if (p.getId() > max) max = p.getId();
        }
        return max + 1;
    }

    // Genera un ID único para un nuevo trato
    public int generaIdTrato() {
        int max = 0;
        for (Usuario u : usuarios) {
            for (Trato t : u.getVentas()) {
                if (t.getId() > max) max = t.getId();
            }
            for (Trato t : u.getCompras()) {
                if (t.getId() > max) max = t.getId();
            }
        }
        return max + 1;
    }

    // Devuelve el usuario que tiene un producto concreto en su lista en venta
    public Usuario getUsuarioPorProducto(long idProducto) {
        for (Usuario u : usuarios) {
            if (u.getProducto(idProducto) != null) return u;
        }
        return null;
    }

    // Cierra una venta: el vendedor indica qué producto vendió y a quién
    // Se crea un trato en el vendedor, otro en el comprador y se notifica al comprador
    public boolean cerrarVenta(Usuario vendedor, long idProducto, String emailComprador, double precio) {
        Usuario comprador = buscaMail(emailComprador);
        if (comprador == null) return false;           // El comprador no existe
        if (comprador.equals(vendedor)) return false;  // No se puede vender a uno mismo

        Producto prod = vendedor.getProducto(idProducto);
        if (prod == null) return false; // El vendedor no tiene ese producto

        int idTrato = generaIdTrato();

        // Trato que queda en el vendedor
        Trato tratoVenta = new Trato(idTrato, "venta", emailComprador, prod, precio);
        vendedor.getVentas().add(tratoVenta);
        vendedor.quitaProducto(idProducto); // Ya no está en venta

        // Trato que queda en el comprador (mismo ID para poder relacionarlos)
        Trato tratoCompra = new Trato(idTrato, "compra", vendedor.getEmail(), prod, precio);
        comprador.addTratoCompra(tratoCompra);

        // El comprador recibe la valoración pendiente
        comprador.addValoracionPendiente(idTrato);

        // Notificación al sistema (se imprime cuando el vendedor cierra la venta)
        System.out.println("\n>>> NOTIFICACIÓN: " + emailComprador +
                " tiene una nueva valoración pendiente por la compra de: " + prod.getTitulo());

        return true;
    }
}
