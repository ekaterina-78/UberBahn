<%@include file="/WEB-INF/jspf/header.jspf" %>
<%@include file="/WEB-INF/jspf/nav.jspf" %>

<section>
<c:choose>
    <c:when test="${not empty exception}">
        <h3>${exception}</h3>
    </c:when>
    <c:otherwise>
    <h2>Route Information</h2>
    <p>Id: ${route.id}</p>
    <p>Title: ${route.title}</p>
    <p>Time of Departure: ${route.timeOfDeparture}</p>
    <p>Stations: </p>
    <ul>
    <c:forEach var="spot" items="${route.spots}">
        <li>${spot.stationTitle}</li>
    </c:forEach>
    </ul>
    <p>Price per Minute: ${route.pricePerMinute}</p>

    </c:otherwise>
</c:choose>
</section>
<%@include file="/WEB-INF/jspf/footer.jspf" %>
