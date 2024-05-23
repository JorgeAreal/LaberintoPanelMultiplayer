/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.laberintopanel;

//import static com.mycompany.laberintopanel.MenuLaberinto.frame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Jorge
 */
public class LaberintoPanel extends JPanel implements KeyListener {

    private JPanel buttonsPanel;

    private int puntuacion; // Puntuacion con la que empiezas

    // Este string es modificado por el keyListener para que rote el personaje
    private String direccionPersonaje;

    // Este es el numero del nivel donde se encuentra el jugador actualmente. Este numero sera obtenida de personajesComprados.txt mediante el metodo obtenerSkin()
    private int nivelActual;

    // Matriz del laberinto con las dimensiones suficientes para ocupar toda la pantalla
    private int[][] laberinto = new int[15][31];

    // Inicializar variables de posicion del personaje
    private int posX; // fila
    private int posY; // columna

    // Hago referencia a la instancia para poder llamarla desde MenuLabeinto.java y ModoEditor.java
    public static LaberintoPanel laberintoPanel;

    // Campo para almacenar la instancia de la ventana de la tienda para que se pueda controlar su apertura desde distintos métodos
    private TiendaFrame tiendaFrame;

    // Constructor
    public LaberintoPanel() {
        creaVentanaLaberinto();
        obtenerNumeroNivelActual(); // Inicializar partida con el numero del nivel que se quedó la ultima vez el jugador
        cargarLlaves();
        decidirCargarNivel();
        abrirMeta();
        ReproductorSonidos.reproducirEfectoSonido("entrarpartida", 0.5f);
        setFocusable(true);
        addKeyListener(this);
        requestFocus();
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    // Coloca llaves aleatoriamente
    private void colocarLlaves() {
        Random random = new Random();
        int llavesColocadas = 0;

        while (llavesColocadas < 6) {
            int i = random.nextInt(laberinto.length);
            int j = random.nextInt(laberinto[0].length);

            if (laberinto[i][j] == 0) {
                laberinto[i][j] = 2; // Representaremos las llaves con el número 2
                llavesColocadas++;
            }
        }
    }

    // Elimina las llaves azules para ayudar a reiniciar correctamente la partida
    // Para ello se recorre el array, y si hay un bloque = 2, se transforma en 0
    private void quitarLlaves() {
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 2) {
                    laberinto[i][j] = 0;
                }
            }
        }
        repaint();
    }

    // Detecta cuando el personaje esté dentro de una llave
    private void recogerLlave() {

        // Si el jugador se encuentra en la misma casilla que una llave, la recoge
        if (laberinto[posX][posY] == 2) {
            // Esta condicion comprueba que si la llave se coge por primera vez muestre en pantalla un aviso de que debe coger todas las llaves para llegar a la meta
            // Solo funciona para el nivel 1
            if (puntuacion == 0 && nivelActual == 1) {
                // Llamar a metodo que genera el JPanel del tutorial
                tutorialLlavesPanel();
            }

            puntuacion += 1; // Aumentar la puntuacion por cada llave
            laberinto[posX][posY] = 0; // Restaurar la casilla a cesped, porque la llave ya se ha cogido
            // Se guarda en un archivo txt el numero de llaves automaticamente
            guardarLlaves();
            ReproductorSonidos.reproducirEfectoSonido("obtenermoneda1", 0.5f);
        }
    }

    // Muestra una imagen explicativa para que el usuario entienda que debe coger todas las llaves para llegar a la meta
    public void tutorialLlavesPanel() {
        // Crear un nuevo JLayeredPane para superponer componentes dentro de la pantalla de confirmacion de compra
        JLayeredPane layeredPaneTutorial = new JLayeredPane();
        layeredPaneTutorial.setPreferredSize(new Dimension(MenuLaberinto.frame.getWidth(), MenuLaberinto.frame.getHeight())); // Dimensiones del JFrame

        //              AGREGAR IMAGEN DE FONDO
        // Cargar la imagen de fondo en un label
        ImageIcon getFondo = new ImageIcon("recursos/textures/fondo_difuminado_nivel1.jpg");
        // Ajustar el tamaño de la imagen al tamaño del JLabel con un offset para adaptarlo bien a la ventana
        Image fondoSinEscalar = getFondo.getImage().getScaledInstance(MenuLaberinto.frame.getWidth() - 16, MenuLaberinto.frame.getHeight() - 80, Image.SCALE_SMOOTH);
        // Cambiar el formato de "Image" a "ImageIcon" para que sea compatible
        ImageIcon fondoEscalado = new ImageIcon(fondoSinEscalar);
        //Agregar imagen al label
        JLabel fondoLabel = new JLabel(fondoEscalado);
        // Agregar propiedades de posicion y tamaño al label
        fondoLabel.setBounds(0, 0, fondoEscalado.getIconWidth(), fondoEscalado.getIconHeight());

        //              AGREGAR IMAGEN Y MOUSEADAPTER DE CONFIRMAR 
        // Crear etiquetas para los botones de aceptar
        ImageIcon aceptarIcon = new ImageIcon("recursos/textures/tienda/ok_button.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image aceptarSinEscalar = aceptarIcon.getImage().getScaledInstance(220, 80, Image.SCALE_SMOOTH);
        ImageIcon aceptarEscalado = new ImageIcon(aceptarSinEscalar);

        JLabel aceptarLabel = new JLabel(aceptarEscalado);
        aceptarLabel.setBounds(500, 520, aceptarEscalado.getIconWidth(), aceptarEscalado.getIconHeight());
        aceptarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                layeredPaneTutorial.setVisible(false); // Dejar de hacer visible la pantalla de tutorial
                laberintoPanel.setVisible(true); // Volver a hacer visible la pantalla del laberinto
                requestFocus();
            }
        });

        // Añadir las etiquetas al JLayeredPane
        layeredPaneTutorial.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPaneTutorial.add(aceptarLabel, JLayeredPane.POPUP_LAYER);

        laberintoPanel.setVisible(false); // Quitar visibilidad del layeredPanel de la tienda
        MenuLaberinto.frame.add(layeredPaneTutorial); // Agregar el layeredPane al JFrame para mostrar la pantalla de confirmacion
    }

    // Si en todo el array no hay ninguna llave, se desbloquea la meta
    private void abrirMeta() {
        boolean isLlaveEncontrada = false;

        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 2) {
                    isLlaveEncontrada = true; // Se encontró una llave
                    break;
                }
            }
        }

        // Si la llave no se ha encontrado despues de realizar todo el escaneo, la meta falsa y verdadera se abre
        if (!isLlaveEncontrada) {
            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    // Si encuentra la meta verdadera, abrirla
                    if (laberinto[i][j] == 4) {
                        laberinto[i][j] = 3;
                        // Agregar efecto de sonido de abrir la meta
                        ReproductorSonidos.reproducirEfectoSonido("abrirmeta1", 0.5f);
                        break;

                        // Si encuentra la meta falsa, abrirla
                    } else if (laberinto[i][j] == 9) {
                        laberinto[i][j] = 8;
                    }
                }
            }
        }
    }

    // Vuelve a bloquear la meta poniendola de color gris
    private void cerrarMeta() {
        // Recorro el array, y si hay alguna meta abierta lo sustituye por una cerrada
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 3) {
                    laberinto[i][j] = 4;
                    break;
                }
            }
        }
    }

    // Detecta cuando el personaje esté dentro de una casilla verde
    private void llegarAMeta() {
        repaint(); // Refresca la imagen justo antes de entrar a la meta

        // Si isNormalMode esta en true, entonces significa que estamos jugando un nivel principal, y por tanto, que se debe reiniciar automaticamente en el mismo nivel
        // Si esta en false, entonces refrescará el nivel que se está jugando
        if (VariablesGenerales.isNormalMode) {
            if (laberinto[posX][posY] == 3) {
                // Dar 10 llaves a cambio de llegar a la meta correcta
                puntuacion += 10;
                // Registrar los puntos en el txt
                guardarLlaves();
                JOptionPane.showMessageDialog(this, "¡Has salido del laberinto!\n+10 llaves por llegar a la meta \nLlaves totales: " + puntuacion);
                nivelActual += 1; // Aumento el nivel despues de llegar a la meta
                guardarNumeroNivelActual(); // Guardo en nivelActual.txt el valor del nuevo nivel
                cargarNivelPrincipal(); // Cargar el nivel con el numero del nivel actualizado
                // Reproducir sonido de entrar a la meta
                ReproductorSonidos.reproducirEfectoSonido("entrarmeta2", 0.8f);
                // Si la meta es falsa, restar numero de llaves, y en caso de quedarse sin llaves, reiniciar el nivel y poner puntuacion a 0
            } else if (laberinto[posX][posY] == 9) {
                // Restar puntuacion
                puntuacion -= 5;
                // Reproducir sonido de error
                ReproductorSonidos.reproducirEfectoSonido("denegado", 0.5f);
                // Si el jugador tiene menos de 0 llaves, reiniciar el nivel
                if (puntuacion < 0) {
                    JOptionPane.showMessageDialog(this, "¡Te has quedado sin llaves :(");
                    puntuacion = 0;
                    decidirCargarNivel();
                }
                // Aplicar los cambios de la puntuacion escribiendolo en el txt
                guardarLlaves();

            }
            // Si el usuario llega a la meta en un nivel personalizado, mostrar el mensaje de victoria y reiniciar la partida
        } else if (!VariablesGenerales.isNormalMode) {
            if (laberinto[posX][posY] == 3) {
                JOptionPane.showMessageDialog(this, "¡Has salido del laberinto!\nLlaves recogidas: " + puntuacion);
                reiniciarPartidaEditor();
            }
        }
    }

    // Escanea si el personaje ha llegado a la tienda y abre un JFrame correspondiente a la tienda si es asi
    private void entrarTienda() {
        repaint(); // Refrescar pantalla para que se vea al personaje encima de la tienda
        // Si el personaje se encuentra encima de una casilla de tienda y no hay ninguna tienda abierta ya
        if (laberinto[posX][posY] == 6 && VariablesGenerales.isTiendaAbierta == false) {
            // Mostrar dialogo de Si o No
            int opcion = JOptionPane.showConfirmDialog(this, "¿Quieres entrar en la tienda?", "Tienda", JOptionPane.YES_NO_OPTION);

            // Si el usuario pulsa el botón "si"
            if (opcion == JOptionPane.YES_OPTION) {

                // Actualizar estado de isTiendaAbierta para bloquear los keyListener
                VariablesGenerales.isTiendaAbierta = true;

                // Reproducir musica de tienda
                ReproductorSonidos.reproducirMusicaFondo("tiendaLoop", 0.5f);

                // Llamamiento a la nueva clase que abre la pantalla de la tienda
                tiendaFrame = new TiendaFrame();

                // Hacer visible el nuevo frame
                tiendaFrame.setVisible(true);
            }
        }
    }

    // Si la tienda está abierta, la cierra y refresca el boolean "isTiendaAbierta" a false
    private void cerrarTienda() {
        if (VariablesGenerales.isTiendaAbierta) {
            // Refrescar el estado de isTiendaAbierta a false para evitar bugs
            VariablesGenerales.isTiendaAbierta = false;
            // Detener musica de la tienda
            ReproductorSonidos.detenerMusicaFondo();
            // Cerrar la tienda en caso de que esté abierta
            tiendaFrame.dispose();
        }
    }

    /*
        METODOS GUARDAR, RECUPERAR, REINICIAR: realizan las funciones de los botones de abajo
     */
    // Guarda los datos de posicion y de nivel de la partida de ese momento.
    // IMPORTANTE: solo funciona con niveles oficiales
    private void guardarPartida() {
        try (FileWriter writer = new FileWriter("recursos/nivelesPrincipales/nivel_backup.txt")) {
            writer.write(posX + "\n");
            writer.write(posY + "\n");

            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    writer.write(laberinto[i][j] + " ");
                }
                writer.write("\n");
            }
            // Almacenar numero del nivel donde se hace la accion de guardar
            writer.write("\n" + nivelActual);
            // Almacenar numero del llaves disponibles en el momento que se hace la accion de guardar
            writer.write("\n" + puntuacion);

            // Agregar efecto de sonido de felicidad
            ReproductorSonidos.reproducirEfectoSonido("abrirmeta2", 0.5f);
            // Agregar dialogo informativo
            JOptionPane.showMessageDialog(this, "Partida guardada. Pulsa el botón 'recuperar' para volver al punto guardado");
        } catch (IOException ex) {
            // Si no se ha podido guardar el archivo, mandar mensaje de error
            // Agregar efecto de sonido de denegación
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "No se pudieron guardar los datos de guardado. Comprueba si hay espacio en memoria suficiente en tu equipo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        requestFocus();  // Solicitar el foco después de reiniciar la partida
    }

    // Recupera los datos de posicion y puntuacion de la partida guardada anteriormente por guardarPartida().
    // IMPORTANTE: solo funciona con niveles oficiales
    private void recuperarPartida() {
        try (Scanner scanner = new Scanner(new File("recursos/nivelesPrincipales/nivel_backup.txt"))) {
            posX = scanner.nextInt();
            posY = scanner.nextInt();

            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    laberinto[i][j] = scanner.nextInt();
                }
            }
            // Leer el nivel donde se guardó para que se reinicie ahi
            nivelActual = scanner.nextInt();
            // Leer el numero de llaves que había cuando se reinicio
            puntuacion = scanner.nextInt();
            guardarLlaves();
            direccionPersonaje = obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
            // Reproducir sonido de entrar a la partida
            ReproductorSonidos.reproducirEfectoSonido("entrarpartida", 0.5f);
        } catch (FileNotFoundException ex) {
            // Si no se ha encontrado el archivo de guardado, mandar mensaje de error correspondiente
            // Agregar efecto de sonido de denegación
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "El archivo de guardado no ha sido encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            // Si no se reconoce el archivo a cargar, mandar mensaje de error correspondiente
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "El archivo de guardado no ha sido reconocido.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        requestFocus();  // Solicitar el foco después de reiniciar la partida
        repaint();
    }

    // Si isNormalMode==false, entonces se reinicia el nivel bajo las condiciones del nivel editado
    // La diferencia con reiniciarPartidaNormal() es que aqui se vuelve a leer el archivo del nivel elegido por ultima vez 
    private void reiniciarPartidaEditor() {
        boolean inicioColocado;
        boolean metaColocada;

        try (BufferedReader reader = new BufferedReader(new FileReader(VariablesGenerales.nivelEditorSeleccionado))) {
            // Leemos las variables posX, posY y puntuacion
            posX = Integer.parseInt(reader.readLine());
            posY = Integer.parseInt(reader.readLine());

            // Leemos la matriz
            for (int i = 0; i < laberinto.length; i++) {
                String[] valores = reader.readLine().trim().split("\\s+");
                for (int j = 0; j < laberinto[i].length; j++) {
                    laberinto[i][j] = Integer.parseInt(valores[j]);
                }
            }

            // Leemos los booleanos inicioColocado y metaColocada
            inicioColocado = Boolean.parseBoolean(reader.readLine());
            metaColocada = Boolean.parseBoolean(reader.readLine());

            direccionPersonaje = obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
            abrirMeta(); // Escanear si el nivel tiene alguna llave para abrir la meta en caso de que no

        } catch (IOException | NumberFormatException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "Error al reiniciar la partida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        requestFocus();
        repaint();
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
        ImageIcon sueloIcon = new ImageIcon(obtenerGraficos() + "/suelo.jpg");
        Image sueloImage = sueloIcon.getImage();

        // Cargar textura pared
        ImageIcon paredIcon = new ImageIcon(obtenerGraficos() + "/pared.jpg");
        Image paredImage = paredIcon.getImage();

        // Cargar textura llave
        ImageIcon llaveIcon = new ImageIcon(obtenerGraficos() + "/llave.jpg");
        Image llaveImage = llaveIcon.getImage();

        // Cargar textura inicio
        ImageIcon inicioIcon = new ImageIcon(obtenerGraficos() + "/inicio.jpg");
        Image inicioImage = inicioIcon.getImage();

        // Cargar textura meta cerrada
        ImageIcon metaCloseIcon = new ImageIcon(obtenerGraficos() + "/metaCerrada.jpg");
        Image metaCerradaImage = metaCloseIcon.getImage();

        // Cargar textura meta abierta
        ImageIcon metaOpenIcon = new ImageIcon(obtenerGraficos() + "/metaAbierta.jpg");
        Image metaAbiertaImage = metaOpenIcon.getImage();

        // Cargar textura tienda
        ImageIcon tiendaIcon = new ImageIcon(obtenerGraficos() + "/tienda.jpg");
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
                        if ("recursos/textures/graficos/fiesta/".equals(obtenerGraficos())) {

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

        //          DIBUJAR SKIN ELEGIDA
        ImageIcon personajeIcon = new ImageIcon(direccionPersonaje);
        Image iconoPersonaje = personajeIcon.getImage();
        g.drawImage(iconoPersonaje, posY * dimension + 5, posX * dimension + 5, 30, 30, null);

        //          COLOCAR CONTADOR LLAVES
        // Colocar icono de llave
        g.drawImage(llaveContadorImage, 2, 560, 37, 37, null);

        //
        /* Se colocan 2 textos iguales:
           1. En color blanco
           2. En color negro un poco desplazado para dar un efecto de sombra
         */
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40)); // Establecer la fuente y el tamaño del texto
        g.drawString(puntuacion + "", 43, 595);

        // Colocar contador de llaves
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40)); // Establecer la fuente y el tamaño del texto
        g.drawString(puntuacion + "", 41, 593);
    }

    // Lector de teclas
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Si la tienda no está abierta, SOLAMENTE cuando no esta abierta, habilitar las teclas de movimiento
        if (key == KeyEvent.VK_LEFT && posY > 0 && laberinto[posX][posY - 1] != 1) {
            posY--;
            direccionPersonaje = obtenerSkin() + "_izquierda.png"; // Cambia string de la direccion de la imagen del personaje

        } else if (key == KeyEvent.VK_RIGHT && posY < laberinto[0].length - 1 && laberinto[posX][posY + 1] != 1) {
            posY++;
            direccionPersonaje = obtenerSkin() + "_derecha.png";
        } else if (key == KeyEvent.VK_UP && posX > 0 && laberinto[posX - 1][posY] != 1) {
            posX--;
            direccionPersonaje = obtenerSkin() + "_arriba.png";
        } else if (key == KeyEvent.VK_DOWN && posX < laberinto.length - 1 && laberinto[posX + 1][posY] != 1) {
            posX++;
            direccionPersonaje = obtenerSkin() + "_abajo.png";
            // Si el juego está puesto en modo normal y el usuario pulsa la tecla "r" encima de una casilla de inicio, vuelve al anterior nivel
        } else if (key == KeyEvent.VK_R && nivelActual > 1 && VariablesGenerales.isNormalMode && laberinto[posX][posY] == 5) {
            nivelActual -= 1; // Dismunuyo el valor para retroceder de nivel
            guardarNumeroNivelActual(); // Guardo en nivelActual.txt el valor del nuevo nivel
            cargarNivelPrincipal(); // Cargo el nuevo nivel
            quitarLlaves();
        }

        // Tras cada pulsacion, se llamaran a los siguientes metodos:
        recogerLlave(); // Escanea si la posicion actual del personaje es igual a una llave, y la recoge en caso de que si
        llegarAMeta(); // Escanea si la posicion actual del personaje es igual a la posicino de la meta
        cargarLlaves();
        cerrarTienda(); // Cerrar tienda en caso de que esté abierta
        entrarTienda(); // Escanea si el personaje ha llegado a la tienda
        abrirMeta(); // Escanea si todas las llaves del nivel estan cogidas para abrir la meta en ese caso
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /*
            SECCION CARGA-NIVELES: carga los niveles segun corresopnda
     */
    // Lee el archivo txt correspondiente al nivel por defecto y lo muestra por pantalla
    private void cargarNivelPrincipal() {
        try (Scanner scanner = new Scanner(new File("recursos/nivelesPrincipales/" + nivelActual + ".txt"))) { // cargar numero del nivel guardado en nivelActual.txt
            System.out.println("recursos/nivelesPrincipales/" + nivelActual + ".txt");
            posX = scanner.nextInt(); // fila
            posY = scanner.nextInt(); // columna

            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    laberinto[i][j] = scanner.nextInt();
                }
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "No se ha encontrado el archivo del nivel a cargar o no tiene el formato adecuado.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // En este if decido qué niveles deben tener llaves aleatorias
        if (nivelActual == 1 || nivelActual == 2 || nivelActual == 3) {
            quitarLlaves(); // Quita los azules para limpiar bien el mapa
            colocarLlaves(); // Coloca aleatoriamente 6 bloques azules distribuidos por el mapa
        }

        direccionPersonaje = obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
        cerrarMeta(); // Vuelve a bloquear la meta poniendola de color gris
        requestFocus();  // Solicitar el foco para que se lean bien los keyListener
        repaint(); // Refrescar pantalla
    }

    /* Despues de haber seleccionado el archivo desde seleccionarNivelPersonalizado() en ModoEditor.java
    se lee el archivo seleccionado y se carga */
    private void cargarNivelPersonalizado() {

        boolean inicioColocado;
        boolean metaColocada;

        try (BufferedReader reader = new BufferedReader(new FileReader(VariablesGenerales.nivelEditorSeleccionado))) {
            // Leemos las variables posX, posY y puntuacion
            posX = Integer.parseInt(reader.readLine()); // Fila
            posY = Integer.parseInt(reader.readLine()); // Columna

            // Leemos la matriz
            for (int i = 0; i < laberinto.length; i++) {
                String[] valores = reader.readLine().trim().split("\\s+");
                for (int j = 0; j < laberinto[i].length; j++) {
                    laberinto[i][j] = Integer.parseInt(valores[j]);
                }
            }

            // Leemos los booleanos inicioColocado y metaColocada
            inicioColocado = Boolean.parseBoolean(reader.readLine());
            metaColocada = Boolean.parseBoolean(reader.readLine());

            direccionPersonaje = obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
            requestFocus();  // Solicitar el foco para que se lean bien los keyListener
            repaint(); // Refrescar pantalla

        } catch (IOException | NumberFormatException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "El archivo no tiene el formato adecuado. Asegúrate de seleccionar un nivel válido con extensión .txt.", "Error", JOptionPane.ERROR_MESSAGE);
            // Limpiar el panel de botones en el laberinto
            // Esto soluciona el bug de que al salir y entrar varias veces se acumulen los botones
            removeAll();

            // Hacer visible el frame principal
            MenuLaberinto.panelLayered.setVisible(true);
            // Eliminar la visibilidad a los paneles que conforman la pantalla de juego del laberinto
            setVisible(false);
            buttonsPanel.setVisible(false);

            // Agregar al frame el menú principal
            MenuLaberinto.frame.add(MenuLaberinto.panelLayered);
            // Repintar el contenedor principal para actualizar los cambios
            repaint();
        }
    }

    // Método que segun el estado de isNormalMode llamará al método de cargar nivel principal, o cargar nivel editado
    // Este método es llamado desde el constructor y permite al programa diferenciar qué debe tipo de nivel debe cargar
    private void decidirCargarNivel() {
        if (!VariablesGenerales.isNormalMode) {
            cargarNivelPersonalizado();
        } else {
            cargarNivelPrincipal();
        }
    }

    /*
            CREADOR DE VENTANAS: crea la ventana y personaliza los botones
     */
    // Metodo donde se crea y se configura el panel principal, panel de los botones y botones.
    private void creaVentanaLaberinto() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Establece el aspecto "nimbus" para obtener un estilo más moderno
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            MenuLaberinto.frame.add(laberintoPanel); // Agregar el panel del laberinto al frame principal
            requestFocus(); // Solicitar foco justo despues de agregar el panel al frame para que se lea correctamente el keyListener

            buttonsPanel = new JPanel();

            // Establecer el color de fondo
            buttonsPanel.setBackground(Color.WHITE); // Puedes usar cualquier color que desees

            // Crear JLabels con imágenes
            JLabel guardarPartidaButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/guardar_button.png"));
            JLabel recuperarPartidaButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/recuperar_button.png"));
            JLabel reiniciarPartidaButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/reiniciar_button.png"));
            JLabel volverEditorButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/volverEditor_button.png"));
            JLabel volverMenuButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/salirMenu_button.png"));

            //       ZONA MOUSE LISTENERS
            guardarPartidaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    guardarPartida();
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                }
            });

            recuperarPartidaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Llamar al método que se encarga de cerrar la tienda por si ha quedado abierta
                    cerrarTienda();
                    recuperarPartida();
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                }
            });

            reiniciarPartidaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Llamar al método que se encarga de cerrar la tienda por si ha quedado abierta
                    cerrarTienda();
                    if (VariablesGenerales.isNormalMode) {
                        cargarNivelPrincipal();
                    } else {
                        reiniciarPartidaEditor();
                    }
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                }
            });

            volverEditorButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Llamar al método que se encarga de cerrar la tienda por si ha quedado abierta
                    cerrarTienda();

                    // Eliminar la visibilidad a los paneles que conforman la pantalla de juego del laberinto
                    laberintoPanel.setVisible(false);
                    buttonsPanel.setVisible(false);

                    // Limpiar el panel de botones en el laberinto
                    // Esto soluciona el bug de que al salir y entrar varias veces se acumulen los botones
                    removeAll();
                    buttonsPanel.removeAll();

                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                    // Crear instancia del modo editor y añadrilo al frame principal
                    ModoEditor modoEditor = new ModoEditor(new int[15][31]);
                    MenuLaberinto.frame.add(modoEditor);
                }
            });

            volverMenuButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Llamar al método que se encarga de cerrar la tienda por si ha quedado abierta
                    cerrarTienda();

                    // Eliminar la visibilidad a los paneles que conforman la pantalla de juego del laberinto
                    laberintoPanel.setVisible(false);
                    buttonsPanel.setVisible(false);

                    // Limpiar el panel de botones en el laberinto
                    // Esto soluciona el bug de que al salir y entrar varias veces se acumulen los botones
                    removeAll();
                    buttonsPanel.removeAll();

                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                    // Crear instancia de menu principal y hacerla visible
                    MenuLaberinto.panelLayered.setVisible(true);
                    MenuLaberinto.panelLayered.removeAll(); // Quitar elementos del menu para que no se acumulen al volver a cargarlos
                    MenuLaberinto menu = new MenuLaberinto();
                }
            });

            //              Agregar los JLabels al panel buttonsPanel
            buttonsPanel.add(reiniciarPartidaButton);
            // Si estamos jugando un nivel editado se pondrá el boton "volver al menú"
            // Si estamos jugando un nivel oficial se pondrán los botones guardar partida y recuperar partida
            if (VariablesGenerales.isNormalMode == false) {
                buttonsPanel.add(volverEditorButton);
            } else {
                buttonsPanel.add(guardarPartidaButton);
                buttonsPanel.add(recuperarPartidaButton);
            }
            buttonsPanel.add(volverMenuButton);

            // Agregar el panel buttonsPanel al frame en la parte sur
            MenuLaberinto.frame.add(buttonsPanel, BorderLayout.SOUTH);
            buttonsPanel.setVisible(true);
            MenuLaberinto.frame.setVisible(true);
        });
    }

    /*
            GUARDAR DATOS: Métodos relacionados con la lectura y escritura de datos en ficheros externos txt
     */
    // Se encarga de guardar el numreo de llaves dentro de datosJugador.txt para que se conserve la puntuacion del jugador
    private void guardarLlaves() {
        try (FileWriter writer = new FileWriter("recursos/datosJugador/llavesJugador.txt")) {
            writer.write(puntuacion + "\n");
        } catch (IOException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha podido registrar la llave.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Se encarga de cargar el numero de llaves dentro de datosJugador.txt para que cuando se abra el juego se abra con el numero de llaves que tenia
    private void cargarLlaves() {
        try (Scanner scanner = new Scanner(new File("recursos/datosJugador/llavesJugador.txt"))) {
            puntuacion = scanner.nextInt();
        } catch (FileNotFoundException | NumberFormatException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha encontrado la ruta donde se guardan las llaves", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para leer la dirección de la skin elegida desde el archivo txt
    public static String obtenerSkin() {
        String rutaImagenPersonaje = "";
        try (Scanner scanner = new Scanner(new File("recursos/datosJugador/skinActual.txt"))) {
            rutaImagenPersonaje = scanner.nextLine();
        } catch (FileNotFoundException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha podido cargar la skin.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return rutaImagenPersonaje;
    }

    // Método para leer la dirección de los graficos elegidos desde la tienda
    public static String obtenerGraficos() {
        String graficos = null;
        try (Scanner scanner = new Scanner(new File("recursos/datosJugador/graficosActuales.txt"))) {
            graficos = scanner.nextLine();
        } catch (FileNotFoundException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha podido cargar los graficos", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return graficos;
    }

    // Metodo para leer el numero del nivel que aparece en nivelActual.txt
    private void obtenerNumeroNivelActual() {
        try (BufferedReader reader = new BufferedReader(new FileReader("recursos/datosJugador/nivelActual.txt"))) {
            //String linea = reader.readLine(); // Leer la primera línea del archivo
            nivelActual = Integer.parseInt(reader.readLine()); // Leer la primera línea del archivo y guardarlo en la variable nivelActual
        } catch (IOException | NumberFormatException ex) {
            // Si no se puede obtener el numero del nivel, cargar el nivel 1
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha encontrar el nivel actual. Te mandaremos al nivel 1", "Error", JOptionPane.ERROR_MESSAGE);
            nivelActual = 1;
            guardarNumeroNivelActual();
            cargarNivelPrincipal();
            creaVentanaLaberinto();
            repaint();
        }
    }

    // Método para escribir el numero del nivel actual del usuario en nivelActual.txt
    private void guardarNumeroNivelActual() {
        try (FileWriter writer = new FileWriter("recursos/datosJugador/nivelActual.txt")) {

            // Guarda la vairable nivelActual en el txt
            writer.write(nivelActual + "\n");

        } catch (IOException e) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha podido guardar el numero del nivel", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
