<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page import="org.openmrs.web.WebConstants"%>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
	pageContext.setAttribute("return_url", request.getParameter("return_url") != null ? request.getParameter("return_url") : "");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<openmrs:htmlInclude file="/openmrs.css" />
<openmrs:htmlInclude file="/style.css" />
<openmrs:htmlInclude
	file="/moduleResources/patientsummary/patientsummary.css" />
<openmrs:htmlInclude file="/openmrs.js" />

<script type="text/javascript">
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
			
			function goBack() {
				if ( '${return_url}' != '' ) {
					location.href = '${return_url}';
				} else {
					if ( window.opener ) {
						window.opener.focus();
						window.close();
					} else {
						history.back();
					}
				}
			}
		</script>

<!--  Page Title : '${pageTitle}' 
			OpenMRS Title: <spring:message code="openmrs.title"/>
		-->
<c:choose>
	<c:when test="${!empty pageTitle}">
		<title>${pageTitle}</title>
	</c:when>
	<c:otherwise>
		<title><spring:message code="openmrs.title" /></title>
	</c:otherwise>
</c:choose>

</head>

<c:forEach items="${relationships}" var="r" varStatus="s">
	<c:if
		test="${r.relationshipType.relationshipTypeId == 1 && r.personA.personId != patient.patientId}">
		<c:set var="accompFound" value="true" />
	</c:if>
</c:forEach>

<body>
<div id="pageBody">
<div id="contentMinimal"><c:if test="${msg != null}">
	<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}"
		arguments="${msgArgs}" /></div>
</c:if> <c:if test="${err != null}">
	<div id="openmrs_error"><spring:message code="${err}"
		text="${err}" arguments="${errArgs}" /></div>
</c:if>
<div id="patientsummarymodule">
<div class="header">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr height="27px">
		<td rowspan="2" align="center" class="picture"><img
			src="${pageContext.request.contextPath}/images/patient_${patient.gender}.gif"
			width="110px" alt="${patient.personName}" /><br />
		</td>
		<td valign="top" colspan="2">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td class="nowrap">
				<h1>${patient.personName} <span id="died"><c:if
					test="${patient.dead == 'true'}">(Dead)</c:if></span></h1>
				</td>
				<td class="noprint" align="right" valign="top"><input
					style="position: relative; top: -5px;" type="button"
					value="<spring:message code="Back" />"
					onclick="javascript:goBack();" /> <input
					style="position: relative; top: -5px;" type="button"
					value="<spring:message code="Print" />"
					onclick="javascript:window.print();" /> <!-- <input style="position: relative; top: -5px;" type="button" value="<spring:message code="Mistakes?" />" onclick="javascript:window.print();" /> -->
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<hr>
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="top">
		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<c:forEach var="identifier" items="${patient.identifiers}">
					<c:if
						test="${identifier.identifierType.name == 'IMB ID' && not empty identifier.identifier}">
						<c:set var="imbIdflag" value="true" />
						<td class="nowrap" align="right"><spring:message code="patientsummary.imbid" />
						</td>
						<td><b>${identifier.identifier}</b></td>
					</c:if>
				</c:forEach>
			</tr>
			<tr>
				<td align="right"><spring:message code="Patient.gender" /></td>
				<td><b> <c:if test="${patient.gender == 'M'}">
					<spring:message code="Patient.gender.male" />
				</c:if> <c:if test="${patient.gender == 'F'}">
					<spring:message code="Patient.gender.female" />
				</c:if> </b></td>
			</tr>
			<tr>
				<td align="right"><spring:message code="Person.age" /></td>
				<td><b><c:choose>
					<c:when test="${patient.age <= maxage}">
												${agestring}
											</c:when>
					<c:otherwise>
												${patient.age} <spring:message code="Person.age.years" />
					</c:otherwise>
				</c:choose> </b> (<c:if test="${patient.birthdateEstimated}">~</c:if><openmrs:formatDate
					date="${patient.birthdate}" type="medium" />)</td>
			</tr>
			<c:forEach var="patientProgram" items="${programs}">
				<c:if test="${patientProgram.program!=null}">
					<tr>
						<td class="nowrap" align="right">${patientProgram.program.name}</td>
						<td colspan="3">
							<c:choose>
								<c:when test="${patientProgram.dateCompleted!=null}">
									<i>Completed on <openmrs:formatDate date="${patientProgram.dateCompleted}"
					type="medium" /></i>
								</c:when>
								<c:otherwise>
									<b>
										<c:forEach var="patientState" items="${patientProgram.currentStates}">
											${patientState.state.concept.name.name}<br/>
										</c:forEach>
									</b>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>
			</c:forEach>
			</tr>			<c:if test="${not empty lastencounter.timeago}">
				<tr>
					<td align="right"><spring:message
						code="patientsummary.lastvisit" /></td>
					<td><b>${lastencounter.timeago}</b> (<openmrs:formatDate
						date="${lastencounter.datetime}" type="medium" />)</td>
				</tr>
				<tr>
					<td align="right"></td>
					<td><b>${lastencounter.encounterType}</b>
						by <b>${lastencounter.provider.personName}</b> @ <b>${lastencounter.location}</b> </td>
				</tr>
			</c:if>
			
			
			
			
		</table>
		</td>
		<td valign="top" align="right">
		<table cellspacing="0" cellpadding="0" border="0">
			<c:forEach var="identifier" items="${patient.identifiers}"
				varStatus="status">
				<c:if test="${identifier.identifierType.name != 'IMB ID'}">
					<tr>
						<td align="right" class="nowrap">${identifier.identifierType.name}:</td>
						<td>${identifier.identifier}</td>
					</tr>
				</c:if>
			</c:forEach>
		</table>
		</td>

	</tr>
</table>
</div>

<div class="section">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td class="th"><spring:message code="patientsummary.alerts" /></td>
		<td class="noborder"></td>
		<td class="th"><spring:message code="patientsummary.comments" /></td>
	</tr>
	<tr>
		<td class="alert"><c:forEach var="alertconcept"
			items="${alertconcepts}">
			<openmrs:concept conceptId="${alertconcept}" var="concept"
				nameVar="name" numericVar="num">
				<c:if test="${not empty alerts[alertconcept]}">
							No <c:choose>
						<c:when test="${!empty concept.name.shortName}">
									${concept.name.shortName}
								</c:when>
						<c:otherwise>
									${concept.name}
								</c:otherwise>
					</c:choose> ${alerts[alertconcept]} <br />
				</c:if>
			</openmrs:concept>
		</c:forEach> <c:if test="${empty accompFound && patient.age <= maxage}">
			<spring:message code="patientsummary.noaccomp" />
			<br />
		</c:if></td>
		<td class="noborder"></td>
		<td>
		<c:if test="${empty imbIdflag}">
			<spring:message code="patientsummary.noimbid" />
			<br />
		</c:if> 
		<c:if test="${empty lastencounter.timeago}">
			<spring:message code="patientsummary.nolastvisit" />
			<br />
		</c:if> 
		<c:if test="${empty adverse['recent'] && empty adverse['old']}">
			<spring:message code="patientsummary.noadverse" />
			<br />
		</c:if> 
		<c:if test="${empty infections['recent'] && empty infections['old']}">
			<spring:message code="patientsummary.noinfections" />
			<br />
		</c:if> 
		<c:if test="${empty symptoms['recent'] && empty symptoms['old']}">
			<spring:message code="patientsummary.nosymptoms" />
			<br />
		</c:if> 
		<c:if test="${empty prevdiags['recent'] && empty prevdiags['old']}">
			<spring:message code="patientsummary.nodiagnoses" />
			<br />
		</c:if> 
		<c:if test="${empty drugorders}">
			<spring:message code="patientsummary.nodrugs" />
			<br />
		</c:if> 
		<c:if
			test="${empty vitals['recent'] && empty vitals['old'] && patient.age <= maxage}">
			<spring:message code="patientsummary.novitals" />
			<br />
		</c:if> 
		<c:if test="${empty labdata['recent'] && empty labdata['old']}">
			<spring:message code="patientsummary.nolabdata" />
			<br />
		</c:if> 
		<c:if test="${empty notes}">
			<spring:message code="patientsummary.noclinicalnotes" />
			<br />
		</c:if></td>
	</tr>

</table>
</div>

<c:if test="${not empty adverse['recent'] || not empty adverse['old']}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.adverse" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
			<td class="th"><spring:message code="patientsummary.comments" /></td>
		</tr>
		<openmrs:forEachObs obs="${adverse['recent']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} --><b>${ob.valueAsString[locale]}</b></td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
				<td>${ob.comment}</td>
			</tr>
		</openmrs:forEachObs>
		<openmrs:forEachObs obs="${adverse['old']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} -->${ob.valueAsString[locale]}</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
				<td>${ob.comment}</td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if> 
<c:if
	test="${not empty infections['recent'] || not empty infections['old']}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.infections" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
		</tr>
		<openmrs:forEachObs obs="${infections['recent']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} --><b>${ob.valueAsString[locale]}</b></td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
		<openmrs:forEachObs obs="${infections['old']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} -->${ob.valueAsString[locale]}</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if> 
<c:if
	test="${not empty symptoms['recent'] || not empty symptoms['old']}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.symptoms" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
		</tr>
		<openmrs:forEachObs obs="${symptoms['recent']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} --><b>${ob.valueAsString[locale]}</b></td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
		<openmrs:forEachObs obs="${symptoms['old']}" var="ob" descending="true">
			<tr>
				<td><!-- ${ob.obsId} -->${ob.valueAsString[locale]}</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if> 
<c:if
	test="${not empty prevdiags['recent'] || not empty prevdiags['old']}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.diagnosis" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
		</tr>
		<openmrs:forEachObs obs="${prevdiags['recent']}" var="ob"
			descending="true">
			<tr>
				<td><!-- ${ob.obsId} --><b>${ob.valueAsString[locale]}</b></td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
		<openmrs:forEachObs obs="${prevdiags['old']}" var="ob"
			descending="true">
			<tr>
				<td><!-- ${ob.obsId} -->${ob.valueAsString[locale]}</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if> <c:if test="${not empty drugorders}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.drugorders" /></td>
			<td class="th"><spring:message code="patientsummary.dose" /></td>
			<td class="th"><spring:message code="patientsummary.frequency" /></td>
			<td class="th"><spring:message code="patientsummary.startdate" /></td>
			<td class="th"><spring:message code="patientsummary.stopdate" /></td>
			<td class="th"><spring:message code="patientsummary.comments" /></td>
		</tr>
		<openmrs:forEachDrugOrder drugOrders="${drugorders}" var="drug"
			descending="true">
			<c:if test="${drug.voided == false && drug.discontinued == false}">
				<tr>
					<td class="nowrap"><b>${drug.drug.name}</b></td>
					<td class="nowrap">${drug.dose} ${drug.units}</td>
					<td class="nowrap">${drug.frequency}</td>
					<td class="nowrap"><openmrs:formatDate
						date="${drug.startDate}" type="medium" /></td>
					<td class="nowrap"><openmrs:formatDate
						date="${drug.discontinuedDate}" type="medium" /></td>
					<td>${drug.instructions}</td>
				</tr>
			</c:if>
		</openmrs:forEachDrugOrder>
		<openmrs:forEachDrugOrder drugOrders="${drugorders}" var="drug"
			descending="true">
			<c:if test="${drug.voided == false && drug.discontinued == true}">
				<tr>
					<td class="nowrap">${drug.drug.name} (stopped)</td>
					<td class="nowrap">${drug.dose} ${drug.units}</td>
					<td class="nowrap">${drug.frequency}</td>
					<td class="nowrap"><openmrs:formatDate
						date="${drug.startDate}" type="medium" /></td>
					<td class="nowrap"><openmrs:formatDate
						date="${drug.discontinuedDate}" type="medium" /></td>
					<td>${drug.discontinuedReason.name}</td>
				</tr>
			</c:if>
		</openmrs:forEachDrugOrder>
	</table>
	</div>
</c:if> <c:if test="${not empty vitals['recent'] || not empty vitals['old']}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.vitals" /></td>
			<td class="th"><spring:message code="patientsummary.result" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
			<td class="th"><spring:message code="patientsummary.comments" /></td>
		</tr>
		<openmrs:forEachObs obs="${vitals['recent']}" var="ob"
			descending="true">
			<tr>
				<td class="nowrap">
					<!-- ${ob.obsId} -->
					<b><c:choose>
					<c:when test="${!empty ob.concept.name.shortName}">
										${ob.concept.name.shortName}
									</c:when>
					<c:otherwise>
										${ob.concept.name}
									</c:otherwise>
				</c:choose></b></td>
				<td class="nowrap"><b>${ob.valueNumeric}</b></td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
				<td>${ob.comment}</td>
			</tr>
		</openmrs:forEachObs>
		<openmrs:forEachObs obs="${vitals['old']}" var="ob" descending="true">
			<tr>
				<td class="nowrap">
				<!-- ${ob.obsId} -->
				<c:choose>
					<c:when test="${!empty ob.concept.name.shortName}">
										${ob.concept.name.shortName}
									</c:when>
					<c:otherwise>
										${ob.concept.name}
									</c:otherwise>
				</c:choose></td>
				<td class="nowrap">${ob.valueNumeric}</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
				<td>${ob.comment}</td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if> 
<c:if test="${not empty labdata['recent'] || not empty labdata['old']}">

<style>
#labTestTable th { width: 200px; } 
</style>

	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message code="patientsummary.labtests" /></td>
		</tr>
		<tr>
			<td width="100%">		
				<openmrs:obsTable
					observations="${labdata['all']}"
					concepts="name:CD4 COUNT|name:WEIGHT (KG)|set:name:LABORATORY EXAMINATIONS CONSTRUCT"
					conceptLink="admin/observations/personObs.form?personId=${model.patientId}&"
					id="labTestTable"
					showEmptyConcepts="false"
					showConceptHeader="true"
					showDateHeader="true"
					orientation="horizontal"
					sort="asc"
					combineEqualResults="true"
					limit="25"
				/>
			</td>
		</tr>
		
	</table>
	</div>
</c:if>

<div class="section">
<table width="100%" cellpadding="1" cellspacing="1" border="0">
	<tr>
		<td class="th"><spring:message code="patientsummary.graphs" /></td>
	</tr>
	<tr>
		<td align="center">
			<c:forEach var="graphconcept" items="${graphconcepts}">
				<img
					src="${pageContext.request.contextPath}/showGraphServlet?patientId=${patient.patientId}&conceptId=${graphconcept}&width=200&height=200&minRange=<c:out value="${graphdata['floor'][graphconcept]}" default="0.0"/>&maxRange=<c:out value="${graphdata['ceiling'][graphconcept]}" default="200.0"/>" width="200" height="200" />
			</c:forEach>
		</td>
	</tr>
</table>
</div>
<c:if test="${not empty notes}">
	<div class="section">
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr>
			<td class="th"><spring:message
				code="patientsummary.clinicalnotes" /></td>
			<td class="th"><spring:message code="patientsummary.date" /></td>
			<td class="th"><spring:message code="patientsummary.clinician" /></td>
		</tr>
		
		<openmrs:forEachObs obs="${notes}" var="ob" descending="true">
			<tr>
				<td>
					<b>${ob.concept.name}</b><br />
					<div class="indent">${notesText[ob]}</div>
				</td>
				<td class="nowrap"><openmrs:formatDate date="${ob.obsDatetime}"
					type="medium" /></td>
				<td class="nowrap">${ob.encounter.provider.personName}</td>
			</tr>
		</openmrs:forEachObs>
	</table>
	</div>
</c:if></div>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>