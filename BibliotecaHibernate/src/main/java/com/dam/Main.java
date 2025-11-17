package com.dam;

import com.dam.gestion.GestionBiblioteca;
import com.dam.util.HibernateUtil;
import com.dam.modelo.Autor;
import com.dam.modelo.Libro;
import com.dam.modelo.Ejemplar;
import com.dam.modelo.Ejemplar.EstadoEjemplar;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

public class Main {

  public static void main(String[] args) {
    // Inicializa la utilidad de Hibernate para configurar la SessionFactory
    System.out.println("Iniciando conexión con Hibernate...");
    HibernateUtil.getSessionFactory();

    GestionBiblioteca gestion = new GestionBiblioteca();
    Scanner scanner = new Scanner(System.in);
    int opcion = -1;

    do {
      mostrarMenu();
      try {
        // Leer la opción como entero
        opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
          case 1:
            // Opción 1: Crear Autor y Libros
            solicitarDatosCrearAutor(gestion, scanner);
            break;
          case 2:
            // Opción 2: Agregar Ejemplar a Libro Existente
            solicitarDatosEjemplar(gestion, scanner);
            break;
          case 3:
            gestion.listarTodosLosAutores();
            break;
          case 4:
            solicitarDatosBusquedaLibro(gestion, scanner);
            break;
          case 5:
            solicitarDatosBusquedaEjemplarPorEstado(gestion, scanner);
            break;
          case 6:
            gestion.mostrarEstadisticas();
            break;
          case 7:
            solicitarDatosActualizacionEjemplar(gestion, scanner);
            break;
          case 8:
            solicitarDatosActualizacionLibro(gestion, scanner);
            break;
          case 9:
            solicitarDatosEliminarEjemplar(gestion, scanner);
            break;
          case 10:
            solicitarDatosEliminarLibro(gestion, scanner);
            break;
          case 11:
            solicitarDatosEliminarAutor(gestion, scanner);
            break;
          case 0:
            System.out.println("\nCerrando aplicación...");
            break;
          default:
            System.out.println("Opción no válida. Inténtelo de nuevo.");
        }
      } catch (InputMismatchException e) {
        // Maneja InputMismatchException y otras excepciones en el bucle principal
        /**
         * La excepción InputMismatchException se usa específicamente para
         * manejar errores de entrada cuando el usuario intenta ingresar un tipo
         * de dato que no coincide con lo que el método del Scanner está
         * esperando (p. ej., texto donde se espera un número entero).
         */
        System.err.println("Error en la entrada de datos. Por favor, asegúrese de introducir un número válido para la opción.");
        // Si ocurre un error, especialmente InputMismatchException,
        // debemos limpiar el buffer del scanner antes de volver a intentar leer.
        if (scanner.hasNextLine()) {
          scanner.nextLine();
        }
        opcion = -1;
      }

    } while (opcion != 0);

    HibernateUtil.shutdown();
    scanner.close();
  }

  private static void mostrarMenu() {
    System.out.println("\n=== SISTEMA DE GESTIÓN DE BIBLIOTECA ===");
    System.out.println(" 1. Crear nuevo autor con libros.");
    System.out.println(" 2. Agregar ejemplar a libro existente.");
    System.out.println(" 3. Listar todos los autores.");
    System.out.println(" 4. Buscar libro por ID.");
    System.out.println(" 5. Buscar ejemplares por estado.");
    System.out.println(" 6. Mostrar estadísticas.");
    System.out.println(" 7. Actualizar estado de ejemplar.");
    System.out.println(" 8. Actualizar datos de libro.");
    System.out.println(" 9. Eliminar ejemplar.");
    System.out.println("10. Eliminar libro.");
    System.out.println("11. Eliminar autor (con cascada).");
    System.out.println(" 0. Salir.");
    System.out.print("Seleccione una opción: ");
  }

  // MÉTODOS PARA SOLICITAR DATOS
  // Opción 1: Solicita datos completos para crear Autor, Libros y Ejemplares.
  private static void solicitarDatosCrearAutor(GestionBiblioteca gestion, Scanner scanner) {
    // --- 1. DATOS DEL AUTOR ---
    System.out.println("\n--- Creación de Nuevo Autor ---");
    System.out.print("Nombre del Autor: ");
    String nombre = scanner.nextLine();
    System.out.print("Apellidos del Autor: ");
    String apellidos = scanner.nextLine();
    System.out.print("Nacionalidad: ");
    String nacionalidad = scanner.nextLine();
    System.out.print("Fecha de Nacimiento (dd/MM/yyyy): ");
    String fechaNacimientoStr = scanner.nextLine();

    Date fechaNacimiento = null;
    try {
      fechaNacimiento = new SimpleDateFormat("dd/MM/yyyy").parse(fechaNacimientoStr);
    } catch (Exception e) {
      System.err.println("Formato de fecha no válido. Usando fecha actual.");
      fechaNacimiento = new Date();
    }

    Autor nuevoAutor = new Autor(nombre, apellidos, nacionalidad, fechaNacimiento);

    // --- 2. DATOS DE LOS LIBROS ---
    int numLibros = 0;
    try {
      System.out.print("¿Cuántos libros desea añadir a este autor? ");
      numLibros = scanner.nextInt();
      scanner.nextLine(); // Consumir
    } catch (InputMismatchException e) {
      System.err.println("Entrada no válida. Asumiendo 0 libros.");
      scanner.nextLine();
    }

    for (int i = 0; i < numLibros; i++) {
      System.out.println("\n--- Libro " + (i + 1) + " ---");
      System.out.print("Título: ");
      String titulo = scanner.nextLine();
      System.out.print("ISBN: ");
      String isbn = scanner.nextLine();

      System.out.print("Fecha de Publicación (dd/MM/yyyy): ");
      String fechaPubStr = scanner.nextLine();
      Date fechaPublicacion = null;
      try {
        fechaPublicacion = new SimpleDateFormat("dd/MM/yyyy").parse(fechaPubStr);
      } catch (Exception e) {
        System.err.println("Formato de fecha no válido. Usando fecha actual.");
        fechaPublicacion = new Date();
      }

      int numPaginas = 0;
      try {
        System.out.print("Número de Páginas: ");
        numPaginas = scanner.nextInt();
        scanner.nextLine(); // Consumir
      } catch (InputMismatchException e) {
        System.err.println("Entrada no válida. Asumiendo 100 páginas.");
        scanner.nextLine();
        numPaginas = 100;
      }

      Libro nuevoLibro = new Libro(titulo, isbn, fechaPublicacion, numPaginas);

      // --- 3. DATOS DE LOS EJEMPLARES ---
      int numEjemplares = 0;
      try {
        System.out.print("¿Cuántos ejemplares de '" + titulo + "' desea añadir? ");
        numEjemplares = scanner.nextInt();
        scanner.nextLine(); // Consumir
      } catch (InputMismatchException e) {
        System.err.println("Entrada no válida. Asumiendo 0 ejemplares.");
        scanner.nextLine();
      }

      for (int j = 0; j < numEjemplares; j++) {
        System.out.println("--- Ejemplar " + (j + 1) + " del libro " + (i + 1) + " ---");
        System.out.print("Código de Ejemplar (Ej: EJ-101): ");
        String codigoEjemplar = scanner.nextLine();

        System.out.println("Estados válidos: DISPONIBLE, PRESTADO, REPARACION, BAJA");
        System.out.print("Estado del Ejemplar: ");
        String estadoStr = scanner.nextLine().toUpperCase();

        EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE; // Valor por defecto
        try {
          estado = EstadoEjemplar.valueOf(estadoStr);
        } catch (IllegalArgumentException e) { // Captura si un argumento tiene un valor inapropiado (p. ej., un String que no coincide con un valor de Enum como EstadoEjemplar).
          System.err.println("Estado no válido. Usando DISPONIBLE.");
        }

        System.out.print("Ubicación (Ej: Estantería A3): ");
        String ubicacion = scanner.nextLine();

        Ejemplar nuevoEjemplar = new Ejemplar(codigoEjemplar, estado, ubicacion);

        // Asocia el ejemplar al libro (helper)
        nuevoLibro.addEjemplar(nuevoEjemplar);
      }

      // Asocia el libro al autor (helper)
      nuevoAutor.addLibro(nuevoLibro);
    }

    // --- 4. LLAMAR A LA CAPA DE GESTIÓN ---
    gestion.crearAutorConLibros(nuevoAutor);
  }

  /**
   * Opción 2: Solicita ISBN, código de ejemplar y UBICACIÓN, y llama a la capa
   * de gestión.
   */
  private static void solicitarDatosEjemplar(GestionBiblioteca gestion, Scanner scanner) {
    System.out.print("Introduzca el ISBN del libro (Ej: 978-84-376-0494-7): ");
    String isbn = scanner.nextLine();

    System.out.print("Introduzca el Código de Ejemplar del nuevo Ejemplar a añadir (Ej: EJ-100-2025): ");
    String codigoEjemplar = scanner.nextLine();

    gestion.agregarEjemplarALibroExistente(isbn, codigoEjemplar);
  }

  /**
   * Opción 4: Solicita el ID del libro.
   */
  private static void solicitarDatosBusquedaLibro(GestionBiblioteca gestion, Scanner scanner) {
    try {
      System.out.print("Introduzca el ID del Libro a buscar: ");
      Integer idLibro = scanner.nextInt();
      scanner.nextLine();

      gestion.buscarLibroPorId(idLibro);

    } catch (InputMismatchException e) {
      System.err.println("Error: El ID del libro debe ser un número entero.");
      scanner.nextLine();
    }
  }

  /**
   * Opción 5: Solicita el estado de los ejemplares a buscar.
   */
  private static void solicitarDatosBusquedaEjemplarPorEstado(GestionBiblioteca gestion, Scanner scanner) {
    System.out.println("Estados disponibles: DISPONIBLE, PRESTADO, REPARACION, BAJA");
    System.out.print("Introduzca el estado por el que desea buscar: ");
    String estadoStr = scanner.nextLine();

    gestion.buscarEjemplaresPorEstado(estadoStr);
  }

  /**
   * Opciones 7-11: Métodos STUB de solicitud de datos.
   */
  private static void solicitarDatosActualizacionEjemplar(GestionBiblioteca gestion, Scanner scanner) {
    System.out.print("Introduzca el Código de Ejemplar a actualizar: ");
    String codigoEjemplar = scanner.nextLine();
    System.out.println("Estados disponibles: DISPONIBLE, PRESTADO, REPARACION, BAJA");
    System.out.print("Introduzca el NUEVO estado: ");
    String nuevoEstadoStr = scanner.nextLine();
    gestion.actualizarEstadoEjemplar(codigoEjemplar, nuevoEstadoStr);
  }

  private static void solicitarDatosActualizacionLibro(GestionBiblioteca gestion, Scanner scanner) {
    try {
      System.out.print("Introduzca el ID del Libro a actualizar: ");
      Integer idLibro = scanner.nextInt();
      scanner.nextLine();
      System.out.print("Introduzca el nuevo Título (o deje vacío): ");
      String nuevoTitulo = scanner.nextLine();
      System.out.print("Introduzca el nuevo Nº de Páginas (o 0 si no cambia): ");
      Integer nuevasPaginas = scanner.nextInt();
      // Eliminado el scanner.nextLine() duplicado para evitar problemas
      gestion.actualizarDatosLibro(idLibro, nuevoTitulo, nuevasPaginas);
    } catch (InputMismatchException e) {
      System.err.println("Error: El ID/Nº de páginas debe ser un número entero. Limpiando buffer.");
      // Si el error es InputMismatchException, es crucial limpiar el buffer
      if (scanner.hasNextLine()) {
        scanner.nextLine();
      }
    } catch (Exception e) { // Capturar otras excepciones generales
      System.err.println("Ocurrió un error inesperado al leer los datos de actualización del libro.");
      scanner.nextLine();
    }
  }

  private static void solicitarDatosEliminarEjemplar(GestionBiblioteca gestion, Scanner scanner) {
    System.out.print("Introduzca el Código de Ejemplar a eliminar: ");
    String codigoEjemplar = scanner.nextLine();
    gestion.eliminarEjemplar(codigoEjemplar);
  }

  private static void solicitarDatosEliminarLibro(GestionBiblioteca gestion, Scanner scanner) {
    try {
      System.out.print("Introduzca el ID del Libro a eliminar: ");
      Integer idLibro = scanner.nextInt();
      scanner.nextLine();
      gestion.eliminarLibro(idLibro);
    } catch (InputMismatchException e) {
      System.err.println("Error: El ID del libro debe ser un número entero.");
      scanner.nextLine();
    }
  }

  private static void solicitarDatosEliminarAutor(GestionBiblioteca gestion, Scanner scanner) {
    try {
      System.out.print("Introduzca el ID del Autor a eliminar: ");
      Integer idAutor = scanner.nextInt();
      scanner.nextLine();
      gestion.eliminarAutor(idAutor);
    } catch (InputMismatchException e) {
      System.err.println("Error: El ID del autor debe ser un número entero.");
      scanner.nextLine();
    }
  }
}
