/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryjava;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author casa
 */
public class ConfigWindow extends JFrame{
    private JTextField entryHostname; 
    private JTextField entryDatabase; 
    private JTextField entryUser; 
    private JPasswordField entryPassword; 
    private JButton buttonConnect; 
    private JButton buttonCancel; 
    
    private EditableTable parentTable; 
    
    public ConfigWindow(EditableTable parentTable){
        this.parentTable = parentTable; 
        
        setTitle("Config"); 
        setSize(400, 260); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); 
        setResizable(false); 
        
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        
        // hostname
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.weightx = 0.0; 
        panel.add(new JLabel("Hostname:"), gbc); 
        
        gbc.gridx = 1;
        gbc.gridy = 0; 
        gbc.weightx = 1.0; 
        entryHostname = new JTextField(20); 
        panel.add(entryHostname, gbc); 
        
        // database
        
        gbc.gridx = 0; 
        gbc.gridy = 1;
        gbc.weightx = 0.0; 
        panel.add(new JLabel("Database:"), gbc); 
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; 
        entryDatabase = new JTextField(20); 
        panel.add(entryDatabase, gbc); 
        
        // user
        
        gbc.gridx = 0; 
        gbc.gridy = 2; 
        gbc.weightx = 0.0; 
        panel.add(new JLabel("User:"), gbc); 
        
        gbc.gridx = 1; 
        gbc.gridy = 2; 
        gbc.weightx = 1.0; 
        entryUser = new JTextField(20); 
        panel.add(entryUser, gbc); 
        
        // password
        
        gbc.gridx = 0; 
        gbc.gridy = 3;
        gbc.weightx = 0.0; 
        panel.add(new JLabel("Password:"), gbc); 
        
        gbc.gridx = 1;
        gbc.gridy = 3; 
        gbc.weightx = 1.0; 
        entryPassword = new JPasswordField(20);
        panel.add(entryPassword, gbc); 
        
        loadConfig(); 
        
        // buttons
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonConnect = new JButton("Connect"); 
        buttonCancel = new JButton("Cancel"); 
       
        panelButtons.add(buttonConnect); 
        panelButtons.add(buttonCancel); 
        
        gbc.gridx = 0; 
        gbc.gridy = 4;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.EAST; 
        panel.add(panelButtons, gbc); 
        
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                testConnection(); 
            }
        }); 
        
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dispose(); 
            }
        });
        
        add(panel); 
        
        
        
    }
    
    private void testConnection(){
        String hostname = entryHostname.getText().trim(); 
        String database = entryDatabase.getText().trim();
        String user = entryUser.getText().trim();
        String password = new String(entryPassword.getPassword()); 
        
        
        if (hostname.isEmpty() || database.isEmpty() || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please fill in all required fields..", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
               
        DatabaseConfig config = new DatabaseConfig(hostname, database, user, password); 
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
        try (FileWriter writer = new FileWriter("config.json")){
            gson.toJson(config, writer); 

        } catch (IOException ex){
            JOptionPane.showMessageDialog(this, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
                    
        dispose(); 
            
        if(parentTable != null){
            parentTable.reconnectAndReload();
        }

    }
    
    private void loadConfig(){
        Gson gson = new Gson(); 
        try(FileReader reader = new FileReader("config.json")){
            DatabaseConfig config = gson.fromJson(reader, DatabaseConfig.class);
            if(config != null){
                if (config.getHostname() != null)
                    entryHostname.setText(config.getHostname()); 
                if (config.getDatabase() != null)
                    entryDatabase.setText(config.getDatabase()); 
                if (config.getUsername() != null)
                    entryUser.setText(config.getUsername()); 
                if (config.getPassword() != null)
                    entryPassword.setText(config.getPassword()); 
            }        
        } catch (IOException e){    
        }
    }
    
}
