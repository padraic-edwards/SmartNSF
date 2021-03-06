package org.openntf.xrest.xsp.exec.convertor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.convertor.datatypes.ColumnInfo;
import org.openntf.xrest.xsp.model.RouteProcessor;

import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

public class ViewEntryList2JsonConverterBase {

	protected final RouteProcessor routeProcessor;
	protected final View view;
	protected final Context context;
	protected List<ColumnInfo> columnInfo;
	protected Map<String, ColumnInfo> columnInfoMap;

	public ViewEntryList2JsonConverterBase(final RouteProcessor routeProcessor, final View view, final Context context) {
		this.routeProcessor = routeProcessor;
		this.view = view;
		this.context = context;
	}

	protected Map<String, ColumnInfo> getColumnInfoMap() throws NotesException {
		if (columnInfoMap == null) {
			columnInfoMap = new LinkedHashMap<String, ColumnInfo>();
			for (ColumnInfo columnInfo : getColumnInfos()) {
				columnInfoMap.put(columnInfo.getItemName(), columnInfo);
			}
		}
		return columnInfoMap;
	}

	private List<ColumnInfo> getColumnInfos() throws NotesException {
		if (columnInfo == null) {
			@SuppressWarnings("unchecked")
			Vector<ViewColumn> columns = view.getColumns();
			List<ColumnInfo> result = new ArrayList<ColumnInfo>(columns.size() + 1);
			int constCols = 0;
			for (ViewColumn col : columns) {
				ColumnInfo ci = new ColumnInfo(col, context);
				result.add(ci);
				if (ci.getColumnValuesIndex() == 65535) {
					constCols++;
				}
			}
			// add "system @unid" column at last index position
			// to get real last index we need tu substract all const columns
			result.add(new ColumnInfo("@unid", result.size() - constCols, null));
			columnInfo = result;
		}
		// System.out.println("DEBUG:" + columnInfo);
		return columnInfo;
	}

}