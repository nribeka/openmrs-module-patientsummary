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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummary;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The main controller.
 */
@Controller
public class PatientSummaryManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/patientsummary/manage")
	public void manage(ModelMap model,
					   @RequestParam(required=false, value="summaryId") Integer summaryId,
					   @RequestParam(required=false, value="patientId") Integer patientId) {
		
		PatientSummaryService pss = Context.getService(PatientSummaryService.class);
		List<PatientSummary> patientSummaries = pss.getAllPatientSummaries(false);
		PatientSummary summaryToPreview = (summaryId == null ? null :  pss.getPatientSummary(summaryId));

		model.addAttribute("patientSummaries", patientSummaries);
		model.addAttribute("summaryToPreview", summaryToPreview);
		model.addAttribute("patientId", patientId);
		
		String errorDetails = null;
		
		if (summaryToPreview != null && patientId == null) {
			errorDetails = "Please select a patient to preview a Patient Summary";
		}
		
		if (summaryToPreview != null && patientId != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ReportDesign design = summaryToPreview.getReportDesign();
				ReportDefinition d = design.getReportDefinition();
				EvaluationContext context = new EvaluationContext();
				Cohort baseCohort = new Cohort();
				baseCohort.addMember(patientId);
				context.setBaseCohort(baseCohort);
				ReportData data = Context.getService(ReportDefinitionService.class).evaluate(d, context);
				ReportRenderer renderer = design.getRendererType().newInstance();
				renderer.render(data, design.getUuid(), baos);
				String generatedSummary = baos.toString("UTF-8");
				model.addAttribute("generatedSummary", generatedSummary);
			}
			catch (Exception e) {
				errorDetails = e.getMessage();
			}
			finally {
				try {
					baos.close();
				}
				catch (Exception e) {}
			}
		}
		
		model.addAttribute("errorDetails", errorDetails);
	}
}
