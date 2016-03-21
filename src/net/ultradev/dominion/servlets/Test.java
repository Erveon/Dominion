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
		
		if(req.getParameter("action") == null || req.getParameter("type") == null) {
			res.getWriter().append("Invalid request: need action & type");
			return;
		}
		
		String action = req.getParameter("action").toLowerCase();
		String type = req.getParameter("type").toLowerCase();
		
		switch(action) {
			case "create":
				if(type.equals("local")) {
					LocalGame.createGame(req.getSession());
					res.getWriter().append("OK");
					return;
				}
			case "info":
				if(type.equals("local")) {
					LocalGame g = LocalGame.getGame(req.getSession());
					//TODO return game info
					if(g == null) {
						res.getWriter().append("Invalid request: No local game running");
					} else {
						res.getWriter().append(g.getAsJson().toString());
					}
					return;
				}
			case "setconfig":
				if(type.equals("local")) {
					if(req.getParameter("key") == null || req.getParameter("value") == null) {
						res.getWriter().append("Invalid request: need key & value pair");
						return;
					}
					LocalGame g = LocalGame.getGame(req.getSession());
					if(g == null) {
						res.getWriter().append("Invalid request: No local game running");
					} else {
						g.getConfig().handle(req.getParameter("key"), req.getParameter("value"));
						res.getWriter().append("OK");
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
