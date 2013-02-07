<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<style type="text/css">
.tab {
	background-color: #DEDEDE;
}
</style>

<script type="text/javascript">
	$(document).ready(function() {
		$("#textTemplate").tabs();
		
		$("#rendererType").change(function() {
			if ($(this).find("option:selected").attr("value") == "org.openmrs.module.reporting.report.renderer.TextTemplateRenderer") {
				$("#templateConfiguration").hide();
				$("#textTemplate").show();
			} else {
				$("#templateConfiguration").show();
				$("#textTemplate").hide();
			}
		});
		
		$("#rendererType").trigger("change");
		
		$("#previewLink").click(function() {
			var form = $("#templateForm");
			form.attr("target", "previewFrame");
			form.attr("action", '<c:url value="/module/patientsummary/previewSummaries.form" />')
			
			form.submit();
			
			form.attr("target", "");
			form.attr("action", "");
		});
		
		$("#editLink").trigger("click");
	});
</script>

<form id="templateForm" method="post" enctype="multipart/form-data">
<input type="hidden" name="templateUuid" value="${template.uuid}" />
<input type="hidden" name="summaryId" value="${template.id}" />
<input type="hidden" name="iframe" value="true" />

<div style="float: left; width: 30%">

	<div class="boxHeader">
		<spring:message code="patientsummary.template.details" />
	</div>
	<div class="box">
		<table>
			<tr><td><spring:message code="patientsummary.name" />:</td><td><input name="name" value="${template.reportDesign.name}"/></td></tr>
			<tr>
				<td><spring:message code="patientsummary.type" />:</td>
				<td>
				<select id="rendererType" name="rendererType">
				<c:forEach items="${rendererTypes}" var="rendererType">
					<c:choose>
					<c:when test="${rendererType eq template.reportDesign.rendererType}">
						<option selected="selected" value="${rendererType.name}"><rpt:displayLabel type="${rendererType.name}"/></option>
					</c:when>
					<c:otherwise>
						<option value="${rendererType.name}"><rpt:displayLabel type="${rendererType.name}"/></option>
					</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
				</td>
			</tr>
		</table>
		<br/><input type="checkbox" name="enableOnPatientDashboard" <c:if test="${enableOnPatientDashboard}">checked</c:if> ><spring:message code="patientsummary.enableOnPatientDashboard"/>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		<spring:message code="patientsummary.dataSchema" />
	</div>
	<div class="box">
		<table style="width:100%">
			<tr style="text-align: left;"><th><spring:message code="patientsummary.name" /></th><th><spring:message code="patientsummary.type" /></th></tr>
			<c:forEach items="${dataSchema}" var="item">
			<tr><td>${item.key}</td><td>${item.value}</td></tr>
			</c:forEach>
		</table>
	</div>
</div>

<div style="float: right; width: 69%">
	<div id="templateConfiguration">
		<div class="boxHeader">
			<spring:message code="patientsummary.template.configuration" />
		</div>
		<div class="box">
			<spring:message code="patientsummary.template.upload" />: <input name="resource" type="file" /><br/>
			<c:if test="${!empty template.reportDesign.resources}">
				<spring:message code="patientsummary.template.files" />:
				<ul>
				<c:forEach items="${template.reportDesign.resources}" var="resource">
					<li>${resource.name} <input type="button" value="<spring:message code="patientsummary.delete" />" onclick="window.location='editTemplate/deleteResource.form?templateUuid=${template.uuid}&resourceUuid=${resource.uuid}'"/></li>
				</c:forEach>
				</ul>
			</c:if>
			<br/>
			<spring:message code="patientsummary.template.configuration" />: <br/>
			<wgt:widget id="properties" name="properties" object="${template.reportDesign}" property="properties" attributes="rows=20|cols=50"/>
		</div>
	</div>
	
	<div id="textTemplate">
		<ul>
			<li><a id="editLink" href="#edit"><spring:message code="patientsummary.template.edit" /></a></li>
			<li><a id="previewLink" href="#preview"><spring:message code="patientsummary.template.preview" /></a></li>
		</ul>
			
		<div id="edit">
			<spring:message code="patientsummary.template.scriptType" />:
			<select name="scriptType">
			<c:forEach var="type" items="${scriptTypes}">
				<c:choose>
				<c:when test="${scriptType eq type}">
					<option selected="selected">${type}</option>
				</c:when>
				<c:otherwise>
					<option>${type}</option>
				</c:otherwise>
				</c:choose>
			</c:forEach>
			
			<br/><br/>
			<textarea name="script" rows="20" cols="2" style="width:99%">${script}</textarea>
		</div>
		<div id="preview">
			<iframe id="previewFrame" style="width: 99%; height: 300px"></iframe>
		</div>
	</div>
	
	<div style="text-align: right;">
		<input type="button" value="<spring:message code="patientsummary.template.backToSummary" />" onclick="window.location='editSummary.form?uuid=${template.reportDesign.reportDefinition.uuid}'"/> <input type="button" value="<spring:message code="patientsummary.cancel" />" onclick="window.location='editTemplate.form?templateUuid=${template.uuid}'"/> <input type="submit" value="<spring:message code="patientsummary.save" />"/>
	</div>
</div>

<div style="clear: both"></div>

</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>