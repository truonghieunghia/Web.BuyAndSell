package groupbase.thn.web.libs;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResultData {
	private int mRowCount;
	private int mColumnCount;
	private ArrayList<DataRow> mListData;

	public ResultData( ResultSet resultSet) {
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			mColumnCount = rsmd.getColumnCount();
			mRowCount = 0;
			mListData = new ArrayList<DataRow>();
			while (resultSet.next()) {
				mRowCount++;
				DataRow obj = new DataRow();
				for (int columnIndex = 1; columnIndex <= mColumnCount; columnIndex++) {
					DataColumn dataColumn = new DataColumn();
					dataColumn.setValue(resultSet.getObject(columnIndex));
					dataColumn.setColumnName(rsmd.getColumnName(columnIndex));
					obj.addData(dataColumn);
				}

				mListData.add(obj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getColumnCount() {
		return mColumnCount;
	}

	public int getRowCount() {
		return mRowCount;
	}

	public DataColumn getDataColumn(int rowIndex,int columnIndex) {
		DataRow obj = mListData.get(rowIndex);
		return obj.getDataColumn(columnIndex);
	}

	public DataRow getDataRow(int rowIndex) {
		return mListData.get(rowIndex);
	}
	public <T> T getDataRow(int rowIndex,Class<T> entry) {
		return mListData.get(rowIndex).getObjec(entry);
	}
	public <T> ArrayList<T> getAllDataRow(Class<T> entry) {
		ArrayList<T> result = new ArrayList<T>();
		for(int i = 0 ; i < mRowCount; i++){
			result.add(getDataRow(i,entry));
		}
		return result;
	}
}
