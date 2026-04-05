package models;

import java.util.Calendar;

// Representa una operación de compraventa cerrada
// Cada venta genera DOS tratos: uno en el vendedor (tipo "venta") y otro en el comprador (tipo "compra")
// Ambos comparten el mismo ID para poder relacionarlos
public class Trato {

    private int id;
    private String tipo;            // "venta" o "compra" según a quién pertenece el trato
    private String emailOtroUser;   // Email de la otra parte: si es venta → email del comprador, y viceversa
    private Producto producto;      // Copia del producto en el momento de la venta
    private Calendar fecha;         // Fecha en la que se cerró el trato
    private double precio;          // Precio final acordado
    private String comentario;      // Comentario de la valoración (si la hay)
    private int puntuacion;         // 0 = sin valorar, 1-5 = valorado

    public Trato(int id, String tipo, String emailOtroUser, Producto producto, double precio) {
        this.id = id;
        this.tipo = tipo;
        this.emailOtroUser = emailOtroUser;
        this.producto = producto;
        this.fecha = Calendar.getInstance(); // Se guarda la fecha actual al crear el trato
        this.precio = precio;
        this.comentario = "No valorado";
        this.puntuacion = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getEmailOtroUser() { return emailOtroUser; }
    public Producto getProducto() { return producto; }
    public Calendar getFecha() { return fecha; }
    public double getPrecio() { return precio; }
    public String getComentario() { return comentario; }
    public int getPuntuacion() { return puntuacion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEmailOtroUser(String emailOtroUser) { this.emailOtroUser = emailOtroUser; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public void setFecha(Calendar fecha) { this.fecha = fecha; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    // Devuelve true si el trato ya tiene una puntuación asignada
    public boolean estaValorado() {
        return puntuacion != 0;
    }

    @Override
    public String toString() {
        String puntuacionStr = (puntuacion == 0) ? "No valorado" : String.valueOf(puntuacion);
        String comentarioStr = (puntuacion == 0) ? "No valorado" : comentario;

        return String.format(
            "**************************************************\n" +
            "Información de Venta\n" +
            "ID: %d\n" +
            "Vendedor: %s\n" +
            "Producto: %s\n" +
            "Precio: %.1f\n" +
            "Puntuación: %s\n" +
            "Comentario: %s",
            id,
            emailOtroUser,
            producto.getTitulo(),
            precio,
            puntuacionStr,
            comentarioStr
        );
    }
}
