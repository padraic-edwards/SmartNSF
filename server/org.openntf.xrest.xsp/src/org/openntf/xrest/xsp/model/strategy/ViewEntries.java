package org.openntf.xrest.xsp.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.exec.DatabaseProvider;
import org.openntf.xrest.xsp.exec.ExecutorException;
import org.openntf.xrest.xsp.exec.convertor.ViewEntryList2JsonConverter;
import org.openntf.xrest.xsp.exec.datacontainer.ViewEntryListDataContainer;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

import com.ibm.commons.util.io.json.JsonJavaArray;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

public class ViewEntries extends AbstractViewDatabaseStrategy implements StrategyModel<ViewEntryListDataContainer, JsonJavaArray> {

	@SuppressWarnings("unchecked")
	@Override
	public ViewEntryListDataContainer buildDataContainer(final Context context) throws ExecutorException {
		try {
			Database dbAccess = DatabaseProvider.INSTANCE.getDatabase(getDatabaseNameValue(context), context.getDatabase(), context
					.getSession());
			View viewAccess = dbAccess.getView(getViewNameValue(context));
			viewAccess.setAutoUpdate(false);
			List<List<Object>> entries = new ArrayList<List<Object>>();

			ViewNavigator vnav = viewAccess.createViewNav();
			ViewEntry entCurrent = vnav.getFirst();
			while (entCurrent != null && entCurrent.isValid()) {
				List<Object> columnValues = new ArrayList<Object>();
				columnValues.addAll(entCurrent.getColumnValues());
				entries.add(columnValues);
				ViewEntry nextEntry = vnav.getNext();
				// recycle!
				entCurrent.recycle();
				entCurrent = nextEntry;
			}
			vnav.recycle();
			return new ViewEntryListDataContainer(entries, viewAccess, dbAccess);
		} catch (Exception ex) {
			throw new ExecutorException(500, ex, "", "getmodel");
		}
	}

	@Override
	public JsonJavaArray buildResponse(final Context context, final RouteProcessor routeProcessor, final DataContainer<?> dc)
			throws NotesException {
		ViewEntryListDataContainer veldc = (ViewEntryListDataContainer) dc;
		ViewEntryList2JsonConverter d2jc = new ViewEntryList2JsonConverter(veldc, routeProcessor, context);
		return d2jc.buildJsonFromDocument();
	}
}