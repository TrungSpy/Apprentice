package svc;

import java.util.List;
import java.util.Map;

import main.Start;

import org.json.JSONObject;

import servlet.Service;

public class ListData implements Service {
	
	private String sql = null;
	
	public @Override String doIt(JSONObject arg) throws Exception {
		
		List<Map<String, Object>> list = Start.DB.queryForList(sql, null);
		
		JSONObject rtn =  new JSONObject().put("dataList", list);
		return rtn.toString();
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}


}
