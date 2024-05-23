/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Jorge
 */
public class TiendaFrame extends JFrame {

    // JLayeredPane para superponer componentes
    private JLayeredPane layeredPane = new JLayeredPane();

    // Crear un panel principal con BorderLayout
    private Container panelPrincipal = new JPanel(new BorderLayout());

    // Inicializar la variable donde se cargaran el numero de llaves a partir del txt llavesJugador.txt
    private int puntuacion;

    // Inicializar la variable donde se almacenará la cadena de texto correspondiente al nombre de la foto que se necesite cargar
    // Es utilizado en el metodo creaProductos()
    private String indicarFotoProducto;

    // En este array se almacenará la matriz correspondiente a personajesComprados.txt, el cual registra qué ha comprado y qué no ha comprado el usuario
    // Dependiendo de si el usuario ha comprado o no ciertos productos, se mostraran imagenes distintas
    // Esta variable trabaja junto a la variable indicarFotoProducto para mostrar las imagenes de los productos pertinentes en el menu de tienda
    private int[][] leerCompras = new int[3][3];

    /* En este array se almacenarán los precios para cada uno de los productos. Trabaja similar que leerCompras, pero con la diferencia de que esta
    se lee para los precios de cada producto, y los datos se insertan directamente en codigo en lugar de un txt externo. */
    private final int[][] precios = {
        {0, 10, 50},
        {0, 1000, 0},
        {0, 0, 10000}
    };

    // Muy parecido a precios[][], pero que guarda el nombre de cada producto
    // Se utiliza en equiparSkin() para modificar la direccion de la imagen de la skin in-game actual
    private final String[][] nombresProductos = {
        {"pacman", "pacman_azul", "rana_verde"},
        {"basico/", "fiesta/", ""},
        {"", "", "og_skin"}
    };

    // Declarar variable string en donde se guardará la direccion en las carpetas del personaje elegido
    private String direccionSkinElegido;

    // Constructor
    public TiendaFrame() {
        super("Tienda"); // Título del JFrame
        getNumeroLlaves(); // Lee el archivo txt correspondiente al numero de llaves. Se usa para hacer la logica de la compra
        creaVentana(); // Crea el JFrame y se configura el fondo de pantalla, donde mas adelante se superpondrán los productos
        creaCerrarYLlaves(); // Muestra en pantalla el icono de cerrar(x) y el contador de llaves
        creaProductos(); // Utiliza el laberProductos[][] para poner cada producto en su sitio
    }

    // Crea el JFrame y se configura el fondo de pantalla, donde mas adelante se superpondrán los productos. Este metodo es llamado por el constructor
    private void creaVentana() {

        // Tamaño del JFrame con barra de navegacion (en desuso)
        //this.setSize(565, 490);
        // Tamaño del JFrame sin barra de navegación
        this.setSize(549, 454);

        // Inicializar variable que contiene el icono de la tienda y aplicarlo al nuevo frame de la tienda
        ImageIcon iconoTiendaFrame = new ImageIcon("recursos\\tiendaIcon.jpg");
        this.setIconImage(iconoTiendaFrame.getImage()); // Colocar logo del laberinto utilizando la variable icono creada

        // Llamar al metodo que se encarga exclusivamente de poner el fondo de pantalla
        ponerFondo();

        // Agregar el panel principal en la capa predeterminada
        layeredPane.add(panelPrincipal, JLayeredPane.DEFAULT_LAYER);

        // Agregar el JLayeredPane al JFrame
        this.add(layeredPane);

        // Hacer que el JFrame no tenga barra de navegación predeterminada
        setUndecorated(true);

        // Hacer que el JFrame no sea redimensionable
        setResizable(false);

        // Centrar el JFrame en la pantalla
        setLocationRelativeTo(null);

    }

    // Se encarga de poner el fondo de pantalla en el panel principal de la tienda. Este método es llamado por creaVentana()
    private void ponerFondo() {
        try {
            // Cargar imagen de fondo
            BufferedImage image = ImageIO.read(new File("recursos/textures/tienda/fondo_tienda2.jpg"));
            JLabel background = new JLabel(new ImageIcon(image));

            // Meter propiedades a la imagen de fondo
            background.setBounds(0, 0, background.getPreferredSize().width, background.getPreferredSize().height);

            // Agregar la imagen de fondo en la capa predeterminada
            layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se ha podido cargar el fondo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Recorre la matriz de personajesComprados.txt para obtener informacion de qué personajes estan comprados y cuales no.
    // Dentro de la matriz: 1 = comprado, 0 = sin comprar, 2 = comprado y equipado
    // Se utiliza en creaProductos() para mostrar la imagen correcta en cada producto
    private void obtenerCompras() {

        try (BufferedReader reader = new BufferedReader(new FileReader("recursos/datosJugador/personajesComprados.txt"))) {

            // Leemos la matriz
            for (int i = 0; i < leerCompras.length; i++) {
                String[] valores = reader.readLine().trim().split("\\s+");
                for (int j = 0; j < leerCompras[i].length; j++) {
                    leerCompras[i][j] = Integer.parseInt(valores[j]);
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer las compras obtenidas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Muestra los productos a comprar en la tienda y le agrega a cada uno un mouse adapter. Este método es llamado por el constructor
    private void creaProductos() {

        // Inicializar matriz bidimensional de JLabels. En este caso, la tienda va a tener 3 filas y 3 columnas de productos y cada producto se guardará en una parte del array
        JLabel[][] labelsProductos = new JLabel[3][3];

        //Inicializar variables necesarias para la generacion de JLabels en el bucle for
        int numeroColumnaInicial = 80;
        int alturaFilaInicial = 60;
        int anchoPanel = 100;
        int altoPanel = 100;
        int espacioEntreColumnas = 145;
        int espacioEntreFilas = 139;

        // Llamar al metodo para saber qué personajes estan comprados
        obtenerCompras();

        // Llenar matriz con nuevos labels y configurar cada uno con una imagen
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                labelsProductos[i][j] = new JLabel();

                // Recorriendo el array de los personajes comprados, en caso de 1 el producto esta comprado, y en 0 no esta comprado
                // Lo que hace exactametne es modificar el string de la direccion de la imagen que se debe mostrar
                switch (leerCompras[i][j]) {
                    case 0:
                        indicarFotoProducto = "_comprar"; // Si no esta comprado, mostrar la imagen donde aparece el precio
                        // Refresco el estado de boolean a false porque el numero 1 no se trata de un grafico, sino de una skin
                        break;
                    case 1:
                        indicarFotoProducto = "_equipar"; // Si esta comprado, mostrar la imagen de "equipar"
                        break;
                    case 2:
                        indicarFotoProducto = "_equipado"; // Si esta comprado y ademas equipado, mostrar la imagen de "equipado"
                        break;

                    // A continuacion, los siguientes case estan reservados solamente para las compras de graficos
                    // De esta forma, la compra de graficos no interfiere con el entorno de compra de skins
                    case 3:
                        indicarFotoProducto = "_comprar"; // Si un pack de graficos está sin comprar
                        break;
                    case 4:
                        indicarFotoProducto = "_equipar";
                        break;
                    case 5:
                        indicarFotoProducto = "_equipado"; // Si un pack de graficos está comprado y ademas equipado, mostrar la imagen de "equipado"
                        break;
                    default:
                        break;
                }

                // Construir la ruta de la imagen del producto
                String rutaImagen = "recursos/textures/tienda/productosTienda/producto" + i + j + indicarFotoProducto + ".png";
                File archivoImagen = new File(rutaImagen);

                // Verificar si la imagen existe
                if (archivoImagen.exists()) {
                    // Cargar la imagen desde un archivo y guardarla en la variable "icono"
                    ImageIcon icono = new ImageIcon(rutaImagen);

                    // Ajustar el tamaño de la imagen al tamaño del JLabel
                    Image imagenEscalada = icono.getImage().getScaledInstance(anchoPanel, altoPanel, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(imagenEscalada);

                    // Establecer la imagen ya escalada dentro del JLabel
                    labelsProductos[i][j].setIcon(scaledIcon);

                    // Utilizamos variables finales para poder mandarlas como parametro a los metodos necesarios
                    final int finalI = i;
                    final int finalJ = j;

                    // Agregar un MouseAdapter a cada label
                    labelsProductos[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // Determinar si el usuario tiene llaves suficientes para la transaccion y el objeto todavia no esta comprado:
                            if (puntuacion >= precios[finalI][finalJ] && leerCompras[finalI][finalJ] == 0) {
                                // Si tiene llaves suficientes y el objeto no esta comprado, llamar al metodo que muestra la pantalla de confirmacion de compra
                                mostrarPantallaCompra(finalI, finalJ);

                                // Si no tiene llaves suficientes y el objeto no esta comprado, llamar al metodo que muestra la pantalla de error de compra
                            } else if (puntuacion <= precios[finalI][finalJ] && leerCompras[finalI][finalJ] == 0) {
                                // Si no tiene, llamar al metodo que muestra la pantalla de error de llaves insufucientes
                                pantallaErrorCompra("compra");

                                // Si el objeto esta comprado pero no equipado
                            } else if (leerCompras[finalI][finalJ] == 1) {
                                // Llamar al metodo que se encarga de equipar la skin correctamente para poder usarla en el juego
                                equiparSkin(finalI, finalJ);
                            } else if (leerCompras[finalI][finalJ] == 3) {
                                mostrarPantallaCompra(finalI, finalJ);
                            } else if (leerCompras[finalI][finalJ] == 4) {
                                equiparGraficos(finalI, finalJ);
                            }
                        }
                    });
                }

                // Establecer el tamaño y posición del JLabel utilizando las variables inicializadas anteriormente
                int x = numeroColumnaInicial + (j * espacioEntreColumnas);
                int y = alturaFilaInicial + (i * espacioEntreFilas);
                labelsProductos[i][j].setBounds(x, y, anchoPanel, altoPanel);

                // Añadir el JLabel al layeredPane
                layeredPane.add(labelsProductos[i][j], JLayeredPane.PALETTE_LAYER);
            }
        }
    }

    // Se encarga de actualizar la skin en el juego y de actualizar el frame de la tienda segun qué skin está seleccionada
    private void equiparSkin(int i, int j) {
        // Cambiar dirección de la skin que se va a utilizar durante el juego
        direccionSkinElegido = "recursos/textures/skins/" + nombresProductos[i][j];

        // Buscar y cambiar el valor de 2 a 1 en la matriz, para deseleccionar un producto
        for (int fila = 0; fila < leerCompras.length; fila++) {
            for (int columna = 0; columna < leerCompras[fila].length; columna++) {
                if (leerCompras[fila][columna] == 2) {
                    leerCompras[fila][columna] = 1;
                    break; // Terminar la búsqueda una vez encontrado el valor
                }
            }
        }

        // Ahora, mostramos la imagen de "equipado" al nuevo producto
        leerCompras[i][j] = 2;

        // Guardar la matriz actualizada en el archivo
        try (FileWriter writer = new FileWriter("recursos/datosJugador/personajesComprados.txt")) {
            for (int fila = 0; fila < leerCompras.length; fila++) {
                for (int columna = 0; columna < leerCompras[fila].length; columna++) {
                    writer.write(String.valueOf(leerCompras[fila][columna]));
                    if (columna < leerCompras[fila].length - 1) {
                        writer.write(" ");
                    }
                }
                // Agregar un salto de linea al final de la matriz
                if (fila < leerCompras.length - 1) {
                    writer.write("\n");
                }
            }
            // Agregar la dirección de la skin elegida al final del archivo
            guardarEnArchivo("recursos/DatosJugador/skinActual.txt", direccionSkinElegido);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al equipar la skin.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Eliminar todos los componentes del layeredPane antes de agregar los nuevos
        layeredPane.removeAll();
        
        // Reproducir el efecto de sonido epico
        ReproductorSonidos.reproducirEfectoSonido("boton2", 0.4f);

        // Reiniciar gráficos
        ponerFondo();
        creaCerrarYLlaves();
        creaProductos(); // Llamar a creaProductos() para refrescar las imágenes de la tienda
        layeredPane.setVisible(true);
    }

    // Se encarga de actualizar la skin en el juego y de actualizar el frame de la tienda segun qué skin está seleccionada
    private void equiparGraficos(int i, int j) {
        // Cambiar dirección de la skin que se va a utilizar durante el juego
        String direccionGraficosElegidos = "recursos/textures/graficos/" + nombresProductos[i][j];

        // Buscar y cambiar el valor de 2 a 1 en la matriz, para deseleccionar un producto
        for (int fila = 0; fila < leerCompras.length; fila++) {
            for (int columna = 0; columna < leerCompras[fila].length; columna++) {
                if (leerCompras[fila][columna] == 5) {
                    leerCompras[fila][columna] = 4;
                    break; // Terminar la búsqueda una vez encontrado el valor
                }
            }
        }

        // Ahora, mostramos la imagen de "equipado" al nuevo producto
        leerCompras[i][j] = 5;

        // Guardar la matriz actualizada en el archivo
        try (FileWriter writer = new FileWriter("recursos/datosJugador/personajesComprados.txt")) {
            for (int fila = 0; fila < leerCompras.length; fila++) {
                for (int columna = 0; columna < leerCompras[fila].length; columna++) {
                    writer.write(String.valueOf(leerCompras[fila][columna]));
                    if (columna < leerCompras[fila].length - 1) {
                        writer.write(" ");
                    }
                }
                // Agregar un salto de linea al final de la matriz
                if (fila < leerCompras.length - 1) {
                    writer.write("\n");
                }
            }
            // Agregar la dirección de la skin elegida al final del archivo
            guardarEnArchivo("recursos/DatosJugador/graficosActuales.txt", direccionGraficosElegidos);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al equipar los graficos.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Eliminar todos los componentes del layeredPane antes de agregar los nuevos
        layeredPane.removeAll();

        // Reproducir el efecto de sonido epico
        ReproductorSonidos.reproducirEfectoSonido("boton2", 0.4f);

        // Reiniciar gráficos
        ponerFondo();
        creaCerrarYLlaves();
        creaProductos(); // Llamar a creaProductos() para refrescar las imágenes de la tienda
        layeredPane.setVisible(true);
    }

    // Hace la transaccion de la compra, restando las llaves pertinentes y registrando el producto como comprado
    private void efectuarCompraSkin(int i, int j) {
        leerCompras[i][j] = 1; // Guardar en archivo txt que el producto está comprado

        try (FileWriter writer = new FileWriter("recursos/datosJugador/personajesComprados.txt")) {
            // Concatenar todos los valores de la matriz en una cadena
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < leerCompras.length; row++) {
                for (int col = 0; col < leerCompras[row].length; col++) {
                    sb.append(leerCompras[row][col]);
                    // Añadir un espacio después de cada valor, excepto el último
                    if (col < leerCompras[row].length - 1) {
                        sb.append(" ");
                    }
                }
                // Añadir un salto de línea después de cada fila, excepto la última
                if (row < leerCompras.length - 1) {
                    sb.append("\n");
                }
            }

            // Escribir la cadena en el archivo
            writer.write(sb.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la compra.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Aplico el precio de producto y lo registro en el txt de numero de llaves
        puntuacion -= precios[i][j];
        //guardarNumeroLlaves();
        guardarEnArchivo("recursos/datosJugador/llavesJugador.txt", puntuacion + "");

        // Eliminar todos los componentes del layeredPane antes de agregar los nuevos
        layeredPane.removeAll();

        // Reproducir efecto de sonido de compra
        ReproductorSonidos.reproducirEfectoSonido("comprar2", 0.5f);

        // Reiniciar graficos
        ponerFondo();
        creaCerrarYLlaves();
        creaProductos(); // Llamar a creaProductos() para refrescar las imagenes de la tienda
        layeredPane.setVisible(true);
        repaint();
    }

    // Hace la transaccion de la compra, restando las monedas pertinentes y registrando el producto como comprado
    private void efectuarCompraGrafico(int i, int j) {

        if (ConexionBaseDatos.testearConexionBBDD() && ConexionBaseDatos.obtenerMonedas() >= precios[i][j]) {

            leerCompras[i][j] = 4; // Guardar en archivo txt que el gráfico está comprado

            try (FileWriter writer = new FileWriter("recursos/datosJugador/personajesComprados.txt")) {
                // Concatenar todos los valores de la matriz en una cadena
                StringBuilder sb = new StringBuilder();
                for (int row = 0; row < leerCompras.length; row++) {
                    for (int col = 0; col < leerCompras[row].length; col++) {
                        sb.append(leerCompras[row][col]);
                        // Añadir un espacio después de cada valor, excepto el último
                        if (col < leerCompras[row].length - 1) {
                            sb.append(" ");
                        }
                    }
                    // Añadir un salto de línea después de cada fila, excepto la última
                    if (row < leerCompras.length - 1) {
                        sb.append("\n");
                    }
                }

                // Escribir la cadena en el archivo
                writer.write(sb.toString());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al registrar la compra.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Aplico el precio de producto
            // En los parámetros, primero pongo el valor a restar y luego la operacion que se va a hacer
            ConexionBaseDatos.actualizarMonedas(precios[i][j], "restar");

            // Eliminar todos los componentes del layeredPane antes de agregar los nuevos
            layeredPane.removeAll();

            // Reproducir efecto de sonido de compra
            ReproductorSonidos.reproducirEfectoSonido("comprar2", 0.5f);

            // Reiniciar graficos
            ponerFondo();
            creaCerrarYLlaves();
            creaProductos(); // Llamar a creaProductos() para refrescar las imagenes de la tienda
            layeredPane.setVisible(true);
            repaint();
        } else if (ConexionBaseDatos.testearConexionBBDD() && ConexionBaseDatos.obtenerMonedas() < precios[i][j]) {

            // Si la conexion fue exitosa pero no hay suficiente dinero, mostrar el mensaje de que no tiene suficientes recursos
            pantallaErrorCompra("compra");

        } else if (!ConexionBaseDatos.testearConexionBBDD()) {
            // Si la conexion no fue exitosa, llamar al metodo que muestra la pantalla error de conexion
            pantallaErrorCompra("bbdd");
        }
    }

    // Metodo que genera un layeredPanel nuevo correspondiente a la pantalla de confirmar compra. Este método es llamado por creaProductos cuando se hace click en un producto
    private void mostrarPantallaCompra(int i, int j) {
        // Crear un nuevo JLayeredPane para superponer componentes dentro de la pantalla de confirmacion de compra
        JLayeredPane layeredPaneCompra = new JLayeredPane();
        layeredPaneCompra.setPreferredSize(new Dimension(565, 500)); // Dimensiones del JFrame

        //              AGREGAR IMAGEN DE FONDO
        // Cargar la imagen de fondo en un label
        ImageIcon getFondo = new ImageIcon("recursos/textures/tienda/fondo_madera.jpg");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image fondoSinEscalar = getFondo.getImage().getScaledInstance(565, 500, Image.SCALE_SMOOTH);
        // Cambiar el formato de "Image" a "ImageIcon" para que sea compatible
        ImageIcon fondoEscalado = new ImageIcon(fondoSinEscalar);
        //Agregar imagen al label
        JLabel fondoLabel = new JLabel(fondoEscalado);
        // Agregar propiedades de posicion y tamaño al label
        fondoLabel.setBounds(0, 0, fondoEscalado.getIconWidth(), fondoEscalado.getIconHeight());

        //              AGREGAR IMAGEN DEL PRODUCTO. ESTA IMAGEN DEPENDERÁ DE LOS VALORES "i" Y "j" OBTENIDOS DEL METODO creaProductos().
        // Cargar la imagen del producto en un label
        ImageIcon getProductoIcon = new ImageIcon("recursos/textures/tienda/productosTienda/producto" + i + j + "_comprar.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image productoSinEscalar = getProductoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon productoEscalado = new ImageIcon(productoSinEscalar);
        JLabel productoLabel = new JLabel(productoEscalado);
        productoLabel.setBounds(175, 40, productoEscalado.getIconWidth(), productoEscalado.getIconHeight());

        //              AGREGAR IMAGENES Y MOUSEADAPTER DE CONFIRMAR Y CANCELAR. 
        // Crear etiquetas para los botones de aceptar y cancelar
        ImageIcon aceptarIcon = new ImageIcon("recursos/textures/tienda/ok_button.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image aceptarSinEscalar = aceptarIcon.getImage().getScaledInstance(220, 80, Image.SCALE_SMOOTH);
        ImageIcon aceptarEscalado = new ImageIcon(aceptarSinEscalar);

        JLabel aceptarLabel = new JLabel(aceptarEscalado);
        aceptarLabel.setBounds(40, 300, aceptarEscalado.getIconWidth(), aceptarEscalado.getIconHeight());
        aceptarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Si el valor en la matriz es de 3, quiere decir que es un producto "grafico", y por tanto, llamar a la funcion de compra de graficos
                if (leerCompras[i][j] == 3) {
                    efectuarCompraGrafico(i, j);
                    // Si no es igual a 3, es un producto "skin"
                } else {
                    efectuarCompraSkin(i, j); // Llamar al metodo encargado de aplicar el precio al producto y guardar el registro de la compra para que el usuario siempre tenga acceso al producto comprado
                }
                layeredPaneCompra.setVisible(false); // Dejar de hacer visible la pantalla de confirmacion
            }
        });

        ImageIcon cancelarIcon = new ImageIcon("recursos/textures/tienda/cancel_button.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image cancelarSinEscalar = cancelarIcon.getImage().getScaledInstance(220, 80, Image.SCALE_SMOOTH);
        ImageIcon cancelarEscalado = new ImageIcon(cancelarSinEscalar);

        JLabel cancelarLabel = new JLabel(cancelarEscalado);
        cancelarLabel.setBounds(280, 300, cancelarEscalado.getIconWidth(), cancelarEscalado.getIconHeight());
        cancelarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                layeredPaneCompra.setVisible(false); // Dejar de hacer visible la pantalla de confirmacion
                layeredPane.setVisible(true); // Hacer visible el panel general de la tienda
            }
        });

        // Añadir las etiquetas al JLayeredPane
        layeredPaneCompra.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPaneCompra.add(productoLabel, JLayeredPane.PALETTE_LAYER);
        layeredPaneCompra.add(aceptarLabel, JLayeredPane.POPUP_LAYER);
        layeredPaneCompra.add(cancelarLabel, JLayeredPane.POPUP_LAYER);

        // Reproducir sonido de boton
        ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

        layeredPane.setVisible(false); // Quitar visibilidad del layeredPanel de la tienda
        this.add(layeredPaneCompra); // Agregar el layeredPane al JFrame para mostrar la pantalla de confirmacion
    }

    // Genera el botón cruz de cerrar y el contador de llaves disponibles
    private void creaCerrarYLlaves() {

        //          PRIMERO CARGAR BOTON DE LA CRUZ Y AGREGAR SU MOUSEADAPTER (mismo metodo que los iconos anteriores de esta clase)
        // Cargar la imagen de la cruz (x)
        ImageIcon getCloseIcon = new ImageIcon("recursos/textures/tienda/close_button.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image closeSinEscalar = getCloseIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon closeEscalado = new ImageIcon(closeSinEscalar);
        JLabel closeLabel = new JLabel(closeEscalado);
        closeLabel.setBounds(480, 10, closeEscalado.getIconWidth(), closeEscalado.getIconHeight());

        // MouseAdapter del boton cruz (x)
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Refrescar el estado de isTiendaAbierta para rehabilitar los keyListener
                VariablesGenerales.isTiendaAbierta = false;
                // Detener musica
                ReproductorSonidos.detenerMusicaFondo();
                // El método dispose () cierra el JFrame actual, en este caso la tienda
                dispose();
            }
        });

        //          CARGAR LLAVES Y CONTADOR
        // Cargar llave
        ImageIcon getLlaveIcon = new ImageIcon("recursos/textures/tienda/llave_sombreada2.png");

        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image keySinEscalar = getLlaveIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon keyEscalado = new ImageIcon(keySinEscalar);
        JLabel llaveLabel = new JLabel(keyEscalado);
        llaveLabel.setBounds(5, 8, closeEscalado.getIconWidth(), closeEscalado.getIconHeight());

        // Cargar contador y estilizarlo por codigo html
        JLabel contadorLlaveLabel = new JLabel("<html><span style='color:white; font-size: 30px; font-family: Unispace;'>" + puntuacion + "</span></html>");
        // Cargo un label identico al contadorLabel, pero le pongo color negro y lo desplazo un poco para que haga un efecto de sombra
        JLabel sombraLlaveContadorLabel = new JLabel("<html><span style='color:black; font-size: 30px; font-family: Unispace;'>" + puntuacion + "</span></html>");

        contadorLlaveLabel.setBounds(55, 9, 180, 50);
        sombraLlaveContadorLabel.setBounds(58, 12, 180, 50);

        //          CARGAR MONEDAS Y CONTADOR DE LAS MONEDAS
        // Cargar imagen moneda
        ImageIcon getMonedaIcon = new ImageIcon("recursos/textures/tienda/moneda_sombrafuerte.png");

        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image monedaSinEscalar = getMonedaIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon monedaEscalada = new ImageIcon(monedaSinEscalar);
        JLabel monedaLabel = new JLabel(monedaEscalada);
        monedaLabel.setBounds(210, 10, closeEscalado.getIconWidth(), closeEscalado.getIconHeight());

        // Cargar contador obteniendo el numero de monedas mediante obtenerMonedas() y estilizarlo por codigo html
        JLabel contadorMonedasLabel = new JLabel("<html><span style='color:white; font-size: 30px; font-family: Unispace;'>" + ConexionBaseDatos.obtenerMonedas() + "</span></html>");
        // Cargo un label identico al contadorLabel, pero le pongo color negro y lo desplazo un poco para que haga un efecto de sombra
        JLabel sombraContadorMonedasLabel = new JLabel("<html><span style='color:black; font-size: 30px; font-family: Unispace;'>" + ConexionBaseDatos.obtenerMonedas() + "</span></html>");

        contadorMonedasLabel.setBounds(260, 9, 180, 50);
        sombraContadorMonedasLabel.setBounds(263, 12, 180, 50);

        // Agregar labels al panel principal
        layeredPane.add(contadorLlaveLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(sombraLlaveContadorLabel, JLayeredPane.PALETTE_LAYER);

        layeredPane.add(contadorMonedasLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(sombraContadorMonedasLabel, JLayeredPane.PALETTE_LAYER);

        layeredPane.add(closeLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(llaveLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(monedaLabel, JLayeredPane.PALETTE_LAYER);
    }

    // Genera un panel para la pantalla de error de compra exclusivamente
    private void pantallaErrorCompra(String error) {
        // Crear un nuevo JLayeredPane para superponer componentes dentro de la pantalla de confirmacion de compra
        JLayeredPane layeredPaneCompraError = new JLayeredPane();
        layeredPaneCompraError.setPreferredSize(new Dimension(565, 500)); // Dimensiones del JFrame

        //              AGREGAR IMAGEN DE FONDO
        // Cargar la imagen de fondo en un label
        ImageIcon getFondo = new ImageIcon("recursos/textures/tienda/error_" + error + ".jpg");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image fondoSinEscalar = getFondo.getImage().getScaledInstance(565, 500, Image.SCALE_SMOOTH);
        // Cambiar el formato de "Image" a "ImageIcon" para que sea compatible
        ImageIcon fondoEscalado = new ImageIcon(fondoSinEscalar);
        //Agregar imagen al label
        JLabel fondoLabel = new JLabel(fondoEscalado);
        // Agregar propiedades de posicion y tamaño al label
        fondoLabel.setBounds(0, 0, fondoEscalado.getIconWidth(), fondoEscalado.getIconHeight());

        //              AGREGAR IMAGENES Y MOUSEADAPTER DE CONFIRMAR Y CANCELAR. 
        // Crear etiquetas para los botones de aceptar y cancelar
        ImageIcon aceptarIcon = new ImageIcon("recursos/textures/tienda/ok_button.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image aceptarSinEscalar = aceptarIcon.getImage().getScaledInstance(240, 80, Image.SCALE_SMOOTH);
        ImageIcon aceptarEscalado = new ImageIcon(aceptarSinEscalar);

        JLabel aceptarLabel = new JLabel(aceptarEscalado);
        aceptarLabel.setBounds(155, 320, aceptarEscalado.getIconWidth(), aceptarEscalado.getIconHeight());
        aceptarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                layeredPaneCompraError.setVisible(false); // Dejar de hacer visible la pantalla de confirmacion
                layeredPane.setVisible(true); // Hacer visible el panel general de la tienda
            }
        });

        // Añadir las etiquetas al JLayeredPane
        layeredPaneCompraError.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPaneCompraError.add(aceptarLabel, JLayeredPane.POPUP_LAYER);
        
        // Reproducir sonido de error
        ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);

        layeredPane.setVisible(false); // Quitar visibilidad del layeredPanel de la tienda
        this.add(layeredPaneCompraError); // Agregar el layeredPane al JFrame para mostrar la pantalla de confirmacion
    }

    // Se encarga de leer el numero de llaves actual en el archivo llavesJugador.txt para mostrarlos por pantalla y realizar operaciones con el. Este método es llamado por el constructor
    private void getNumeroLlaves() {
        try (Scanner scanner = new Scanner(new File("recursos/datosJugador/llavesJugador.txt"))) {
            puntuacion = scanner.nextInt();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado la ruta donde se guardan las llaves", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Se encarga de guardar en un archivo de texto plano el texto definido en el parametro datoAGuardar
    // La direccion del txt también se pone como parámetro
    private void guardarEnArchivo(String direccionTXT, String datoAGuardar) {
        try (FileWriter writer = new FileWriter(direccionTXT)) {
            writer.write(datoAGuardar + "\n");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido guardar el archivo. Comprueba si tienes espacio disponible en tu equipo o comprueba si tienes los permisos suficientes para guardar archivos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
