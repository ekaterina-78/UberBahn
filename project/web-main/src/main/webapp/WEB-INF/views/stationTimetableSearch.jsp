<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/jspf/header.jspf" %>
<%@include file="/WEB-INF/jspf/nav.jspf" %>

<section>
    <h2>Station Timetable</h2>
    <form class="station_timetable_search_form">
        <p><label>Station </label>
            <select id="station">
                <option value="null">Select</option>
                <c:forEach var="station" items="${stations}">
                <option value="${station.id}">${station.title}</option>
                </c:forEach>
            </select></p>
        <p><label>Since </label><input id="sinceDate" type="date"><input id="sinceTime" type="time" value="00:00" /></p>
        <p><label>Until </label><input id="untilDate" type="date"><input id="untilTime" type="time" value="23:59"/></p>
        <p><input id = "search" type="button" value="Search"></p>
    </form>
    <%@include file="/WEB-INF/jspf/image.jspf" %>
</section>
<script src="/scripts/stationTimetableSearch.js"></script>

<%@include file="/WEB-INF/jspf/footer.jspf" %>
