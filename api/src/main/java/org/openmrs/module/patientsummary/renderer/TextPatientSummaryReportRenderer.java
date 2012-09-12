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
package org.openmrs.module.patientsummary.renderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;

/**
 * Report Renderer implementation that supports rendering to a text-based patient summary
 */
@Handler
@Localized("patientsummary.TextPatientSummaryReportRenderer")
public class TextPatientSummaryReportRenderer extends TextTemplateRenderer implements PatientSummaryReportRenderer {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public TextPatientSummaryReportRenderer() {
		super();
	}

	/** 
	 * @see ReportRenderer#render(ReportData, String, OutputStream)
	 */
	public void render(ReportData reportData, String argument, OutputStream out) throws IOException, RenderingException {
		
		Writer pw = null;
		
		try {
			ReportDesign design = getDesign(argument);
			ReportDesignResource r = getTemplate(design);
			String templateContents = new String(r.getContents(), "UTF-8");
			
			Map<String, Object> replacements = getBaseReplacementData(reportData, design);
			
			for (String dsName : reportData.getDataSets().keySet()) {
				DataSet ds = reportData.getDataSets().get(dsName);
				int num = 0;
				for (DataSetRow row : ds) {
					if (num++ > 0) {
						throw new RuntimeException("Currently only datasets with one row are supported.");
					}
					replacements.putAll(getReplacementData(reportData, design, dsName, row));
				}
			}
			
			// TODO: Abstract this away so that we could swap out other templating engine technologies
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.CommonsLogLogChute");
			ve.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "patientsummary_velocity");
			ve.init();
			VelocityContext velocityContext = new VelocityContext();
			velocityContext.put("df", new SimpleDateFormat("dd/MMM/yyyy"));
			
			for (Map.Entry<String, Object> e : replacements.entrySet()) {
				velocityContext.put(e.getKey().replace(".", "-"), e.getValue());
			}
			
			StringWriter writer = new StringWriter();
			ve.evaluate(velocityContext, writer, getClass().getName(), templateContents);
			templateContents = writer.toString();
			
			String prefix = getExpressionPrefix(design);
			String suffix = getExpressionSuffix(design);
			
			pw = new OutputStreamWriter(out,"UTF-8");
			pw.write(EvaluationUtil.evaluateExpression(templateContents, replacements, prefix, suffix).toString());
		}
		catch (Exception e) {
			throw new RenderingException("Unable to render results due to: " + e, e);
		}
		finally {
			IOUtils.closeQuietly(pw);
		}
	}
}
