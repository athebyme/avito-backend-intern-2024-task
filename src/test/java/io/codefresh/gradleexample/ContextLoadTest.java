package io.codefresh.gradleexample;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest

public class ContextLoadTest {

    @Test
    public void contextLoads() {
        assertEquals("Expected correct message","Hello World","Hello "+"World");
    }
}

