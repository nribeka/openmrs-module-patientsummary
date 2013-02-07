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
			$("#edit").hide();
			$("#preview").show();
			
			$("#previewLink").addClass("tab");
			$("#editLink").removeClass("tab");
			
			var form = $("#templateForm");
			form.attr("target", "previewFrame");
			form.attr("action", '<c:url value="/module/patientsummary/previewSummaries.form" />')
			
			form.submit();
			
			form.attr("target", "");
			form.attr("action", "");
		});
		
		$("#editLink").click(function() {
			$("#edit").show();
			$("#preview").hide();
			
			$("#editLink").addClass("tab");
			$("#previewLink").removeClass("tab");
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
		Template Details
	</div>
	<div class="box">
		<span class="metadataField">Name:</span>
		<input name="na	me" value="${template.reportDesign.name}"/><br />
		<span class="metadataField">Type:</span>
		<wgt:widget id="rendererType" name="rendererType" object="${template.reportDesign}" property="rendererType" attributes="type=org.openmrs.module.reporting.report.renderer.ReportRenderer|simple=true"/>
		<br/><input type="checkbox" name="enableOnPatientDashboard" <c:if test="${enableOnPatientDashboard}">checked</c:if> ><spring:message code="patientsummary.enableOnPatientDashboard"/>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		Schema Data
	</div>
	<div class="box">
		<table style="width:100%" class="reporting-data-table display">
			<tr><th>Name</th><th>Type</th></tr>
			<c:forEach items="${dataSchema}" var="item">
			<tr><td>${item.key}</td><td>${item.value}</td></tr>
			</c:forEach>
		</table>
	</div>
</div>

<div style="float: right; width: 69%">
	<div id="templateConfiguration">
		<div class="boxHeader">
			Template Configuration
		</div>
		<div class="box">
			Upload Template File: <input name="resource" type="file" /><br/>
			<c:if test="${!empty template.reportDesign.resources}">
				Template Files:
				<ul>
				<c:forEach items="${template.reportDesign.resources}" var="resource">
					<li>${resource.name} <input type="button" value="Delete" onclick="window.location='editTemplate/deleteResource.form?templateUuid=${template.uuid}&resourceUuid=${resource.uuid}'"/></li>
				</c:forEach>
				</ul>
			</c:if>
			<br/>
			Configuration: <br/>
			<wgt:widget id="properties" name="properties" object="${template.reportDesign}" property="properties" attributes="rows=20|cols=50"/>
		</div>
	</div>
	
	<div id="textTemplate">
		<div class="boxHeader">
			Template Editor
		</div>
		<div class="box">
			<a id="editLink" href="#edit">&nbsp;EDIT&nbsp;</a> <a id="previewLink" href="#preview">&nbsp;PREVIEW&nbsp;</a>
			
			<div id="edit">
				Script Type:
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
				</select>
				<br/><br/>
				<textarea name="script" rows="20" cols="2" style="width:99%">${script}</textarea>
			</div>
			<div id="preview">
				<iframe id="previewFrame" style="width: 99%; height: 600px"></iframe>
			</div>
		</div>
	</div>
	
	<div style="text-align: right;">
		<input type="button" value="Cancel" onclick="window.location='editSummary.form?uuid=${template.reportDesign.reportDefinition.uuid}'"/> <input type="submit" value="Save"/>
	</div>
</div>

<div style="clear: both"></div>

</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>