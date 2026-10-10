/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.libraryjava;

import javax.swing.SwingUtilities;

/**
 *
 * @author casa
 */
public class LibraryJava {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EditableTable().setVisible(true); 
        }); 
    }
}
