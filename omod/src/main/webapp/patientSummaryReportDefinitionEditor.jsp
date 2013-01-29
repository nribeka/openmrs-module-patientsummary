<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript">
	jqUiDecoration();
</script>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/validate/jquery.validate.min.js"/>
<c:if test="${ pageContext.response.locale.language != 'en' }">
	<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/validate/localization/messages_${ pageContext.response.locale.language }.js"/>
</c:if>

<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />

<style>
	#unsaved { background-color: yellow; }
	#unsaved form { display: inline; }
	div.metadataField { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	div.metadataField label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	#sortable-columns { list-style-type: none; margin: 0; padding: 0; width: 60%; }
	#sortable-columns li { margin: 0 3px 3px 3px; padding: 0.4em; padding-left: 1.5em; }
	#sortable-columns li span.column-sort-icon { position: absolute; margin-left: -1.3em; }
	#sortable-columns li span.column-name { font-weight: bold; }
	#sortable-columns li span.column-description { font-style: italic; font-size: 0.9em; font-weight: normal; }
	#sortable-columns li form { float: right; }
	div#addCol { display: inline; }
	.boxHeader input[type=button] { float: right; }
</style>

<script>
$(function() {
	$('#edit-name-dialog,#add-parameter-dialog').dialog({
		autoOpen: false,
		modal: true,
		width: '80%',
		height: 600
	});
	$('#edit-name-button').click(function() {
		$('#edit-name-dialog').dialog('open');
	});
	$('#edit-name-cancel').click(function() {
		$('#edit-name-dialog').dialog('close');
	});
	$('#add-parameter-button').click(function() {
		$('#add-parameter-dialog').dialog('open');
	});
	$('#add-parameter-cancel').click(function() {
		$('#add-parameter-dialog').dialog('close');
	});
	$('#edit-dataset-button').click(function() {
		window.location="../reporting/datasets/patientDataSetEditor.form?uuid=${ rd.patientDataSetDefinition.uuid }" +
			"&successUrl=/module/patientsummary/patientSummaryReportDefinitionEditor.form?uuid=${ rd.uuid }" +
			"&discardUrl=/module/patientsummary/patientSummaryReportDefinitionEditor.form?uuid=${ rd.uuid }";
	});
});
</script>

<c:if test="${ unsaved }">
	<div id="unsaved">
		<spring:message code="patientsummary.unsavedChanges"/>
		<form method="post" action="patientSummaryReportDefinitionEditor-save.form">
			<input type="submit" value="<spring:message code="general.save"/>"/>
		</form>
		<form method="post" action="patientSummaryReportDefinitionEditor-discard.form">
			<input type="submit" value="<spring:message code="reporting.discardButton"/>"/>
		</form>
	</div>
</c:if>

<div style="float: left; width: 40%">

	<div class="boxHeader">
		${ rd.name }
		<input type="button" id="edit-name-button" value="Edit"/>
	</div>
	<div class="box">
		<h3></h3>
		<c:choose>
			<c:when test="${ empty rd.description }">
				<i><span style="color: #e0e0e0"><spring:message code="general.none"/></span></i>
			</c:when>
			<c:otherwise>
				<i>${ rd.description }</i>
			</c:otherwise>
		</c:choose>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		<spring:message code="reporting.Report.parameters" />
		<input type="button" id="add-parameter-button" value="Add"/>
	</div>
	<div class="box">
		<table>
			<tr>
				<th><spring:message code="general.name"/></th>
				<th><spring:message code="reporting.Report.parameter.label"/></th>
				<th><spring:message code="reporting.Report.parameter.type"/></th>
				<th></th>
			</tr>
			<c:forEach var="p" items="${ rd.parameters }">
				<tr>
					<td>${ p.name }</td>
					<td>${ p.label }</td>
					<td>
						<c:choose>
							<c:when test="${p.collectionType != null}">
								${p.collectionType.simpleName}&lt;${p.type.simpleName}&gt;
							</c:when>
							<c:otherwise>
								${p.type.simpleName}
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<form method="post" action="patientSummaryReportDefinitionEditor-removeParam.form">
							<input type="hidden" name="name" value="${ p.name }"/>
							<input type="submit" value="Remove"/>
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		<spring:message code="patientsummary.templates" />
		<input type="button" id="add-template-button" value="Add"/>
	</div>
	<div class="box">
		<table>
			<tr>
				<th><spring:message code="general.name"/></th>
				<th>Type</th>
				<th></th>
			</tr>
			<c:forEach var="p" items="${ rd.parameters }">
				<tr>
					<td>${ p.name }</td>
					<td>${ p.label }</td>
					<td>
						<c:choose>
							<c:when test="${p.collectionType != null}">
								${p.collectionType.simpleName}&lt;${p.type.simpleName}&gt;
							</c:when>
							<c:otherwise>
								${p.type.simpleName}
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<form method="post" action="patientSummaryReportDefinitionEditor-removeParam.form">
							<input type="hidden" name="name" value="${ p.name }"/>
							<input type="submit" value="Remove"/>
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	
	<div id="edit-name-dialog">
		<form method="post" action="patientSummaryReportDefinitionEditor-nameDescription.form">
			<table>
				<tr valign="top">
					<td><spring:message code="general.name"/></td>
					<td>
						<input type="text" name="name" value="<spring:message javaScriptEscape="true" text="${ rd.name }"/>"/>
					</td>
				</tr>
				<tr valign="top">
					<td><spring:message code="general.description"/></td>
					<td>
						<textarea name="description" rows="5" cols="80"><c:out value="${ rd.description }"/></textarea>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="<spring:message code="reporting.apply"/>"/>
						<input type="button" value="<spring:message code="general.cancel"/>" id="edit-name-cancel"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<div id="add-parameter-dialog">
		<form method="post" action="patientSummaryReportDefinitionEditor-addParam.form">

			<div class="metadataField">
				<label class="desc">Type</label>
				<select name="collectionType">
					<option value="">Single</option>
					<c:forEach var="supportedType" items="${ parameterCollectionTypes }">
						<option value="${supportedType.value}">${supportedType.labelText} of</option>
					</c:forEach>
				</select>
				<select name="parameterType">
					<option value=""></option>
					<c:forEach var="supportedType" items="${ parameterTypes }">
						<option value="${supportedType.value}">${supportedType.labelText}</option>
					</c:forEach>
				</select>
			</div>

			<div class="metadataField">
				<label class="desc" for="name"><spring:message code="general.name"/></label>
				<input type="text" id="name" tabindex="1" name="name" size="50"/>
			</div>
			<div class="metadataField">
				<label class="desc" for="label">Label</label>			
				<textarea id="label" cols="50" rows="2" tabindex="2" name="label"></textarea>
			</div>
			<div class="metadataField">
				<label class="desc" for="widgetConfiguration">Advanced Configuration</label>			
				<textarea id="widgetConfiguration" cols="50" rows="5" tabindex="3" name="widgetConfiguration"></textarea>
			</div>

			<input type="submit" value="<spring:message code="reporting.apply"/>" tabindex="4"/>
			<input type="button" value="<spring:message code="general.cancel"/>" id="add-parameter-cancel" tabindex="5"/>
		</form>
	</div>
	
</div>

<div style="float: left; margin-left: 2em; width: 50%">

	<div class="boxHeader">
		<spring:message code="patientsummary.dataSchema" />
		<input type="button" id="edit-dataset-button" value="Edit"/>
	</div>
	<div class="box">
		<c:forEach var="col" items="${ rd.patientDataSetDefinition.columnDefinitions }">
			<span class="column-name">${ col.name }</span>
			<br/>
		</c:forEach>
	</div>
</div>

<div style="clear: both"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>