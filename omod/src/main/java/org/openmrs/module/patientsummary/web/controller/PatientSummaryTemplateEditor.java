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

import java.util.Iterator;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummaryReportDefinition;
import org.openmrs.module.patientsummary.PatientSummaryTemplate;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
@Controller
@RequestMapping(PatientSummaryWebConstants.MODULE_URL + "templateEditor")
public class PatientSummaryTemplateEditor {
	
	@RequestMapping(value = "/newTemplate", method = RequestMethod.GET)
	public String newTemplate(@RequestParam String reportDefinitionUuid, ModelMap model) {
		PatientSummaryReportDefinition reportDefinition = getService().getPatientSummaryReportDefinitionByUuid(
		    reportDefinitionUuid);
		
		ReportDesign reportDesign = new ReportDesign();
		reportDesign.setName("Untitled");
		reportDesign.setRendererType(TextTemplateRenderer.class);
		reportDesign.setReportDefinition(reportDefinition);
		
		PatientSummaryTemplate template = new PatientSummaryTemplate(reportDesign);
		
		template = getService().savePatientSummaryTemplate(template);
		
		model.put("template", template);
		
		return PatientSummaryWebConstants.MODULE_URL + "templateEditor";
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void editTemplate(@RequestParam String templateUuid, ModelMap model) {
		PatientSummaryTemplate template = getService().getPatientSummaryTemplateByUuid(templateUuid);
		
		model.put("template", template);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveTemplate(@ModelAttribute("template") PatientSummaryTemplate formTemplate, byte[] resource, ModelMap model) {
		PatientSummaryTemplate template = getService().getPatientSummaryTemplateByUuid(formTemplate.getUuid());
		
		template.getReportDesign().setName(formTemplate.getReportDesign().getName());
		template.getReportDesign().setRendererType(formTemplate.getReportDesign().getRendererType());
		
		if (resource != null) {
			ReportDesignResource designResource = new ReportDesignResource();
			designResource.setContents(resource);
			template.getReportDesign().addResource(designResource);
		}
		
		getService().savePatientSummaryTemplate(template);
		
		model.put("template", template);
	}
	
	@RequestMapping(value = "/deleteResource", method = RequestMethod.POST)
	public void deleteResource(@RequestParam String templateUuid, @RequestParam String resourceUuid) {
		PatientSummaryTemplate template = getService().getPatientSummaryTemplateByUuid(templateUuid);
		
		Set<ReportDesignResource> resources = template.getReportDesign().getResources();
		for (Iterator<ReportDesignResource> it = resources.iterator(); it.hasNext();) {
			ReportDesignResource resource = it.next();
			if (resourceUuid.equals(resource.getUuid())) {
				it.remove();
				break;
			}
		}
	}
	
	private PatientSummaryService getService() {
		return Context.getService(PatientSummaryService.class);
	}
}
