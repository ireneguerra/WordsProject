package org.example;

import org.neo4j.driver.*;

public class Neo4jClient implements Client {
    private final Driver driver;

    public Neo4jClient(String uri, String username, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    public Session getSession() {
        return driver.session();
    }

    public void close() {
        driver.close();
    }
}
