/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryjava;

import com.google.gson.Gson;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*; 

/**
 *
 * @author casa
 */
public class EditableTable extends JFrame {
    private JTable table; 
    private DefaultTableModel tableModel; 
       
    private String url; 
    private String user; 
    private String password; 
    
    private boolean isUpdatingTable = false; 
    
    public EditableTable(){
        loadConfigFromJson(); 
        
        setTitle("Library"); 
        setSize(600, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null); 
        
        JMenuBar menuBar = new JMenuBar(); 
        JMenu fileMenu = new JMenu("File"); 
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            // Certifique-se de que a classe ConfigWindow existe no mesmo pacote ou está importada
                new ConfigWindow(EditableTable.this).setVisible(true);
            }
        });
        
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0); 
            }
        }); 

        fileMenu.add(editItem); 
        fileMenu.add(exitItem); 
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(EditableTable.this, "Library\nLicense: GPL 3.0", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        }); 
               
        helpMenu.add(aboutItem); 
        
        menuBar.add(fileMenu); 
        menuBar.add(helpMenu); 
        
        setJMenuBar(menuBar); 
        
        String[] columns = {"isbn", "title", "author", "publisher", "price", "year", "edition"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return column != 0; 
            }
        }; 
        
        table = new JTable(tableModel); 
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.setAutoCreateRowSorter(true);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(120);
        }
        
        loadDataFromDatabase();

        tableModel.addTableModelListener(e -> {
            if(isUpdatingTable)
                return; 
            
            int row = e.getFirstRow(); 
            int column = e.getColumn();
            
            if(row >= 0 && column >= 0){
                Object isbnObj = tableModel.getValueAt(row, 0);
                Object newValue = tableModel.getValueAt(row, column); 
                
                String[] dbColumns = {"isbn", "title", "author", "publisher", "price", "year", "edition"};
                
                if (column < dbColumns.length){
                    String dbColumn = dbColumns[column]; 
                    if(!updateDatabase(isbnObj, dbColumn, newValue)){
                        refreshTableData();
                    } 
                }
                
            }
        }); 
        
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    public void reconnectAndReload(){
        loadConfigFromJson(); 
        refreshTableData(); 
    }
    
    private void loadDataFromDatabase(){
        try (Connection connection = DriverManager.getConnection(url, user, password)){
            Statement stmt = connection.createStatement(); 
            ResultSet rs = stmt.executeQuery("SELECT isbn, title, author, publisher, price, year, edition FROM books");
            while(rs.next()){
                Object[] row = {
                   rs.getString("isbn"), 
                   rs.getString("title"),
                   rs.getString("author"),
                   rs.getString("publisher"),
                   rs.getFloat("price"),
                   rs.getInt("year"),
                   rs.getString("edition")
                }; 
                tableModel.addRow(row);
            }
        
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating database: " + e.getMessage()); 
            ConfigWindow configWindow = new ConfigWindow(this);
            configWindow.setVisible(true);
            configWindow.setAlwaysOnTop(true);
        }
    }
    
    public void refreshTableData(){
        isUpdatingTable = true; 
        tableModel.setRowCount(0); 
        loadDataFromDatabase(); 
        isUpdatingTable = false; 
    }
    
    private boolean updateDatabase(Object isbn, String dbColumn, Object newValue){
        String sql = "UPDATE books SET " + dbColumn + " = ? WHERE isbn = ?"; 
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            
            pstmt.setObject(1, newValue); 
            pstmt.setObject(2, isbn); 
            pstmt.executeUpdate(); 
            return true; 
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, "Error updating database: " + e.getMessage()); 
            return false; 
        }  
    }
    
    public void loadConfigFromJson(){
        Gson gson = new Gson(); 
        
        try(FileReader reader = new FileReader("config.json")){
            DatabaseConfig config = gson.fromJson(reader, DatabaseConfig.class);
        
            if (config != null) {
                this.url = "jdbc:mysql://" + config.getHostname() + "/" + config.getDatabase(); 
                this.user = config.getUsername(); 
                this.password = config.getPassword(); 
            } 
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                 "Config error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
