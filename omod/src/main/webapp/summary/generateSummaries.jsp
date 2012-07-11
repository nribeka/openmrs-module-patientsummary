<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Manage Summaries" otherwise="/login.htm" redirect="/module/patientsummary/generateSummarires.form"/>

<%@ include file="../template/localHeader.jsp"%>


<style type="text/css">

    p, h1, form, button {
        border:0;
        margin:0;
        padding:0;
    }

    .spacer{
        clear:both;
        height:1px;
    }

    .form{
        padding:14px;
        width: 500px;
        height: 250px;
    }

    #generate {
        border:solid 2px #b7ddf2;
        background:#ebf4fb;
    }

    #generate h1 {
        font-size:14px;
        font-weight:bold;
        margin-bottom:8px;
    }

    #generate p{
        font-size:11px;
        color:#666666;
        margin-bottom:20px;
        border-bottom:solid 1px #b7ddf2;
        padding-bottom:10px;
    }

    #generate label{
        display:block;
        font-weight:bold;
        text-align:right;
        width:140px;
        float:left;
    }

    #generate .small{
        color:#666666;
        display:block;
        font-size:11px;
        font-weight:normal;
        text-align:right;
        width:140px;
    }

    #generate input{
        float:left;
        font-size:12px;
        padding:4px 2px;
        border:solid 1px #aacfe4;
        width:200px;
        margin:2px 0 20px 10px;
    }

    #generate select {
        float:left;
        font-size:12px;
        padding:4px 2px;
        border:solid 1px #aacfe4;
        width:200px;
        margin:2px 0 20px 10px;
    }

    #generate button{
        clear:both;
        margin-left:150px;
        width:125px;
        height:31px;
        background:#666666;
        text-align:center;
        line-height:31px;
        color:#FFFFFF;
        font-size:11px;
        font-weight:bold;
    }

</style>

<div id="generate" class="form">
    <h1><spring:message code="patientsummary.generate.header"/></h1>
    <p><spring:message code="patientsummary.generate.header.info" /></p>
    <form method="post" enctype="multipart/form-data" action="">

        <label for="cohort">
            <spring:message code="patientsummary.generate.cohort"/>
            <span class="small"><spring:message code="patientsummary.generate.cohort.info" /></span>
        </label>
        <select name="cohort" id="cohort">
            <c:forEach items="${cohorts}" var="cohort">
                <option value="${cohort.id}">${cohort.name}</option>
            </c:forEach>
        </select>


        <label for="template">
            <spring:message code="patientsummary.generate.template"/>
            <span class="small"><spring:message code="patientsummary.generate.template.info" /></span>
        </label>
        <input type="file" name="template" id="template"/>

        <input type="submit" value='<spring:message code="patientsummary.generate"/>'/>
    </form>
    <div class="spacer"></div>
</div>
<div><div class="spacer"></div></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>