/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jorge
 */
// Esta clase se especializa en poner los dialogos pre-partida
// Enseña a modo de tutorial cómo funciona el modo fiesta con pequeñas diapositivas interactivas
public class ModoFiestaTutorial {

    // JLayeredPane para superponer componentes
    private JLayeredPane panelLayeredFiesta = new JLayeredPane();

    // Crear un panel principal con BorderLayout
    private Container panelFiesta = new JPanel(new BorderLayout());

    // Campo de clase para almacenar el índice del diálogo actual
    private int indiceDialogoActual = 1;

    // Campo de clase para almacenar el número de dialogos totales que habrá en el tutorial
    // Sirve para que en pasarDialogo() genere un boton de jugar cuando se haya llegado al numero de dialogos totales
    private final int numeroDialogosTotales = 8;

    // Esta variable se encarga de construir la direccion de la imagen del botón correspondiente en cada momento
    // Se utiliza en creaBotonSiguiente()
    // Esta variable cambiará cuando toque mostrar una imagen distinta al botón siguiente
    private String direccionBoton = "siguiente";

    // Declarar matriz donde se almacenaran los nombres de los niveles a escoger aleatoriamente
    // Uno de los niveles de esta matriz serán escogidos aleatoriamente desde el método seleccionarNivelAlatorio() que se encuentra en ModoFiestaJuego.java
    // La variable se declara publica y estática para que sea facilmente accesible por dicha clase
    public static File[] nivelesAElegir;

    // Constructor: pone el dialogo incial, configura el panel principal, y crea el boton de "siguiente"
    // El cosntructor es llamado tras pulsar en el menú principal el botón "fiesta"
    public ModoFiestaTutorial() {
        aplicarDialogoActual();
        configuraPanel();
        creaBotonTutorial();
        ReproductorSonidos.reproducirMusicaFondo("fiestatutorialLoop", 0.5f);
    }

    // Agrega el panel al frame principal
    private void configuraPanel() {

        // Agregar el panel principal en la capa predeterminada
        panelLayeredFiesta.add(panelFiesta, JLayeredPane.DEFAULT_LAYER);

        // Agregar el JLayeredPane al JFrame
        MenuLaberinto.frame.add(panelLayeredFiesta);
    }

    // Crea el botón siguiente que sirve para pasar los dialogos
    private void creaBotonTutorial() {
        //              AGREGAR IMAGEN Y MOUSEADAPTER DEL BOTÓN SIGUIENTE
        // Crear etiqueta para el boton
        ImageIcon siguienteIcon = new ImageIcon("recursos/textures/fiesta/" + direccionBoton + "_button_fiesta.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image siguienteSinEscalar = siguienteIcon.getImage().getScaledInstance(380, 130, Image.SCALE_SMOOTH);
        ImageIcon siguienteEscalado = new ImageIcon(siguienteSinEscalar);

        JLabel siguienteLabel = new JLabel(siguienteEscalado);
        siguienteLabel.setBounds(460, 490, siguienteEscalado.getIconWidth(), siguienteEscalado.getIconHeight());
        // Agregar moyse adapter a la imagen
        siguienteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Si el boton es "siguiente", pasar el dialogo
                // Si el boton NO es "siguiente", empezar a jugar
                if ("siguiente".equals(direccionBoton)) {
                    // Reproducir el efecto de sonido
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                    pasarDialogo();
                } else {
                    seleccionarCarpetaNivelesEditados();
                }
            }
        });
        // Agregar el boton al panel principal
        panelLayeredFiesta.add(siguienteLabel, JLayeredPane.POPUP_LAYER);
    }

    // Se encarga de poenr el boton salir (x) en la esquina superior derecha, cuya función es volver al menú
    // Se llama cuando se pasa a la última diapositiva
    public void creaBotonSalir() {
        //              AGREGAR IMAGEN Y MOUSEADAPTER DEL BOTÓN X
        // Crear etiquetas para el botón
        ImageIcon salirIcon = new ImageIcon("recursos/textures/fiesta/cerrar_button_fiesta.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image salirSinEscalar = salirIcon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        ImageIcon salirEscalado = new ImageIcon(salirSinEscalar);

        JLabel salirLabel = new JLabel(salirEscalado);
        salirLabel.setBounds(1160, 20, salirEscalado.getIconWidth(), salirEscalado.getIconHeight());
        // Agregar moyse adapter a la imagen
        salirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Detener musica
                ReproductorSonidos.detenerMusicaFondo();
                // Reproducir sonido
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                // Limpiar el panel
                panelLayeredFiesta.removeAll();
                panelLayeredFiesta.setVisible(false);
                MenuLaberinto.panelLayered.setVisible(true);
            }
        });
        // Agregar el boton al panel principal
        panelLayeredFiesta.add(salirLabel, JLayeredPane.POPUP_LAYER);
    }

    // Se encarga de poenr el boton saltar tutorial
    // Se llama cuando se pasa a la segunda diapositiva diapositiva
    private void creaBotonSaltarTutorial() {
        //              AGREGAR IMAGEN Y MOUSEADAPTER DEL BOTÓN X
        // Crear etiquetas para el botón
        ImageIcon salirIcon = new ImageIcon("recursos/textures/fiesta/saltar_button_fiesta.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image salirSinEscalar = salirIcon.getImage().getScaledInstance(300, 130, Image.SCALE_SMOOTH);
        ImageIcon salirEscalado = new ImageIcon(salirSinEscalar);

        JLabel salirLabel = new JLabel(salirEscalado);
        salirLabel.setBounds(80, 490, salirEscalado.getIconWidth(), salirEscalado.getIconHeight());
        // Agregar moyse adapter a la imagen
        salirLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Reproducir sonido
                ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                // Pasar a la última diapositiva
                indiceDialogoActual = 7;
                pasarDialogo();
            }
        });
        // Agregar el boton al panel principal
        panelLayeredFiesta.add(salirLabel, JLayeredPane.POPUP_LAYER);
    }

    // Se encarga de poner el dialogo que corresponde segun el indice del dialogo actual
    // Este método es llamado por pasarDialogo() y por el constructor
    private void aplicarDialogoActual() {
        try {
            // Construir la direccion y cargar la imagen
            BufferedImage image = ImageIO.read(new File("recursos/textures/fiesta/dialogoTutorial/dialogo" + indiceDialogoActual + ".jpg"));
            JLabel background = new JLabel(new ImageIcon(image));

            // Meter propiedades a la imagen de fondo
            background.setBounds(0, 0, background.getPreferredSize().width, background.getPreferredSize().height);

            // Agregar la imagen de fondo en la capa predeterminada
            panelLayeredFiesta.add(background, JLayeredPane.DEFAULT_LAYER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gestiona los cambios que sufre el programa tras pulsar el boton "siguiente"
    // Entre estos cambios se encuentra refrescar la avriable indiceDialogoActual, cambiar la imagen del dialogo
    private void pasarDialogo() {
        // Aumentar el índice del diálogo
        indiceDialogoActual++;

        // Limpiar el panel para quitar el dialogo que se ha pasado
        panelLayeredFiesta.removeAll();

        // Llamar al método que pone el dialogo que corresopnde con el indice del dialogo actual
        aplicarDialogoActual();

        // Este condicional se encarga de poner el boton que corresponda en cada dialogo
        /* Si el indiceDialogoActual es igual a numeroDialogosTotales, quiere decir que el programa se encuentra en el ultimo dialogo
           y por tanto se creará el botón de jugar. Mientras no sea el ultimo dialogo, se crea el botón "siguiente".
         */
        if (indiceDialogoActual == numeroDialogosTotales) {
            // Cambio el string para que al llamar a creaBotonSiguiente() se llame a la imagen de "juagr" en lugar de "siguiente"
            direccionBoton = "jugar";
            creaBotonSalir();
        } else if (indiceDialogoActual == 2){
            creaBotonSaltarTutorial();
        }
        // Llamar al método que genera el botón otra vez
        creaBotonTutorial();
        // Refrescar el aspecto visual del panel principal
        panelLayeredFiesta.repaint();
    }

    // Este método abre un explorador de archivos el cual puede seleccionar unicamente directorios
    // Una vez seleccionado el directorio, lee los archivos que contiene el directorio y elige uno aleatoriamente
    // Se utiliza para cargar niveles aleatoriamente para el modo fiesta
    public void seleccionarCarpetaNivelesEditados() {
        // Crear un cuadro de diálogo para seleccionar una carpeta
        JFileChooser explorador = new JFileChooser();
        // Establecer el directorio inicial
        explorador.setCurrentDirectory(new File("recursos/levelsEditor"));
        explorador.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        explorador.setDialogTitle("Selecciona una carpeta");

        // Mostrar el cuadro de diálogo y esperar a que el usuario seleccione una carpeta
        int resultado = explorador.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtener la carpeta seleccionada por el usuario
            File carpetaSeleccionada = explorador.getSelectedFile();

            // Listar los archivos dentro de la carpeta seleccionada
            nivelesAElegir = carpetaSeleccionada.listFiles();
            
            ReproductorSonidos.detenerMusicaFondo();

            // Verificar si la carpeta no está vacía y si contiene archivos
            if (nivelesAElegir != null && nivelesAElegir.length > 0 && contieneSoloArchivosTxt(nivelesAElegir)) {
                // Crear una instancia de la clase para llamar al método y que empiece la fiesta
                ModoFiestaJuego modoFiestaJuego = new ModoFiestaJuego();

                // Llamar al método que selecciona aleatoriamente uno de los niveles del directorio elegido por el usuario
                // Este método se encuentra en ModoFiestaJuego.java, por tanto el resto de la gestion de la aleatoriedad se realiza en esa clase
                modoFiestaJuego.seleccionarNivelAlatorio();

                // Eliminar visibilidad y limpiar el panel del tutorial del modo fiesta
                panelLayeredFiesta.removeAll();
                panelLayeredFiesta.setVisible(false);

            } else {
                // Si la carpeta no es válida, emitir el sonido pertinente y avisar al usuario por ventana emergente
                ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                JOptionPane.showMessageDialog(null, "La carpeta seleccionada contiene archivos que no corresponde a los niveles o está vacía", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("No se seleccionó ninguna carpeta.");
        }
    }
    
    // Este método comprueba si la carpeta de los niveles txt elegida por el usuario, solamente copntiene archivos txt
    // Devuelve un boolean, true si encontró archivos txt o false si no los encontró
    // Este método forma parte de la gestion de errores de la seleccion de archivos en el modo fiesta
    private static boolean contieneSoloArchivosTxt(File[] archivos) {
        // Si el archivo no contiene nada, devolver false
        if (archivos == null || archivos.length == 0) {
            return false;
        }
        for (File archivo : archivos) {
            if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                return false;
            }
        }
        return true;
    }
}
