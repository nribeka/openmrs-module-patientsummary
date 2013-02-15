package org.openmrs.module.patientsummary.web.extension;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;

public class PatientDashboardActions extends Extension {

	@Override
	public String getOverrideContent(String bodyContent) {
		StringBuilder sb = new StringBuilder();
		String gp = Context.getAdministrationService().getGlobalProperty("patientsummary.enableDefaultSummary");
		if ("true".equals(gp)) {
			sb.append("<tr class=\"patientActionsRow\"><td>");
			sb.append("<a href=\"javascript:window.open('");
			sb.append("module/patientsummary/defaultSummary.form");
			sb.append("?patientId=").append(getParameterMap().get("patientId")).append("'");
			sb.append(", 'summaryWindow', 'toolbar=no,width=660,height=600,resizable=yes,scrollbars=yes').focus()\">");
			sb.append("Get Patient Summary");
			sb.append("</a>");
			sb.append("</td><tr>");
		}
		return sb.toString();
	}

	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}
}
