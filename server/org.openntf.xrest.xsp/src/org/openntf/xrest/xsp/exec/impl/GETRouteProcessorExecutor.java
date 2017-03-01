package org.openntf.xrest.xsp.exec.impl;

import org.openntf.xrest.xsp.exec.Context;
import org.openntf.xrest.xsp.model.DataContainer;
import org.openntf.xrest.xsp.model.RouteProcessor;

public class GETRouteProcessorExecutor extends AbstractRouteProcessorExecutor {

	public GETRouteProcessorExecutor(Context context, RouteProcessor routerProcessor, String path) {
		super(context, routerProcessor, path);
	}

	@Override
	protected void executeMethodeSpecific(Context context, DataContainer<?> container) {
		try {
			setResultPayload(getRouteProcessor().getStrategyModel().buildResponse(context, getRouteProcessor(), container));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
