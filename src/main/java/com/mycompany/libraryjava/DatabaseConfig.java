/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryjava;

/**
 *
 * @author casa
 */
public class DatabaseConfig {
    
    private String hostname;
    private String database;
    private String username;
    private String password; 
    
    public DatabaseConfig(){}
    
    public DatabaseConfig(String hostname, String database, String username, String password){
        this.hostname = hostname; 
        this.database = database; 
        this.username = username; 
        this.password = password; 
    }
    
    public String getHostname(){
        return hostname; 
    }
    
    public String getDatabase(){
        return database; 
    }
    
    public String getUsername(){
        return username; 
    }
    
    public String getPassword(){
        return password; 
    }
    
}
