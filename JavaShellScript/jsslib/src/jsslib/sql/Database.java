/*
 * This Class is a common API for all kinds of sql-Databases
 * actually it works with SQLite und MySQL
 */
package jsslib.sql;

import java.io.File;
import java.sql.*;
import jsslib.util.ExceptionHandling;

/**
 *
 * @author robert schuster
 */
public class Database {
    /**
     * Call get last Error to read the Value of this String. It contains the
     * Stack-Trace of the last Exception
     */
    private String lastError = "";

    /**
     * if true any Exception-Stack-Trace will be written directly to System.out
     */
    private boolean printErrorDirectly = false;

    /**
     * The Connection to the Database
     * call connectToSQLite or connectToMYSQL
     */
    private Connection conn;

    /**
     * Create a new Database-Object.
     * To establish a Connection to a Database call
     * connectToMYSQL() or
     * connectToSQLite()
     */
    public Database() {

    }

    /**
     * This will return a Connection-Object
     * @return
     */
    public Connection getConnection() {
        return null;
    }

    /**
     * This will ceate and return a new Statemant
     * @return
     */
    public Statement getStatemant() {
        return null;
    }
    
    /**
     * Establish a Connection to a SQLite-Datebase-File
     * @param filename the name of the Database-File
     * @param autocreate create a new File, if the file don't exists
     * @return true on success
     */
    public boolean connectToSQLite(String filename, boolean autocreate) {
        //try to find the Driver-Class
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            if (printErrorDirectly) ex.printStackTrace();
            lastError = ExceptionHandling.StackTraceToString(ex.getStackTrace());
            return false;
        }
        
        //check the file
        File file = new File(filename);
        
        //is there a file with this name?
        if (!file.exists() && !autocreate) {
            lastError = "ERROR: connectToSQLite: File "+ filename + " not found!";
            return false;
        } 
        
        //connect to the database
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException ex) {
            if (printErrorDirectly) ex.printStackTrace();
            lastError = ExceptionHandling.StackTraceToString(ex.getStackTrace());
            return false;
        }
                
        //everything allright
        return true;
    }
}
