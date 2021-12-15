package com.telerikacademy.finalprojectpeerreview.repositories.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {

    private ConnectionPool(){
        //private constructor
    }

    /**
     * Connection instance
     */
    private Connection connection = getConnection();

    private static ConnectionPool instance = null;

    public static ConnectionPool getInstance(){
        if (instance==null)
            instance = new ConnectionPool();
        return instance;
    }

    /**
     * Getting connection from connection pool.
     *
     * @see ConnectionPool
     * @throws SQLException
     */
    Connection getConnection() {

        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String username = "ypjvnrwbrfxwqe";
        String password = "6381eeaaa66f83bfa2bd8c858f9a467c8b71f7dabf2f5c1e73c515c5317df995";
        String dbUrl = "postgres://ypjvnrwbrfxwqe:6381eeaaa66f83bfa2bd8c858f9a467c8b71f7dabf2f5c1e73c515c5317df995@ec2-34-250-16-127.eu-west-1.compute.amazonaws.com:5432/ddc23re6p9ocj1";
/*        String dbUrl = "jdbc:postgresql://" + "ec2-34-250-16-127.eu-west-1.compute.amazonaws.com"
                + "/ddc23re6p9ocj1?sslmode=require";*/
        try {
            return DriverManager.getConnection(dbUrl, username, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return connection;
    }
}