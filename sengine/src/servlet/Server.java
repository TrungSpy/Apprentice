package servlet;

import main.KEY;
import main.Start;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class Server {
	private static Logger logger = Logger.getLogger(Server.class);

	public static String doIt(JSONObject arg) {
		
		String rtn = null;
		String sid = null;
		
		sid = arg.optString(KEY.SID);
		Object svc = Start.APP.getBean(sid);
		try {
			rtn = ((Service)svc).doIt(arg);
			return rtn;
		} catch (Exception e) {
			logger.error("error",e);
			rtn = e.getMessage();
			return rtn;
		}
		
		
	}
}
