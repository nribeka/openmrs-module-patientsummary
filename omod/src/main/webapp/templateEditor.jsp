<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

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
		
		$("#textTemplate").tabs();
	});
</script>

<form action="templateEditor.form" method="post">

<div style="float: left; width: 30%">

	<div class="boxHeader">
		Template Details
	</div>
	<div class="box">
		<span class="metadataField">Name:</span>
		<input name="name" value="${template.reportDesign.name}"/><br />
		<span class="metadataField">Type:</span>
		<wgt:widget id="rendererType" name="rendererType" object="${template.reportDesign}" property="rendererType" attributes="type=org.openmrs.module.reporting.report.renderer.ReportRenderer|simple=true"/>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		Schema Data
	</div>
	<div class="box">
		
	</div>
</div>

<div style="float: right; width: 69%">
	<div id="templateConfiguration">
		<div class="boxHeader">
			Template Configuration
		</div>
		<div class="box">
			Template File: <input name="resource" type="file" /><br/><br/>
			Configuration: <br/>
			<textarea rows="10" style="width: 99%"></textarea>
		</div>
	</div>
	
	<div id="textTemplate">
		<ul>
			<li><a href="#edit">Edit</a></li>
			<li><a href="#preview">Preview</a></li>
		</ul>
		<div id="edit"></div>
		<div id="preview"></div>
	</div>
</div>

</form>

<div style="clear: both"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>