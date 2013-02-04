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
package org.openmrs.module.patientsummary;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientsummary.PatientSummaryReportDefinition;
import org.openmrs.module.patientsummary.PatientSummaryTemplate;
import org.openmrs.module.patientsummary.api.PatientSummaryService;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.renderer.TextTemplateRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

/**
 * Tests {@link {PatientSummaryService}}.
 */
public class PatientSummaryBehaviorTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldSupportDemographicData() throws Exception {
		PatientSummaryService pss = Context.getService(PatientSummaryService.class);

		PatientSummaryReportDefinition rd = new PatientSummaryReportDefinition();
		rd.setName("Test Patient Summary");
		PatientDataSetDefinition dsd = rd.getPatientDataSetDefinition();

		dsd.addColumn("patientName", new PreferredNameDataDefinition(), "");
		dsd.addColumn("age", new AgeDataDefinition(), "");
		dsd.addColumn("gender", new GenderDataDefinition(), "");
		dsd.addColumn("birthdate", new BirthdateDataDefinition(), "");
		dsd.addColumn("address", new PreferredAddressDataDefinition(), "");

		rd = pss.savePatientSummaryReportDefinition(rd);

		PatientSummaryTestUtil.testGroovyTemplate(rd, 7, "Demographics");
	}
}
