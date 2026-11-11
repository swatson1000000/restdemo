package com.stack.restdemo.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class InventorySchema {

    private static final String TABLE = "\"STEVE\".inventory";

    @Resource(lookup = "jdbc/steve")
    private DataSource ds;

    @PostConstruct
    public void init() {
        String[] ddl = {
                "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                        + "id BIGSERIAL PRIMARY KEY, "
                        + "item_id BIGINT, "
                        + "product TEXT, "
                        + "previous_value TEXT, "
                        + "op TEXT, "
                        + "changed_at TIMESTAMPTZ NOT NULL DEFAULT now())",
                "ALTER TABLE " + TABLE + " ALTER COLUMN product DROP NOT NULL",
                "ALTER TABLE " + TABLE + " ADD COLUMN IF NOT EXISTS item_id BIGINT",
                "ALTER TABLE " + TABLE + " ADD COLUMN IF NOT EXISTS op TEXT",
                "UPDATE " + TABLE + " SET item_id = id WHERE item_id IS NULL",
                "UPDATE " + TABLE + " SET op = CASE"
                        + "  WHEN product IS NOT NULL AND previous_value IS NULL THEN 'add'"
                        + "  WHEN product IS NOT NULL AND previous_value IS NOT NULL THEN 'update'"
                        + "  WHEN product IS NULL THEN 'delete' END"
                        + " WHERE op IS NULL",
                "CREATE INDEX IF NOT EXISTS inventory_item_id_idx ON " + TABLE + " (item_id)",
                "CREATE OR REPLACE FUNCTION \"STEVE\".inv_set_item_id()"
                        + " RETURNS TRIGGER AS $$"
                        + " BEGIN IF NEW.item_id IS NULL THEN NEW.item_id := NEW.id; END IF;"
                        + " RETURN NEW; END; $$ LANGUAGE plpgsql",
                "CREATE OR REPLACE TRIGGER inv_set_item_id BEFORE INSERT ON " + TABLE
                        + " FOR EACH ROW EXECUTE FUNCTION \"STEVE\".inv_set_item_id()"
        };
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            for (String sql : ddl) {
                st.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("inventory schema init failed", e);
        }
    }
}
