<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring"    uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <c:set var="registerFlag" value="${empty boardVO.id ? 'create' : 'modify'}"/>
    <title>Board <c:if test="${registerFlag == 'create'}">Regist</c:if>
                  <c:if test="${registerFlag == 'modify'}">Update</c:if>
    </title>

    <!-- KRDS CSS -->
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/component/output.css'/>" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/egovframe.css'/>" />
    <script type="text/javascript" src="<c:url value='/js/jquery.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/component/ui-script.js'/>" defer></script>
    <script type="text/javascript" src="<c:url value='/js/egovframework/common.js'/>" defer></script>

    <!--For Custom Validation-->
    <!-- egovframe-Todo: Validator 적용시 아래 주석 코드 참고 -->
    <!-- <script type="text/javascript" src="<c:url value='/js/egovframework/EgovValidation.js'/>" defer></script> -->

    <script type="text/javascript" defer>

    function fn_egov_list() {
    	document.getElementById("detailForm").action = "<c:url value='/board/boardList.do'/>";
       	document.getElementById("detailForm").method = 'get';
       	document.getElementById("detailForm").submit();
    }

    function fn_egov_add() {
        if (confirm('Are you sure you want to register?')) {
            let frm = document.getElementById("detailForm");
            // egovframe-Todo: Validator 적용시 아래 주석 코드 참고
            // EgovValidation.js 파일에 validateBoardVO 함수 추가 필요
            // if (!validateBoardVO(frm)) {
            //     return;
            // }
            frm.action = "<c:url value='/board/addBoard.do'/>";
            frm.submit();
        }
    }

    function fn_egov_update() {
        if (confirm('Are you sure you want to modify?')) {
            let frm = document.getElementById("detailForm");
            // egovframe-Todo: Validator 적용시 아래 주석 코드 참고
            // EgovValidation.js 파일에 validateBoardVO 함수 추가 필요
            // if (!validateBoardVO(frm)) {
            //     return;
            // }
            frm.action = "<c:url value='/board/updateBoard.do'/>";
            frm.submit();
        }
    }

    function fn_egov_delete() {
        if (confirm('Are you sure you want to delete?')) {
        	document.getElementById("detailForm").action = "<c:url value='/board/deleteBoard.do'/>";
           	document.getElementById("detailForm").submit();
        }
    }

    function fn_egov_reset() {
        $('form').each(function() {
            this.reset();
        });
    }
    </script>
</head>

<body>
<div id="container" class="inner">

	<!-- Page Title -->
	<h2 class="heading-large">
		<c:if test="${registerFlag == 'create'}">Regist</c:if>
		<c:if test="${registerFlag == 'modify'}">Update</c:if>
	</h2>

	<form:form id="detailForm" name="detailForm" modelAttribute="boardVO">
    <input type="hidden" id="searchCondition" name="searchCondition" value="${boardVO.searchCondition}" />
    <input type="hidden" id="searchKeyword" name="searchKeyword" value="${boardVO.searchKeyword}" />
    <input type="hidden" id="pageIndex" name="pageIndex" value="${boardVO.pageIndex}" />

		<div class="conts-wrap">
			<div class="fieldset input-form">
                <c:if test="${not empty boardVO.id}">
                <div class="form-group">
                    <div class="form-tit">
                        <label for="id">Id</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="id" readonly="true" cssClass="krds-input"/>
                    </div>
                </div>
                </c:if>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="title">Title</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="title" cssClass="krds-input" />
                        <form:errors path="title" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="content">Content</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="content" cssClass="krds-input" />
                        <form:errors path="content" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="author">Author</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="author" cssClass="krds-input" />
                        <form:errors path="author" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="viewCount">ViewCount</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="viewCount" cssClass="krds-input" />
                        <form:errors path="viewCount" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="createdAt">CreatedAt</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="createdAt" cssClass="krds-input" />
                        <form:errors path="createdAt" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-tit">
                        <label for="updatedAt">UpdatedAt</label>
                    </div>
                    <div class="form-conts">
                        <form:input path="updatedAt" cssClass="krds-input" />
                        <form:errors path="updatedAt" cssClass="error-message" />
                    </div>
                </div>

            </div>

			<!-- Action Buttons -->
			<div class="page-btn-wrap">
                <div class="btn-wrap">
	                <button type="button" class="krds-btn medium secondary" onclick="fn_egov_list()">List</button>
	                <button type="button" class="krds-btn medium tertiary" onclick="fn_egov_reset()">Reset</button>
                </div>
				<div class="btn-wrap">
					<c:if test="${registerFlag == 'modify'}">
						<button type="button" class="krds-btn medium" onclick="fn_egov_update()">Update</button>
						<button type="button" class="krds-btn medium danger" onclick="fn_egov_delete()">Delete</button>
					</c:if>
					<c:if test="${registerFlag == 'create'}">
						<button type="button" class="krds-btn medium" onclick="fn_egov_add()">Regist</button>
					</c:if>
				</div>
	        </div>
		</div>

	</form:form>

</div>

</body>
</html>
