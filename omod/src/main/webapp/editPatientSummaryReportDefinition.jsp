<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<openmrs:require privilege="Manage Patient Summaries" otherwise="/login.htm" redirect="/module/patientsummary/editPatientSummaryReportDefinition.form" />
<c:set var="cancelUrl" value="${pageContext.request.contextPath}/module/patientsummary/manageSummaries.list"/>
<c:set var="successUrl" value="/module/patientsummary/editPatientSummaryReportDefinition.form"/>

<c:choose>
	<c:when test="${report.id == null}">

		<b class="boxHeader"><spring:messag code="patientsummary.createPatientSummaryReport"/></b>
		<div class="box">
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report['class'].name}|size=380|mode=edit|dialog=false|cancelUrl=${cancelUrl}|successUrl=${successUrl}?uuid=uuid" />
		</div>

	</c:when>		
	<c:otherwise>
	
	<div class="boxHeader">
		Columns
	</div>
	<div class="box">
		<ul id="sortable-columns">
			<c:forEach var="col" items="${ dsd.columnDefinitions }">
				<li class="ui-state-default">
					<form method="post" action="patientDataSetEditor-removeColumn.form">
						<input type="hidden" name="name" value="${ col.name }"/>
						<input type="submit" value="Remove"/>
					</form>

					<span class="column-sort-icon ui-icon ui-icon-arrowthick-2-n-s"></span>
					<span class="column-name">${ col.name }</span>
					<br/>
					<span class="column-description">
						${ col.dataDefinition.parameterizable.name }
						<c:forEach var="mapping" items="${ col.dataDefinition.parameterMappings }">
							<br/>
							&nbsp;&nbsp;
							${ mapping.key } -> <rpt:format object="${ mapping.value }"/>
						</c:forEach>
					</span>
				</li>
			</c:forEach>
		</ul>
	</div>

	<br/>
	
	<div class="boxHeader">
		<spring:message code="general.add"/>
	</div>
	<div class="box">
		<form id="add-column-form" method="post" action="patientDataSetEditor-addColumn.form" style="background-color: #e0e0e0">
			<table>
				<tr>
					<td>Label</td>
					<td><input type="text" name="label"/></td>
					<td></td>
				</tr>
				<tr>
					<td>Definition</td>
					<td><rptTag:chooseDataDefinition id="addCol" formFieldName="columnDefinition"/></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="<spring:message code="general.add"/>"/></td>
				</tr>
			</table>
		</form>
	</div>
	
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>