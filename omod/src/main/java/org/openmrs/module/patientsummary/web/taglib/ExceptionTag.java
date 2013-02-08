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
package org.openmrs.module.patientsummary.web.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.renderer.template.TemplateEvaluationException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class ExceptionTag extends BodyTagSupport {
	
	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());

	private Throwable exception = null;

	public int doStartTag() throws JspException {
		try {
			Throwable previous = null;
			Throwable current = exception;
			while (previous != current && current != null) {
				String currentMessage = current.getMessage();
				if (previous == null || !currentMessage.equals(previous.getMessage())) {
					pageContext.getOut().write(currentMessage + "<br/>");
				}
				previous = current;
				current = current.getCause();
			}
		}
		catch (IOException e) {
			log.error("Unable to write Exception to output", e);
		}		
		return SKIP_BODY;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
