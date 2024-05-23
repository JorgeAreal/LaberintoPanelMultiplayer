/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.laberintopanel;

import java.io.File;

/**
 *
 * @author Jorge
 */

// En esta clase se almacenaran las variables que seran usadas por multiples clases a lo largo del programa
// Por ello, es importante que se tenga acceso facil a estas variables
public class VariablesGenerales {

    // Esta variable dirá si true = se esta juganfo una partida normal, o false = se esta jugando un nivel creado por el usuario
    /* Dependiendo del estado de este boolean, los métodos encargados de generar niveles sabrán si hay que generar nivel oficial o nivel personalizado,
    o de qué forma reiniciar el nivel */
    public static boolean isNormalMode;
    
    // Variable donde se almacenará el archivo seleccionado tras pulsar el botón "jugar" dentro del modo editor
    // Se utiliza en LaberintoPanel.java en el método para leerlo y cargar el nivel seleccionado
    public static File nivelEditorSeleccionado;
    
    // Atributo para mantener el estado de la tienda y controlar los keyListener segun si la tienda esta abierta o no dentro de LaberintoPanel
    public static boolean isTiendaAbierta = false;
}
