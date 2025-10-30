package com.example.servlet;

import com.example.db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/execute")
public class ExecuteSQLServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql = req.getParameter("sql");
        if (sql == null || sql.trim().isEmpty()) {
            req.setAttribute("message", "SQL query is empty.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        String resultHtml = "";
        String message = "";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // By default PostgreSQL JDBC does not allow multiple statements in one execute call when using PreparedStatement.
            // For Statement, if multiple commands separated by ';' are sent, behavior depends. Here we will execute single statement at a time.
            boolean hasResultSet = stmt.execute(sql);

            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    resultHtml = renderResultSetAsHtmlTable(rs);
                }
            } else {
                int count = stmt.getUpdateCount();
                message = "Update count: " + count;
            }

        } catch (SQLException e) {
            message = "SQL Error: " + e.getMessage();
        }

        req.setAttribute("resultHtml", resultHtml);
        req.setAttribute("message", message);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private String renderResultSetAsHtmlTable(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        sb.append("<table border='1' cellpadding='4' cellspacing='0'>");
        sb.append("<tr style='background:#eee;'>");
        for (int i = 1; i <= cols; i++) {
            sb.append("<th>").append(escapeHtml(md.getColumnLabel(i))).append("</th>");
        }
        sb.append("</tr>");
        while (rs.next()) {
            sb.append("<tr>");
            for (int i = 1; i <= cols; i++) {
                Object val = rs.getObject(i);
                sb.append("<td>").append(escapeHtml(val == null ? "" : val.toString())).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'", "&#x27;");
    }
}
