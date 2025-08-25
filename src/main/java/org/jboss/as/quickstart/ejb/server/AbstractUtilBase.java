/*
 * Copyright 2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.quickstart.ejb.server;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.sql.PooledConnection;

/**
 *
 */
public abstract class AbstractUtilBase {

    protected Logger log = Logger.getLogger(getClass().getSimpleName());

    protected static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected static void safeClose(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Throwable t) {
        }
    }

    protected static void safeClose(AutoCloseable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Throwable t) {
        }
    }

    protected static void safeClose(PooledConnection closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Throwable t) {
        }
    }

    protected Object lookup(String jndiPath) {
        try {
            return new InitialContext().lookup(jndiPath);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private static void createTable(Connection conn, String tableName) {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            stmt.executeUpdate(String.format("DROP TABLE IF EXISTS %s", tableName));

            stmt.executeUpdate(String.format(
                    "CREATE TABLE %s (id NUMBER(6) PRIMARY KEY, first_name VARCHAR2(20), last_name VARCHAR2(20) )", tableName));

            stmt.executeUpdate(
                    String.format("CREATE SEQUENCE %s_seq MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10", tableName));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            safeClose(stmt);
        }
    }

    private static void insertPersonIntoTable(Connection conn, String tableName, String first_name, String last_name) {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(String.format("INSERT INTO %s (id, first_name, last_name) VALUES ('%s_seq', '%s', '%s')", tableName,
                    tableName, first_name, last_name));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            safeClose(stmt);
        }
    }
}