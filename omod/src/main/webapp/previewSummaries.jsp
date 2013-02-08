<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="ps" uri="/WEB-INF/view/module/patientsummary/resources/patientsummary.tld" %>

<c:choose>
	<c:when test="${not iframe}">
		<%@ include file="/WEB-INF/template/header.jsp"%>
		<%@ include file="template/localHeader.jsp"%>
	</c:when>
	<c:otherwise>
		<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
	</c:otherwise>
</c:choose>

<table style="width:99%;">
	<tr>
		<td style="white-space:nowrap; vertical-align:top; padding-right:10px;">
			<form>
				<c:if test="${!empty iframe}">
					<input type="hidden" name="iframe" value="${iframe}"/>
				</c:if>
				<c:if test="${!empty scriptType}">
					<input type="hidden" name="script" value="${script}"/>
					<input type="hidden" name="scriptType" value="${scriptType}"/>
				</c:if>
				<b>Choose Patient:</b>
				<openmrs_tag:patientField formFieldName="patientId" initialValue="${patientId}" />
				<c:choose>
					<c:when test="${!empty patientSummaries}">
						<select name="summaryId">
							<option value="">Choose Summary:</option>
							<c:forEach items="${patientSummaries}" var="ps">
								<c:set var="isSelected" value="${summaryToPreview != null && summaryToPreview.id == ps.id ? ' selected' : ''}"/>
								<option value="${ps.id}"${isSelected}>${ps.name}</option>
							</c:forEach>
						</select>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="summaryId" value="${summaryToPreview.id}"/>
					</c:otherwise>
				</c:choose>
				<input type="submit" value="Preview"/>
				<hr/>
				<c:choose>
					<c:when test="${!empty errorDetails}">
						<b style="font-size:smaller;"><ps:exception exception="${errorDetails}"/></b><br/>
					</c:when>
					<c:when test="${empty summaryToPreview and empty patientId}">
						Select a Patient and a Patient Summary from the left and click Preview to view
					</c:when>
					<c:when test="${empty summaryToPreview}">
						Select a Patient Summary from the left and click Preview to view
					</c:when>
					<c:when test="${empty patientId}">
						Select a Patient on the left and click Preview to view
					</c:when>
					<c:otherwise>
						${generatedSummary}
					</c:otherwise>
				</c:choose>
			</form>
		</td>
	</tr>
</table>

<c:choose>
	<c:when test="${not iframe}">
		<%@ include file="/WEB-INF/template/footer.jsp"%>
	</c:when>
	<c:otherwise>
		<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>
	</c:otherwise>
</c:choose>