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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummaryReportRenderer;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The main controller.
 */
@Controller
public class  PatientSummaryManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/patientsummary/manage")
	public void manage(ModelMap model,
					   @RequestParam(required=false, value="summaryId") Integer summaryId,
					   @RequestParam(required=false, value="patientId") Integer patientId) {
		
		List<ReportDesign> patientSummaries = new ArrayList<ReportDesign>();
		ReportDesign designToPreview = null;
		
		ReportService rs = Context.getService(ReportService.class);
		
		for (ReportDesign d : rs.getAllReportDesigns(false)) {
			if (PatientSummaryReportRenderer.class.isAssignableFrom(d.getRendererType())) {
				patientSummaries.add(d);
			}
			if (summaryId != null && d.getId().equals(summaryId)) {
				designToPreview = d;
			}
		}
		model.addAttribute("patientSummaries", patientSummaries);
		model.addAttribute("designToPreview", designToPreview);
		model.addAttribute("patientId", patientId);
		
		String errorDetails = null;
		
		if (designToPreview != null && patientId == null) {
			errorDetails = "Please select a patient to preview a Patient Summary";
		}
		
		if (designToPreview != null && patientId != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ReportDefinition d = designToPreview.getReportDefinition();
				EvaluationContext context = new EvaluationContext();
				Cohort baseCohort = new Cohort();
				baseCohort.addMember(patientId);
				context.setBaseCohort(baseCohort);
				ReportData data = Context.getService(ReportDefinitionService.class).evaluate(d, context);
				ReportRenderer renderer = designToPreview.getRendererType().newInstance();
				renderer.render(data, designToPreview.getUuid(), baos);
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
