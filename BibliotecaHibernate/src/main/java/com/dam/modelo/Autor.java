package com.dam.modelo;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clave primaria autogenerada
    @Column(name = "id_autor")
    private Integer idAutor;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE) 
    private Date fechaNacimiento;

    // mappedBy: Indica que el campo 'autor' en la clase Libro es el dueño de la FK.
    // CascadeType.ALL: Propaga las operaciones.
    @OneToMany(
        mappedBy = "autor",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Libro> libros = new ArrayList<>();

    // Constructores
    public Autor() {
    }

    /**
     * CONSTRUCTOR DE CONVENIENCIA: Usado por Main.java para crear un nuevo Autor sin ID (que es autogenerado).
     */
    public Autor(String nombre, String apellidos, String nacionalidad, Date fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * CONSTRUCTOR COMPLETO: Incluye el ID (útil para consultas).
     */
    public Autor(Integer idAutor, String nombre, String apellidos, String nacionalidad, Date fechaNacimiento) {
        this.idAutor = idAutor;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    // MÉTODOS HELPER (Consistencia bidireccional)
    public void addLibro(Libro libro) {
        libros.add(libro);
        libro.setAutor(this);
    }

    public void removeLibro(Libro libro) {
        libros.remove(libro);
        libro.setAutor(null);
    }

    // Getter and Setter
    public Integer getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(Integer idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Date getFechaNacimiento() { 
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getter para acceder a la colección de libros del autor (lado 1:N de la relación).
    public List<Libro> getLibros() {
        return libros;
    }

    // Setter para reemplazar toda la colección de libros. 
    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    // ToString
    @Override
    public String toString() {
        return "Autor [ID=" + idAutor + 
                ", Nombre Completo=" + nombre + " " + apellidos + 
                ", Nacionalidad=" + nacionalidad + "]";
    }
}