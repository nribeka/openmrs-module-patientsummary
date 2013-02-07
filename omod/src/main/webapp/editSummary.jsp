<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.list" />

<c:url value="/module/patientsummary/editSummary.form" var="pageUrl">
	<c:param name="uuid" value="${report.uuid}" />
</c:url>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/patientsummary/manageSummaries.form" />';
		});

		<c:forEach items="${templates}" var="template" varStatus="templateStatus">
			$('#${template.uuid}TemplateEditLink').click(function(event){
				window.location.href='<c:url value="/module/patientsummary/editTemplate.form?templateUuid=${template.uuid}" />';
			});
			$('#${template.uuid}TemplateRemoveLink').click(function(event){					
				if (confirm('Please confirm you wish to permanantly delete <b>${template.name}</b>')) {
					document.location.href='<c:url value="/module/reporting/reports/deleteReportDesign.form?uuid=${template.uuid}&returnUrl=${pageUrl}" />';
				}
			});
		</c:forEach>
	} );
</script>

<div id="page">
	<div id="container">
		<h1><spring:message code="patientsummary.summary.title"/></h1>
		
		<c:choose>
			
			<c:when test="${report.id == null}">
				<b class="boxHeader"><spring:message code="patientsummary.summary.new"/></b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.patientsummary.PatientSummaryReportDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageSummaries.form|successUrl=/module/patientsummary/editSummary.form?uuid=uuid" />
				</div>
			</c:when>
			
			<c:otherwise>
			<table style="font-size:small; width:100%;">
				<tr>
					<td valign="top" style="width:50%;">
						<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report['class'].name}|uuid=${report.uuid}|label=Basic Details" />
						<br/>
						<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${report['class'].name}|uuid=${report.uuid}|label=Parameters|parentUrl=${pageUrl}" />
						<br/>
						<b class="boxHeader" style="text-align:right;">
							<span style="float:left;"><spring:message code="patientsummary.template.title"/></span>
							<a style="color:lightyellow; font-weight:bold;" href="editTemplate/newTemplate.form?reportDefinitionUuid=${report.uuid}">[+] <spring:message code="patientsummary.add"/></a>
						</b>
						<div class="box">
							<c:if test="${!empty templates}">
								<table width="100%" style="margin-bottom:5px;">
									<tr>
										<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="patientsummary.name"/></th>
										<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="patientsummary.type"/></th>
										<th style="border-bottom:1px solid black;">[X]</th>
									</tr>
									<c:forEach items="${templates}" var="template" varStatus="designStatus">
										<tr>
											<td nowrap><a href="#" id="${template.uuid}TemplateEditLink">${template.name}</a></td>
											<td width="100%"><rpt:displayLabel type="${template.reportDesign.rendererType.name}"/></td>
											<td nowrap align="center"><a href="#" id="${template.uuid}TemplateRemoveLink">[X]</a></td>
										</tr>
									</c:forEach>
								</table>
							</c:if>
						</div>
					</td>
					<td valign="top" style="width:50%; padding-left:20px;">
						<b class="boxHeader" style="text-align:right;">
							<span style="float:left;"><spring:message code="patientsummary.dataSchema"/></span>
							<a style="color:lightyellow; font-weight:bold;" href="editSummary/editDataSchema.form?uuid=${report.uuid}"><spring:message code="patientsummary.edit"/></a>
						</b>
						<div class="box">
							<table id="dataSchema" style="width:100%">
								<tr style="text-align: left;"><th><spring:message code="patientsummary.name"/></th><th><spring:message code="patientsummary.type"/></th></tr>
								<c:forEach items="${dataSchema}" var="item">
								<tr><td>${item.key}</td><td>${item.value}</td></tr>
								</c:forEach>
							</table>
						</div>
					</td> 
				</tr>
			</table>
			</c:otherwise>
			
		</c:choose>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>