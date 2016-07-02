package org.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JSONUtil {
	
	
	public static JSONArray jsonarrayDeepCopy( JSONArray jsonarray ) {
		JSONArray newjsonarray = null;
		try {
			newjsonarray = new JSONArray(jsonarray.toString());
		} catch (JSONException e) {
			newjsonarray = new JSONArray();
		}
		return newjsonarray;
	}

	public static ArrayList<Object> jsonArrayToArrayList( JSONArray jsonArray ) {
		ArrayList<Object> list = new ArrayList<Object>();
		if ( jsonArray != null && jsonArray.length() > 0 ) {
			try {
				for (int i = 0; i < jsonArray.length(); i++) {
					list.add( jsonArray.getJSONArray(i) );
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static JSONArray arrayListToJSONArray( List<Object> list ) {
		JSONArray jsonarray = new JSONArray();
		for (Object object : list) {
			try {
				jsonarray.put(jsonarray.length(), (JSONArray)object);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonarray;
	}

	public static JSONArray getJSONArrayFromJSONArray( JSONArray jarray, int index ) {

		JSONArray returnValue = new JSONArray();

		if ( jarray != null && jarray.length() > 0 ) {
			if ( index >= 0 && index < jarray.length() ) {
				try {
					returnValue = jarray.getJSONArray(index);
				} catch (JSONException e) {
					returnValue = new JSONArray();
				}
			}
		}

		return returnValue;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getMapFromObject(JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		Map map = new HashMap();
		Iterator it = jsonObj.keys();
		while (it.hasNext()) {
			String key = (String)it.next();
			map.put(key, jsonObj.get(key));
		}
		return map;
	}
}
