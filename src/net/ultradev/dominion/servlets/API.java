package net.ultradev.dominion.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ultradev.dominion.game.GameManager;
import net.ultradev.dominion.game.card.CardManager;
import net.ultradev.dominion.game.local.LocalGame;

/**
 * Servlet implementation class Test
 */
@WebServlet({ "/API", "/api" })
public class API extends HttpServlet {
	
	//Auto generated ID
	private static final long serialVersionUID = 1L;

	//AJAX CALLS
		// Create game > ?action=create&type=local
		// Destroy game > ?action=destroy&type=local
		// Game info > ?action=info&type=local
		// Set config > ?action=setconfig&type=local&key=addcard&value=Cellar
		// Add player > ?action=addplayer&type=local&name=Bob | Doen wanneer de user finaal is
		// Start game > ?action=start&type=local
		// End turn > ?action=endturn&type=local
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public API() {
        super();
    }
    
    public void init() throws ServletException {
        CardManager.setup();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("utf-8");
		
		if(req == null || req.getParameter("action") == null || req.getParameter("type") == null) {
			res.getWriter().append(GameManager.getInvalid("Need a type & action").toString());
			return;
		}
		
		String type = req.getParameter("type").toLowerCase();
		if(type.equals("local")) {
			LocalGame g = LocalGame.getGame(req.getSession());
			res.getWriter().append(GameManager.handleLocalRequest(getParameters(req), g, req.getSession()).toString());
			return;
		}
		
		res.getWriter().append(GameManager.getInvalid("Unhandled game type: " + type).toString());
	}
	
	public Map<String, String> getParameters(HttpServletRequest req) {
		Map<String, String> params = new HashMap<>();
		for(String s : req.getParameterMap().keySet())
			params.put(s, req.getParameter(s));
		return params;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
