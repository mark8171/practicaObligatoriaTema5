package models;

// Representa un producto que un usuario pone en venta
// Implementa Comparable para poder ordenar por precio
public class Producto implements Comparable<Producto> {

    private long id;           // ID único del producto en todo el sistema
    private String titulo;     // Nombre del producto
    private String descripcion;
    private double precio;
    private String estado;     // Ej: "Nuevo", "Como nuevo", "Poco uso", "Usado"

    public Producto(long id, String titulo, String descripcion, double precio, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
    }

    // Getters
    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getEstado() { return estado; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setEstado(String estado) { this.estado = estado; }

    // Necesario para Collections.sort() — ordena de menor a mayor precio
    @Override
    public int compareTo(Producto otro) {
        return Double.compare(this.precio, otro.precio);
    }

    // Formato de salida por consola
    @Override
    public String toString() {
        return String.format(
            "**************************************************\n" +
            "Información del producto. id: %d\n" +
            "Título: %s\n" +
            "Descripción: %s\n" +
            "Precio: %.1f\n" +
            "Estado: %s",
            id, titulo, descripcion, precio, estado
        );
    }
}
