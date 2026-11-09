<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>KHALID – MATH WHIZ | Leaderboard</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="site">
    <h1>KHALID – MATH WHIZ</h1>
    <p>Top 10 Leaderboard</p>
</header>

<main>
    <c:if test="${not empty error}">
        <div class="msg bad"><c:out value="${error}"/></div>
    </c:if>

    <div class="card">
        <h2>Top 10 Scores</h2>
        <table>
            <tr><th>Rank</th><th>Player</th><th>Score</th><th>Difficulty</th><th>Date / Time</th></tr>
            <c:forEach var="s" items="${scores}" varStatus="st">
                <tr>
                    <td class="rank">${st.index + 1}</td>
                    <td><c:out value="${s.playerName}"/></td>
                    <td>${s.score} / 10</td>
                    <td><span class="diff ${s.difficultyCss}"><c:out value="${s.difficultyLabel}"/></span></td>
                    <td><c:out value="${s.playedAtFormatted}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty scores}">
                <tr><td colspan="5" class="empty">No scores yet. Be the first to play!</td></tr>
            </c:if>
        </table>
    </div>

    <div class="actions">
        <a class="btn" href="${ctx}/index.jsp">Play Game</a>
    </div>
</main>
</body>
</html>
