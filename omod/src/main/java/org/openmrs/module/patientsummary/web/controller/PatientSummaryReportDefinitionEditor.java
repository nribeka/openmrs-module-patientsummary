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

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetUtil;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.patientsummary.PatientSummaryReportDefinition;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.web.util.ParameterUtil;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * For creating and editing {@link PatientSummaryReportDefinitionEditor}s
 */
@Controller
public class PatientSummaryReportDefinitionEditor {
	
	public final static String RD_ATTR = "PatientSummaryReportDefinitionEditor_rd";
	
	public final static String IS_UNSAVED_ATTR = "PatientSummaryReportDefinitionEditor_is_unsaved";
	
	Log log = LogFactory.getLog(getClass());
	
	@ModelAttribute("rd")
	public PatientSummaryReportDefinition getDataSetDefinition(@RequestParam(required = false, value = "uuid") String uuid,
	                                                     HttpSession session) {
		PatientSummaryReportDefinition rd = getFromSession(session);
		if (rd != null && uuid != null && !uuid.equals(rd.getUuid())) {
			removeFromSession(session);
			rd = null;
		}
		
		if (rd == null) {
			if (uuid != null) {
				rd = (PatientSummaryReportDefinition) Context.getService(PatientSummaryService.class)
				        .getPatientSummaryReportDefinitionByUuid(uuid);
				if (rd == null)
					throw new RuntimeException("No PatientDataSetDefinition found with uuid " + uuid);
				putInSession(session, rd, false);
			} else {
				rd = new PatientSummaryReportDefinition();
				rd.setName("Untitled");
				putInSession(session, rd, true);
			}
		}
		
		return rd;
	}
	
	@ModelAttribute("parameterTypes")
	public List<Option> getAvailableParameterTypes() {
		return ParameterUtil.getSupportedTypes();
	}
	
	@ModelAttribute("parameterCollectionTypes")
	public List<Option> getAvailableParameterCollectionTypes() {
		return ParameterUtil.getSupportedCollectionTypes();
	}
	
	private void putInSession(HttpSession session, PatientSummaryReportDefinition rd, boolean isUnsaved) {
		session.setAttribute(RD_ATTR, rd);
		session.setAttribute(IS_UNSAVED_ATTR, isUnsaved);
	}
	
	private void removeFromSession(HttpSession session) {
		session.removeAttribute(RD_ATTR);
		session.removeAttribute(IS_UNSAVED_ATTR);
	}
	
	private PatientSummaryReportDefinition getFromSession(HttpSession session) {
		try {
			return (PatientSummaryReportDefinition) session.getAttribute(RD_ATTR);
		}
		catch (ClassCastException ex) {
			// module has been reloaded
			removeFromSession(session);
			return null;
		}
	}
	
	@RequestMapping("/module/patientsummary/patientSummaryReportDefinitionEditor")
	public void showDataset(HttpSession session, Model model) {
		model.addAttribute("unsaved", session.getAttribute(IS_UNSAVED_ATTR));
		model.addAttribute("rd", session.getAttribute(RD_ATTR));
		model.addAttribute("dataDefinitionTypes",
		    PatientDataDefinition.class.getName() + "," + PersonDataDefinition.class.getName());
	}
	
	@RequestMapping(value = "/module/patientsummary/patientSummaryReportDefinitionEditor-nameDescription", method = RequestMethod.POST)
	public String changeNameAndDescription(@RequestParam("name") String name,
	                                       @RequestParam("description") String description, HttpSession session) {
		PatientSummaryReportDefinition rd = getFromSession(session);
		rd.setName(name);
		rd.setDescription(description);
		putInSession(session, rd, true);
		return "redirect:patientSummaryReportDefinitionEditor.form";
	}
	
	@RequestMapping(value = "/module/patientsummary/patientSummaryReportDefinitionEditor-addParam", method = RequestMethod.POST)
	public String addParameter(@RequestParam(value = "collectionType", required = false) Class<? extends Collection<?>> collectionType,
	                           @RequestParam(value = "parameterType", required = false) Class<?> parameterType,
	                           @RequestParam(value = "name", required = false) String name,
	                           @RequestParam(value = "label", required = false) String label,
	                           @RequestParam(value = "widgetConfiguration", required = false) String widgetConfiguration,
	                           HttpSession session) {
		if (parameterType == null || name == null || label == null) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Type, Name, and Label are required");
			return "redirect:patientSummaryReportDefinitionEditor.form";
		}
		
		Properties widgetConfig = null;
		if (ObjectUtil.notNull(widgetConfiguration)) {
			widgetConfig = WidgetUtil.parseInput(widgetConfiguration, Properties.class);
		}
		
		PatientSummaryReportDefinition rd = getFromSession(session);
		rd.addParameter(new Parameter(name, label, parameterType, collectionType, null, widgetConfig));
		putInSession(session, rd, true);
		return "redirect:patientSummaryReportDefinitionEditor.form";
	}
	
	@RequestMapping(value = "/module/patientsummary/patientSummaryReportDefinitionEditor-removeParam", method = RequestMethod.POST)
	public String removeParameter(@RequestParam("name") String name, HttpSession session) {
		PatientSummaryReportDefinition rd = getFromSession(session);
		rd.removeParameter(name);
		putInSession(session, rd, true);
		return "redirect:patientSummaryReportDefinitionEditor.form";
	}
	
	@RequestMapping(value = "/module/patientsummary/patientSummaryReportDefinitionEditor-save", method = RequestMethod.POST)
	public String save(HttpSession session) {
		PatientSummaryReportDefinition rd = getFromSession(session);
		Context.getService(PatientSummaryService.class).savePatientSummaryReportDefinition(rd);
		removeFromSession(session);
		return "redirect:patientSummaryReportDefinitionEditor.form?uuid=" + rd.getUuid();
	}
	
	@RequestMapping(value = "/module/patientsummary/patientSummaryReportDefinitionEditor-discard", method = RequestMethod.POST)
	public String discard(HttpSession session) {
		removeFromSession(session);
		return "redirect:/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition";
	}
	
}
