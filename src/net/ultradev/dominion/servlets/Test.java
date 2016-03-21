package net.ultradev.dominion.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ultradev.dominion.game.LocalGame;

/**
 * Servlet implementation class Test
 */
@WebServlet({ "/Test", "/test" })
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("utf-8");
		
		if(req.getParameter("action") == null) {
			res.getWriter().append("Invalid request 1");
			return;
		}
		
		String type = req.getParameter("type");;
		
		switch(req.getParameter("action").toLowerCase()) {
			case "create":
				if(type != null && type.toLowerCase().equals("local")) {
					LocalGame.createGame(req.getSession());
					res.getWriter().append("OK");
					return;
				}
			case "info":
				type = req.getParameter("type");
				if(type != null && type.toLowerCase().equals("local")) {
					LocalGame g = LocalGame.getGame(req.getSession());
					if(g == null) {
						res.getWriter().append("No local game running");
					} else {
						res.getWriter().append("local game found with id: " + req.getSession().getId());
					}
					return;
				}
				
		}
		
		res.getWriter().append("Invalid request: EOL");
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
