package com.dam.modelo;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria autogenerada
    @Column(name = "id_libro")
    private Integer idLibro;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    @Column(name = "fecha_publicacion")
    @Temporal(TemporalType.DATE)
    private Date fechaPublicacion;

    @Column(name = "numero_paginas")
    private Integer numeroPaginas;

    // RELACIÓN (N:1 con Autor)
    // ManyToOne es el dueño de la relación (contiene la FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor", nullable = false) // Columna FK en la tabla 'libros'
    private Autor autor;

    // RELACIÓN (1:N con Ejemplar)
    // Configura la cascada para que al eliminar el Libro, se eliminen sus Ejemplares
    @OneToMany(
        mappedBy = "libro",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Ejemplar> ejemplares = new ArrayList<>();

    // CONSTRUCTORES
    public Libro() {
    }

    // CONSTRUCTOR DE CONVENIENCIA (Usado en Main.java para crear nuevos libros antes de asignarles el ID y el Autor)
    public Libro(String titulo, String isbn, Date fechaPublicacion, Integer numeroPaginas) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.fechaPublicacion = fechaPublicacion;
        this.numeroPaginas = numeroPaginas;
    }

    public Libro(Integer idLibro, String titulo, String isbn, Date fechaPublicacion, Integer numeroPaginas, Autor autor) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.isbn = isbn;
        this.fechaPublicacion = fechaPublicacion;
        this.numeroPaginas = numeroPaginas;
        this.autor = autor;
    }

    // MÉTODOS HELPER (Consistencia bidireccional Ejemplar)
    public void addEjemplar(Ejemplar ejemplar) {
        ejemplares.add(ejemplar);
        ejemplar.setLibro(this);
    }

    public void removeEjemplar(Ejemplar ejemplar) {
        ejemplares.remove(ejemplar);
        ejemplar.setLibro(null);
    }

    // Getters and Setters
    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Integer getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(Integer numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public List<Ejemplar> getEjemplares() {
        return ejemplares;
    }

    public void setEjemplares(List<Ejemplar> ejemplares) {
        this.ejemplares = ejemplares;
    }

    // Getter para obtener el Autor asociado a este libro. Es el lado N de la relación N:1.
    public Autor getAutor() {
        return autor;
    }

    // Setter para establecer el Autor. Utilizado por los métodos helper del Autor 
    public void setAutor(Autor autor) {
        this.autor = autor;
    }

}