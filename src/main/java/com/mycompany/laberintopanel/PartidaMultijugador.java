/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;

/**
 *
 * @author Jorge Areal Alberich
 */
public class PartidaMultijugador extends JPanel implements KeyListener {

    private JPanel buttonsPanel;

    private int puntuacion; // Puntuacion con la que empiezas

    // Matriz del laberinto con las dimensiones suficientes para ocupar toda la pantalla
    private int[][] laberinto = new int[15][31];

    // Inicializar variables de posicion del jugador 1
    private int posXJ1; // fila
    private int posYJ1; // columna

    // Inicializar variables de posicion del jugador 2
    private int posXJ2; // fila
    private int posYJ2; // columna

    // Este string es modificado por el keyListener para que rote el personaje 1
    private String direccionPersonajeJ1;

    // Este string es modificado por el keyListener para que rote el personaje 2
    private String direccionPersonajeJ2;

    private Timer actualizacionJugadores;

    public static boolean isJugadorHost;
    
    private boolean mensajeConectadoMostrado = false;

    public PartidaMultijugador() {
        crearYConfigurarPanel();
        cargarNivel();

        iniciarActualizacionMultijugador();
        ReproductorSonidos.reproducirEfectoSonido("entrarpartida", 0.5f);
        setFocusable(true);
        repaint();
        addKeyListener(this);
    }

    /*
            SECCION CARGA-NIVELES: carga los niveles segun corresopnda
     */
    // Lee el archivo txt correspondiente al nivel por defecto y lo muestra por pantalla
    private void cargarNivel() {
        try (Scanner scanner = new Scanner(new File("recursos/nivelesMultiplayer/nivel.txt"))) { // cargar numero del nivel guardado en nivelActual.txt
            System.out.println("recursos/nivelesMultiplayer/nivel.txt");
            posXJ1 = scanner.nextInt(); // fila
            posYJ1 = scanner.nextInt(); // columna

            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    laberinto[i][j] = scanner.nextInt();
                }
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "No se ha encontrado el archivo del nivel a cargar o no tiene el formato adecuado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        direccionPersonajeJ1 = LaberintoPanel.obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
        direccionPersonajeJ2 = LaberintoPanel.obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
        requestFocus();  // Solicitar el foco para que se lean bien los keyListener
        repaint(); // Refrescar pantalla
    }

    /*
            SECCION @OVERRIDES: pintar componentes y capturadores de teclas
     */
    // Pinta todas las texturas del juego
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        int dimension = 40;

        // Cargar textura suelo
        ImageIcon sueloIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/suelo.jpg");
        Image sueloImage = sueloIcon.getImage();

        // Cargar textura pared
        ImageIcon paredIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/pared.jpg");
        Image paredImage = paredIcon.getImage();

        // Cargar textura llave
        ImageIcon llaveIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/llave.jpg");
        Image llaveImage = llaveIcon.getImage();

        // Cargar textura inicio
        ImageIcon inicioIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/inicio.jpg");
        Image inicioImage = inicioIcon.getImage();

        // Cargar textura meta cerrada
        ImageIcon metaCloseIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/metaCerrada.jpg");
        Image metaCerradaImage = metaCloseIcon.getImage();

        // Cargar textura meta abierta
        ImageIcon metaOpenIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/metaAbierta.jpg");
        Image metaAbiertaImage = metaOpenIcon.getImage();

        // Cargar textura tienda
        ImageIcon tiendaIcon = new ImageIcon(LaberintoPanel.obtenerGraficos() + "/tienda.jpg");
        Image tiendaImage = tiendaIcon.getImage();

        // Cargar llave para el contador
        ImageIcon llaveContadorIcon = new ImageIcon("recursos/textures/llaveContador.png");
        Image llaveContadorImage = llaveContadorIcon.getImage();

        // Recorrer array y colocar texturas donde tocan
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                switch (laberinto[i][j]) {
                    case 0: // Suelo
                        g.drawImage(sueloImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 1: // Pared

                        // Si la textura elegida es la del modo fiesta, poner la logica de la unificacion de paredes, igual que en ModoFiestaJuego
                        if ("recursos/textures/graficos/fiesta/".equals(LaberintoPanel.obtenerGraficos())) {

                            // Determinar si los bloques adyacentes son paredes para realizar la unificacion
                            boolean tieneArriba = (i > 0 && laberinto[i - 1][j] == 1);
                            boolean tieneAbajo = (i < laberinto.length - 1 && laberinto[i + 1][j] == 1);
                            boolean tieneIzquierda = (j > 0 && laberinto[i][j - 1] == 1);
                            boolean tieneDerecha = (j < laberinto[i].length - 1 && laberinto[i][j + 1] == 1);

                            // Determinar qué textura de pared usar según los bloques adyacentes
                            String texturaPared = ModoFiestaJuego.determinarTexturaPared(tieneArriba, tieneAbajo, tieneIzquierda, tieneDerecha);
                            // Cargar y dibujar la textura de la pared que corresponde
                            ImageIcon paredIconFiesta = new ImageIcon("recursos/textures/graficos/fiesta/" + texturaPared + ".jpg");
                            paredImage = paredIconFiesta.getImage();
                        }

                        g.drawImage(paredImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;

                    case 2: // Llave
                        g.drawImage(llaveImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 3: // Meta abierta
                        g.drawImage(metaAbiertaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 4: // Meta cerrada
                        g.drawImage(metaCerradaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 5: // Inicio
                        g.drawImage(inicioImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 6: // Tienda
                        g.drawImage(tiendaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 8: // Meta falsa abierta
                        // Cargar la misma imagen que en la meta original
                        g.drawImage(metaAbiertaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 9: //Meta falsa cerrada
                        g.drawImage(metaCerradaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    default:
                        break;
                }
            }
        }

        //          DIBUJAR SKIN ELEGIDA J1
        ImageIcon personajeIcon = new ImageIcon(direccionPersonajeJ1);
        Image iconoPersonaje = personajeIcon.getImage();
        g.drawImage(iconoPersonaje, posYJ1 * dimension + 5, posXJ1 * dimension + 5, 30, 30, null);

        // Si el J2 está conectado, pintar su skin. Si no, no pintarla.
        if ("si".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2))) {
            //          DIBUJAR SKIN ELEGIDA J2
            ImageIcon personajeIconJ2 = new ImageIcon(direccionPersonajeJ2);
            Image iconoPersonajeJ2 = personajeIconJ2.getImage();
            g.drawImage(iconoPersonajeJ2, posYJ2 * dimension + 5, posXJ2 * dimension + 5, 30, 30, null);
        }

        if ("si".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2)) && !mensajeConectadoMostrado && isJugadorHost) {
            mensajeConectadoMostrado = true;
            JOptionPane.showMessageDialog(this, "¡Alguien se ha unido a tu partida!", "Result", JOptionPane.INFORMATION_MESSAGE);
            
        } else if ("no".equals(ConexionBaseDatos.leerCadenaTextoDesdeBD("conectado", 2)) && mensajeConectadoMostrado && isJugadorHost){
            mensajeConectadoMostrado = false;
            JOptionPane.showMessageDialog(this, "El usuario ha abandonado la partida :/", "Result", JOptionPane.INFORMATION_MESSAGE);
        }

        //          COLOCAR CONTADOR LLAVES
        // Colocar icono de llave
        g.drawImage(llaveContadorImage, 2, 560, 37, 37, null);

        //
        /* Se colocan 2 textos iguales:
           1. En color blanco
           2. En color negro un poco desplazado para dar un efecto de sombra
         */
 /*g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40)); // Establecer la fuente y el tamaño del texto
        g.drawString(puntuacion + "", 43, 595);

        // Colocar contador de llaves
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40)); // Establecer la fuente y el tamaño del texto
        g.drawString(puntuacion + "", 41, 593);*/
    }

    // Lector de teclas
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (isJugadorHost) { // El anfitrión va a mover los controles de posXJ1 y posYJ1
            if (key == KeyEvent.VK_LEFT && posYJ1 > 0 && laberinto[posXJ1][posYJ1 - 1] != 1) {
                posYJ1--;
                direccionPersonajeJ1 = LaberintoPanel.obtenerSkin() + "_izquierda.png"; // Cambia string de la direccion de la imagen del personaje

            } else if (key == KeyEvent.VK_RIGHT && posYJ1 < laberinto[0].length - 1 && laberinto[posXJ1][posYJ1 + 1] != 1) {
                posYJ1++;
                direccionPersonajeJ1 = LaberintoPanel.obtenerSkin() + "_derecha.png";
            } else if (key == KeyEvent.VK_UP && posXJ1 > 0 && laberinto[posXJ1 - 1][posYJ1] != 1) {
                posXJ1--;
                direccionPersonajeJ1 = LaberintoPanel.obtenerSkin() + "_arriba.png";
            } else if (key == KeyEvent.VK_DOWN && posXJ1 < laberinto.length - 1 && laberinto[posXJ1 + 1][posYJ1] != 1) {
                posXJ1++;
                direccionPersonajeJ1 = LaberintoPanel.obtenerSkin() + "_abajo.png";
                // Si el juego está puesto en modo normal y el usuario pulsa la tecla "r" encima de una casilla de inicio, vuelve al anterior nivel
            }
        } else if (!isJugadorHost) { // Si el jugador es el que se une, va a mover los controles de posXJ2 y posYJ2
            if (key == KeyEvent.VK_LEFT && posYJ2 > 0 && laberinto[posXJ2][posYJ2 - 1] != 1) {
                posYJ2--;
                direccionPersonajeJ2 = LaberintoPanel.obtenerSkin() + "_izquierda.png"; // Cambia string de la direccion de la imagen del personaje

            } else if (key == KeyEvent.VK_RIGHT && posYJ2 < laberinto[0].length - 1 && laberinto[posXJ2][posYJ2 + 1] != 1) {
                posYJ2++;
                direccionPersonajeJ2 = LaberintoPanel.obtenerSkin() + "_derecha.png";
            } else if (key == KeyEvent.VK_UP && posXJ2 > 0 && laberinto[posXJ2 - 1][posYJ2] != 1) {
                posXJ2--;
                direccionPersonajeJ2 = LaberintoPanel.obtenerSkin() + "_arriba.png";
            } else if (key == KeyEvent.VK_DOWN && posXJ2 < laberinto.length - 1 && laberinto[posXJ2 + 1][posYJ2] != 1) {
                posXJ2++;
                direccionPersonajeJ2 = LaberintoPanel.obtenerSkin() + "_abajo.png";
                // Si el juego está puesto en modo normal y el usuario pulsa la tecla "r" encima de una casilla de inicio, vuelve al anterior nivel
            }
        }

        // Tras cada pulsacion, se llamaran a los siguientes metodos:
        repaint();
    }

    private void iniciarActualizacionMultijugador() {
        actualizacionJugadores = new Timer();
        actualizacionJugadores.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                actualizarJugadores();
            }
        }, 0, 150); // Ejecutar la tarea cada 150 milisegundos
    }

    private void actualizarJugadores() {
        if (isJugadorHost) {
            // Publicar las coordenadas actuales del J1
            ConexionBaseDatos.actualizarPosicionJugadorDesdeBD(posXJ1, posYJ1, direccionPersonajeJ1, 1);
            // Leer las coordenadas actuales del J2
            int[] coordenadasJ2 = ConexionBaseDatos.leerPosicionJugadorDesdeBD(2);
            posXJ2 = coordenadasJ2[0];
            posYJ2 = coordenadasJ2[1];
            // Leer la rotación del J2
            direccionPersonajeJ2 = ConexionBaseDatos.leerCadenaTextoDesdeBD("rotacion", 2);
        } else if (!isJugadorHost) {
            // Publicar las coordenadas actuales del J2
            ConexionBaseDatos.actualizarPosicionJugadorDesdeBD(posXJ2, posYJ2, direccionPersonajeJ2, 2);
            // Leer las coordenadas actuales del J1
            int[] coordenadasJ1 = ConexionBaseDatos.leerPosicionJugadorDesdeBD(1);
            posXJ1 = coordenadasJ1[0];
            posYJ1 = coordenadasJ1[1];
            // Leer la rotación del J2
            direccionPersonajeJ1 = ConexionBaseDatos.leerCadenaTextoDesdeBD("rotacion", 1);
        }
        if (!ConexionBaseDatos.testearConexionMultijugador()) {
            actualizacionJugadores.cancel();

            // Eliminar el panel actual y mostrar el menu principal mostrando mensaje de perdida de conexion
            this.removeAll();
            buttonsPanel.removeAll();
            this.setVisible(false);
            buttonsPanel.setVisible(false);
            MenuLaberinto.panelLayered.setVisible(true);
            MenuLaberinto.panelLayered.removeAll(); // Quitar elementos del menu para que no se acumulen al volver a cargarlos
            MenuLaberinto menu = new MenuLaberinto();
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(MenuLaberinto.frame, "Se ha perdido la conexión", "Error", JOptionPane.ERROR_MESSAGE);
        }
        repaint();
    }

    private void crearYConfigurarPanel() {
        // Limpiar todo el menu principal para que se muestre la nueva pantalla
        MenuLaberinto.panelLayered.removeAll();
        MenuLaberinto.panelLayered.setVisible(false);

        MenuLaberinto.frame.add(this); // Agregar el panel del laberinto al frame principal
        requestFocus(); // Solicitar foco justo despues de agregar el panel al frame para que se lea correctamente el keyListener

        buttonsPanel = new JPanel();

        // Establecer el color de fondo
        buttonsPanel.setBackground(Color.WHITE); // Puedes usar cualquier color que desees

        // Crear JLabels con imágenes
        JLabel volverMenuButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/salirMenu_button.png"));

        //       ZONA MOUSE LISTENERS
        volverMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Detener el timer de actualizacion de movimientos
                actualizacionJugadores.cancel();

                // Llamar al método que se encarga de cerrar la tienda por si ha quedado abierta
                //cerrarTienda();
                // Si es el jugador host, borrar el timer y la partida
                if (isJugadorHost) {
                    // Eliminar la tabla del multijugador
                    ConexionBaseDatos.eliminarTablaMultijugador();
                } else {
                    // Actualizar el estado de "conectado" a "no" para que el sistema deje pasar de nuevo a algun usuario a esta partida
                    ConexionBaseDatos.actualizarEstadoConexionJugadoresBD("no", 2);
                }

                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                // Eliminar el panel actual y mostrar el menu principal
                removeAll();
                buttonsPanel.removeAll();
                setVisible(false);
                buttonsPanel.setVisible(false);
                MenuLaberinto.panelLayered.setVisible(true);
                MenuLaberinto.panelLayered.removeAll(); // Quitar elementos del menu para que no se acumulen al volver a cargarlos
                MenuLaberinto menu = new MenuLaberinto();
            }
        });

        //              Agregar los JLabels al panel buttonsPanel
        buttonsPanel.add(volverMenuButton);

        // Agregar el panel buttonsPanel al frame en la parte sur
        MenuLaberinto.frame.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
