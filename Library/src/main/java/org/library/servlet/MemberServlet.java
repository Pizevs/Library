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

@WebServlet("/members")
public class MemberServlet extends HttpServlet {

    private final Connection connection = DatabaseManager.getInstance().getConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM members");
            ResultSet rs = stmt.executeQuery();
            StringBuilder html = new StringBuilder("<h1>Members</h1><ul>");
            while (rs.next()) {
                html.append("<li>").append(rs.getString("name"))
                        .append(" (").append(rs.getString("email")).append(")</li>");
            }
            html.append("</ul>");
            response.getWriter().println(html);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Database error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name == null || email == null) {
            response.sendError(422, "Missing parameters");
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO members (name, email) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            response.sendRedirect("/members");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(422, "Email must be unique");
        }
    }
}
