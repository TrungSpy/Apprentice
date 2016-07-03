package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.JSONObject;
import org.json.JSONUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

public class DBAPI {
	
	private DataSource dataSource = null;
	private JdbcTemplate jdbcTemplate = null;
	private NamedParameterJdbcTemplate npJdbcTemplate = null;
	
	public void init() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}
	
	
	public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws Exception {
		return npJdbcTemplate.queryForList(sql, paramMap);
	}
	public int update(String sql, Map<String, ?> paramMap) {
		return npJdbcTemplate.update(sql, paramMap);
	}
	
	public int insert(String table, JSONObject data) throws Exception {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(this.jdbcTemplate).withTableName(table);
		Map<String, Object> dataMap = makeDataMap(table, data);
		return insert.execute(dataMap);
	}
	
	public Map<String, Object> makeDataMap(String table, JSONObject data) throws Exception {
		Map<String, Integer> columnsType = getColumnTypes(table);
		Map<String, Object> dataMap = JSONUtil.getMapFromObject(data);
		Iterator<?> it = data.keys();
		while (it.hasNext()) {
			String key = (String)it.next();
			if(columnsType.containsKey(key)) {
				Object value = toSQLType(data.getString(key), columnsType.get(key));
				dataMap.put(key, value);
			}else {
				dataMap.remove(key);
			}
		}
		return dataMap;
	}
	
	public Map<String, Integer> getColumnTypes(String table) throws Exception {
		Map<String, Integer> map = null;
		Connection con = null;
		ResultSet cols = null;
		try {
			con = DataSourceUtils.getConnection(dataSource);
			cols = con.getMetaData().getColumns(null, null, table, null);
			while (cols.next()) {
				if (cols.isFirst()) map = new HashMap<String, Integer>();
				String  name = cols.getString("COLUMN_NAME");
				Integer type = cols.getInt("DATA_TYPE");
				map.put(name, type);
			}
			JdbcUtils.closeResultSet(cols);
			DataSourceUtils.releaseConnection(con, dataSource);
		} catch (Exception e) {
			JdbcUtils.closeResultSet(cols);
			DataSourceUtils.releaseConnection(con, dataSource);
		}
		return map;
	}
	
	public StringBuilder whereStr(List<String> columns) throws Exception {
		StringBuilder where = new StringBuilder().append(" where ");
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) where.append(" and ");
			String columnName = columns.get(i);
			where.append(columnName + " = :" + columnName);
		}
		return where;
	}
	
	public static Object toSQLType(String strValue, int type) throws Exception {
		if (strValue == null || "".equals(strValue)) return null;
		switch (type) {
		case Types.INTEGER:
			return new Integer(strValue);
		case Types.TIMESTAMP:
			if (strValue.length() < 11) { // ! add time zone things later
				return new SimpleDateFormat("yyyy/MM/dd").parse(strValue);
			} else if (strValue.length() < 17) {
				return new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(strValue);
			} else if (strValue.length() < 20) {
				return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(strValue);
			}
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse(strValue);
		case Types.BOOLEAN:
			return new Boolean(strValue);
		case Types.BIT:
			return new Boolean(strValue);
		case Types.NUMERIC:
			return new Double(strValue);
		default:
			return strValue;
		}
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
