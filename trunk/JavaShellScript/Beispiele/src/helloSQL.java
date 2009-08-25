//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * This Example illustrates the usage of jsslib.sql.Database to access any
 * SQL-Database through a simple common API
 */

import java.util.Properties;
import jsslib.shell.ArgParser;
import jsslib.sql.Database;

/**
 *
 * @author robert schuster
 */
public class helloSQL {
    public static void main(String [] args) {
        //have a look at the arguments
        Properties arguments = ArgParser.ArgsToProperties(args);

        //show the discription and exit on no arguments
        if (arguments == null || arguments.size() == 0) {
            ShowDiscription();
            return;
        }

        //the database-object
        Database database = new Database();

        //connect to a SQLite-Datebase?
        String sqlitefile = arguments.getProperty("SQLite");
        if (sqlitefile != null) {
            database.connectToSQLite(sqlitefile, true);
        }
    }

    private static void ShowDiscription() {
        System.out.println();
        System.out.println("This Example illustrates the usage of jsslib.sql.Database to access any");
        System.out.println("SQL-Database through a simple common API");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("helloSQL -SQLite databasefile.db");
    }
}
