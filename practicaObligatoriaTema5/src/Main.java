import controlador.Controlador;

// Punto de entrada del programa — solo arranca el controlador
public class Main {
    public static void main(String[] args) {
        System.out.println("******************************************");
        System.out.println("         Bienvenido a FernanPop");
        System.out.println("     Tu marketplace de segunda mano");
        System.out.println("******************************************\n");

        Controlador controlador = new Controlador();
        controlador.iniciar();
    }
}
