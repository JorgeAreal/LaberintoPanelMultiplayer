/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author Jorge Areal Alberich
 */
// Esta clase se especializa en poner los dialogos post-partida
// Funciona parecido a ModoFiestaTutorial pero con algunas variaciones para adaptarse mejor a la tarea de mostrar las estadísticas
public class ModoFiestaEstadisticas {

    // JLayeredPane para superponer componentes
    private JLayeredPane panelLayeredFiestaEstadisticas = new JLayeredPane();

    // Crear un panel principal con BorderLayout
    private Container panelFiestaEstadisticas = new JPanel(new BorderLayout());

    // Campo de clase para almacenar el índice del diálogo actual
    private int indiceDialogoActual = 1;

    // Constructor
    public ModoFiestaEstadisticas() {
        aplicarDialogoActual();
        configuraPanel();
        // Reproducir sonido de estadisticas
        ReproductorSonidos.reproducirEfectoSonido("mostrarestadisticas", 0.5f);
    }

    // Agrega el panel al frame principal
    private void configuraPanel() {
        // Agregar el panel principal en la capa predeterminada
        panelLayeredFiestaEstadisticas.add(panelFiestaEstadisticas, JLayeredPane.DEFAULT_LAYER);

        // Agregar el JLayeredPane al JFrame
        MenuLaberinto.frame.add(panelLayeredFiestaEstadisticas);
    }

    // Cosntruye la ruta de la imagen del dialogo que se debe aplicar en su debido momento
    // y lo aoplica al panel general
    private void aplicarDialogoActual() {
        try {
            // Construir la direccion y cargar la imagen
            BufferedImage image = ImageIO.read(new File("recursos/textures/fiesta/dialogoEstadisticas/pantalla_estadisticas" + indiceDialogoActual + ".jpg"));
            JLabel background = new JLabel(new ImageIcon(image));

            // Meter propiedades a la imagen de fondo
            background.setBounds(0, 0, background.getPreferredSize().width, background.getPreferredSize().height);

            // Esta cadena if-else crea los labels corespondientes para cada indice de dialogo
            // Para ello, llama a los metodos encargados de construir los labels, y se manda la informacion de posicion mediante parametros
            if (indiceDialogoActual == 1) {
                // Llamar a crearJLabel para crear el letrero con el numero de monedas cogidas en el modo fiesta
                // Creo 2 labels para el mismo contador: uno de color blanco en primer plano, y otro color negro para hacer un efecto de sombra
                JLabel contadorMonedas = crearJLabel(ModoFiestaJuego.monedas + "", 615, 300, "Arial", Font.PLAIN, 165, Color.WHITE);
                JLabel contadorMonedasSombra = crearJLabel(ModoFiestaJuego.monedas + "", 619, 304, "Arial", Font.PLAIN, 165, Color.BLACK);

                // Añadir los JLabel al panel
                panelLayeredFiestaEstadisticas.add(contadorMonedas);
                panelLayeredFiestaEstadisticas.add(contadorMonedasSombra);

                // Llamar al creador de botones para que ponga el boton "siguiente" con sus datos de posicion
                creaBotones(470, 500, "siguiente");
            } else if (indiceDialogoActual == 2) {
                // Crear 2 botones: Un boton de "si" y un boton de "no" con sus parametros de posicion y nombre de la imagen
                creaBotones(325, 430, "si");
                creaBotones(620, 430, "no");
                // La logica del mouse Adapter se encuentra dentro del metodo crearBotones por motivos de legibilidad del codigo

            } else if (indiceDialogoActual == 3) {
                // Colocar el boton siguiente para el dialogo de "exito con la conexion de base de datos"
                creaBotones(470, 500, "siguiente");

            } else if (indiceDialogoActual == 4) {

                JLabel contadorMonedas = crearJLabel(ConexionBaseDatos.obtenerMonedas() + "", 615, 310, "Arial", Font.PLAIN, 130, Color.WHITE);
                JLabel contadorMonedasSombra = crearJLabel(ConexionBaseDatos.obtenerMonedas() + "", 619, 314, "Arial", Font.PLAIN, 130, Color.BLACK);

                creaBotones(470, 510, "siguiente");

                // Añadir los JLabel al panel
                panelLayeredFiestaEstadisticas.add(contadorMonedas);
                panelLayeredFiestaEstadisticas.add(contadorMonedasSombra);

            } else if (indiceDialogoActual == 5) {
                // Crear 2 botones tal y como se hizo anteriormente
                creaBotones(270, 420, "volverJugar");
                creaBotones(680, 420, "salir");
            } else if (indiceDialogoActual == 10) {
                // Crear 2 botones tal y como se hizo anteriormente
                creaBotones(675, 490, "noconectar");
                creaBotones(265, 490, "reintentar");

                // Reiniciar el contador de las monedas del modo fiesta a 0
                ModoFiestaJuego.monedas = 0;
            }

            // Agregar la imagen de fondo en la capa predeterminada
            panelLayeredFiestaEstadisticas.add(background, JLayeredPane.DEFAULT_LAYER);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crea un boton a partir de unos parametros de posicion y direccion de imagen
    // Es llamado por aplicarDialogoActual()
    private void creaBotones(int x, int y, String nombreFoto) {
        //              AGREGAR IMAGEN Y MOUSEADAPTER DEL BOTÓN
        // Crear etiqueta para el boton
        ImageIcon siguienteIcon = new ImageIcon("recursos/textures/fiesta/" + nombreFoto + "_button_fiesta.png");
        // Ajustar el tamaño de la imagen al tamaño del JLabel
        Image siguienteSinEscalar = siguienteIcon.getImage().getScaledInstance(360, 120, Image.SCALE_SMOOTH);
        ImageIcon siguienteEscalado = new ImageIcon(siguienteSinEscalar);

        JLabel siguienteLabel = new JLabel(siguienteEscalado);
        siguienteLabel.setBounds(x, y, siguienteEscalado.getIconWidth(), siguienteEscalado.getIconHeight());
        // Agregar moyse adapter a la imagen
        siguienteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Esta cadena de if else genera una funcion distinta para cada boton segun el tipo de boton que se ha creado
                // Se reconoce el tipo de boton gracias a la cadena string "nombreFoto"
                if ("siguiente".equals(nombreFoto)) {
                    // Aumentar el índice del diálogo
                    indiceDialogoActual++;
                    refrescarDialogo();
                    // Agregar efecto de sonido de boton
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                } else if ("no".equals(nombreFoto) || "noconectar".equals(nombreFoto)) {
                    // Si el usuario pulsa "no", entonces se suman 3 al índice para saltarse las pantallas relacionadas con la BBDD
                    indiceDialogoActual = 5;
                    refrescarDialogo();
                    // Agregar efecto de sonido de boton
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                } else if ("si".equals(nombreFoto) || "reintentar".equals(nombreFoto)) {
                    
                    // Agregar efecto de sonido de boton
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                    // Intentar la conexion a la base de datos
                    ConexionBaseDatos.crearBaseYTabla();

                    // Si la conexion es exitosa, entonces actualizar las monedas de la BBDD y mostrar el dialogo de que se pudo conectar correctamente
                    if (ConexionBaseDatos.testearConexionBBDD()) {
                        ConexionBaseDatos.actualizarMonedas(ModoFiestaJuego.monedas, "sumar");
                        indiceDialogoActual = 3;
                        refrescarDialogo();
                        // Agregar efecto de sonido de felicidad
                        ReproductorSonidos.reproducirEfectoSonido("abrirmeta2", 0.5f);
                        // Si no, mostrar pantalla de error
                    } else {
                        // Refresco la variable a 10 para que la pantalla de error no interfiera con los dialogos normales
                        // El numero 10 para arriba está reservado para errores exclusivamente
                        indiceDialogoActual = 10;
                        refrescarDialogo();
                        // Agregar efecto de sonido de denegación
                        ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                    }

                } else if ("volverJugar".equals(nombreFoto)) {
                    // Limpiar todo el panel y quitarle visibilidad
                    panelLayeredFiestaEstadisticas.removeAll();
                    panelLayeredFiestaEstadisticas.setVisible(false);

                    // Agregar efecto de sonido de boton
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                    ModoFiestaJuego.monedas = 0;
                    // Llamar al constructor de ModoEditor.java y que vuelva a empezar la fiesta
                    ModoFiestaTutorial fiesta = new ModoFiestaTutorial();

                    // Llamar a la funcion de buscar el directorio de los niveles
                    fiesta.seleccionarCarpetaNivelesEditados();
                } else if ("salir".equals(nombreFoto)) {
                    // Limpiar todo el panel del modo fiesta y hacer visible el menu principal
                    panelLayeredFiestaEstadisticas.removeAll();
                    panelLayeredFiestaEstadisticas.setVisible(false);
                    
                    // Reiniciar el contador de monedas a 0
                    ModoFiestaJuego.monedas = 0;

                    // Agregar efecto de sonido de boton
                    ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);

                    MenuLaberinto.panelLayered.setVisible(true);
                }
            }
        });
        // Agregar el boton al panel principal
        panelLayeredFiestaEstadisticas.add(siguienteLabel, JLayeredPane.POPUP_LAYER);
    }

    // Crea un JLabel personalizado con texto, ubicación, fuente, tamaño de fuente y color
    // Esto permite crear un label mucho mas rapido y sin tener que configurarlo, pues viene configurado de serie en este método
    private JLabel crearJLabel(String texto, int x, int y, String nombreFuente, int estiloFuente, int tamanoFuente, Color color) {
        JLabel label = new JLabel(texto); // Creamos el JLabel con el texto dado
        label.setBounds(x, y, 700, 200); // Establecemos la ubicación y el tamaño del JLabel
        Font fuente = new Font(nombreFuente, estiloFuente, tamanoFuente); // Creamos la fuente personalizada
        label.setFont(fuente); // Establecemos la fuente del JLabel
        label.setForeground(color); // Establecemos el color del texto del JLabel
        return label; // Devolvemos el JLabel creado
    }

    // Gestiona los cambios que sufre el programa tras pulsar el boton "siguiente"
    // Entre estos cambios se encuentra refrescar la pantalla, y cambiar la imagen del dialogo
    private void refrescarDialogo() {
        // Limpiar el panel para quitar el dialogo que se ha pasado
        panelLayeredFiestaEstadisticas.removeAll();

        // Llamar al método que pone el dialogo que corresopnde con el indice del dialogo actual
        aplicarDialogoActual();

        // Refrescar el aspecto visual del panel principal
        panelLayeredFiestaEstadisticas.repaint();
    }

}
