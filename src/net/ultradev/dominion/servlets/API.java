package net.ultradev.dominion.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ultradev.dominion.GameServer;
import net.ultradev.dominion.game.local.LocalGame;

/**
 * Servlet implementation class Test
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/API", "/api"}, asyncSupported = true)
public class API extends HttpServlet {

	//AJAX CALLS
		// Create game > ?action=create&type=local
		// Destroy game > ?action=destroy&type=local
		// Game info > ?action=info&type=local
		// End phase(action/buys/turn) > ?action=endphase&type=local
		// Buy card > ?action=buycard&type=local&card=copper
		// Select card > ?action=selectcard&type=local&card=copper
		// Set card set > ?action=setconfig&type=local&key=setcardset&value=test
		// Play card > ?action=playcard&type=local&card=copper
		// Stop an active action & continue card actions > ?action=stopaction&type=local
	
	//DEPRECATED CALLS
		// Set config > ?action=setconfig&type=local&key=addcard&value=Cellar
		// Add player > ?action=addplayer&type=local&name=Bob | Doen wanneer de user finaal is
		// Start game > ?action=start&type=local
	// -> instead, use > ?action=setup&type=local&players="Bob","Nick"&cardset=firstgame
	
	GameServer gs;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public API() {
        super();
    }
    
    public void init() throws ServletException {
        this.gs = new GameServer();
    }
    
    public GameServer getGameServer() {
    	return gs;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("application/json");
		res.setCharacterEncoding("utf-8");
		
		if(req == null || req.getParameter("action") == null || req.getParameter("type") == null) {
			res.getWriter().append(getGameServer().getGameManager().getInvalid("Need a type & action").toString());
			return;
		}
		
		String type = req.getParameter("type").toLowerCase();
		if(type.equals("local")) {
			LocalGame g = getGameServer().getGameManager().getGame(req.getSession());
			res.getWriter().append(getGameServer().getGameManager().handleLocalRequest(getParameters(req), g, req.getSession()).toString());
			return;
		} else if(type.equals("mp")) {
			res.getWriter().append(getGameServer().getGameManager().getInvalid("Multiplayer is unsupported at this time").toString());
		}
		
		res.getWriter().append(getGameServer().getGameManager().getInvalid("Unhandled game type: " + type).toString());
	}
	
	public Map<String, String> getParameters(HttpServletRequest req) {
		Map<String, String> params = new HashMap<>();
		for(String s : req.getParameterMap().keySet()) {
			params.put(s, req.getParameter(s));
		}
		return params;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
