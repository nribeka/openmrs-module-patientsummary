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
package org.openmrs.module.patientsummary.web.controller;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummaryReportDefinition;
import org.openmrs.module.patientsummary.PatientSummaryTemplate;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.patientsummary.web.controller.util.DataSchemaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * For creating and editing {@link PatientSummaryEditor}s
 */
@Controller
@RequestMapping(PatientSummaryWebConstants.MODULE_URL + "editSummary")
public class PatientSummaryEditor {
	
	/**
	 * Retrieves either an existing or new report to edit
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void editReport(ModelMap model, @RequestParam(required = false, value = "uuid") String uuid) {
		if (uuid == null) {
			return;
		}
		
		PatientSummaryReportDefinition report = getService().getPatientSummaryReportDefinitionByUuid(uuid);
		model.addAttribute("report", report);
		
		model.addAttribute("dataSchema", DataSchemaUtil.getDataSchema(report));
		
		List<PatientSummaryTemplate> templates = getService().getPatientSummaryTemplates(report, false);
		model.addAttribute("templates", templates);
	}
	
	private PatientSummaryService getService() {
		return Context.getService(PatientSummaryService.class);
	}
	
	@RequestMapping(value = "/editDataSchema")
	public String editDataSchema(String uuid) {
		PatientSummaryReportDefinition report = getService().getPatientSummaryReportDefinitionByUuid(uuid);
		
		getService().savePatientSummaryReportDefinition(report);
		
		return "redirect:/module/reporting/datasets/patientDataSetEditor.form?uuid="
		        + report.getPatientDataSetDefinition().getUuid()
		        + "&successUrl=/module/patientsummary/editSummary.form?uuid=" + report.getUuid()
		        + "&discardUrl=/module/patientsummary/editSummary.form?uuid=" + report.getUuid();
	}
}
