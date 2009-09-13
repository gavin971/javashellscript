//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8

/**
 * Copyright (c) 2009, Robert Schuster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * - Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * - Neither the name of Robert Schuster nor the names of his
 *   contributors may be used to endorse or promote products
 *   derived from this software without specific prior written
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This Example illustrates the usage of jsslib.sql.Database to access any
 * SQL-Database through a simple common API
 */

import java.sql.ResultSet;
import java.sql.SQLException;
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
        System.out.println("connect to the database ... ");
        String sqlitefile = arguments.getProperty("SQLite");
        if (sqlitefile != null) {
            //Create the File if it don't exists?
            boolean autocreate = false;
            if (arguments.getProperty("autocreate") != null)
                autocreate = true;
            //Try to connect
            if (!database.connectToSQLite(sqlitefile, autocreate)) {
                System.out.println(database.getLastError());
                System.out.println();
                System.out.println("Not Connected to the Database -> Exit!");
                return;
            }
        } else {
            ShowDiscription();
            return;
        }
        System.out.println("... connected!");

        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
        //Delete a table if it already exists
        System.out.println("write some sample data to the database ... ");
        int result;
        result = database.executeUpdate("drop table if exists test_table;");

        //Any Errors?
        if (result == -1) { System.out.println(database.getLastError()); return; }

        //Create a new table with a primary key and two values
        result = database.executeUpdate("create table test_table (id integer primary key autoincrement, city text, population integer);");

        //Any Errors?
        if (result == -1) { System.out.println(database.getLastError()); return; }

        //insert some data
        result = database.executeUpdate("insert into test_table(city,population) values('Cologne',995420);");
        //Any Errors?
        if (result == -1) { System.out.println(database.getLastError()); return; }
        //insert some data
        result = database.executeUpdate("insert into test_table(city,population) values('New York City',8274527);");
        //Any Errors?
        if (result == -1) { System.out.println(database.getLastError()); return; }
        //insert some data
        result = database.executeUpdate("insert into test_table(city,population) values('Tokio',8483050);");
        //Any Errors?
        if (result == -1) { System.out.println(database.getLastError()); return; }

        System.out.println("... writing done!");

        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
        System.out.println("reading the whole sample data ... \n");
        //Read data from the database
        ResultSet data = database.executeQuery("select * from test_table;");
        //while reading the ResultSet a error could occure
        try {
            System.out.println(String.format("%5s%20s%15s", "ID", "CITY", "POPULATION"));
            //a loop over all datasets
            while (data.next()) {
                System.out.println(String.format("%5d%20s%15d", data.getInt(1), data.getString(2), data.getInt(3)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println();

        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
        System.out.println("reading the sample data with population bigger then 8.000.000 ... \n");
        //Read data from the database
        data = database.executeQuery("select * from test_table where population > 8000000;");
        //while reading the ResultSet a error could occure
        try {
            System.out.println(String.format("%5s%20s%15s", "ID", "CITY", "POPULATION"));
            //a loop over all datasets
            while (data.next()) {
                System.out.println(String.format("%5d%20s%15d", data.getInt(1), data.getString(2), data.getInt(3)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println();

        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
        //Close the connection
        System.out.println("close the connect and exit.");
        database.disconnect();
    }

    private static void ShowDiscription() {
        System.out.println();
        System.out.println("This Example illustrates the usage of jsslib.sql.Database to access any");
        System.out.println("SQL-Database through a simple common API");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("helloSQL -SQLite databasefile.db");
        System.out.println("helloSQL -SQLite databasefile.db -autocreate");
    }
}
