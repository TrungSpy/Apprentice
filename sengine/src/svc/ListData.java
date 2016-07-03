package svc;

import java.util.List;
import java.util.Map;

import main.Start;

import org.json.JSONObject;

import servlet.Service;

public class ListData implements Service {
	
	private String sql = null;
	
	@SuppressWarnings("unchecked")
	public @Override String doIt(JSONObject arg) throws Exception {
		JSONObject param = arg.getJSONObject("param");
		JSONObject where = param.getJSONObject("where");
		List<Map<String, Object>> list = null;
		if(where != null) {
			Map<String, Object> pmap = (Map<String, Object>)where.getMap();
			list = Start.DB.queryForList(sql, pmap);
		}else {
			list = Start.DB.queryForList(sql, null);
		}
		
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
