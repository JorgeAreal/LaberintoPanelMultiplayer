/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class ConexionBaseDatos {

    // Como atributo de clase guardo en variables los datos necesarios para la conexion a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";
    private static final String NOMBRE_BASE_DE_DATOS = "monedasjorge";
    private static final String NOMBRE_TABLA = "monedasfiesta";

    // Crea todo el entorno de la base de datos, tabla, e inserta el campo inicial donde trabajar
    // Se hace público y estático para ser facilmente llamado por las demás clases segun haga falta
    public static void crearBaseYTabla() {
        Connection conexion = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Conectar a MySQL
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            statement = conexion.createStatement();

            // Crear la base de datos si no existe
            String sqlCrearBaseDatos = "CREATE DATABASE IF NOT EXISTS " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlCrearBaseDatos);
            System.out.println("Base de datos creada correctamente.");

            // Seleccionar la base de datos recién creada
            String sqlUsarBaseDatos = "USE " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlUsarBaseDatos);

            // Crear la tabla si no existe
            String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS " + NOMBRE_TABLA + " (id INT, monedas INT)";
            statement.executeUpdate(sqlCrearTabla);
            System.out.println("Tabla creada correctamente.");

            // Verificar si ya existe un registro con id = 1
            String sqlContarRegistros = "SELECT COUNT(*) AS count FROM " + NOMBRE_TABLA + " WHERE id = 1";
            resultSet = statement.executeQuery(sqlContarRegistros);
            resultSet.next();
            int count = resultSet.getInt("count");

            if (count == 0) {
                // Insertar un dato en la tabla solo si no existe un registro con id = 1
                String sqlInsertarDato = "INSERT INTO " + NOMBRE_TABLA + " VALUES (1, 0)";
                statement.executeUpdate(sqlInsertarDato);
                System.out.println("Dato insertado correctamente.");
            } else {
                System.out.println("Ya existe un registro con id = 1, no es necesario insertar.");
            }
        } catch (SQLException e) {
            System.out.println("Error con la conexion a base de datos");
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Se encarga de realizar operaciones entre las monedas guardadas en base de datos, y en las monedas locales
    // Puede sumar o restar, dependiendo de lo que se le ponga como parámetro
    public static void actualizarMonedas(int cantidadAOperar, String operacion) {
        // Obtener las monedas actuales
        int monedasActuales = obtenerMonedas();

        // Determinar la operación a realizar
        int nuevaCantidad;

        // Si el parámetro "operacion" es igual a "sumar", suma las dos cantidades
        // Es es restar, resta las cantidades
        if (operacion.equals("sumar")) {
            nuevaCantidad = monedasActuales + cantidadAOperar;
        } else if (operacion.equals("restar")) {
            nuevaCantidad = monedasActuales - cantidadAOperar;
            // Verificar que la cantidad no sea negativa
            if (nuevaCantidad < 0) {
                System.out.println("No hay suficientes monedas :(");
            }
        } else {
            System.out.println("Operación no válida");
            return;
        }

        // Actualizar las monedas en la base de datos
        try (Connection conexion = DriverManager.getConnection(URL + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA)) {
            String sql = "UPDATE " + NOMBRE_TABLA + " SET monedas = ? WHERE id = 1";
            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setInt(1, nuevaCantidad);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar las monedas: " + e.getMessage());
        }
    }

    // Se conecta a la base de datos y extrae el valor del numero de mones que hay registrada en la base de datos en el momento
    public static int obtenerMonedas() {
        // Declaración de variables
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int monedas = 0;

        try {
            // Establecer conexión con la base de datos
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA);

            // Crear una declaración SQL
            statement = connection.createStatement();

            // Ejecutar la consulta para obtener el número de monedas
            String sqlObtenerMonedas = "SELECT monedas FROM " + NOMBRE_TABLA + " WHERE id = 1";
            resultSet = statement.executeQuery(sqlObtenerMonedas);

            // Si se encuentra el registro con id = 1, obtener el valor de monedas
            if (resultSet.next()) {
                monedas = resultSet.getInt("monedas");
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo la cantidad de monedas :(");
            return 0;
        } finally {
            // Cerrar recursos
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Devolver el número de monedas obtenido
        return monedas;
    }

    //          ZONA MULTIJUGADOR
    // Método que crea la tabla donde se van a guardar las coordenadas de los jugadores
    public static void crearTablaMultijugador() {
        Connection conexion = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Conectar a MySQL
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            statement = conexion.createStatement();

            // Seleccionar la base de datos recién creada
            String sqlUsarBaseDatos = "USE " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlUsarBaseDatos);

            // Crear la tabla si no existe
            String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS multijugador ("
                    + " id INT,"
                    + " posX INT,"
                    + " posY INT,"
                    + " puntuacion INT,"
                    + " rotacion VARCHAR(255),"
                    + " skin VARCHAR(255),"
                    + " conectado VARCHAR(255)"
                    + ");";
            statement.executeUpdate(sqlCrearTabla);

            System.out.println("Tabla creada correctamente.");

            // Insertar datos en la tabla por defecto
            String sqlInsertarDato = "INSERT INTO multijugador VALUES (1, 0, 0, 0, '_derecha', '" + LaberintoPanel.obtenerSkin() + "', 'si')";
            statement.executeUpdate(sqlInsertarDato);
            String sqlInsertarDato2 = "INSERT INTO multijugador VALUES (2, 0, 0, 0, '_derecha', 'recursos/textures/skins/pacman', 'no')";
            statement.executeUpdate(sqlInsertarDato2);
            System.out.println("Dato insertado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error con la conexión a la base de datos. Crear");
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Elimina la tabla "multijugador" creada anteriormente. Se utiliza cuando el host se va de la partida
    public static void eliminarTablaMultijugador() {
        Connection conexion = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Conectar a MySQL
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            statement = conexion.createStatement();

            // Seleccionar la base de datos recién creada
            String sqlUsarBaseDatos = "USE " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlUsarBaseDatos);

            // Eliminar la tabla si no existe
            String sqlEliminarTabla = "DROP TABLE multijugador;";
            statement.executeUpdate(sqlEliminarTabla);

            System.out.println("Tabla eliminada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error con la conexión a la base de datos. Borrar");
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para leer los datos de posición del jugador desde la base de datos
    // Recibe como parámetro el numero del jugador
    public static int[] leerPosicionJugadorDesdeBD(int Jugador) {
        int[] posicion = new int[2]; // Array para almacenar las coordenadas posX y posY de J1

        Connection conexion = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establecer conexión a la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            statement = conexion.createStatement();

            // Seleccionar la base de datos recién creada
            String sqlUsarBaseDatos = "USE " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlUsarBaseDatos);

            // Realizar consulta SQL para obtener las coordenadas de posición de J1
            String sql = "SELECT posX, posY FROM multijugador WHERE id = " + Jugador + ""; // Jugador es el ID que se va  modificar
            resultSet = statement.executeQuery(sql);

            // Obtener los resultados de la consulta
            if (resultSet.next()) {
                posicion[0] = resultSet.getInt("posX");
                posicion[1] = resultSet.getInt("posY");
            }
        } catch (SQLException e) {
            System.out.println("Error con la conexión a la base de datos. Lectura");
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return posicion;
    }

    public static String leerCadenaTextoDesdeBD(String columna, int Jugador) {
        String texto = "";

        Connection conexion = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establecer conexión a la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            statement = conexion.createStatement();

            // Seleccionar la base de datos recién creada
            String sqlUsarBaseDatos = "USE " + NOMBRE_BASE_DE_DATOS;
            statement.executeUpdate(sqlUsarBaseDatos);

            // Realizar consulta SQL para obtener las coordenadas de posición de J1
            String sql = "SELECT " + columna + " FROM multijugador WHERE id = " + Jugador + ""; // Jugador es el ID que se va  modificar
            resultSet = statement.executeQuery(sql);

            // Obtener los resultados de la consulta
            if (resultSet.next()) {
                texto = resultSet.getString(columna);
            }
        } catch (SQLException e) {
            System.out.println("Error con la conexión a la base de datos. Lectura");
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return texto;
    }

    // Método para actualizar la posición de J1 en la base de datos
    public static void actualizarPosicionJugadorDesdeBD(int posX, int posY, String direccionPersonaje, int Jugador) {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            // Conectar a la base de datos
            conexion = DriverManager.getConnection(URL + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA);

            // Sentencia SQL para actualizar la posición de J1
            String sqlActualizar = "UPDATE multijugador SET posX = ?, posY = ?, rotacion = ? WHERE id = ?";
            statement = conexion.prepareStatement(sqlActualizar);
            statement.setInt(1, posX); // Posición X
            statement.setInt(2, posY); // Posición Y
            statement.setString(3, direccionPersonaje); // Rotacion
            statement.setInt(4, Jugador);

            // Ejecutar la actualización
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión y el statement
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Método para actualizar el estado "conectado" en la tabla "multijugador"
    // Esta verificacion sirve para gestionar la entrada de usuarios duplicados, haciendo que estos no puedan entrar si el servidor ya tiene dos personas dentro simultaneamente
    public static void actualizarEstadoConexionJugadoresBD(String conectado, int Jugador) {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            // Conectar a la base de datos
            conexion = DriverManager.getConnection(URL + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA);

            // Sentencia SQL para actualizar la posición de J1
            String sqlActualizar = "UPDATE multijugador SET conectado = ? WHERE id = ?";
            statement = conexion.prepareStatement(sqlActualizar);
            statement.setString(1, conectado); // Posición X
            statement.setInt(2, Jugador);

            // Ejecutar la actualización
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión y el statement
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Se encarga de testear la conexion a base de datos intentando acceder a ella
    // Devuelve un valor boolean true = si se ha conectado correctamente y false = si no se ha podido conectar
    public static boolean testearConexionBBDD() {
        try {
            // Cargar el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión con la base de datos
            Connection conexion = DriverManager.getConnection(URL + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA);
            // Si se llega a este punto, la conexión fue exitosa
            conexion.close();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar con base de datos");
            return false;
        }
    }

    // Testear la conexion de partida multijugador
    public static boolean testearConexionMultijugador() {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean conexionExitosa = false;

        try {
            // Conectar a la base de datos
            conexion = DriverManager.getConnection(URL + NOMBRE_BASE_DE_DATOS, USUARIO, CONTRASENA);

            // Verificar si la tabla "multijugador" existe
            String sqlVerificarTabla = "SELECT * FROM multijugador LIMIT 1";
            statement = conexion.prepareStatement(sqlVerificarTabla);
            resultSet = statement.executeQuery();

            // Si la consulta se ejecuta sin errores, la conexión es exitosa
            conexionExitosa = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión, el statement y el resultSet
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return conexionExitosa;
    }

    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
