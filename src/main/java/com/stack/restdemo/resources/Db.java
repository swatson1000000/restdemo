package com.stack.restdemo.resources;

import jakarta.annotation.Resource;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

@Path("db")
public class Db {

    private static final String QUERY =
            "SELECT * FROM qcew_2.qcew_2001 ORDER BY id LIMIT 10";

    @Resource(lookup = "jdbc/qcew")
    private DataSource ds;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response firstTen() {
        JsonArrayBuilder rows = Json.createArrayBuilder();
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(QUERY)) {

            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            while (rs.next()) {
                JsonObjectBuilder row = Json.createObjectBuilder();
                for (int i = 1; i <= n; i++) {
                    String name = md.getColumnLabel(i);
                    Object val = rs.getObject(i);
                    if (val == null) {
                        row.addNull(name);
                        continue;
                    }
                    switch (md.getColumnType(i)) {
                        case Types.SMALLINT:
                        case Types.INTEGER:
                            row.add(name, rs.getInt(i));
                            break;
                        case Types.BIGINT:
                            row.add(name, rs.getLong(i));
                            break;
                        case Types.REAL:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                        case Types.NUMERIC:
                        case Types.DECIMAL:
                            row.add(name, rs.getDouble(i));
                            break;
                        default:
                            row.add(name, val.toString());
                    }
                }
                rows.add(row);
            }
            return Response.ok(rows.build()).build();
        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "sql error" : e.getMessage().replace("\"","\\\"");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + msg + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
