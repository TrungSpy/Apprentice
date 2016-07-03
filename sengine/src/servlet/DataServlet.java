package servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.KEY;

import org.apache.log4j.Logger;
import org.json.JSONObject;


public class DataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public @Override void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = null;
		try {
			request.setCharacterEncoding("UTF-8");
			String sessionId = request.getSession(true).getId();
			String sid = request.getParameter(KEY.SID);
			
			if(sid == null || sid.length() < 1 ) {
				throw new Exception("The "+KEY.SID+" must be set !!");
			}
			
			JSONObject param = new JSONObject();
			param.put(KEY.SESSION_ID, sessionId);
			param.put(KEY.SID, sid);
			JSONObject paramData = new JSONObject();
			dealParam(paramData, request);
			param.put("param", paramData);
			
			logger.info("param----------------");
			logger.info(param);
			
			
			String rtnVal = Server.doIt(param);
			
			logger.info("return---------------");
			logger.info(rtnVal);
			logger.info("-----------------------------------------------------------------");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("cache-control", "no-cache");
			writer = new PrintWriter(new OutputStreamWriter( response.getOutputStream(), "UTF-8" ));
			writer.write(rtnVal);
			writer.flush();
			writer.close();

		} catch (Exception e) {
			if (writer != null) writer.close();
			throw new ServletException(e);
		}
	}
	
	protected void dealParam (JSONObject param, HttpServletRequest request) throws Exception {
		Map<?,?> map = request.getParameterMap();
		for (Object key : map.keySet()) {
			String[] paramValues = (String[])map.get(key);
			if (paramValues != null ) {
				if (paramValues.length == 1) {
					param.put((String)key, paramValues[0].matches("^\\{.*\\}$")?new JSONObject(paramValues[0]):paramValues[0]);
				} else {
					param.put((String)key, paramValues);
				}
			}
		}
	}
	
	
	
	
}
