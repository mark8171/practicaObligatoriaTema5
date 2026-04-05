package vista;

import java.util.Scanner;

// Gestiona toda la entrada y salida por consola
// El Controlador usa esta clase para mostrar cosas y pedir datos al usuario
// Si algún día se cambia a interfaz gráfica, solo habría que tocar esta clase
public class Vista {

    private static final Scanner sc = new Scanner(System.in);

    public static void mostrarMensaje(String msg) {
        System.out.println(msg);
    }

    public static void mostrarError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    public static void mostrarNotificacion(String msg) {
        System.out.println(">>> " + msg);
    }

    public static void mostrarSeparador() {
        System.out.println("**************************************************");
    }

    // Pide un texto al usuario (puede estar vacío, el controlador decide qué hacer con eso)
    public static String pedirTexto(String etiqueta) {
        System.out.print(etiqueta);
        return sc.nextLine().trim();
    }

    // Pide un entero y no deja seguir hasta que el usuario meta uno válido
    public static int pedirEntero(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Debes introducir un número entero.");
            }
        }
    }

    // Igual que pedirEntero pero para IDs de productos (que son long)
    public static long pedirLong(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            try {
                return Long.parseLong(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Debes introducir un número válido.");
            }
        }
    }

    // Para precios y valores decimales
    public static double pedirDouble(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Debes introducir un número decimal válido.");
            }
        }
    }

    // Pausa la ejecución hasta que el usuario pulse Enter — para que pueda leer la pantalla
    public static void pausar() {
        System.out.print("Pulse tecla + Enter para continuar......");
        sc.nextLine();
    }

    // Menú principal para usuarios no logueados
    public static void mostrarMenuSinLogin() {
        mostrarSeparador();
        System.out.println("            Menú sin loguear");
        System.out.println("1. Buscar Productos.");
        System.out.println("2. Iniciar sesión.");
        System.out.println("3. Registrarse");
        System.out.println("4. Salir");
    }

    // Menú principal para usuarios logueados
    // Muestra el número de valoraciones pendientes en la cabecera, como notificación
    public static void mostrarMenuConLogin(String nombreUsuario, int valoracionesPendientes) {
        mostrarSeparador();
        System.out.println("Tiene usted " + valoracionesPendientes + " tratos que valorar");
        System.out.println("            Menú de usuario");
        System.out.println("1. Mostrar mi perfil de usuario");
        System.out.println("2. Cambiar mis datos personales");
        System.out.println("3. Ver mis productos en venta");
        System.out.println("4. Introducir un producto para vender");
        System.out.println("5. Buscar Productos.");
        System.out.println("6. Ver valoraciones pendientes.");
        System.out.println("7. Ver mi historial de tratos.");
        System.out.println("8. Borrar mi perfil de usuario.");
        System.out.println("9. Cerrar sesión");
        System.out.println("10. Salir");
    }

    // Submenú de búsqueda de productos (accesible con y sin login)
    public static void mostrarMenuBusquedaProductos() {
        mostrarSeparador();
        System.out.println("        Menú de búsqueda de productos");
        System.out.println("1. Mostrar todos los productos del programa");
        System.out.println("2. Buscar productos con una id determinada");
        System.out.println("3. Buscar productos por texto en el nombre");
        System.out.println("4. Volver");
    }

    // Submenú para gestionar los productos propios del usuario
    public static void mostrarMenuMisProductos() {
        mostrarSeparador();
        System.out.println("        Menú de mis productos en venta");
        System.out.println("1. Mostrar todos mis productos");
        System.out.println("2. Borrar un producto");
        System.out.println("3. Vender un producto");
        System.out.println("4. Volver");
    }

    // Submenú para ver y gestionar valoraciones pendientes
    public static void mostrarMenuValoraciones() {
        mostrarSeparador();
        System.out.println("        Menú de valoraciones");
        System.out.println("1. Mostrar mis valoraciones pendientes");
        System.out.println("2. Valorar una compra");
        System.out.println("3. Volver");
    }
}
