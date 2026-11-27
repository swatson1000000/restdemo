package com.stack.restdemo.resources;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Covers the input-validation branches of {@link inventory} that return a
 * {@code BAD_REQUEST} response before touching the injected {@code DataSource}.
 * Full CRUD paths require Postgres and are exercised via integration tests.
 */
class InventoryValidationTest {

    private final inventory resource = new inventory();

    @Test
    void addRejectsNullProduct() {
        Response r = resource.addproducts(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void addRejectsEmptyProduct() {
        Response r = resource.addproducts("");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void deleteRejectsNullProduct() {
        Response r = resource.deleteproducts(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void deleteRejectsEmptyProduct() {
        Response r = resource.deleteproducts("");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void historyRejectsNullProduct() {
        Response r = resource.historyFor(null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void historyRejectsEmptyProduct() {
        Response r = resource.historyFor("");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Product cannot be null or empty\n", r.getEntity());
    }

    @Test
    void updateRejectsNullOldProduct() {
        Response r = resource.updateproduct(null, "new");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Both path product and body value are required\n", r.getEntity());
    }

    @Test
    void updateRejectsEmptyOldProduct() {
        Response r = resource.updateproduct("", "new");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Both path product and body value are required\n", r.getEntity());
    }

    @Test
    void updateRejectsNullNewProduct() {
        Response r = resource.updateproduct("old", null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Both path product and body value are required\n", r.getEntity());
    }

    @Test
    void updateRejectsEmptyNewProduct() {
        Response r = resource.updateproduct("old", "");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        assertEquals("Both path product and body value are required\n", r.getEntity());
    }
}
