package com.stack.restdemo.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("inventory")
public class inventory {

    private static final String TABLE = "\"STEVE\".inventory";

    @Resource(lookup = "jdbc/steve")
    private DataSource ds;

    @POST
    @Path("add")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addproducts(String product) {
        if (product == null || product.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product cannot be null or empty\n").build();
        }
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO " + TABLE
                            + " (product, previous_value, op) VALUES (?, NULL, 'add')")) {
                ps.setString(1, product);
                ps.executeUpdate();
            }
            return Response.ok("Product : " + product + " Added successfully\n").build();
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    @GET
    @Path("get")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getproducts() {
        try (Connection c = ds.getConnection()) {
            List<String> items = new ArrayList<>();
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT product FROM ("
                                 + "   SELECT DISTINCT ON (item_id) item_id, product"
                                 + "   FROM " + TABLE
                                 + "   ORDER BY item_id, id DESC"
                                 + " ) latest"
                                 + " WHERE product IS NOT NULL"
                                 + " ORDER BY item_id")) {
                while (rs.next()) {
                    items.add(rs.getString(1));
                }
            }
            StringJoiner sj = new StringJoiner(", ", "[", "]");
            items.forEach(sj::add);
            return Response.ok(sj.toString() + "\n").build();
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    @GET
    @Path("get_deleted")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getproductsIncludingDeleted() {
        try (Connection c = ds.getConnection()) {
            List<String> items = new ArrayList<>();
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT CASE WHEN product IS NOT NULL THEN product"
                                 + "        ELSE previous_value || ' (deleted)' END AS display"
                                 + " FROM ("
                                 + "   SELECT DISTINCT ON (item_id) item_id, product, previous_value"
                                 + "   FROM " + TABLE
                                 + "   ORDER BY item_id, id DESC"
                                 + " ) latest"
                                 + " ORDER BY (product IS NULL), item_id")) {
                while (rs.next()) {
                    items.add(rs.getString(1));
                }
            }
            StringJoiner sj = new StringJoiner(", ", "[", "]");
            items.forEach(sj::add);
            return Response.ok(sj.toString() + "\n").build();
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    @PUT
    @Path("update/{product}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateproduct(@PathParam("product") String oldProduct, String newProduct) {
        if (oldProduct == null || oldProduct.isEmpty()
                || newProduct == null || newProduct.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Both path product and body value are required\n").build();
        }
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO " + TABLE
                            + " (item_id, product, previous_value, op)"
                            + " SELECT item_id, ?, ?, 'update' FROM ("
                            + "   SELECT DISTINCT ON (item_id) item_id, product"
                            + "   FROM " + TABLE
                            + "   ORDER BY item_id, id DESC"
                            + " ) latest"
                            + " WHERE product = ?"
                            + " ORDER BY item_id LIMIT 1")) {
                ps.setString(1, newProduct);
                ps.setString(2, oldProduct);
                ps.setString(3, oldProduct);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    return Response.ok("Product: " + oldProduct
                            + " updated to " + newProduct + "\n").build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found\n").build();
            }
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    @DELETE
    @Path("delete/{product}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteproducts(@PathParam("product") String product) {
        if (product == null || product.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product cannot be null or empty\n").build();
        }
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO " + TABLE
                            + " (item_id, product, previous_value, op)"
                            + " SELECT item_id, NULL, ?, 'delete' FROM ("
                            + "   SELECT DISTINCT ON (item_id) item_id, product"
                            + "   FROM " + TABLE
                            + "   ORDER BY item_id, id DESC"
                            + " ) latest"
                            + " WHERE product = ?"
                            + " ORDER BY item_id LIMIT 1")) {
                ps.setString(1, product);
                ps.setString(2, product);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    return Response.ok("Product: " + product + " removed successfully\n").build();
                }
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found\n").build();
            }
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    @GET
    @Path("history/{product}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response historyFor(@PathParam("product") String product) {
        if (product == null || product.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Product cannot be null or empty\n").build();
        }
        try (Connection c = ds.getConnection()) {
            long itemId;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT item_id FROM " + TABLE
                            + " WHERE product = ? OR previous_value = ?"
                            + " ORDER BY id DESC LIMIT 1")) {
                ps.setString(1, product);
                ps.setString(2, product);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity("No item with value " + product + "\n").build();
                    }
                    itemId = rs.getLong(1);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("item_id=").append(itemId).append("\n");
            int n = 0;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id, op, product, previous_value, changed_at FROM " + TABLE
                            + " WHERE item_id = ? ORDER BY id")) {
                ps.setLong(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        n++;
                        String op = rs.getString("op");
                        String curr = rs.getString("product");
                        String prev = rs.getString("previous_value");
                        Timestamp ts = rs.getTimestamp("changed_at");
                        sb.append("#").append(n)
                          .append(" [").append(ts).append("] ")
                          .append(String.format("%-6s", op == null ? "?" : op))
                          .append(" ").append(prev == null ? "null" : prev)
                          .append(" -> ").append(curr == null ? "null" : curr)
                          .append("\n");
                    }
                }
            }
            return Response.ok(sb.toString()).build();
        } catch (SQLException e) {
            return sqlError(e);
        }
    }

    private static Response sqlError(SQLException e) {
        String msg = e.getMessage() == null ? "sql error" : e.getMessage();
        if (!msg.endsWith("\n")) msg += "\n";
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.TEXT_PLAIN)
                .entity(msg)
                .build();
    }
}
