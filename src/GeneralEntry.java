
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneralEntry {

	public static void main(String[] args) throws Exception {
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		ArrayList<String> listTable = new ArrayList<String>();
		HashMap<String, Object> tabledetail = new HashMap<String, Object>();
		String showtable = "show tables from buyandsell";
		String showcolumn = "SHOW COLUMNS FROM ";
		// TODO Auto-generated method stub
		String Path = System.getProperty("user.dir") + "/src/groupbase/thn/web/buyandsell/entry/";
		String Pathjson = System.getProperty("user.dir") + "/src/groupbase/thn/web/buyandsell/api/result/json/";

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager.getConnection("jdbc:mysql://localhost/BuyAndSell?" + "user=root&password=");
			statement = connect.createStatement();
			resultSet = statement.executeQuery(showtable);
			while (resultSet.next()) {
				System.out.println(resultSet.getString("Tables_in_buyandsell"));
				listTable.add(resultSet.getString("Tables_in_buyandsell"));
			}
			for (String table : listTable) {
				resultSet = statement.executeQuery(showcolumn + table);
				ArrayList<Object> listcolumn = new ArrayList<Object>();
				while (resultSet.next()) {
					HashMap<String, String> obj = new HashMap<String, String>();
					obj.put("Field", resultSet.getString("Field"));
					obj.put("Type", resultSet.getString("Type"));
					obj.put("Null", resultSet.getString("Null"));
					obj.put("Key", resultSet.getString("Key"));
					obj.put("Default", resultSet.getString("Default"));
					obj.put("Extra", resultSet.getString("Extra"));
					listcolumn.add(obj);
				}
				tabledetail.put(table, listcolumn);
			}
			resultSet.close();
			statement.close();
			connect.close();
			//
			for (String table : listTable) {
				PrintWriter writer = new PrintWriter(Pathjson + table + "EntryJson.java");
				writer.println("package groupbase.thn.web.buyandsell.api.result.json;");
				writer.println();
				writer.println("import java.util.ArrayList;");
				writer.println();
				writer.println("import groupbase.thn.web.buyandsell.entry." + table + "Entry;");
				writer.println();
				writer.println("public class " + table + "EntryJson {");
				writer.println("\tpublic ArrayList<" + table + "Entry> " + table.toLowerCase() + "_list;");
				writer.println("}");
				writer.close();
			}
			for (String table : listTable) {
				PrintWriter writer = new PrintWriter(Path + table + "Entry.java");
				writer.println("package groupbase.thn.web.buyandsell.entry;");
				writer.println();

				writer.println("import java.io.Serializable;");
				writer.println();
				writer.println("import groupbase.thn.web.libs.ColumnNameAnnotation;");
				writer.println("import groupbase.thn.web.libs.EntryAnnotation;");
				writer.println();
				writer.println("@EntryAnnotation(TableName = \"" + table + "\")");
				writer.println("public class " + table + "Entry implements Serializable {");
				writer.println();
				writer.println("\tprivate static final long serialVersionUID = 1L;");

				@SuppressWarnings("unchecked")
				ArrayList<Object> listcolumn = (ArrayList<Object>) tabledetail.get(table);
				for (Object obj : listcolumn) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> columnDetail = (HashMap<String, Object>) obj;
					String Field = columnDetail.get("Field").toString();
					writer.println("\tpublic static String " + Field + "_Field = \"" + Field + "\";");

				}
				writer.println();
				for (Object obj : listcolumn) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> columnDetail = (HashMap<String, Object>) obj;
					String Field = columnDetail.get("Field").toString();
					String Type = columnDetail.get("Type").toString();
					String Key = columnDetail.get("Key").toString();
					String Extra = columnDetail.get("Extra").toString();
					int start = Type.indexOf('(');
					int end = Type.indexOf(')');
					String length;
					if (start != -1) {
						length = Type.substring(start + 1, end);
					} else {
						length = "0";
					}
					String type = "String";
					String isPrimary;
					String isAuto_increment;
					if (Type.contains("int")) {
						if (Type.contains("bigint")) {
							Type = "Long.class";
							type = "long";
						} else {
							Type = "Integer.class";
							type = "int";
						}
					}

					if (Type.contains("varchar") | Type.contains("tinytext") | Type.contains("text")
							| Type.contains("char")) {
						Type = "String.class";
						type = "String";
					}
					isPrimary = Key.length() > 0 ? "true" : "false";
					isAuto_increment = Extra.length() > 0 ? "true" : "false";

					String data = "\t@ColumnNameAnnotation(ColumnName = \"" + Field + "\", FieldType = " + Type;
					if (isPrimary.equals("true")) {
						data = data + ", isPrimary = " + isPrimary;
					}
					if (isAuto_increment.equals("true")) {
						data = data + ", isAuto_increment = " + isAuto_increment;
					}
					if (!length.equals("0")) {
						data = data + ", MaxLength = " + length;
					}
					data = data + ")";
					writer.println(data);
					writer.println("\tprivate " + type + " " + Field + ";");
					writer.println();

				}

				for (Object obj : listcolumn) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> columnDetail = (HashMap<String, Object>) obj;
					String Field = columnDetail.get("Field").toString();
					String Type = columnDetail.get("Type").toString();
					String type = "String";
					String setField = Field.substring(1);
					String temp = Field.substring(0, 1).toLowerCase();

					setField = temp + setField;
					if (Type.contains("int")) {
						if (Type.contains("bigint")) {
							Type = "Long.class";
							type = "long";
						} else {
							Type = "Integer.class";
							type = "int";
						}
					}

					if (Type.contains("varchar") | Type.contains("tinytext") | Type.contains("text")
							| Type.contains("char")) {
						Type = "String.class";
						type = "String";
					}
					writer.println("\tpublic " + type + " get" + Field + "() {");
					writer.println("\t\treturn " + Field + ";");
					writer.println("\t}");
					writer.println();
					writer.println("\tpublic void set" + Field + "(" + type + " " + setField + ") {");
					writer.println("\t\t" + Field + " = " + setField + ";");
					writer.println("\t}");
					writer.println();
				}
				writer.println("}");
				writer.close();
			}

			//
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

}
