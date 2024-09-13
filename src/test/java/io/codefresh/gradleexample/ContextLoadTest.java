package io.codefresh.gradleexample;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest

public class ContextLoadTest {

    @Test
    public void contextLoads() {
        assertEquals("Expected correct message","Hello World","Hello "+"World");
    }

    @Test
    public void testEnvVariable() {
        String jdbcUrl = System.getenv("POSTGRES_JDBC_URL");
        System.out.println("POSTGRES_JDBC_URL: " + jdbcUrl);
        assertNotNull("Переменная POSTGRES_JDBC_URL не найдена", jdbcUrl);
    }
}

