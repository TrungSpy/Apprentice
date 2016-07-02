package svc;

import java.util.ArrayList;
import java.util.Map;

import main.Start;

import org.json.JSONObject;

import servlet.Service;

public class Delete implements Service{
	
	private String talbe = null;
	
	public @Override String doIt(JSONObject arg) throws Exception {
		JSONObject param = arg.getJSONObject("param");
		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = arg.getJSONObject("deleteParam").getMap();
		System.out.println("delete from " + talbe + Start.DB.whereStr(new ArrayList<String>(dataMap.keySet())));
		Start.DB.update("delete from " + talbe + Start.DB.whereStr(new ArrayList<String>(dataMap.keySet())), dataMap);
		return new JSONObject().put("success", "OK").toString();
	}
	
	

	public String getTalbe() {
		return talbe;
	}

	public void setTalbe(String talbe) {
		this.talbe = talbe;
	}

}
