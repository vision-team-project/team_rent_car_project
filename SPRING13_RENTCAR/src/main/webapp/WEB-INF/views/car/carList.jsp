<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    <c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>

<head>
	<meta charset="UTF-8">
	<title>렌트카 리스트</title>
	<link href="${path}/resources/css/memList.css" rel="stylesheet" />
	<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"
	integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
	<script src="https://kit.fontawesome.com/c895b3190c.js"
	crossorigin="anonymous"></script>
	<%
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		session = request.getSession();
	%>
</head>

<body>
	<div class="navi_container">
		<nav class="navi">
			<div class="navi_logo">
				<a href="/rentcar/">V-RENTCAR</a>
			</div>
			<ul class="naiv_menu">
			<%
			//===============================================================================
			//						로	그	인	O	-	관	리	자
			//===============================================================================
				if(session.getAttribute("login") != null) {
					String mid = (String)session.getAttribute("id"); //String mid생성
				
				if(mid.equals("admin"))	{	//관리자(admin) 로그인
			%>
				<li><a href="/rentcar/admin/pageAnalyze">PageAnalyze</a></li>
				<li><a href="/rentcar/car/carList">RentCarList</a></li>
				<li><a href="/rentcar/member/memList">MemberList</a></li>
				<li><a href="/rentcar/reserve/allList">ReserveList</a></li>
				<li><a href="/rentcar/board/listPage">QnA</a></li>
				<li><a href="/rentcar/member/myList?id=${login.id}">MyPage</a></li>
				<li id="a"><i class="fas fa-user"></i>관리자</li>
				<li><a href="logout">Logout</a></li>
				<li><a href="#" class="navi__toggle"><i class="fas fa-bars"></i></a></li>
			<%
				} else{
			//===============================================================================
			//							로	그	인	O	-	유	저
			//===============================================================================
			%>		
				<li><a href="/rentcar/reserve/catalog">RentCar</a></li>
				<li><a href="/rentcar/board/listPage">QnA</a></li>
				<li><a href="/rentcar/reserve/byMemList?memid=${login.id}">MyReserve</a><li>
				<li><a href="/rentcar/member/myList?id=${login.id}">MyPage</a><li>
				<li id="a"><i class="fas fa-user"></i><%=mid %>님</li>
				<li><a href="logout">Logout</a></li>
				<li><a href="#" class="navi__toggle"><i class="fas fa-bars"></i></a></li>
			<%	
				}
				} else {
			//===============================================================================
			//								로	그	인	X
			//===============================================================================
			%>		
				<li><a href="/rentcar/reserve/catalog">RentCar</a></li>
				<li><a href="/rentcar/board/listPage">QnA</a></li>
				<li><a href="/rentcar/member/loginForm">Login</a></li>
				<li><a href="#" class="navi__toggle"><i class="fas fa-bars"></i></a></li>
			<%
				}
			%>
			</ul>
		</nav>
	</div>
	<!-- ===========================================네비 메뉴 끝 =============================== -->

	<section class="joinSection">
			<div class="reserveCheckWrap">
	<h3 class="recoText carlist">렌트카 리스트</h3>
	<div class="carinsertButton">
		<a href="register">차량 등록</a>
	</div>
	
	<form role="form" method="get">
		<div class="search" style="float:right;">
			<select name="searchType">
				<option value="cn"
					<c:out value="${scri.searchType eq 'cn' ? 'selected' : ''}"/>>차량
					이름</option>
			</select> 
			<input type="text" name="keyword" id="keywordInput"
				value="${scri.keyword}" />
	
			<button id="searchBtn" type="submit">검색</button>
			<script>
				$(function() {
					$('#searchBtn').click(
							function() {
								self.location = "catalog"
										+ '${pageMaker.makeQuery(1)}'
										+ "&searchType="
										+ $("select option:selected").val()
										+ "&keyword="
										+ encodeURIComponent($('#keywordInput')
												.val());
							});
				});
			</script>
		</div>	
	</form>
	
	
	
	
	
	
	<table class="reserveList">
		<tr>
			<th>번호</th>
			<th>모델명</th>
			<th>종류</th>
			<th>가격</th>
			<th>인승</th>
			<th>제조사</th>
			<th>이미지</th>
			<th>상세정보</th>
			<th>편집</th>
		</tr>
		<c:forEach items="${carList}" var="dto">
			<tr>
				<td>${dto.no}</td>
				<td>${dto.name}</td>
				<td>
					<c:set var="category" value="${dto.category}"/>
						<c:choose>
							<c:when test="${category eq '1'}">
								소형
							</c:when>
							<c:when test="${category eq '2'}">
								중형
							</c:when>
							<c:when test="${category eq '3'}">
								대형
							</c:when>
							<c:otherwise>
								정보없음
							</c:otherwise>
						</c:choose>
				</td>
				<td>${dto.price}</td>
				<td>${dto.usepeople}</td>
				<td>${dto.company}</td>
				<td><img src="<spring:url value='/resources/img/${dto.img}'/>" alt="car" width="200" height="150"></td>
				<td>${dto.info}</td>
				<td>
					<button onclick="location.href='delete?no=${dto.no}'">삭제</button>
					<button onclick="location.href='updateView?no=${dto.no}'">수정</button>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>
</section>
</body>
</html>
