package com.example.cli;

import com.example.db.DBUtil;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {

    // шаблони для команд
    private static final Pattern INSERT_PATTERN =
            Pattern.compile("(?i)^insert\\s+(\\w+)\\s*\\((.*)\\)\\s*;?$");
    private static final Pattern DELETE_PATTERN =
            Pattern.compile("(?i)^delete\\s+(\\w+)\\s*\\((.*)\\)\\s*;?$");
    private static final Pattern SELECT_PATTERN =
            Pattern.compile("(?i)^read\\s+(\\w+)\\s*(?:\\((.*)\\))?\\s*;?$");
    private static final Pattern UPDATE_PATTERN =
            Pattern.compile("(?i)^update\\s+(\\w+)\\s*\\((.*)\\)\\s*;?$");

    // парсинг пари key='value'
    private Map<String, String> parseKvPairs(String body) {
        Map<String,String> map = new LinkedHashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        // розбір, але з підтримкою лапок: знаходимо key='value' повторно
        Pattern kv = Pattern.compile("(\\w+)\\s*=\\s*'(.*?)'\\s*(?:,|$)");
        Matcher m = kv.matcher(body + ","); // додаємо кому для останнього
        while (m.find()) {
            map.put(m.group(1), m.group(2));
        }
        return map;
    }

    public String execute(String command) {
        command = command.trim();
        try {
            Matcher mi = INSERT_PATTERN.matcher(command);
            Matcher md = DELETE_PATTERN.matcher(command);
            Matcher mr = SELECT_PATTERN.matcher(command);
            Matcher mu = UPDATE_PATTERN.matcher(command);

            if (mi.matches()) {
                String table = mi.group(1);
                String body = mi.group(2);
                return doInsert(table, parseKvPairs(body));
            } else if (md.matches()) {
                String table = md.group(1);
                String body = md.group(2);
                Map<String,String> where = parseKvPairs(body);
                return doDelete(table, where);
            } else if (mr.matches()) {
                String table = mr.group(1);
                String body = mr.group(2);
                Map<String,String> where = parseKvPairs(body);
                return doRead(table, where);
            } else if (mu.matches()) {
                String table = mu.group(1);
                String body = mu.group(2);
                // очікуємо id=... і інші поля для оновлення
                Map<String,String> kv = parseKvPairs(body);
                return doUpdate(table, kv);
            } else {
                return "Unknown command format.";
            }
        } catch (SQLException e) {
            return "SQL Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // INSERT через PreparedStatement
    private String doInsert(String table, Map<String,String> kv) throws SQLException {
        if (kv.isEmpty()) return "No data to insert.";

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner vals = new StringJoiner(", ");
        List<String> params = new ArrayList<>();
        for (Map.Entry<String,String> e : kv.entrySet()) {
            cols.add(e.getKey());
            vals.add("?");
            params.add(e.getValue());
        }
        String sql = "INSERT INTO " + table + " (" + cols.toString() + ") VALUES (" + vals.toString() + ")";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setString(i+1, params.get(i));
            int updated = ps.executeUpdate();
            return "Inserted rows: " + updated;
        }
    }

    // DELETE за умовою (має бути хоча б один ключ)
    private String doDelete(String table, Map<String,String> where) throws SQLException {
        if (where.isEmpty()) return "Delete requires a condition (e.g. id='1001').";

        StringBuilder sb = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ");
        List<String> params = new ArrayList<>();
        Iterator<Map.Entry<String,String>> it = where.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> e = it.next();
            sb.append(e.getKey()).append(" = ?");
            params.add(e.getValue());
            if (it.hasNext()) sb.append(" AND ");
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i+1, params.get(i));
            int count = ps.executeUpdate();
            return "Deleted rows: " + count;
        }
    }

    // Read (SELECT * FROM table WHERE ...), якщо умова пуста — повернути всі рядки
    private String doRead(String table, Map<String,String> where) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(table);
        List<String> params = new ArrayList<>();
        if (!where.isEmpty()) {
            sb.append(" WHERE ");
            Iterator<Map.Entry<String,String>> it = where.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> e = it.next();
                sb.append(e.getKey()).append(" = ?");
                params.add(e.getValue());
                if (it.hasNext()) sb.append(" AND ");
            }
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setString(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetToString(rs);
            }
        }
    }

    // Update: очікуємо id=... в параметрах або передаємо id останнім
    private String doUpdate(String table, Map<String,String> kv) throws SQLException {
        if (kv.isEmpty()) return "No data to update.";
        // припустимо, що id має бути в kv і використовується в WHERE
        if (!kv.containsKey("id")) return "Update requires id='...' in parameters.";

        String idValue = kv.get("id");
        Map<String,String> toSet = new LinkedHashMap<>(kv);
        toSet.remove("id");

        if (toSet.isEmpty()) return "No fields to update.";

        StringJoiner sets = new StringJoiner(", ");
        List<String> params = new ArrayList<>();
        for (Map.Entry<String,String> e : toSet.entrySet()) {
            sets.add(e.getKey() + " = ?");
            params.add(e.getValue());
        }

        String sql = "UPDATE " + table + " SET " + sets.toString() + " WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            for (String p : params) ps.setString(i++, p);
            ps.setString(i, idValue);
            int count = ps.executeUpdate();
            return "Updated rows: " + count;
        }
    }

    private String resultSetToString(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        // header
        for (int i = 1; i <= cols; i++) {
            sb.append(md.getColumnLabel(i)).append("\t");
        }
        sb.append("\n");
        while (rs.next()) {
            for (int i = 1; i <= cols; i++) {
                sb.append(rs.getString(i)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
