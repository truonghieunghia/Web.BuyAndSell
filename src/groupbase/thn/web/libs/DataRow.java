package groupbase.thn.web.libs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataRow {
	private ArrayList<DataColumn> mListData = new ArrayList<DataColumn>();
	private ArrayList<String> mListColumnName = new ArrayList<String>();
	private HashMap<String, DataColumn> mListDataMap = new HashMap<String, DataColumn>();

	public ArrayList<DataColumn> getData() {
		return mListData;
	}

	public void addData(DataColumn dataColumn) {
		mListData.add(dataColumn);
		mListColumnName.add(dataColumn.getColumnName());
		mListDataMap.put(dataColumn.getColumnName(), dataColumn);
	}

	public DataColumn getDataColumn(int index) {
		return mListData.get(index);
	}
	public DataColumn getDataColumn(String columnName) {
		return mListDataMap.get(columnName);
	}
	public int getRowCount() {
		return mListData.size();
	}

	public <T> T getObjec(Class<T> entry) {
		try {
			List<Field> fields = new ArrayList<Field>();
			fields.addAll(Arrays.asList(entry.getDeclaredFields()));
			Object result = entry.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				ColumnNameAnnotation columnNameAnnotation = field
						.getAnnotation(ColumnNameAnnotation.class);
				if (columnNameAnnotation != null) {
					Object value = mListDataMap.get(columnNameAnnotation
							.ColumnName()).getValue(columnNameAnnotation.FieldType());
					if (value != null) {
						field.set(result, value);
					} else {
						field.set(result, null);
					}
				}
			}
			return entry.cast(result);
		} catch (Exception e) {
			return null;
		}

	}
}
