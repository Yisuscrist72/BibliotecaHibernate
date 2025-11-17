package com.dam.modelo;

import javax.persistence.*;

@Entity
@Table(name = "ejemplares")
public class Ejemplar {

  // ENUM ANIDADO (EstadoEjemplar)
  /**
   * Define los posibles estados de un ejemplar físico (DISPONIBLE, PRESTADO,
   * etc.). Al estar anidado, se referencia como Ejemplar.EstadoEjemplar en
   * otras clases.
   */
  public enum EstadoEjemplar {
    DISPONIBLE,
    PRESTADO,
    REPARACION,
    BAJA
  }

  // ATRIBUTOS DE LA ENTIDAD
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_ejemplar")
  private Integer idEjemplar;

  @Column(name = "codigo_ejemplar", unique = true, nullable = false)
  private String codigoEjemplar;

  // Mapeo del Enum como String en la base de datos
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private EstadoEjemplar estado;

  @Column(name = "ubicacion")
  private String ubicacion;

  // RELACIÓN (N:1 con Libro)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_libro", nullable = false)
  private Libro libro;

  // CONSTRUCTORES
  public Ejemplar() {
  }

  public Ejemplar(String codigoEjemplar, EstadoEjemplar estado, String ubicacion) {
    this.codigoEjemplar = codigoEjemplar;
    this.estado = estado;
    this.ubicacion = ubicacion;
  }

  // GETTERS Y SETTERS
  public Integer getIdEjemplar() {
    return idEjemplar;
  }

  public void setIdEjemplar(Integer idEjemplar) {
    this.idEjemplar = idEjemplar;
  }

  public String getCodigoEjemplar() {
    return codigoEjemplar;
  }

  public void setCodigoEjemplar(String codigoEjemplar) {
    this.codigoEjemplar = codigoEjemplar;
  }

  public EstadoEjemplar getEstado() {
    return estado;
  }

  public void setEstado(EstadoEjemplar estado) {
    this.estado = estado;
  }

  public String getUbicacion() {
    return ubicacion;
  }

  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }

  public Libro getLibro() {
    return libro;
  }

  // Setter utilizado por los métodos helper de Libro para establecer la bidireccionalidad
  public void setLibro(Libro libro) {
    this.libro = libro;
  }

  @Override
  public String toString() {
    // Usa el operador ternario para acceder de forma segura al título del libro
    // y evitar una NullPointerException si la relación N:1 ('libro') es nula o no está cargada.  
    String tituloLibro = (libro != null) ? libro.getTitulo() : "N/A";
    return "Ejemplar [Código=" + codigoEjemplar
      + ", Estado=" + estado
      + ", Ubicación=" + ubicacion
      + ", Libro=" + tituloLibro + "]";
  }
}
