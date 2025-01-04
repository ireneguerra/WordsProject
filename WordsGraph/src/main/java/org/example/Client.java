package org.example;

import org.neo4j.driver.Session;

public interface Client {
    Session getSession();
    void close();

}
