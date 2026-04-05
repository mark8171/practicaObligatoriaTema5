# FernanPop

App de compraventa de segunda mano por consola hecha en Java. Práctica del Tema 5 de 1º DAM (estructuras de datos).

---

## Requisitos

- Java JDK 8 o superior
- IntelliJ IDEA (recomendado, aunque vale cualquier IDE)

Sin dependencias externas, solo la librería estándar de Java.

---

## Estructura

```
FernanPop/
└── src/
    ├── Main.java
    ├── modelo/
    │   ├── Producto.java
    │   ├── Trato.java
    │   ├── Usuario.java
    │   └── GestionAPP.java
    ├── vista/
    │   └── Vista.java
    └── controlador/
        └── Controlador.java
```

---

## Cómo ejecutarlo

**IntelliJ:** abrir el proyecto, asegurarse de que `src/` está como Sources Root y darle run a `Main.java`.

**Terminal:**
```bash
javac -d out -sourcepath src src/Main.java src/modelo/*.java src/vista/*.java src/controlador/*.java
java -cp out Main
```

---

## Usuarios de prueba

Al arrancar se cargan datos automáticamente para no tener que introducirlos a mano cada vez.

| Email | Contraseña |
|-------|-----------|
| pepe@test.com | 1234 |
| ana@test.com | 1234 |
| luis@test.com | 1234 |

Luis tiene una valoración pendiente desde el inicio (compró el BMW a Pepe).

---

## Cosas a tener en cuenta

- Los datos no se guardan al cerrar. Todo está en memoria.
- No se puede usar el mismo email para dos cuentas.
- No te puedes vender un producto a ti mismo.
- Los productos siempre salen ordenados de menor a mayor precio.
- Las valoraciones pendientes salen de más antigua a más reciente.

---

## Cómo funciona una venta

1. El vendedor va a *Mis productos → Vender un producto*.
2. Mete el ID del producto y el email del comprador.
3. El sistema cierra el trato: quita el producto, lo registra en el historial de ambos y le manda una notificación al comprador para que valore.
4. El comprador valora desde su menú con puntuación del 1 al 5 y un comentario.

---

## Versión

v1.0 — Tema 5: Estructuras de datos · 1º DAM
