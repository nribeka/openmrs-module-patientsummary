<%@ include file="template/reportingInclude.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		$(".summary-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );
	} );

	function confirmDelete(schemaName, summaryName, uuid) {
		if (confirm("Are you sure you want to delete the patient summary named " + summaryName + " for schema " + schemaName + "?")) {
			document.location.href = '${pageContext.request.contextPath}/module/patientsummary/purgeSummary.form?uuid=' + uuid;
		}
	}

</script>

<style>
	.small { font-size: x-small; }
</style>

<div id="page">
	<div id="container">
		<h1><spring:message code="patientsummary.listAll.title"/></h1>
		
		<table class="summary-data-table display">
			<thead>
				<tr>
					<th><spring:message code="patientsummary.summarySchemaName"/></th>
					<th><spring:message code="patientsummary.summaryViewName"/></th>
					<th><spring:message code="general.creator"/></th>
					<th><spring:message code="general.dateCreated"/></th>
					<th align="center" width="1%"><spring:message code="patientsummary.actions"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${schemas}" var="schema" varStatus="schemaStatus">
					<c:forEach items="${summaries}" var="summary" varStatus="summaryStatus">
						<c:if test="${schema == summary.reportDesign.reportDefinition}">
							<tr>
								<td width="20%" nowrap>
									${summary.reportDesign.reportDefinition.name}
								</td>
								<td width="20%">
									${summary.reportDesign.name}
								</td>
								<td width="5%" nowrap>
									${summary.reportDesign.creator}
								</td>
								<td width="5%" nowrap>
									<rpt:timespan then="${summary.reportDesign.dateCreated}"/>
								</td>
								<td width="1%" align="center" nowrap>
									&nbsp;
									<a href="${pageContext.request.contextPath}/module/patientsummary/editSummary.form?uuid=${summary.uuid}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
									&nbsp;
									<a href="javascript:confirmDelete(escape('${schema.name}'), escape('${summary.name}'),'${summary.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
									&nbsp;
									<a href="${pageContext.request.contextPath}/module/patientsummary/previewSummaries.form?summaryId=${summary.id}">
										<img src='<c:url value="/images/play.gif"/>' align="absmiddle" border="0"/>
									</a>	
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</c:forEach>	
			</tbody>
			<tfoot></tfoot>
		</table>
	
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>