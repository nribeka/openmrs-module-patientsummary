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
package org.openmrs.module.patientsummary.web.controller.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.patientsummary.PatientSummaryReportDefinition;
import org.openmrs.module.patientsummary.util.StringUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;


/**
 * Used to create simplified data schema.
 */
public class DataSchemaUtil {
	
	public static Map<String, String> getDataSchema(PatientSummaryReportDefinition reportDefinition) {
		Map<String, String> dataSchema = new LinkedHashMap<String, String>();
		
		for (ColumnDefinition column : reportDefinition.getPatientDataSetDefinition().getColumnDefinitions()) {
			for (DataSetColumn dataSetColumn : column.getDataSetColumns()) {
				Class<?> dataType = dataSetColumn.getDataType();
				dataSchema.put(dataSetColumn.getName(), StringUtil.splitCamelCase(dataType.getSimpleName()));
			}
		}
		
		return dataSchema;
	}
}
