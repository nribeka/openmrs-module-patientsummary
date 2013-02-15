/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientsummary.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummaryTemplate;
import org.openmrs.module.patientsummary.api.PatientSummaryService;

public class PatientSummaryWebConfiguration {

	private static final Log log = LogFactory.getLog(PatientSummaryWebConfiguration.class);

	public static final String DEFAULT_TAB_NAME_OR_MSG_CODE = 		"patientsummary.defaultTabNameMessageCode";
	public static final String GP_PATIENT_SUMMARY_TAB_NAME = 		"patientsummary.tabNameOrMessageCode";
	private static final String GP_PATIENT_DASHBOARD_SUMMARIES = 	"patientsummary.patientDashboardSummaries";

	/**
	 * Returns the label or message code for the patient summary tab as defined by the value of the
	 * PatientSummaryConstants.GP_PATIENT_SUMMARY_TAB_NAME {@link GlobalProperty} otherwise defaults
	 * to PatientSummaryConstants.DEFAULT_TAB_NAME_OR_MSG_CODE
	 *
	 * @return the text or message code
	 */
	public static String getPatientSummaryTabName() {
		String code = Context.getAdministrationService().getGlobalProperty(GP_PATIENT_SUMMARY_TAB_NAME);
		if (StringUtils.isBlank(code)) {
			code = DEFAULT_TAB_NAME_OR_MSG_CODE;
		}
		return code;
	}

	/**
	 * @param name the name to set the Patient Summary Tab to
	 */
	public static void setPatientSummaryTabName(String name) {
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(GP_PATIENT_SUMMARY_TAB_NAME);
		if (gp == null) {
			gp = new GlobalProperty(GP_PATIENT_SUMMARY_TAB_NAME);
		}
		gp.setPropertyValue(name);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}

	/**
	 * @return true if the passed template is enabled on the dashboard
	 */
	public static boolean isEnabledOnPatientDashboard(PatientSummaryTemplate template) {
		String uuids = Context.getAdministrationService().getGlobalProperty(GP_PATIENT_DASHBOARD_SUMMARIES, "");
		return uuids.contains(template.getUuid());
	}

	/**
	 * @return all patient summary templates enabled on the patient dashboard, in the order specified
	 */
	public static List<PatientSummaryTemplate> getPatientSummaryTemplatesForDashboard() {
		List<PatientSummaryTemplate> ret = new ArrayList<PatientSummaryTemplate>();
		String gpValue = Context.getAdministrationService().getGlobalProperty(GP_PATIENT_DASHBOARD_SUMMARIES, "");
		if (StringUtils.isNotBlank(gpValue)) {
			String[] uuids = StringUtils.split(gpValue, ",");
			for (String uuid : uuids) {
				PatientSummaryTemplate t = Context.getService(PatientSummaryService.class).getPatientSummaryTemplateByUuid(uuid);
				if (t != null) {
					ret.add(t);
				}
				else {
					log.warn("Unable to find a Patient Summary Template with uuid " + uuid + ". Please check your " + GP_PATIENT_DASHBOARD_SUMMARIES + " global property.");
				}
			}
		}
		return ret;
	}
	
	public static void saveEnableTemplateOnPatientDashboard(PatientSummaryTemplate template, boolean enableOnPatientDashboard) {
		PatientSummaryService service = Context.getService(PatientSummaryService.class);
		String templateUuid = template.getUuid();
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(GP_PATIENT_DASHBOARD_SUMMARIES);
		if (gp == null) {
			gp = new GlobalProperty(GP_PATIENT_DASHBOARD_SUMMARIES);
		}
		String uuids = gp.getPropertyValue();
		if (uuids == null) {
			uuids = "";
		}
		
		uuids = StringUtils.deleteWhitespace(uuids);
		String[] uuidArray = uuids.split(",");
		List<String> uuidList = new ArrayList<String>();
		for (int index = 0; index < uuidArray.length; index++) {
			String uuid = uuidArray[index];
			if (StringUtils.isBlank(uuid)) {
				continue;
			}
			
			//Add only if uuid references an existing template.
			if (service.getPatientSummaryTemplateByUuid(uuid) != null) {
				uuidList.add(uuid);
			}
		}
		
		if (enableOnPatientDashboard) {
			//Add this template uuid if not already part of the global property value.
			if (!uuidList.contains(templateUuid)) {
				uuidList.add(templateUuid);
			}
		} else {
			//Remove this template uuid if already part of the global property value.
			if (uuidList.contains(templateUuid)) {
				uuidList.remove(templateUuid);
			}
		}
		
		uuids = StringUtils.join(uuidList, ",");
		gp.setPropertyValue(uuids);
		Context.getAdministrationService().saveGlobalProperty(gp);
	}
}
