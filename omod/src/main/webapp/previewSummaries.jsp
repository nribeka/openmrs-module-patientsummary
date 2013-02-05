<%@ include file="/WEB-INF/template/include.jsp"%>

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
				<c:if test="${!empty script}">
					<input type="hidden" name="script" value="${script}"/>
					<input type="hidden" name="scriptType" value="${scriptType}"/>
				</c:if>
				
				<b>Choose Patient:</b><br/>
				<openmrs_tag:patientField formFieldName="patientId" initialValue="${patientId}" /><br/>
				<c:choose>
					<c:when test="${!empty patientSummaries}">
						<br/>
						<b>Choose Summary:</b><br/>
						<c:forEach items="${patientSummaries}" var="ps">
							<input type="radio" name="summaryId" value="${ps.id}" <c:if test="${summaryToPreview != null && summaryToPreview.id == ps.id}">checked</c:if>/> ${ps.name}<br/>
						</c:forEach>
						<br/>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="summaryId" value="${summaryToPreview.id}"/>
					</c:otherwise>
				</c:choose>
				<input type="submit" value="Preview"/>
			</form>
		</td>
		<td style="width:95%; vertical-align:top;">
			<c:choose>
				<c:when test="${!empty errorDetails}">
					An error occurred generating the patient summary named ${summaryToPreview.name}:<br/>
					<pre>
						${errorDetails}
					</pre>
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
					Preview of ${summaryToPreview.name}:<hr />
					${generatedSummary}
				</c:otherwise>
			</c:choose>
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