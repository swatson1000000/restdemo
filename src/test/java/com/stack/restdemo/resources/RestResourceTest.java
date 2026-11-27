package com.stack.restdemo.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestResourceTest {

    @Test
    void helloReturnsGreetingRecord() {
        HelloRecord r = new RestResource().hello();
        assertNotNull(r);
        assertEquals("Hello from Jakarta EE", r.text());
    }
}
