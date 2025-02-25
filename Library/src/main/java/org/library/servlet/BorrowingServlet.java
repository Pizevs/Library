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
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet("/borrow")
public class BorrowingServlet extends HttpServlet {

    private final Connection connection = DatabaseManager.getInstance().getConnection();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM borrowings WHERE return_date IS NULL");
            ResultSet rs = stmt.executeQuery();
            StringBuilder html = new StringBuilder("<h1>Borrowed Books</h1><ul>");
            while (rs.next()) {
                html.append("<li>Book Code: ").append(rs.getString("book_code"))
                        .append(", Member ID: ").append(rs.getInt("member_id"))
                        .append(", Borrowed on: ").append(rs.getTimestamp("borrow_date"))
                        .append("</li>");
            }
            html.append("</ul>");
            response.getWriter().println(html);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Database error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookCode = request.getParameter("bookCode");
        String memberId = request.getParameter("memberId");

        if (bookCode == null || memberId == null) {
            response.sendError(422, "Missing parameters");
            return;
        }

        try {
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT * FROM borrowings WHERE book_code = ? AND return_date IS NULL"
            );
            checkStmt.setString(1, bookCode);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                response.sendError(422, "This book is already borrowed");
                return;
            }

            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO borrowings (book_code, member_id, borrow_date) VALUES (?, ?, ?)"
            );
            stmt.setString(1, bookCode);
            stmt.setInt(2, Integer.parseInt(memberId));
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
            response.sendRedirect("/borrow");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error borrowing book");
        }
    }
}
