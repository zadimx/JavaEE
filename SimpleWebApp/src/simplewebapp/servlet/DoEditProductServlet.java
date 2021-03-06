package simplewebapp.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simplewebapp.beans.Product;
import simplewebapp.conn.ConnectionUtils;
import simplewebapp.utils.DBUtils;
import simplewebapp.utils.MyUtils;

@WebServlet(urlPatterns = {"/doEditProduct"})
public class DoEditProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public DoEditProductServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        Connection conn = MyUtils.getStoredConnection(request);

        String code = (String) request.getParameter("code");
        String name = (String) request.getParameter("name");
        String priceStr = (String) request.getParameter("price");
        float price = 0;
        try {
            price = Float.parseFloat(priceStr);
        } catch (Exception e) {
        }
        Product product = new Product(code, name, price);
        String errorString = null;

        try {
            if (conn == null) // if conn was not stored
                conn = ConnectionUtils.getConnection();
            DBUtils.updateProduct(conn, product);
        } catch (Exception e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        // store info to request attribute, before forward to views.
        request.setAttribute("errorString", errorString);
        request.setAttribute("product", product);

        // if error, forward to Edit page
        if (errorString != null) {
            RequestDispatcher dispatcher = request.getServletContext()
                .getRequestDispatcher("/WEB-INF/views/editProductView.jsp");
            dispatcher.forward(request, response);
        }

        // if everything good
        // redirect to the product listing page 
        else {
            response.sendRedirect(request.getContextPath() + "/productList");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}