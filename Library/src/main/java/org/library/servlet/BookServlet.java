package org.library.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.library.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private final Connection connection = DatabaseManager.getInstance().getConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM books");
            ResultSet rs = stmt.executeQuery();
            StringBuilder html = new StringBuilder("<h1>Books</h1><ul>");
            while (rs.next()) {
                html.append("<li>").append(rs.getString("title")).append(" by ")
                        .append(rs.getString("author")).append("</li>");
            }
            html.append("</ul>");
            response.getWriter().println(html);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Database error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String code = request.getParameter("code");

        if (title == null || author == null || code == null) {
            response.sendError(422, "Missing parameters");
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO books (code, title, author) VALUES (?, ?, ?)");
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.executeUpdate();
            response.sendRedirect("/books");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(422, "Book code must be unique");
        }
    }
}
