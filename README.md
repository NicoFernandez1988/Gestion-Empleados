# Sistema de Gestión de Empleados

Este proyecto es una aplicación backend para gestionar la información de empleados, utilizando Java 21 por ser la ultima version LTS y SpringBoot 3. La aplicación proporciona una API REST para realizar operaciones CRUD en la base de datos y una interfaz para le gestion de los empleados. Creada con HTML, CSS y Javascript.

## Descripción

El sistema permite gestionar empleados, incluyendo la creación, lectura, actualización y eliminación de registros. Utiliza Spring Boot para el desarrollo del backend, Spring Data JPA para la capa de acceso a datos, y H2 para la gestión de la base de datos de forma local.

## Tabla de Contenidos

- [Instalación](#instalación)
- [Uso](#uso)
- [Documentación de la API](#documentación-de-la-api)
- [Pruebas](#pruebas)


## Instalación

### Requisitos Previos

- [Java 21](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/)

### Pasos de Instalación

1. Descomprimir el archivo rar:
    ```sh
    Una vez descargado el archivo rar, va a figurar con el nombre de Empleados.rar
    ```
2. Navegar al directorio del proyecto:
    ```sh
    cd Empleados
    ```
3. Construir y ejecutar la aplicación con los siguientes comandos (actualmente en esta version todo se migra y carga a una base de datos en memoria de H2 integrado, no son necesarios pasos adicionales.
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```

### Uso
Una vez corriendo el back, la aplicación estará disponible en http://localhost:8080.

Endpoints Principales:
  ``` 
  - Crear empleado: POST /api/employees
  - Obtener todos los empleados: GET /api/employees
  - Obtener empleado por ID: GET /api/employees/{id} 
  - Actualizar empleado: PUT /api/employees/{id}
  - Eliminar candidato: DELETE /api/employees/{id}
  ```

### Documentación de la API
La documentación completa de la API está disponible en Swagger. Se puede acceder a http://localhost:8080/swagger-ui.html para ver y probar los endpoints.


### Pruebas
Para ejecutar las pruebas unitarias e integración, utiliza el siguiente comando:

  ``` 
mvn test
  ``` 
