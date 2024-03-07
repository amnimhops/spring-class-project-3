# Proyecto 3 : _Uso de servicios y repositorios_

## Briefing

Este proyecto modela un servicio web que expone las operaciones básicas
sobre una biblioteca de libros online. A nivel de software, consta de
un único controlador _LibraryController_ y solo un servicio _LibraryService_
que aglutina todas las operaciones del sistema. Dado que los datos de la
aplicación deben conservarse entre ejecuciones, se dispondrá un sistema
de persistencia basado en una base de datos MySQL y gestionada con JPA/
Hibernate. El acceso a datos desde las capas de servicio se hará mediante
el patrón _repositorio_, creando interfaces que extiendan de `JpaRepository<K,T>`
e indicando las operaciones adecuadas para llevar a cabo las consultas
necesarias.

## Objetivo

El objetivo de este proyecto es completar la API que que efectúa las operaciones
CRUD sobre una base de datos de una librería. Los _endpoints_ publicados son:

- **GET /library/books/{id}**
  - Obtiene información de un libro
- **POST /library/books**
  - Crea un nuevo libro
- **POST /library/books/search**
  - Efectúa una consulta en base a una serie de criterios
- **DELETE /library/books/{id}**
  - Borra un ejemplar de la librería
- **GET /library/authors/{id}**
  - Obtiene la información de un autor y todos los libros escritos

## Tareas

### 0. Configurar el proyecto

Antes de comenzar, habrá que configurar el archivo `application.properties`
para que pueda acceder a la base de datos local. Así pues, lo siguientes
valores habrán de cambiarse:
- `DATABASE_NAME` debe tener el nombre de una base de datos válida, vacía,
localizada en el servidor local.
- `DATABASE_USER` debe ser un usuario válido en el servidor local, con 
permisos de lectura, escritura y modificación sobre `DATABASE_NAME`
- `DATABASE_PASSWORD` debe ser una clave de acceso válida para conectar
con `DATABASE_USER`

### 1. Definir el modelo de datos

El modelo de datos deseado tiene las siguientes características:
- Materias:
  - Cada materia tiene un **identificador númerico único** (clave primaria)
  - Cada materia tiene un título
- Autores:
  - Cada autor tiene un **identificador númerico único** (clave primaria)
  - Cada autor tiene una biografía, de tipo texto
  - Cada autor tiene una lista de libros en los que figura como autor
- Libros:
  - Cada libro tiene **un título**
  - Cada libro tiene **un único autor**
  - Cada libro tiene **una lista de materias a las que pertenece**

Ademas, hay que tener en cuenta las relaciones entre las entidades:
- La relación entre libros y materias es de **muchos a muchos**
  - Un libro está asociado a muchas materias
  - Una materia está asociada a muchos libros
- La relación entre un libro y su autor es de **muchos a uno**
  - Un libro está asociado a un único autor
- La relación entre un autor y sus libros es de **uno a muchos**
  - Un autor está asociado a muchos libros

Como resultado de esta tarea, se habrán generado las clases que modelan
las entidades de la base de datos, y que permitirán acceder al sistema
de persistencia. 

**Si el modelo se ha generado correctamente y se
ha configurado bien el proyecto, las tablas se crearán en el momento
del próximo arranque**.

### 2. Creación de los repositorios

Para poder manipular el contenido de la base de datos será necesario
crear los repositorios que se encargan de gestionar cada tipo de entidad:
- _SubjectRepository_, a cargo de la gestión de las materias
- _BookRepository_, a cargo de la gestión de los libros
- _AuthorRepository_, a cargo de la gestión de los autores

Cada repositorio será una interfaz que extenderá de `JpaInterface<K,I>`, 
donde `K` será la clase de la entidad que se gestiona e `I` el tipo de dato
de su identificador o clave primaria. Por ejemplo, un repositorio que 
gestionara vehículos cuyo identificador es la matrícula, que es de tipo textual, 
sería:
```java
public interface VehicleRepository extends JpaRepository<Vehicle,String>{
    ...
}
```
Los repositorios recién creados tendrán solo los métodos heredados de `JpaRepository`,
por lo que es posible que adelante haya que incorporar nuevas funcionalidades.

> **Nota** La ubicación de los repositorios será el paquete `com.springclass.library.repositories`

### 3. Creación del servicio

Una vez generados los repositorios, habrá que crear un servicio que haga
uso de ellos, de manera que al inyectarlo en el controlador la API quede
completa. 

El servicio deberá crearse como una clase Java que implemente la interfaz
`LibraryService`, y cuyo comportamiento habrá de adecuarse a la documentación
que acompaña a los métodos de dicha interfaz.

> **Nota** La ubicación del servicio será el paquete `com.springclass.library.services.impl`

Una vez se haya creado el servicio e implementado los métodos, habrá que 
agregar la anotación `@Service` a la definición de la clase para informar
a _Spring_ que debe inyectarlo como implementación de la interfaz `LibraryService`.

### 4. Lógica del servicio

La lógica de los métodos de servicio es bastante simple. Aunque el _javadoc_ describe
cómo deben comportarse, a continuación se ofrece una breve reseña de cada uno:

- BookDTO loadBook(Integer id)
  - Debe lanzar una excepción del tipo adecuado si el argumento no es válido
  - Debe lanzar una excepción del tipo adecuado si no existe un libro con el identificador indicado
  - Debe convertir correctamente la entidad encontrada al DTO requerido
- public boolean deleteBook(Integer id)
  - Debe lanzar una excepción del tipo adecuado si el argumento no es válido
  - Debe lanzar una excepción del tipo adecuado si no existe un libro con el identificador indicado
  - Debe eliminar el libro indicado de la base de datos
- public List<BookDTO> findBooks(BookSearch search)
  - En caso de no encontrar ningún libro, devolverá una lista vacía
  - Los libros encontrados deberán cumplir **todos los criterios indicados**
- public BookDTO createBook(CreateBookDTO input)
  - Si el autor indicado en el argumento no existe, habrá que crearlo.
  - El autor del libro deberá quedar vinculado al libro
  - Si la materia no existe, habrá que crearla primero.
  - La materia quedará vinculada al libro.
- AuthorDTO loadAuthor(Integer id)
  - Debe lanzar una excepción del tipo adecuado si el argumento no es válido
  - Debe lanzar una excepción del tipo adecuado si no existe un autor con el identificador indicado
  - Debe convertir correctamente la entidad encontrada al DTO requerido
  
## Bibliografía
- [A simple Spring Boot application that uses MySQL and Rest endpoints](https://medium.com/@KkambizZ/a-simple-spring-boot-application-that-uses-mysql-and-rest-endpoints-a71e06400eab)
- [Introduction to Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)
- [Guide to Spring @Autowired](https://www.baeldung.com/spring-autowire)
- [Understanding JPA/Hibernate](https://www.baeldung.com/jpa-hibernate-associations)
- [Many to many relationships in JPA](https://www.baeldung.com/jpa-many-to-many)