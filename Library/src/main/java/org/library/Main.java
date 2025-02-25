package org.library;

import org.apache.catalina.startup.Tomcat;

public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.start();
        System.out.println("Tomcat started at http://localhost:8080");
        tomcat.getServer().await();
    }
}
