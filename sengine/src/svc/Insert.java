package svc;

import main.Start;

import org.json.JSONObject;

import servlet.Service;

public class Insert implements Service{
	
	private String table = null;
	
	public @Override String doIt(JSONObject arg) throws Exception {
		JSONObject param = arg.getJSONObject("param");
		JSONObject insertData = param.getJSONObject("insertData");
		Integer newid = (Integer)Start.DB.queryForList("select nextval('seq_" + insertData + "') as newid", null).get(0).get("newid");
		insertData.put(table + "_id", newid);
		Start.DB.insert(table, insertData);
		
		return new JSONObject().put("success", "OK").toString();
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	
}
