package com.dam.gestion;

import com.dam.modelo.Autor;
import com.dam.modelo.Ejemplar;
import com.dam.modelo.Libro;
import com.dam.util.HibernateUtil;
import com.dam.modelo.Ejemplar.EstadoEjemplar;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class GestionBiblioteca {
  // APARTADO 3: OPERACIONES CREATE (Opciones 1 y 2)

  // Opción 1: Persiste un Autor completo con sus Libros y Ejemplares asociados. Recibe el objeto 'Autor' construido desde la capa de presentación (Main).
  
  public void crearAutorConLibros(Autor autor) {
    Session session = null;
    Transaction transaction = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      
      // Pone el objeto 'autor' en estado persistente.
      session.persist(autor);

      transaction.commit();
      System.out.println("Autor '" + autor.getApellidos() + "' y sus relaciones fueron insertados correctamente.");

    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.err.println("Error al persistir el autor y sus relaciones: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }


  // Opción 2: Busca un libro existente por ISBN y añade un nuevo ejemplar. Recibe los datos primitivos (ISBN, codigoEjemplar) desde el Main.
  
  public void agregarEjemplarALibroExistente(String isbn, String codigoEjemplar) {
    Session session = null;
    Transaction transaction = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      // 1. Buscar el libro por ISBN
      // Le damos un alias a libro (l) para acortar código y :isbn es un parametro nombrado un marcador de posición que le dice a Hibernate: 
      // "Aquí voy a insertar el valor del ISBN más adelante, antes de ejecutar la consulta."
      Query<Libro> query = session.createQuery("FROM Libro l WHERE l.isbn = :isbn", Libro.class);
      query.setParameter("isbn", isbn); // Asigna el valor real del ISBN (obtenido del Scanner) al marcador de posición ":isbn"
      Libro libro = query.uniqueResult(); // Ahora sí, se ejecuta la consulta con el valor real insertado de forma segura

      if (libro == null) {
        System.err.println("Error: No se encontró ningún libro con el ISBN: " + isbn);
        transaction.rollback();
        return;
      }

      // 2. Crear el nuevo ejemplar (GestionBiblioteca crea el objeto Ejemplar)
      Ejemplar nuevoEjemplar = new Ejemplar(
        codigoEjemplar,
        EstadoEjemplar.DISPONIBLE,
        "Almacén"
      );

      // 3. Asociar ejemplar al libro y actualizar
      libro.addEjemplar(nuevoEjemplar);
      session.merge(libro); // Sincroniza el Libro con la sesión, asegurando la persistencia en cascada del nuevo Ejemplar.

      transaction.commit();
      System.out.println("Ejemplar '" + codigoEjemplar + "' agregado correctamente al libro: " + libro.getTitulo());

    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.err.println("Error al agregar ejemplar: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }

  // APARTADO 4: OPERACIONES READ (Opciones 3, 4, 5 y 6)
  
  // Opción 3: Recupera todos los autores y muestra el número de libros.
  
  public void listarTodosLosAutores() {
    Session session = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      Query<Autor> query = session.createQuery("FROM Autor", Autor.class);
      List<Autor> autores = query.list();

      if (autores.isEmpty()) {
        System.out.println("No hay autores registrados en la base de datos.");
        return;
      }

      System.out.println("\n=== LISTADO DE AUTORES REGISTRADOS ===");
      System.out.printf("%-5s | %-30s | %-15s | %s\n", "ID", "NOMBRE COMPLETO", "NACIONALIDAD", "Nº LIBROS");
      System.out.println("------------------------------------------------------------------");

      for (Autor autor : autores) {
        int numLibros = autor.getLibros().size();

        System.out.printf("%-5d | %-30s | %-15s | %d\n",
          autor.getIdAutor(),
          autor.getNombre() + " " + autor.getApellidos(),
          autor.getNacionalidad(),
          numLibros
        );
      }
      System.out.println("==================================================================");

    } catch (Exception e) {
      System.err.println("Error al listar autores: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }


  // Opción 4: Busca un Libro por su clave primaria (ID).
  
  public void buscarLibroPorId(Integer idLibro) {
    Session session = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      Libro libro = session.get(Libro.class, idLibro);

      if (libro == null) {
        System.out.println("No se encontró ningún libro con el ID: " + idLibro);
        return;
      }

      System.out.println("\n=== DETALLES DEL LIBRO (ID: " + idLibro + ") ===");
      System.out.println("Título: " + libro.getTitulo());
      System.out.println("ISBN: " + libro.getIsbn());
      System.out.println("Páginas: " + libro.getNumeroPaginas());

      if (libro.getAutor() != null) {
        System.out.println("Autor: " + libro.getAutor().getNombre() + " " + libro.getAutor().getApellidos());
      }

      System.out.println("--- Ejemplares (" + libro.getEjemplares().size() + ") ---");
      if (libro.getEjemplares().isEmpty()) {
        System.out.println("Sin ejemplares registrados");
      } else {
        for (Ejemplar ejemplar : libro.getEjemplares()) {
          System.out.println("  [Código: " + ejemplar.getCodigoEjemplar() + " | Estado: " + ejemplar.getEstado() + " | Ubicación: " + ejemplar.getUbicacion() + "]");
        }
      }
      System.out.println("=========================================");

    } catch (Exception e) {
      System.err.println("Error al buscar libro por ID: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }

  
  // Opción 5: Busca y lista todos los ejemplares que se encuentren en el estado proporcionado.
  
  public void buscarEjemplaresPorEstado(String estadoStr) {
    Session session = null;

    try {
      EstadoEjemplar estado;
      try {
        estado = EstadoEjemplar.valueOf(estadoStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.err.println("Error: El estado '" + estadoStr + "' no es válido. Opciones: DISPONIBLE, PRESTADO, REPARACION, BAJA.");
        return;
      }

      session = HibernateUtil.getSessionFactory().openSession();

      Query<Ejemplar> query = session.createQuery("FROM Ejemplar e WHERE e.estado = :estado", Ejemplar.class);
      query.setParameter("estado", estado);
      List<Ejemplar> ejemplares = query.list();

      if (ejemplares.isEmpty()) {
        System.out.println("No se encontraron ejemplares en estado: " + estado.toString());
        return;
      }

      System.out.println("\n=== EJEMPLARES EN ESTADO: " + estado.toString() + " ===");
      System.out.printf("%-15s | %-40s | %s\n", "CÓDIGO", "TÍTULO DEL LIBRO", "UBICACIÓN");
      System.out.println("-------------------------------------------------------------------------");

      for (Ejemplar ejemplar : ejemplares) {
        String tituloEjemplar = (ejemplar.getLibro() != null) ? ejemplar.getLibro().getTitulo() : "N/A";

        System.out.printf("%-15s | %-40s | %s\n",
          ejemplar.getCodigoEjemplar(),
          tituloEjemplar,
          ejemplar.getUbicacion()
        );
      }
      System.out.println("=========================================================================");

    } catch (Exception e) {
      System.err.println("Error al buscar ejemplares por estado: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }

  // Opción 6: Muestra el número total de autores, libros y ejemplares registrados.
  
  public void mostrarEstadisticas() {
    Session session = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      
      // 
      Long totalAutores = session.createQuery("SELECT COUNT(a) FROM Autor a", Long.class).uniqueResult();
      Long totalLibros = session.createQuery("SELECT COUNT(l) FROM Libro l", Long.class).uniqueResult();
      Long totalEjemplares = session.createQuery("SELECT COUNT(e) FROM Ejemplar e", Long.class).uniqueResult();

      System.out.println("\n=== ESTADÍSTICAS GLOBALES DE LA BIBLIOTECA ===");
      System.out.println("  Autores registrados: " + totalAutores);
      System.out.println("  Libros únicos en catálogo: " + totalLibros);
      System.out.println("  Ejemplares físicos en total: " + totalEjemplares);
      System.out.println("==============================================");

    } catch (Exception e) {
      System.err.println("Error al mostrar estadísticas: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }

  // APARTADO 5: MÉTODOS DE ACTUALIZACIÓN (UPDATE)

  // Opción 7: Actualiza el estado de un Ejemplar por su código.

  public void actualizarEstadoEjemplar(String codigoEjemplar, String nuevoEstadoStr) {
    Session session = null;
    Transaction transaction = null;

    try {
        EstadoEjemplar nuevoEstado;
        // Intenta convertir el String (en mayúsculas) a EstadoEjemplar; si el valor obtenido no es inválido, captura la excepción y notifica el error.
        try {
            nuevoEstado = EstadoEjemplar.valueOf(nuevoEstadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: El estado '" + nuevoEstadoStr + "' no es válido. Opciones: DISPONIBLE, PRESTADO, REPARACION, BAJA.");
            return;
        }

        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        // Buscar el ejemplar por su código único
        Query<Ejemplar> query = session.createQuery("FROM Ejemplar e WHERE e.codigoEjemplar = :codigo", Ejemplar.class);
        query.setParameter("codigo", codigoEjemplar);
        Ejemplar ejemplar = query.uniqueResult();

        if (ejemplar != null) {
            ejemplar.setEstado(nuevoEstado);
            transaction.commit();
            System.out.println("Ejemplar '" + codigoEjemplar + "' actualizado a estado: " + nuevoEstado.toString());
        } else {
            transaction.rollback(); // Revierte todos los cambios de la transacción a la base de datos debido a un error
            System.out.println("No se encontró el ejemplar con código: " + codigoEjemplar);
        }

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        System.err.println("Error al actualizar estado del ejemplar: " + e.getMessage());
    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
  }

  // Opción 8: Actualiza el título y/o el número de páginas de un Libro por su ID.
  
  public void actualizarDatosLibro(Integer idLibro, String nuevoTitulo, Integer nuevasPaginas) {
    Session session = null;
    Transaction transaction = null;

    try {
      session = HibernateUtil.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      // 1. Cargar el Libro por su ID
      Libro libro = session.get(Libro.class, idLibro);

      if (libro != null) {
        boolean modificado = false;
        
        // 2. Actualizar Título si se proporciona un valor no vacío
        if (nuevoTitulo != null && !nuevoTitulo.trim().isEmpty()) {
          libro.setTitulo(nuevoTitulo.trim());
          modificado = true;
          System.out.println(" --> Título actualizado a: " + nuevoTitulo);
        }

        // 3. Actualizar Número de Páginas si es mayor que 0
        // Se maneja la nulidad, crucial cuando el parámetro es Integer
        if (nuevasPaginas != null && nuevasPaginas > 0) {
          libro.setNumeroPaginas(nuevasPaginas); 
          modificado = true;
          System.out.println(" --> Nº de Páginas actualizado a: " + nuevasPaginas);
        }
        
        if (modificado) {
            transaction.commit();
            System.out.println("Libro con ID " + idLibro + " actualizado correctamente en la base de datos.");
        } else {
            transaction.rollback();
            System.out.println("No se realizaron cambios en el libro con ID " + idLibro + " (datos vacíos o no válidos).");
        }
        
      } else {
        transaction.rollback();
        System.out.println("No se encontró el libro con ID: " + idLibro + ". No se pudo actualizar.");
      }

    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.err.println("Error al actualizar datos del libro: " + e.getMessage());
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }


  // APARTADO 6: MÉTODOS DE ELIMINACIÓN (DELETE)

  // Opción 9: Elimina un ejemplar por su código.
  
  public void eliminarEjemplar(String codigoEjemplar) {
    Session session = null;
    Transaction transaction = null;

    try {
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        // Buscar el ejemplar por su código único
        Query<Ejemplar> query = session.createQuery("FROM Ejemplar e WHERE e.codigoEjemplar = :codigo", Ejemplar.class);
        query.setParameter("codigo", codigoEjemplar);
        Ejemplar ejemplar = query.uniqueResult();

        if (ejemplar != null) {
            session.remove(ejemplar);
            transaction.commit();
            System.out.println("Ejemplar '" + codigoEjemplar + "' eliminado correctamente.");
        } else {
            transaction.rollback();
            System.out.println("No se encontró el ejemplar con código: " + codigoEjemplar);
        }

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        System.err.println("Error al eliminar ejemplar: " + e.getMessage());
    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
  }

  // Opción 10: Elimina un libro por su ID.
  
  public void eliminarLibro(Integer idLibro) {
    Session session = null;
    Transaction transaction = null;

    try {
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        // 1. Cargar el Libro por su ID (PK)
        Libro libro = session.get(Libro.class, idLibro);

        if (libro != null) {
            session.remove(libro); // Asume que la cascada elimina los ejemplares asociados
            transaction.commit();
            System.out.println("Libro con ID " + idLibro + " ('" + libro.getTitulo() + "') eliminado correctamente.");
            System.out.println("Ejemplares del libro tambien eliminados");
        } else {
            transaction.rollback();
            System.out.println("No se encontró el libro con ID: " + idLibro);
        }

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        System.err.println("Error al eliminar libro: " + e.getMessage());
    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
  }

  // Opción 11: Elimina un autor por su ID (con cascada).
  
  public void eliminarAutor(Integer idAutor) {
    Session session = null;
    Transaction transaction = null;

    try {
        session = HibernateUtil.getSessionFactory().openSession();
        transaction = session.beginTransaction();

        // 1. Cargar el Autor por su ID (PK)
        Autor autor = session.get(Autor.class, idAutor);

        if (autor != null) {
            session.remove(autor); // Asume CascadeType.ALL en Autor -> Libro -> Ejemplar
            transaction.commit();
            System.out.println("Autor con ID " + idAutor + " ('" + autor.getApellidos() + "') y todas sus relaciones eliminados correctamente.");
        } else {
            transaction.rollback();
            System.out.println("No se encontró el autor con ID: " + idAutor);
        }

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        System.err.println("Error al eliminar autor: " + e.getMessage());
    } finally {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
  }
}
