/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Jorge Areal Alberich
 */
public class MenuLaberinto {

    // Inicializo el frame con el que se trabajará durante todo el juego. Se hace público para que sea facilmente accesible por el resto de clases
    public static JFrame frame = new JFrame("Laberinto Game");

    // JLayeredPane para superponer componentes
    public static JLayeredPane panelLayered = new JLayeredPane();

    // Crear un panel principal con BorderLayout
    private Container panelMenu = new JPanel(new BorderLayout());

    // Constructor
    public MenuLaberinto() {
        creaVentanaPrincipal();
        creaBotonesPrincipalesMenu();
        frame.setVisible(true);
    }

    // Método que crea y configura el frame en donde se mostrará y desarrollará todo el juego del laberinto
    private void creaVentanaPrincipal() {
        frame.setSize(1256, 720); //Tamaño justo y necesario para el tamaño de los niveles del laberinto
        frame.setLocationRelativeTo(null); // Centro la ventana

        // Inicializo la variable que contiene la imagen del icono del laberinto
        ImageIcon iconoPrincipal = new ImageIcon("recursos\\icono2.png");
        // Aplico la variable que contiene la imagen del icono y la pongo como icono del frame
        frame.setIconImage(iconoPrincipal.getImage()); // Colocar logo del laberinto utilizando la variable icono creada anteriormente

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Agregar propiedad de que no se pueda extender ni disminuir la ventana (soluciona muchos bugs)
        frame.setResizable(false);

        // Llamar al método que se encarga de aplicar el fondo del menú
        ponerFondoMenu();

        // Agregar el panel principal en la capa predeterminada
        panelLayered.add(panelMenu, JLayeredPane.DEFAULT_LAYER);

        // Agregar el JLayeredPane al JFrame
        frame.add(panelLayered);
    }

    // Método que crea y configura los botones del menu con sus respectivas funcionalidades
    public static void creaBotonesPrincipalesMenu() {
        // Obtener las dimensiones del JFrame
        int frameWidth = frame.getWidth();

        //             ZONA CREACIÓN LABELS
        // En esta zona se encuentra la lógica para generar los labels y configurarlos
        // Crear JLabel del logo del titulo
        JLabel titulo = new JLabel(new ImageIcon("recursos/textures/menu/titulo2.png"));

        // Crear JLabels para los botones
        JLabel jugarButton = new JLabel(new ImageIcon("recursos/textures/menu/jugar_button2.png"));
        JLabel editorButton = new JLabel(new ImageIcon("recursos/textures/menu/editor_button2.png"));
        JLabel fiestaButton = new JLabel(new ImageIcon("recursos/textures/menu/fiesta_button2.png"));
        JLabel salirButton = new JLabel(new ImageIcon("recursos/textures/menu/salir_button2.png"));

        // Configurar posicion del título
        titulo.setBounds(460, 2, 320, 220);

        // Configurar posición de los botones
        int botonWidth = jugarButton.getPreferredSize().width;
        int botonHeight = jugarButton.getPreferredSize().height;
        int espacioVertical = 30; // Espacio vertical entre botones
        int yInicial = 240; // Posición inicial vertical

        jugarButton.setBounds((frameWidth - botonWidth) / 2, yInicial, botonWidth, botonHeight);
        editorButton.setBounds((frameWidth - botonWidth) / 2, yInicial + botonHeight + espacioVertical, botonWidth, botonHeight);
        fiestaButton.setBounds((frameWidth - botonWidth) / 2, yInicial + 2 * (botonHeight + espacioVertical), botonWidth, botonHeight);
        salirButton.setBounds((frameWidth - botonWidth) / 2, yInicial + 3 * (botonHeight + espacioVertical), botonWidth, botonHeight);

        //             ZONA MOUSE ADAPTERS
        // En esta zona se encuentra la lógica de los lectores de click para cada botón, y la lógica de qué va a hacer cada botón
        // Boton jugar
        jugarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ocultar los botones
                jugarButton.setVisible(false);
                editorButton.setVisible(false);
                fiestaButton.setVisible(false);
                salirButton.setVisible(false);

                // Eliminar los botones del panelLayered
                panelLayered.remove(titulo);
                panelLayered.remove(jugarButton);
                panelLayered.remove(editorButton);
                panelLayered.remove(fiestaButton);
                panelLayered.remove(salirButton);

                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                // Crea y muestra los nuevos botones
                creaBotonesSeleccionJugadores();
            }
        });

        // Boton editor
        editorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panelLayered.setVisible(false);
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                // Llamar a ModoEditor.java mandando como parametro las dimensiones de la matriz
                ModoEditor editor = new ModoEditor(new int[15][31]);
                frame.add(editor);
            }
        });

        // Boton fiesta
        fiestaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Quitar visibiildad al panel del menu
                panelLayered.setVisible(false);
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                // Llamar al constructor de ModoEditor.java y que empiece la fiesta
                ModoFiestaTutorial fiesta = new ModoFiestaTutorial();
            }
        });

        // Boton salir
        salirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Cerrar aplicación
                System.exit(0);
            }
        });

        // Agregar labels al panelLayered
        panelLayered.add(titulo, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(jugarButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(editorButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(fiestaButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(salirButton, JLayeredPane.PALETTE_LAYER);
    }

    // Crea los botones "un jugador" "dos jugadores" y "atrás" tras pulsar el botón "Jugar" en el menú principal
    private static void creaBotonesSeleccionJugadores() {
        // Obtener las dimensiones del JFrame
        int frameWidth = frame.getWidth();

        //             ZONA CREACIÓN LABELS
        // En esta zona se encuentra la lógica para generar los labels y configurarlos
        // Crear JLabels para los botones
        JLabel unJugadorButton = new JLabel(new ImageIcon("recursos/textures/menu/1jugador_button.png"));
        JLabel dosJugadoresButton = new JLabel(new ImageIcon("recursos/textures/menu/2jugadores_button.png"));
        JLabel atrasButton = new JLabel(new ImageIcon("recursos/textures/menu/atras_button.png"));

        // Configurar posición de los botones
        int botonWidth = unJugadorButton.getPreferredSize().width;
        int botonHeight = unJugadorButton.getPreferredSize().height;
        int espacioVertical = 30; // Espacio vertical entre botones
        int yInicial = 240; // Posición inicial vertical

        unJugadorButton.setBounds((frameWidth - botonWidth) / 2, yInicial, botonWidth, botonHeight);
        dosJugadoresButton.setBounds((frameWidth - botonWidth) / 2, yInicial + botonHeight + espacioVertical, botonWidth, botonHeight);
        atrasButton.setBounds((frameWidth - botonWidth) / 2, yInicial + 2 * (botonHeight + espacioVertical), botonWidth, botonHeight);

        //             ZONA MOUSE ADAPTERS
        // En esta zona se encuentra la lógica de los lectores de click para cada botón, y la lógica de qué va a hacer cada botón
        // Boton un jugador
        unJugadorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                unJugadorButton.setVisible(false);
                dosJugadoresButton.setVisible(false);
                atrasButton.setVisible(false);

                // Eliminar los botones del panelLayered
                panelLayered.remove(unJugadorButton);
                panelLayered.remove(dosJugadoresButton);
                panelLayered.remove(atrasButton);

                VariablesGenerales.isNormalMode = true;
                panelLayered.setVisible(false);
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                // Llamar al constructor de la clase LaberintoPanel.java
                LaberintoPanel laberintoPanel = new LaberintoPanel();
                LaberintoPanel.laberintoPanel = laberintoPanel;
            }
        });

        // Boton dos jugadores
        dosJugadoresButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                System.out.println("Me gusta comer cemento");

                unJugadorButton.setVisible(false);
                dosJugadoresButton.setVisible(false);
                atrasButton.setVisible(false);

                // Eliminar los botones del panelLayered
                panelLayered.remove(unJugadorButton);
                panelLayered.remove(dosJugadoresButton);
                panelLayered.remove(atrasButton);

                creaBotonesSeleccionMultijugador();
            }
        });

        // Boton dos atrás
        atrasButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                unJugadorButton.setVisible(false);
                dosJugadoresButton.setVisible(false);
                atrasButton.setVisible(false);

                // Eliminar los botones del panelLayered
                panelLayered.remove(unJugadorButton);
                panelLayered.remove(dosJugadoresButton);
                panelLayered.remove(atrasButton);

                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                creaBotonesPrincipalesMenu();
            }
        });

        // Agregar labels al panelLayered
        panelLayered.add(unJugadorButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(dosJugadoresButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(atrasButton, JLayeredPane.PALETTE_LAYER);
    }

    // Crea los botones "hostear", "unirse" y "atrás" tras pulsar el botón "2 jugadores"
    private static void creaBotonesSeleccionMultijugador() {

        // Obtener las dimensiones del JFrame
        int frameWidth = frame.getWidth();

        //             ZONA CREACIÓN LABELS
        // En esta zona se encuentra la lógica para generar los labels y configurarlos
        // Crear JLabels para los botones
        JLabel hostearButton = new JLabel(new ImageIcon("recursos/textures/menu/hostear_button.png"));
        JLabel unirseButton = new JLabel(new ImageIcon("recursos/textures/menu/unirse_button.png"));
        JLabel atrasButton = new JLabel(new ImageIcon("recursos/textures/menu/atras_button.png"));

        // Configurar posición de los botones
        int botonWidth = hostearButton.getPreferredSize().width;
        int botonHeight = hostearButton.getPreferredSize().height;
        int espacioVertical = 30; // Espacio vertical entre botones
        int yInicial = 240; // Posición inicial vertical

        hostearButton.setBounds((frameWidth - botonWidth) / 2, yInicial, botonWidth, botonHeight);
        unirseButton.setBounds((frameWidth - botonWidth) / 2, yInicial + botonHeight + espacioVertical, botonWidth, botonHeight);
        atrasButton.setBounds((frameWidth - botonWidth) / 2, yInicial + 2 * (botonHeight + espacioVertical), botonWidth, botonHeight);

        //             ZONA MOUSE ADAPTERS
        // En esta zona se encuentra la lógica de los lectores de click para cada botón, y la lógica de qué va a hacer cada botón
        // Boton hostear
        hostearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                // Si es capaz de conectarse a la base de datos pero no existe ninguna partida
                if (ConexionBaseDatos.testearConexionBBDD() && !ConexionBaseDatos.testearConexionMultijugador()) {
                    PartidaMultijugador.isJugadorHost = true;
                    ConexionBaseDatos.crearTablaMultijugador();
                    PartidaMultijugador hostear = new PartidaMultijugador();
                
                // Si existe alguna partida, denegar el acceso a crear otra nueva
                } else if (ConexionBaseDatos.testearConexionMultijugador()) {
                    ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    JOptionPane.showMessageDialog(panelLayered, "Error al crear la partida. Ya hay una partida empezada.", "Error", JOptionPane.ERROR_MESSAGE);
                // Cualquier otro error, lanzar mensaje de error genérico
                } else {
                    ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    JOptionPane.showMessageDialog(panelLayered, "Error al crear la partida. Debes estar conectado a internet", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Boton dos jugadores
        unirseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                System.out.println("Me gusta comer cemento");
                System.out.println("no".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2)));

                // Si es capaz de conectarse a la base de datos y no hay ningun usuario conectado a la partida, entrar a la partida
                if (ConexionBaseDatos.testearConexionMultijugador() && "no".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2))) {
                    // Actualizar el estado de "conectado" a "si" para que el sistema no deje pasar a mas usuarios a la misma partida
                    ConexionBaseDatos.actualizarEstadoConexionJugadoresBD("si", 2);
                    // Poner el estado de isJugadorHost en false para que el sistema detecte que se une un tercero, y no un host
                    PartidaMultijugador.isJugadorHost = false;
                    // Inicializar constructor de la clase que gestiona el modo multijugador
                    PartidaMultijugador unirse = new PartidaMultijugador();
                } else if (ConexionBaseDatos.testearConexionMultijugador() && "si".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2))) {
                    ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    JOptionPane.showMessageDialog(panelLayered, "Error al unirse a la partida. El servidor está lleno.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!ConexionBaseDatos.testearConexionMultijugador()) {
                    ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    JOptionPane.showMessageDialog(panelLayered, "Error al unirse a la partida. Debe haber una partida en curso.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    JOptionPane.showMessageDialog(panelLayered, "Error al unirse a la partida. Debes estar conectado a internet.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Boton atrás
        atrasButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hostearButton.setVisible(false);
                unirseButton.setVisible(false);
                atrasButton.setVisible(false);

                // Eliminar los botones del panelLayered
                panelLayered.remove(hostearButton);
                panelLayered.remove(unirseButton);
                panelLayered.remove(atrasButton);

                // Reproducir el efecto de sonido del botón
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                creaBotonesPrincipalesMenu();
            }
        });

        // Agregar labels al panelLayered
        panelLayered.add(hostearButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(unirseButton, JLayeredPane.PALETTE_LAYER);
        panelLayered.add(atrasButton, JLayeredPane.PALETTE_LAYER);
    }

    // Lee la imagen de fondo para el menu y lo aplica al panelLayered
    private void ponerFondoMenu() {
        try {
            // Cargar imagen de fondo
            BufferedImage image = ImageIO.read(new File("recursos/textures/menu/fondo5.jpg"));
            JLabel background = new JLabel(new ImageIcon(image));

            // Meter propiedades a la imagen de fondo
            background.setBounds(0, 0, background.getPreferredSize().width, background.getPreferredSize().height);

            // Agregar la imagen de fondo en la capa predeterminada
            panelLayered.add(background, JLayeredPane.DEFAULT_LAYER);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Llamar al constructor MenuLaberinto() para que construya la ventana y el menu principal con sus funcionalidades
        MenuLaberinto menu = new MenuLaberinto();
    }
}
