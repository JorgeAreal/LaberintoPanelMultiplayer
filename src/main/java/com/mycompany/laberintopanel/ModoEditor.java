/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Jorge Areal Alberich
 */
public class ModoEditor extends JPanel {

    private int posX; // fila
    private int posY; // columna
    //private int puntuacion;

    private int[][] matriz;  // Tu matriz de nivel
    private int tipoBloqueSeleccionado = 1; // Variable para representar el tipo de bloque seleccionado
    private boolean inicioColocado = false; // Variable para rastrear si la meta ya ha sido colocada
    private boolean metaColocada = false; // Variable para rastrear si la meta ya ha sido colocada

    /*
            ZONA CONSTRUCCION: Metodos relacionados con generar graficos
     */
    // Constructor: crea el mouse listener a cada una de las casillas para que el editor responda a los clicks
    // Tambien establece y un diseño del panel principal
    public ModoEditor(int[][] matrizRecibida) {
        // Guardar la matriz recibida como parámetro en la variable de atributo de la clase "matriz", que es con la que va a trabajar todo el programa
        this.matriz = matrizRecibida;

        setLayout(new BorderLayout()); // Establecer el diseño del panel principal

        // Agrega un MouseListener para agregar la interacción del usuario
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Calcula la posición de la celda en la matriz
                int fila = e.getY() / getTamanioCelda();
                int columna = e.getX() / getTamanioCelda();

                // LLamar al método que coloca cada tipo de bloque en su lugar segun el contenido de la matriz
                cambiarValorCelda(fila, columna);

                // Vuelve a pintar el panel para reflejar los cambios
                repaint();
            }
        });
        // Método que genera los botones colocados al sur de la pantalla
        creaBotonesEditor();
    }

    // Pinta los componentes graficos
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int dimension = 40;

        // Dibuja la matriz en el panel
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[0].length; columna++) {
                int valor = matriz[fila][columna];

                // Esta cadena de texto proviene de obtenerTexturaPorValor(valor)
                // Aqui se recibe l string que corresponde a cada una de las texturas del editor (pared, inicio, etc...)
                String direccionTextura = obtenerTexturaPorValor(valor);

                // Creo instancia de la textura, y le paso la direccion obtenida de obtenerTexturaPorValor()
                ImageIcon Icono = new ImageIcon(direccionTextura);
                Image iconoTextura = Icono.getImage();

                // Finalmente dibujo el cuadrado
                g.drawImage(iconoTextura, columna * dimension, fila * dimension, dimension, dimension, null);
            }
        }
    }

    // Método para obtener el bloque correspondiente al valor en la matriz
    // La variable "valor" se pasa a "direccionTextura" dentro de paintComponent con la direccion de la textura pertinente
    private String obtenerTexturaPorValor(int valor) {
        switch (valor) {
            case 0:
                return "recursos/textures/editor/cespedEditor2.jpg"; // Cesped
            case 1:
                return "recursos/textures/editor/paredEditor2.jpg"; // Pared
            case 2:
                return "recursos/textures/editor/llaveEditor2.jpg"; // Llaves
            case 3:
                return "recursos/textures/editor/metaAbiertaEditor2.jpg"; // Meta
            case 4:
                return "recursos/textures/editor/metaAbiertaEditor2.jpg"; // Meta
            case 5:
                return "recursos/textures/editor/inicioEditor.jpg"; // Inicio
            default:
                return "recursos/textures/editor/cespedEditor2.jpg"; // Valor desconocido
        }
    }

    // Método para obtener el tamaño de la celda en función del tamaño del panel y la matriz
    private int getTamanioCelda() {
        int anchoPanel = getWidth();
        int altoPanel = getHeight();
        int columnas = matriz[0].length;
        int filas = matriz.length;
        return Math.min(anchoPanel / columnas, altoPanel / filas);
    }

    // Método para cambiar la matriz de nivel
    public void setMatriz(int[][] matriz) {
        this.matriz = matriz;
        repaint();
    }

    /*
            ZONA MOUSE CLICK: Metodos relacionados con la respuesta de los clicks del usuario
     */
    // Método para cambiar el valor de una celda en la matriz
    // Lee la fila y columna donde el usuario ha hecho click, y pone el bloque correspondiente a la eleccion del usuario
    private void cambiarValorCelda(int fila, int columna) {

        // Si se pretende colocar un inicio o una meta cuando ya está puesta, mostrar un mensaje de advertencia
        if (tipoBloqueSeleccionado == 4 && metaColocada) {
            // Agregar efecto de sonido de denegacion
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "La meta ya ha sido colocada.", "Error al poner la meta", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (tipoBloqueSeleccionado == 5 && inicioColocado) {
            // Agregar efecto de sonido de denegacion
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(this, "El inicio ya ha sido colocado.", "Error al poner el inicio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Comprueba si se pone un bloque encima de la meta o del inicio
        // Si se pone cualquier bloque en el lugar de meta o inicio, se cambian sus respectivos booleans para que permita al usuario volver a colocarlo en otro sitio
        if (matriz[fila][columna] == 4) {
            metaColocada = false;
        } else if (matriz[fila][columna] == 5) {
            inicioColocado = false;
        }

        // Colocar el tipo de bloque seleccionado en la casilla seleccionada
        matriz[fila][columna] = tipoBloqueSeleccionado;

        // Actualizar el estado de las variables de inicio o meta despues de haberlas colocado
        if (tipoBloqueSeleccionado == 4) {
            metaColocada = true;
        } else if (tipoBloqueSeleccionado == 5) {
            inicioColocado = true;
        }
    }

    // Método para cambiar el tipo de bloque seleccionado
    private void cambiarTipoBloqueSeleccionado(int tipoBloque) {
        tipoBloqueSeleccionado = tipoBloque; // Establecer el tipo de bloque seleccionado
    }

    /*
            ZONA BOTONES:
            - Metodos que crean el comportamiento de los botones tipo de lectura y escritura (guardar partida, importar...)
            - Metodos que directamente crean los botones
     */
    // Guarda la matriz hecha en el editor
    private void guardarPartidaEditada() {
        // Crear una ventana de entrada para que el usuario ingrese el nombre del archivo
        String nombreArchivo = JOptionPane.showInputDialog(this, "Ingrese el nombre del nivel:", "Guardar Partida", JOptionPane.PLAIN_MESSAGE);

        // Si el usuario presiona "Cancelar" o ingresa un nombre vacío, no guardamos la partida
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return;
        }

        String rutaArchivo = "recursos/levelsEditor/" + nombreArchivo + ".txt";

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Buscar donde esta el inicio
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    if (matriz[i][j] == 5) { // Si encontramos el inicio (valor 5)
                        posX = i; // La fila donde se encuentra
                        posY = j; // La columna donde se encuentra
                        break; // Terminamos la búsqueda una vez encontrado el inicio
                    }
                }
            }

            // Guarda la posicion y puntuacion de donde va a respawnear el personaje
            writer.write(posX + "\n");
            writer.write(posY + "\n");

            // Recorrer matriz para guardarlo en el archivo txt
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    writer.write(matriz[i][j] + " ");
                }
                writer.write("\n");

            }
            //colocar true o false si se ha colocado o no el inicio y la meta
            writer.write(inicioColocado + "\n");
            writer.write(metaColocada + "\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el nivel. Comprueba si tienes espacio de almacenamiento suficiente en tu equipo", "Error", JOptionPane.ERROR_MESSAGE);
        }
        requestFocus();  // Solicitar el foco después de reiniciar la partida
    }

    // Método para borrar toda la matriz y restablecerla a 0. Utilizada en el action listener del boton "boorar todo"
    private void borrarTodo() {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                matriz[i][j] = 0;
            }
        }
        inicioColocado = false; // Restablecer la variable de control de inicio colocado
        metaColocada = false; // Restablecer la variable de control de meta colocada
        repaint(); // Vuelve a pintar el panel para reflejar los cambios
    }

    // Lee el archivo txt seleccionado por el usuario tras pulsar el boton "importar" y lo abre en el editor
    private void importarNivel() {
        JFileChooser fileChooser = new JFileChooser();

        // Establecer el directorio inicial
        fileChooser.setCurrentDirectory(new File("recursos/levelsEditor"));

        int resultado = fileChooser.showOpenDialog(this); // Mostrar el diálogo para seleccionar archivos

        if (resultado == JFileChooser.APPROVE_OPTION) { // Si el usuario selecciona un archivo
            File archivoSeleccionado = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(archivoSeleccionado))) {
                // Leemos las variables posX, posY y puntuacion
                posX = Integer.parseInt(reader.readLine()); // guardar fila
                posY = Integer.parseInt(reader.readLine()); // guardar columna

                // Leemos la matriz
                for (int i = 0; i < matriz.length; i++) {
                    String[] valores = reader.readLine().trim().split("\\s+");
                    for (int j = 0; j < matriz[i].length; j++) {
                        matriz[i][j] = Integer.parseInt(valores[j]);
                    }
                }

                // Leemos los booleanos inicioColocado y metaColocada
                inicioColocado = Boolean.parseBoolean(reader.readLine());
                metaColocada = Boolean.parseBoolean(reader.readLine());

            } catch (IOException | NumberFormatException ex) {
                ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
                JOptionPane.showMessageDialog(this, "El archivo no es reconocible por el editor. Asegúrate de seleccionar un nivel válido con extensión .txt", "Error", JOptionPane.ERROR_MESSAGE);

            }
        }
        repaint();
    }

    // Botones para manejar el editor (pared, inicio, guardar...)
    private void creaBotonesEditor() {
        // Crear paneles de los botones y sus botones
        JPanel botonesEditor = new JPanel(new GridLayout(2, 4)); // Layout para colocar los botones verticalmente

        // Configuro cada uno de los botones con su texto y su funcion
        // Los botones que se encargan de colocar bloques, llaman a la funcion cambiarTipoBloqueSeleccionado()
        // con el valor del bloque que va a poner como parámetro
        JButton botonInicio = new JButton("Inicio");
        botonInicio.addActionListener(e -> cambiarTipoBloqueSeleccionado(5));
        botonesEditor.add(botonInicio);

        JButton botonEspacio = new JButton("Suelo");
        botonEspacio.addActionListener(e -> cambiarTipoBloqueSeleccionado(0));
        botonesEditor.add(botonEspacio);

        JButton botonPared = new JButton("Pared");
        botonPared.addActionListener(e -> cambiarTipoBloqueSeleccionado(1));
        botonesEditor.add(botonPared);

        JButton botonParedAzul = new JButton("Llaves");
        botonParedAzul.addActionListener(e -> cambiarTipoBloqueSeleccionado(2));
        botonesEditor.add(botonParedAzul);

        JButton botonVerde = new JButton("Meta");
        botonVerde.addActionListener(e -> cambiarTipoBloqueSeleccionado(4));
        botonesEditor.add(botonVerde);

        JButton borrarTodoButton = new JButton("Borrar Todo");
        borrarTodoButton.addActionListener(e -> borrarTodo());
        botonesEditor.add(borrarTodoButton);

        JButton importarButton = new JButton("Importar");
        importarButton.addActionListener(e -> importarNivel());
        botonesEditor.add(importarButton);

        JButton guardarNivelBoton = new JButton("Guardar nivel");
        guardarNivelBoton.addActionListener(e -> {
            guardarPartidaEditada();
        });
        botonesEditor.add(guardarNivelBoton);

        JButton jugarNivelButton = new JButton("Jugar nivel");
        jugarNivelButton.addActionListener(e -> {

            seleccionarNivelPersonalizado();
        });
        botonesEditor.add(jugarNivelButton);

        JButton salirButton = new JButton("Volver al menú");
        salirButton.addActionListener(e -> {
            // Mostrar un mensaje de confirmación al usuario
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de que quieres volver al menú? El progreso no guardado se perderá.",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );

            // Verificar la opción seleccionada por el usuario
            if (opcion == JOptionPane.YES_OPTION) {
                // Si el usuario elige "Sí", ocultar el panel del editor y volver al menú principal
                this.setVisible(false);
                botonesEditor.setVisible(false);
                MenuLaberinto.panelLayered.setVisible(true);
            }
        });
        botonesEditor.add(salirButton);

        // Personaliza el estilo de los botones
        personalizarBoton(botonInicio);
        personalizarBoton(botonEspacio);
        personalizarBoton(botonPared);
        personalizarBoton(botonParedAzul);
        personalizarBoton(botonVerde);
        personalizarBoton(borrarTodoButton);
        personalizarBoton(importarButton);
        personalizarBoton(guardarNivelBoton);
        personalizarBoton(jugarNivelButton);
        personalizarBoton(salirButton);

        // Agregar el panel de botones al sur del panel principal
        add(botonesEditor, BorderLayout.SOUTH);
    }

    // Este metodo es llamado por creaBotonesEditor(): personaliza los botones del panel de botones de abajo
    private void personalizarBoton(JButton button) {
        button.setBackground(new Color(255, 255, 255)); // Gris
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // Este método es llamado cuando se pulsa el botón del editor "Jugar Nivel"
    // Se encarga de preguntar por la ubicacion del nivel que se quiere abrir
    // Si se abre algun archivo, se abre el laberintoPanel con el nivel que se ha seleccionado
    private void seleccionarNivelPersonalizado() {
        JFileChooser fileChooser = new JFileChooser();
        // Establecer el directorio inicial
        fileChooser.setCurrentDirectory(new File("recursos/levelsEditor"));

        int resultado = fileChooser.showOpenDialog(null); // Mostrar el diálogo para seleccionar archivos

        if (resultado == JFileChooser.APPROVE_OPTION) { // Si el usuario selecciona un archivo
            // Almacenar el archivo seleccionado en la variable
            // Esta variable será leída en LaberintoPanel.java para cargar el nivel seleccionado
            VariablesGenerales.nivelEditorSeleccionado = fileChooser.getSelectedFile();

            // Cambiar el estado de isNormalMode en false
            VariablesGenerales.isNormalMode = false;

            // Eliminar la visibilidad del panel del editor
            this.setVisible(false);

            // Llamar al constructor de la clase LaberintoPanel.java
            LaberintoPanel laberintoPanel = new LaberintoPanel();
            LaberintoPanel.laberintoPanel = laberintoPanel;
        }
    }
}
