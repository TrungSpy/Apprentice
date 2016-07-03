package svc;

import java.util.HashMap;
import java.util.Map;

import main.Start;

import org.json.JSONObject;

import servlet.Service;

public class Delete implements Service{
	
	private String sql = null;
	
	public @Override String doIt(JSONObject arg) throws Exception {
		JSONObject param = arg.getJSONObject("param");
		Integer deleteKey = param.getInt("deleteKey");
		Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("deleteKey", deleteKey);
		Start.DB.update(sql,  pmap);
		return new JSONObject().put("success", "OK").toString();
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	
}
