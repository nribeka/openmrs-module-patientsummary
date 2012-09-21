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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.patientsummary.PatientSummary;
import org.openmrs.module.patientsummary.PatientSummaryResult;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.patientsummary.util.ConfigurationUtil;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Processes request for viewing patient summaries
 */
@Controller
public class ViewPatientSummaryFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Receives requests to run a patient summary.
	 * @param patientId the id of patient whose summary you wish to view
	 * @param summaryId the id of the patientsummary you wish to view
	 * @param showParametersFormIfNecessary specifies if we need to prompt the user for required parameter values
	 */
	@RequestMapping("/module/" + ConfigurationUtil.MODULE_ID + "/viewPatientSummary")
	public void viewPatientSummary(ModelMap model, HttpServletRequest request, HttpServletResponse response,
									@RequestParam("patientId") Integer patientId,                       
									@RequestParam("summaryId") Integer summaryId,
									@RequestParam(required = false, value = "showParametersFormIfNecessary") boolean showParametersFormIfNecessary) throws IOException {
		
		PatientSummary ps = Context.getService(PatientSummaryService.class).getPatientSummary(summaryId);
		if (ps != null) {
			ReportDesign rd = ps.getReportDesign();
			
			// If we need to prompt the user for parameters, do this instead of rendering the summary
			if (showParametersFormIfNecessary && !rd.getReportDefinition().getParameters().isEmpty()) {
				model.addAttribute("showParametersForm", true);
				model.addAttribute("patientId", patientId);
				model.addAttribute("summaryId", summaryId);
				model.addAttribute("parameters", rd.getReportDefinition().getParameters());
			}
			// Otherwise, render the summary
			else {
				// Retrieve all parameter values from the request.  If any are invalid, return to form
				Map<String, Object> parameterValueMap = new HashMap<String, Object>();
				Map<String, String> parameterErrors = new HashMap<String, String>();
				if (!rd.getReportDefinition().getParameters().isEmpty()) {
					for (Parameter parameter : rd.getReportDefinition().getParameters()) {
						try {
							Object paramVal = WidgetUtil.getFromRequest(request, parameter.getName(), parameter.getType(), parameter.getCollectionType());
							if (paramVal == null) {
								parameterErrors.put(parameter.getName(), "error.required");
							}
							else {
								parameterValueMap.put(parameter.getName(), paramVal);
							}
						}
						catch (Exception e) {
							parameterErrors.put(parameter.getName(), "error.invalid");
						}
					}
				}
				
				// If there are no missing parameters
				if (parameterErrors.isEmpty()) {
					PatientSummaryService pss = Context.getService(PatientSummaryService.class);
					PatientSummaryResult result = pss.evaluatePatientSummary(ps, patientId, parameterValueMap);
					if (result.getErrorDetails() != null) {
						result.getErrorDetails().printStackTrace(response.getWriter());
					}
					else {
						response.setContentType(result.getContentType());
						response.getOutputStream().write(result.getRawContents());
						response.getOutputStream().flush();
					}
				} 
				else {
					model.addAttribute("showParametersForm", true);
					model.addAttribute("summaryid", summaryId);
					model.addAttribute("patientId", patientId);
					model.addAttribute("parameters", rd.getReportDefinition().getParameters());
					model.addAttribute("parameterValueMap", parameterValueMap);
					model.addAttribute("parameterErrors", parameterErrors);
				}
			}
		}
	}
}
