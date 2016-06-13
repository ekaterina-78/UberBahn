
<%@include file="/WEB-INF/jspf/header.jspf" %>
<%@include file="/WEB-INF/jspf/nav.jspf" %>

<section>
    <!--<ul>
        <li><a href="/stationTimetable">StationTimetable</a></li>
        <li><a href="/trainTimetable">TrainTimetable</a></li>

    </ul>-->
    <h2>Choose train:</h2>
    <table class="table">
        <tr>
            <th></th>
            <th>Train</th>
            <th>Route Title</th>
            <th>Departure</th>
            <th>Arrival</th>
            <th>Number Of Seats <br />Available</th>
        </tr>

        <tr>
            <td><input type="radio" /></td>
            <td>TrainId</td>
            <td>Route</td>
            <td>Date<br />Time<br />Station</td>
            <td>Date<br />Time<br />Station</td>
            <td>Seats</td>
        </tr>
    </table>
    <br />
    <input type="submit" value="Choose Train">
    <%@include file="/WEB-INF/jspf/image.jspf" %>
</section>
<%@include file="/WEB-INF/jspf/footer.jspf" %>
