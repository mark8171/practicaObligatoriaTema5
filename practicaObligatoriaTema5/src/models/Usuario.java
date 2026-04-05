package models;

import java.util.ArrayList;
import java.util.Collections;

public class Usuario {

    private int id;
    private String nombre;
    private String apel;
    private String email;   // Se usa como identificador único de usuario (como un username)
    private String clave;
    private int movil;

    private ArrayList<Producto> enVenta;              // Productos que el usuario tiene actualmente en venta
    private ArrayList<Trato> ventas;                  // Historial de cosas que ha vendido
    private ArrayList<Trato> compras;                 // Historial de cosas que ha comprado
    private ArrayList<Integer> valoracionesPendientes; // IDs de tratos que el usuario todavía no ha valorado

    public Usuario(int id, String nombre, String apel, String email, String clave, int movil) {
        this.id = id;
        this.nombre = nombre;
        this.apel = apel;
        this.email = email;
        this.clave = clave;
        this.movil = movil;
        this.enVenta = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.compras = new ArrayList<>();
        this.valoracionesPendientes = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApel() { return apel; }
    public String getEmail() { return email; }
    public String getClave() { return clave; }
    public int getMovil() { return movil; }
    public ArrayList<Producto> getEnVenta() { return enVenta; }
    public ArrayList<Trato> getVentas() { return ventas; }
    public ArrayList<Trato> getCompras() { return compras; }
    public ArrayList<Integer> getValoracionesPendientesIds() { return valoracionesPendientes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApel(String apel) { this.apel = apel; }
    public void setEmail(String email) { this.email = email; }
    public void setClave(String clave) { this.clave = clave; }
    public void setMovil(int movil) { this.movil = movil; }

    // Cuántos productos tiene en venta actualmente
    public int productosEnVenta() {
        return enVenta.size();
    }

    // Añade un producto a la lista en venta
    public boolean addProducto(Producto p) {
        if (p == null) return false;
        enVenta.add(p);
        return true;
    }

    // Elimina un producto de la lista en venta buscándolo por su ID
    public boolean quitaProducto(long idProducto) {
        for (Producto p : enVenta) {
            if (p.getId() == idProducto) {
                enVenta.remove(p);
                return true;
            }
        }
        return false; // No se encontró el producto
    }

    // Guarda el ID de un trato que el usuario tiene pendiente de valorar
    public boolean addValoracionPendiente(int idTrato) {
        if (valoracionesPendientes.contains(idTrato)) return false; // Evita duplicados
        valoracionesPendientes.add(idTrato);
        return true;
    }

    // Crea un trato de venta, lo añade al historial y quita el producto de la lista en venta
    // Devuelve el ID del trato creado, o -1 si el producto no existe
    public int addTratoVenta(long idProducto, String emailComprador, double precioFinal) {
        Producto prod = getProducto(idProducto);
        if (prod == null) return -1;

        int idTrato = ventas.size() + compras.size() + 1;
        Trato trato = new Trato(idTrato, "venta", emailComprador, prod, precioFinal);
        ventas.add(trato);
        quitaProducto(idProducto); // El producto ya no está disponible
        return idTrato;
    }

    // Añade un trato de compra al historial (lo llama GestionAPP al cerrar una venta)
    public boolean addTratoCompra(Trato trato) {
        if (trato == null) return false;
        compras.add(trato);
        return true;
    }

    // Cuántas valoraciones tiene pendientes
    public int valoracionesPendientes() {
        return valoracionesPendientes.size();
    }

    // Elimina un ID de la lista de valoraciones pendientes (cuando el usuario ya valoró)
    public boolean quitaValoracionPendiente(int idTrato) {
        return valoracionesPendientes.remove(Integer.valueOf(idTrato));
    }

    // Busca un producto en venta por su ID
    public Producto getProducto(long idProducto) {
        for (Producto p : enVenta) {
            if (p.getId() == idProducto) return p;
        }
        return null;
    }

    // Busca un trato por ID, mirando tanto en ventas como en compras
    public Trato getTrato(int idTrato) {
        for (Trato t : ventas) {
            if (t.getId() == idTrato) return t;
        }
        for (Trato t : compras) {
            if (t.getId() == idTrato) return t;
        }
        return null;
    }

    // Comprueba si el email y la clave coinciden con los del usuario
    public boolean login(String email, String clave) {
        return this.email.equals(email) && this.clave.equals(clave);
    }

    // Calcula la media de puntuaciones recibidas en sus ventas
    // Si nadie le ha valorado todavía devuelve 0.0
    public double notaMedia() {
        int total = 0;
        int count = 0;
        for (Trato t : ventas) {
            if (t.estaValorado()) {
                total += t.getPuntuacion();
                count++;
            }
        }
        if (count == 0) return 0.0;
        return (double) total / count;
    }

    // Devuelve los productos en venta ordenados de menor a mayor precio
    public ArrayList<Producto> getProductosOrdenados() {
        ArrayList<Producto> lista = new ArrayList<>(enVenta);
        Collections.sort(lista); // Usa el compareTo de Producto
        return lista;
    }

    @Override
    public String toString() {
        return String.format(
            "**************************************************\n" +
            "Perfil de usuario\n" +
            "ID: %d\n" +
            "Nombre: %s %s\n" +
            "Email: %s\n" +
            "Móvil: %d\n" +
            "Productos en venta: %d\n" +
            "Nota media: %.1f",
            id, nombre, apel, email, movil, enVenta.size(), notaMedia()
        );
    }
}
