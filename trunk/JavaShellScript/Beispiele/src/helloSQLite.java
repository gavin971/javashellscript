//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * This example will create a sample SQLite-Database file.
 * It is based on a sample for SQLiteJDBC from http://www.zentus.com/sqlitejdbc/
 *
 * See also helloSQL for the connection to any SQL-Database (SQLite, MySQL, ...)
 * through the jsslib class jsslib.sql.Database
 */

import java.sql.*;

/**
 *
 * @author robert schuster
 */
public class helloSQLite {

  public static void main(String[] args) throws Exception {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
      Statement stat = conn.createStatement();
      stat.executeUpdate("drop table if exists people;");
      stat.executeUpdate("create table people (name, occupation);");
      PreparedStatement prep = conn.prepareStatement(
          "insert into people values (?, ?);");

      prep.setString(1, "Gandhi");
      prep.setString(2, "politics");
      prep.addBatch();
      prep.setString(1, "Turing");
      prep.setString(2, "computers");
      prep.addBatch();
      prep.setString(1, "Wittgenstein");
      prep.setString(2, "smartypants");
      prep.addBatch();

      conn.setAutoCommit(false);
      prep.executeBatch();
      conn.setAutoCommit(true);

      ResultSet rs = stat.executeQuery("select * from people;");
      while (rs.next()) {
          System.out.println("name = " + rs.getString("name"));
          System.out.println("job = " + rs.getString("occupation"));
      }
      rs.close();
      conn.close();
  }
}

