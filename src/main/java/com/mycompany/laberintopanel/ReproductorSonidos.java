/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Jorge
 */
// Esta clase se dedica única y exclusivamente a la reproducción de sonidos dentro del juego
// Todos los métodos son públicos y estáticos para que sean accesibles por cualquier clase del programa
public class ReproductorSonidos {

    // Inicializar variables que intervienen durante la reproducción de sonidos
    // Se ponen como atributo para que todos los métodos tengan acceso a ellas
    private static Clip musicaFondo;
    private static Clip efectoSonido;
    private static boolean estaMusicaPausada = false;
    private static boolean estaSonidoPausado = false;

    // Reproduce música en bucle o hasta que se llame a su función antagónica detenerMusicaFondo()
    // Recibe como parámetro el nombre del archivo de audio que se quiere reproducir, y el volumen
    public static void reproducirMusicaFondo(String nombreMusica, float volumen) {
        try {
            // Cargar el archivo de música de fondo desde la ruta proporcionada
            File archivoWav = new File("recursos/musica/" + nombreMusica + ".wav");
            // Crear un flujo de entrada de audio a partir del archivo
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoWav);
            // Obtener un clip de audio para reproducir la música de fondo
            musicaFondo = AudioSystem.getClip();
            // Abrir el clip de audio con el flujo de entrada de audio
            musicaFondo.open(audioInputStream);

            // Configurar el control de volumen del clip de audio
            FloatControl gainControl = (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);

            // Convertir el valor de volumen proporcionado a dB
            // El rango de valores de volumen es de 0.0f a 1.0f
            // Convertimos este rango a dB (-80 a 6 dB)
            float dB = (float) (Math.log(volumen) / Math.log(10.0) * 20.0);

            // Establecer el volumen del control de volumen en dB
            gainControl.setValue(dB);

            // Reproducir la música de fondo en bucle
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Detiene la musica puesta con reproducirMusicaFondo()
    public static void detenerMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isOpen()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }

    // Pausa la música puesta con reproducirMusicaFondo()
    public static void pausarMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop(); // Detener la reproducción de la música
            estaMusicaPausada = true; // Marcar como pausado
        }
    }

    // Reanuca la música pausada con pausarMusicaFondo()
    public static void reanudarMusicaFondo() {
        if (musicaFondo != null && estaMusicaPausada) {
            musicaFondo.start(); // Reanudar la reproducción de la música
            estaMusicaPausada = false; // Marcar como no pausado
        }
    }

    // Método especializado en reproducir efectos de sonidos
    // Funciona parecido a su hermana reproducirMusicaFondo() pero no reproduce los sonidos en bucle
    public static void reproducirEfectoSonido(String nombreSonido, float volumen) {
        try {
            // Cargar el archivo de sonido desde la ruta proporcionada
            File archivoWav = new File("recursos/sonidos/" + nombreSonido + ".wav");
            // Crear un flujo de entrada de audio a partir del archivo
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivoWav);
            // Obtener un clip de audio para reproducir el sonido
            efectoSonido = AudioSystem.getClip();
            // Abrir el clip de audio con el flujo de entrada de audio
            efectoSonido.open(audioInputStream);

            // Configurar el control de volumen del clip de audio
            FloatControl gainControl = (FloatControl) efectoSonido.getControl(FloatControl.Type.MASTER_GAIN);

            // Convertir el valor de volumen proporcionado a dB
            // El rango de valores de volumen es de 0.0f a 1.0f
            // Convertimos este rango a dB (-80 a 6 dB)
            float dB = (float) (Math.log(volumen) / Math.log(10.0) * 20.0);

            // Establecer el volumen del control de volumen en dB
            gainControl.setValue(dB);

            // Reproducir el sonido
            efectoSonido.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Detiene el efecto de sonido
    public static void detenerEfectoSonido() {
        if (efectoSonido != null && efectoSonido.isOpen()) {
            efectoSonido.stop();
            efectoSonido.close();
        }
    }
    
    // Pausa el efecto de sonido
    public static void pausarEfectoSonido() {
        if (efectoSonido != null && efectoSonido.isRunning()) {
            efectoSonido.stop(); // Detener la reproducción de la música
            estaSonidoPausado = true; // Marcar como pausado
        }
    }

    // Reanuca el efecto de sonido pausado
    public static void reanudarEfectoSonido() {
        if (efectoSonido != null && estaSonidoPausado) {
            efectoSonido.start(); // Reanudar la reproducción de la música
            estaSonidoPausado = false; // Marcar como no pausado
        }
    }
}
