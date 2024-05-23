/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author Jorge
 */
// Clase que se encarga de cargar los niveles en modo fiesta y con sus propiedades
// Tiene muchas cosas en comun con LaberintoPanel pero con algunas reglas cambiadas
public class ModoFiestaJuego extends JPanel implements KeyListener {

    // Inicializar como atributo de clase el panel donde se van a poner los botones de abajo
    // Esto permite desde cualquier metodo de la clase acceder a este panel y controlar su visibilidad
    private JPanel buttonsPanel = new JPanel();

    // Matriz del laberinto con las dimensiones suficientes para ocupar toda la pantalla
    private int[][] laberinto = new int[15][31];

    // Inicializar variables de posicion del personaje
    private int posX; // fila
    private int posY; // columna

    // Monedas con las que empiezas la partida
    // Se pone pública y estática para que ModoFiestaEstadisticas.java tenga acceso a ella y pueda mostrarlo en pantalla y registrarlas en la BBDD
    public static int monedas;

    // Esta es la ruta de carpetas donde se encontrará la skin deseada por el usuario. Esta ruta sera obtenida de personajesComprados.txt mediante el metodo obtenerSkin();
    private String rutaImagenPersonaje;

    // Este string es modificado por el keyListener para que rote el personaje
    private String direccionPersonaje;

    // Inicializar variable para el cronómetro del juego
    private Timer timer;

    // Cada 1 segundo, esta variable se va a reducir en una unidad, y cuando llegue a 0 se termina la partida
    private int tiempoRestante = 40; // Tiempo total del cronómetro en segundos

    // Este boolean controla si las letras "TIEMPO!" se muestra por pantalla
    // en paintCompoents hay un if que si mostrarTiempo == true, muestra las letras
    // el metodo que cambia el estado de mostrarTiempo es mostrarLetrasTiempo()
    private boolean mostrarLetrasTiempo = false;

    // El siguiente boolean controla si se puede o no mover el personaje
    // El objetivo es convertir esta variable a "true" y que cuando se termine el juego,
    // el personaje no se pueda mover
    private boolean isTeclasBloqueadas = false;

    // Esta variable controla si el mouse adapter de los botones colocados en el sur funcionan
    // Se utiliza para bloquear el mouse adapter cuando salen las letras "TIEMPO!"
    // Se inicializa el true porque interesa que al principio esten activados
    private boolean isBotonesBloqueados = false;

    // Esta variable controla si el juego está pauasdo o no
    // Si isJuegoPausado == true, el metodo pausarFiesta() pausará el cronómetro y pondrá el bloquearTeclas == true
    private boolean isJuegoPausado = false;

    // Constructor
    public ModoFiestaJuego() {
        creaVentanaBotonesLaberinto("pausa");
        empezarCronometro();
        ReproductorSonidos.reproducirMusicaFondo("fiestajuego", 0.4f);

        setFocusable(true);
        addKeyListener(this);
        requestFocus();
    }

    // Coge un nivel aleatorio de una lista de niveles
    // Este método es llamado desde ModoFiestaTutorial.java a través del método seleccionarCarpetaNivelesEditados()
    public void seleccionarNivelAlatorio() {
        // Seleccionar aleatoriamente un índice de la matriz de nombres de archivos
        int indiceAleatorio = (int) (Math.random() * ModoFiestaTutorial.nivelesAElegir.length);

        // Una vez seleccionado el nivel aleatoriamente, llamar al método que carga definitivamente el nivel
        // La informacion de qué archivo se ha seleccionado se envia a través de parámetro
        cargarNivelAleatorio(ModoFiestaTutorial.nivelesAElegir[indiceAleatorio]);
    }

    // Se encarga de construir el nivel en modo fiesta
    // Recibe como parametro el directorio y nombre del nivel que se ha generado automaticamente en ModoFiesta.java
    private void cargarNivelAleatorio(File nivelAleatorio) {

        boolean inicioColocado;
        boolean metaColocada;

        try (BufferedReader reader = new BufferedReader(new FileReader(nivelAleatorio))) {
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

            // Despues de leer el archivo, llamar a los métodos que se encargan de implementar las reglas del modo fiesta
            quitarLlavesYAbrirMeta();
            colocarMonedas();
            // Llamar al método que se encarga de colocar la meta en caso de que no la haya
            // Se envía como parámetro el boolean metaColocada que es obtenida a traves del txt del nivel
            colocarMeta(metaColocada);

        } catch (IOException | NumberFormatException ex) {
            //      SI HAY UN ERROR DE LECTURA EN EL TXT, EXPULSAR DE LA PARTIDA

            // Detener musica
            ReproductorSonidos.detenerMusicaFondo();
            // Agregar efecto de sonido de error
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);

            // Limpiar el panel de botones en el laberinto
            // Esto soluciona el bug de que al salir y entrar varias veces se acumulen los botones
            buttonsPanel.removeAll();

            // Detener el cronómetro
            timer.stop();

            // Hacer visible el frame principal
            MenuLaberinto.panelLayered.setVisible(true);
            // Eliminar la visibilidad a los paneles que conforman la pantalla de juego del laberinto
            setVisible(false);
            buttonsPanel.setVisible(false);

            // Mostrar mensaje de error
            JOptionPane.showMessageDialog(null, "Creías que podrías engañarme poniendo un txt que no fuera un nivel del editor? Qué iluso", "Error", JOptionPane.ERROR_MESSAGE);

            // Agregar de nuevo el panel de botones al contenedor principal para poder repintarlos
            add(buttonsPanel);
            // Repintar el contenedor principal para actualizar los cambios
            repaint();
        }
        direccionPersonaje = obtenerSkin() + "_derecha.png"; // Inicializar partida con personaje elegido por el usuario, por defecto mirando hacia la derecha
        requestFocus();
        repaint();
    }

    //          ZONA REGLAS DE JUEGO: Aqui van los metodos que se encargan de implementar las reglas del modo fiesta
    // Recorre el array del laberinto para eliminar las llaves y abrir la meta
    private void quitarLlavesYAbrirMeta() {
        // Recorrer array del laberinto: si se encuentran llaves, poner cesped, y si se encuentra meta cerrada, abrirla
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {

                if (laberinto[i][j] == 2) {
                    laberinto[i][j] = 0;
                } else if (laberinto[i][j] == 4) {
                    laberinto[i][j] = 3;
                }
            }
        }
    }

    // Coloca monedas aleatoriamente en el nivel
    private void colocarMonedas() {
        Random random = new Random();
        int sueloContador = 0;
        int maxMonedas = 20; // Máximo número de monedas a colocar
        int monedasColocadas = 0;

        // Contar la cantidad de casillas que contienen suelo (valor 0)
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 0) {
                    sueloContador++;
                }
            }
        }

        // Decidir cuántas monedas colocar en función del suelo disponible
        int monedasRestantes = Math.min(sueloContador, maxMonedas);

        // Colocar las monedas aleatoriamente en las casillas de suelo
        while (monedasColocadas < monedasRestantes) {
            int i = random.nextInt(laberinto.length);
            int j = random.nextInt(laberinto[0].length);

            if (laberinto[i][j] == 0) {
                laberinto[i][j] = 7; // Representaremos las monedas con el número 7
                monedasColocadas++;
            }
        }
    }

    // Escanea si el personaje esta en la misma casilla que la moneda
    private void recogerMoneda() {
        // Si el jugador se encuentra en la misma casilla que una llave, la recoge
        if (laberinto[posX][posY] == 7) {

            ReproductorSonidos.reproducirEfectoSonido("obtenermoneda1", 0.7f);
            monedas += 1; // Aumentar la puntuacion por cada moneda
            laberinto[posX][posY] = 0; // Restaurar la casilla a cesped, porque la moneda ya se ha cogido

        }
    }

    // Si el personaje llega a la meta, volver a generar un nivel de la lista aleatoriamente
    private void llegarAMeta() {
        if (laberinto[posX][posY] == 3) {
            ReproductorSonidos.reproducirEfectoSonido("entrarmeta2", 0.8f);
            seleccionarNivelAlatorio();
        }
    }

    // Lee si el nivel tiene meta. Si no tiene meta, coloca una meta al lado del jugador para que pueda avanzar de nivel
    /* Este método intentará colocar la meta al lado derecho del jugador, luego abajo,
    luego a la izquierda y finalmente arriba, en ese orden.*/
    private void colocarMeta(boolean tieneMeta) {
        if (!tieneMeta) {
            // Intentar colocar la meta al lado derecho del jugador
            if (posY + 1 < laberinto[0].length) {
                laberinto[posX][posY + 1] = 3; // Colocar la meta
            } else if (posX + 1 < laberinto.length) {
                // Si no se puede colocar al lado derecho, intentar abajo
                laberinto[posX + 1][posY] = 3; // Colocar la meta
            } else if (posY - 1 >= 0) {
                // Si no se puede colocar abajo, intentar al lado izquierdo
                laberinto[posX][posY - 1] = 3; // Colocar la meta
            } else if (posX - 1 >= 0) {
                // Si no se puede colocar al lado izquierdo, intentar arriba
                laberinto[posX - 1][posY] = 3; // Colocar la meta
            } else {
                // Si no hay espacio disponible alrededor del jugador, no se puede colocar la meta
                System.out.println("No se puede colocar la meta.");
            }
        }
    }

    // Comienza el cronómetro para la partida utilizando timer
    // Cuando termine el cronometro, se encarga de llamar al método pertinente que se encarga de cerrar la partida
    // Es llamado por el constructor
    private void empezarCronometro() {
        // Inicializar el timer para actualizar el tiempo restante cada segundo
        // delay: 1000 significa que la pantalla se actualiza cada 1000 milisegundos (1 segundo)
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tiempoRestante--;
                if (tiempoRestante == 0) {
                    timer.stop(); // Detener el timer si el tiempo llega a cero
                    // Detener la música
                    ReproductorSonidos.detenerMusicaFondo();
                    // Agregar efecto de sonido de que ya se ha terminado la partida
                    ReproductorSonidos.reproducirEfectoSonido("TIEMPO", 0.8f);
                    // Llamar al método que muestra las letras de TIEMPO! y se muestre la pantalla final
                    finalizarPartida();

                    // Llamar al método que guarda el record de las monedas si es que realmente hay un record
                    guardarRecordMonedas();
                } else if (tiempoRestante <= 10 && tiempoRestante > 0) {
                    ReproductorSonidos.reproducirEfectoSonido("aguja", 0.3f);
                }
                repaint(); // Volver a pintar el panel para actualizar el tiempo
            }
        });
        timer.start(); // Iniciar el timer
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

        // CARGAR TODAS LAS TEXTURAS GRAFICAS
        // Cargar textura suelo
        ImageIcon sueloIcon = new ImageIcon("recursos/textures/graficos/fiesta/suelo.jpg");
        Image sueloImage = sueloIcon.getImage();

        // Cargar textura pared
        //ImageIcon paredIcon = new ImageIcon("recursos/textures/pared.png");
        //Image paredImage = paredIcon.getImage();
        // Cargar textura llave
        ImageIcon llaveIcon = new ImageIcon("recursos/textures/graficos/fiesta/llave.jpg");
        Image llaveImage = llaveIcon.getImage();

        // Cargar textura inicio
        ImageIcon inicioIcon = new ImageIcon("recursos/textures/graficos/fiesta/inicio.jpg");
        Image inicioImage = inicioIcon.getImage();

        // Cargar textura meta cerrada
        ImageIcon metaCloseIcon = new ImageIcon("recursos/textures/graficos/fiesta/metaCerrada.jpg");
        Image metaCerradaImage = metaCloseIcon.getImage();

        // Cargar textura meta abierta
        ImageIcon metaOpenIcon = new ImageIcon("recursos/textures/graficos/fiesta/metaAbierta.jpg");
        Image metaAbiertaImage = metaOpenIcon.getImage();

        // Cargar textura tienda
        ImageIcon tiendaIcon = new ImageIcon("recursos/textures/graficos/fiesta/tienda.jpg");
        Image tiendaImage = tiendaIcon.getImage();

        // Cargar textura moneda dentro del mapa
        ImageIcon monedaIcon = new ImageIcon("recursos/textures//fiesta/moneda3.png");
        Image monedaImage = monedaIcon.getImage();

        // Cargar textura moneda con sombra fuerte (para el contador)
        ImageIcon monedaSombraIcon = new ImageIcon("recursos/textures/fiesta/moneda_sombrafuerte.png");
        Image monedaSombraImage = monedaSombraIcon.getImage();

        // Cargar textura cronómetro
        ImageIcon cronoIcon = new ImageIcon("recursos/textures/fiesta/cronometro.png");
        Image cronoImage = cronoIcon.getImage();

        // Cargar imagen de las letras TIEMPO!
        ImageIcon letrasIcon = new ImageIcon("recursos/textures/fiesta/pantalla_tiempo.png");
        Image letrasImage = letrasIcon.getImage();

        // Recorrer array y colocar texturas donde tocan
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                switch (laberinto[i][j]) {
                    case 0: // Cesped
                        g.drawImage(sueloImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    case 1: // Pared

                        // Determinar si los bloques adyacentes son paredes para realizar la unificacion
                        boolean tieneArriba = (i > 0 && laberinto[i - 1][j] == 1);
                        boolean tieneAbajo = (i < laberinto.length - 1 && laberinto[i + 1][j] == 1);
                        boolean tieneIzquierda = (j > 0 && laberinto[i][j - 1] == 1);
                        boolean tieneDerecha = (j < laberinto[i].length - 1 && laberinto[i][j + 1] == 1);

                        // Determinar qué textura de pared usar según los bloques adyacentes
                        String texturaPared = determinarTexturaPared(tieneArriba, tieneAbajo, tieneIzquierda, tieneDerecha);

                        // Cargar y dibujar la textura de la pared
                        ImageIcon paredIcon = new ImageIcon("recursos/textures/fiesta/texturesFiesta/" + texturaPared + ".jpg");
                        Image paredImage = paredIcon.getImage();

                        // Aplicar la textura de la pared
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
                    case 7: // Moneda
                        g.drawImage(monedaImage, j * dimension, i * dimension, dimension, dimension, null);
                        break;
                    default:
                        break;
                }
            }
        }

        // Dibujar skin elegida
        ImageIcon personajeIcon = new ImageIcon(direccionPersonaje);
        Image iconoPersonaje = personajeIcon.getImage();
        g.drawImage(iconoPersonaje, posY * dimension + 5, posX * dimension + 5, 30, 30, null);

        //              Colocar contador de monedas
        // Se coloca un icono de la moneda
        /* Se colocan 2 textos iguales:
           1. En color blanco
           2. En color negro un poco desplazado para dar un efecto de sombra
         */
        // Colocar icono de la moneda
        g.drawImage(monedaSombraImage, 618, 2, 60, 60, null);
        // Colocar contador de monedas
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 55)); // Establecer la fuente y el tamaño del texto
        g.drawString(monedas + "", 678, 54);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 55)); // Establecer la fuente y el tamaño del texto
        g.drawString(monedas + "", 676, 52);

        //              Colocar contador de tiempo
        // Dibujar la imagen del cronómetro en el panel
        g.drawImage(cronoImage, 480, 0, 60, 60, null);

        // Dibujar el contador del cronómetro en el panel
        // Coloco dos textos iguales para darle sombra igual que se hizo para el contador de monedas
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 55)); // Establecer la fuente y el tamaño del texto
        g.drawString(tiempoRestante + "", 548, 54);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 55)); // Establecer la fuente y el tamaño del texto
        g.drawString(tiempoRestante + "", 546, 52);

        // CUANDO EL CONTADOR LLEGUE A 0, mostratTiempo SE PONDRÁ EN "TRUE" PARA MOSTRAR LAS LETRAS TIEMPO!
        if (mostrarLetrasTiempo) {
            g.drawImage(letrasImage, 0, 0, 1256, 720, null);
        }
    }

    // Método para determinar qué textura de pared usar según los bloques adyacentes
    public static String determinarTexturaPared(boolean tieneArriba, boolean tieneAbajo, boolean tieneIzquierda, boolean tieneDerecha) {

        // Hago una serie de if-else para todas las combinaciones posibles (en total 16) de bloques adyacentes y saco el texto de la imagen
        if (!tieneArriba && !tieneAbajo && !tieneIzquierda && !tieneDerecha) {
            // Si no tiene ninguna pared alrededor
            return "pared";
        } else if (tieneArriba && tieneAbajo && tieneIzquierda && tieneDerecha) {
            // Si tiene paredes en todos los bloques adyacentes
            return "bloque_multidireccion";
        } else if (!tieneArriba && tieneAbajo && !tieneIzquierda && !tieneDerecha) {
            // Si tiene pared abajo
            return "bloque_ladeado_abajo";
        } else if (tieneArriba && !tieneAbajo && !tieneIzquierda && !tieneDerecha) {
            // Si tiene pared arriba
            return "bloque_ladeado_arriba";
        } else if (!tieneArriba && !tieneAbajo && !tieneIzquierda && tieneDerecha) {
            // Si tiene pared en la derecha
            return "bloque_ladeado_derecha";
        } else if (!tieneArriba && !tieneAbajo && tieneIzquierda && !tieneDerecha) {
            // Si tiene pared en la izquierda
            return "bloque_ladeado_izquierda";
        } else if (!tieneArriba && !tieneAbajo && tieneIzquierda && tieneDerecha) {
            // Si tiene pared en derecha y en la izquierda
            return "bloque_seguido_horizontal";
        } else if (tieneArriba && tieneAbajo && !tieneIzquierda && !tieneDerecha) {
            // Si tiene pared arriba y abajo
            return "bloque_seguido_vertical";
        } else if (!tieneArriba && tieneAbajo && tieneIzquierda && tieneDerecha) {
            // Si tiene pared izquierda, abajo y derecha
            return "bloque_varias_direcciones_abajo";
        } else if (tieneArriba && !tieneAbajo && tieneIzquierda && tieneDerecha) {
            // Si tiene pared izquierda, arriba y derecha
            return "bloque_varias_direcciones_arriba";
        } else if (tieneArriba && tieneAbajo && !tieneIzquierda && tieneDerecha) {
            // Si tiene pared arriba, abajo y derecha
            return "bloque_varias_direcciones_derecha";
        } else if (tieneArriba && tieneAbajo && tieneIzquierda && !tieneDerecha) {
            // Si tiene pared arriba, abajo e izquierda
            return "bloque_varias_direcciones_izquierda";
        } else if (!tieneArriba && tieneAbajo && !tieneIzquierda && tieneDerecha) {
            // Si tiene pared abajo y derecha
            return "esquina_abajo_derecha";
        } else if (!tieneArriba && tieneAbajo && tieneIzquierda && !tieneDerecha) {
            // Si tiene pared abajo e izquierda
            return "esquina_abajo_izquierda";
        } else if (tieneArriba && !tieneAbajo && !tieneIzquierda && tieneDerecha) {
            // Si tiene pared arriba y derecha
            return "esquina_arriba_derecha";
        } else if (tieneArriba && !tieneAbajo && tieneIzquierda && !tieneDerecha) {
            // Si tiene pared arriba e izquierda
            return "esquina_arriba_izquierda";
        } else {
            return "pared";
        }
    }

    // Realiza los tramites necesarios para finalizar la partida
    // Cambia el estado de los booleans pertinentes, muestra las letras TIEMPO! con un timer para mostrarlas un determinado tiempo
    // y llama a la funcion encargada de mostrar las estadisticas
    // Este metodo es llamado justo cuando el cronómetro de la partida llega a 0
    private void finalizarPartida() {
        // Cambiar el estado de bloquearTeclas para que el personaje no se pueda mover porque ya se ha finalizado la partida
        isTeclasBloqueadas = true;

        // Cambiar el estado de bloquearBotones para que dejen de funcionar los botones del sur de la pantalla
        isBotonesBloqueados = true;

        // Cambiar estado de mostrarLetrasTiempo a "true" para que se muestre una imagen de las letras TIEMPO!
        mostrarLetrasTiempo = true;
        // Programar un temporizador para que la imagen desaparezca después de 3 segundos
        // Reutilizo la variable del timer ya usada para el cronometro del juego para no declarar una nueva innecesariamente
        timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Borro las letras TIEMPO!
                mostrarLetrasTiempo = false;
                timer.stop(); // Detengo el temporizador
                removeAll();
                buttonsPanel.removeAll();
                setVisible(false);
                buttonsPanel.setVisible(false);
                ModoFiestaEstadisticas modoFiestaJuegoEstadisticas = new ModoFiestaEstadisticas();
                repaint(); // Actualizo el panel para que la imagen desaparezca
            }
        });
        timer.setRepeats(false); // El temporizador se ejecutará solo una vez
        timer.start(); // Comenzar el temporizador
    }

    // Guarda en un txt el numero de monedas record si el usuario ha hecho un record
    private void guardarRecordMonedas() {
        if (monedas > getRecordMonedasActual()){
            guardarRecordEnTXT();
        }
    }
    
    private int getRecordMonedasActual() {
        int recordMonedasActual = 0;
        try (Scanner scanner = new Scanner(new File("recursos/datosJugador/recordMonedas.txt"))) {
            recordMonedasActual = scanner.nextInt();
        } catch (FileNotFoundException ex) {
            ReproductorSonidos.reproducirEfectoSonido("denegado2", 0.5f);
            JOptionPane.showMessageDialog(null, "No se ha podido leer el record de monedas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return recordMonedasActual;
    }

    // Guarda la cantidad de monedas record en el TXT
    private void guardarRecordEnTXT() {
        try (FileWriter writer = new FileWriter("recursos/datosJugador/recordMonedas.txt")) {
            writer.write(monedas + "\n");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No se ha podido guardar el archivo. Comprueba si tienes espacio disponible en tu equipo o comprueba si tienes los permisos suficientes para guardar archivos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lector de teclas
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // A diferencia de LaberintoPanel, aqui se agrega una condicion de que las teclas solo funcionen si bloquearTeclas = false
        // Esto permite controlar el flujo de entrada del teclado y poder bloquearlo cuando termine la partida
        if (key == KeyEvent.VK_LEFT && posY > 0 && laberinto[posX][posY - 1] != 1 && !isTeclasBloqueadas) {
            posY--;
            direccionPersonaje = obtenerSkin() + "_izquierda.png"; // Cambia string de la direccion de la imagen del personaje

        } else if (key == KeyEvent.VK_RIGHT && posY < laberinto[0].length - 1 && laberinto[posX][posY + 1] != 1 && !isTeclasBloqueadas) {
            posY++;
            direccionPersonaje = obtenerSkin() + "_derecha.png";
        } else if (key == KeyEvent.VK_UP && posX > 0 && laberinto[posX - 1][posY] != 1 && !isTeclasBloqueadas) {
            posX--;
            direccionPersonaje = obtenerSkin() + "_arriba.png";
        } else if (key == KeyEvent.VK_DOWN && posX < laberinto.length - 1 && laberinto[posX + 1][posY] != 1 && !isTeclasBloqueadas) {
            posX++;
            direccionPersonaje = obtenerSkin() + "_abajo.png";
        }

        // Tras cada pulsacion, se llamaran a los siguientes metodos:
        recogerMoneda();
        llegarAMeta();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // Configura el panel principal y coloca un panel con botones en la parte sur
    // Se envia como parametro el nombre del boton que se debe poner
    // Dependiendo de si esta el juego pausado o no, se pone el boton de reanudar o el de pausar
    private void creaVentanaBotonesLaberinto(String botonPausa) {
        SwingUtilities.invokeLater(() -> {

            MenuLaberinto.frame.add(this); // Agregar el panel del laberinto al frame principal
            requestFocus(); // Solicitar foco justo despues de agregar el panel al frame para que se lea correctamente el keyListener

            // Establecer el color de fondo
            buttonsPanel.setBackground(Color.WHITE); // Puedes usar cualquier color que desees

            // Crear JLabels con imágenes
            JLabel pausarPartidaButton = new JLabel(new ImageIcon("recursos/textures/fiesta/" + botonPausa + "_button_fiesta.png"));
            JLabel volverMenuButton = new JLabel(new ImageIcon("recursos/textures/panelBotonesAbajo/salirMenu_button.png"));

            //       ZONA MOUSE LISTENERS
            pausarPartidaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isBotonesBloqueados) {
                        isJuegoPausado = !isJuegoPausado; // Alternar el boolean
                        pausarFiesta(); // Llamar al método encargado de pausar o despausar el juego (depende del estado del boolean)
                        // Agregar efecto de sonido de boton
                        ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                    }
                }
            });

            volverMenuButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!isBotonesBloqueados) {
                        // Eliminar la visibilidad a los paneles que conforman la pantalla de juego del laberinto
                        setVisible(false);
                        buttonsPanel.setVisible(false);

                        repaint();

                        // Limpiar el panel de botones en el laberinto
                        // Esto soluciona el bug de que al salir y entrar varias veces se acumulen los botones
                        removeAll();
                        buttonsPanel.removeAll();

                        // Detener el cronómetro
                        timer.stop();
                        // Detener musica
                        ReproductorSonidos.detenerMusicaFondo();
                        // Agregar efecto de sonido de boton
                        ReproductorSonidos.reproducirEfectoSonido("boton2", 0.5f);
                        // Reiniciar el contador de monedas a 0
                        monedas = 0;
                        // Hacer visible el frame principal
                        MenuLaberinto.panelLayered.setVisible(true);
                    }
                }
            });

            //              Agregar los JLabels al panel buttonsPanel
            buttonsPanel.add(pausarPartidaButton);
            buttonsPanel.add(volverMenuButton);

            // Agregar el panel buttonsPanel al frame en la parte sur
            MenuLaberinto.frame.add(buttonsPanel, BorderLayout.SOUTH);
            buttonsPanel.setVisible(true);
            MenuLaberinto.frame.setVisible(true);
        });
    }

    // Se encarga de pausar o despausar el juego despues de pulsar "pausar".
    // Se pausara o despausará dependiendo del estado de la variable isJuegoPausado
    private void pausarFiesta() {
        if (isJuegoPausado) {
            timer.stop(); // Esto pausará el Timer
            // Pausar musica
            ReproductorSonidos.pausarMusicaFondo();
            isTeclasBloqueadas = true;

            buttonsPanel.removeAll();
            creaVentanaBotonesLaberinto("reanudar");
        } else {
            timer.start(); // Esto reanudará el Timer
            // Reanudar musica
            ReproductorSonidos.reanudarMusicaFondo();
            isTeclasBloqueadas = false;

            buttonsPanel.removeAll();
            creaVentanaBotonesLaberinto("pausa");
        }
    }

    // Método para leer la dirección de la skin elegida desde el archivo txt
    private String obtenerSkin() {
        try (BufferedReader reader = new BufferedReader(new FileReader("recursos/datosJugador/skinActual.txt"))) {
            // Variable para almacenar cada línea del archivo
            String linea;
            // Leer el archivo línea por línea hasta que lleguemos al final
            while ((linea = reader.readLine()) != null) {
                // Actualizamos la variable direccionSkin con la línea actual del archivo
                // La última línea leída contendrá la dirección de la skin elegida
                rutaImagenPersonaje = linea;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rutaImagenPersonaje;
    }
}
