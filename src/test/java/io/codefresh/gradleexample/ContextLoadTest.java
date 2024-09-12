package io.codefresh.gradleexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
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

