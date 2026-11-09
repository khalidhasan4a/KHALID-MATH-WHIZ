<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${empty state}"><c:redirect url="/index.jsp"/></c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>KHALID – MATH WHIZ | Result</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="site">
    <h1>KHALID – MATH WHIZ</h1>
    <p>Final result</p>
</header>

<main>
    <c:if test="${not empty message}">
        <div class="msg ${messageOk ? 'ok' : 'bad'}"><c:out value="${message}"/></div>
    </c:if>

    <div class="card final">
        <p class="hint">Player</p>
        <h2 style="margin:0"><c:out value="${state.playerName}"/></h2>
        <div class="total">${state.score} / ${state.maxScore}</div>
        <p class="hint">Difficulty: <span class="diff ${state.difficultyCss}">${state.difficultyLabel}</span></p>
    </div>

    <div class="card">
        <h2>Your answers</h2>
        <table>
            <tr><th>Module</th><th>Level</th><th>Question</th><th>Your answer</th><th>Correct answer</th><th>Marks</th></tr>
            <c:forEach var="a" items="${state.attempts}">
                <tr class="${a.correct ? 'ok' : 'bad'}">
                    <td><c:out value="${a.moduleLabel}"/></td>
                    <td><span class="diff ${a.difficultyCss}">${a.difficultyLabel}</span></td>
                    <td><c:out value="${a.question}"/></td>
                    <td><c:out value="${a.yourAnswer}"/></td>
                    <td class="right-answer"><c:out value="${a.correctAnswer}"/></td>
                    <td>${a.marks} / ${a.maxMarks}</td>
                </tr>
                <tr class="why-row">
                    <td colspan="6"><b>Why?</b> <c:out value="${a.explanation}"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <c:choose>
        <c:when test="${state.saved}">
            <div class="msg ok">Your score was saved to the leaderboard database.</div>
        </c:when>
        <c:otherwise>
            <div class="msg bad">
                Your score could not be saved to the database.
                <c:if test="${not empty state.saveError}"><br><small><c:out value="${state.saveError}"/></small></c:if>
                <form method="post" action="${ctx}/game" style="margin-top:10px">
                    <input type="hidden" name="action" value="finish">
                    <button type="submit" class="btn">Retry Save</button>
                </form>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="actions">
        <a class="btn" href="${ctx}/index.jsp">Play Again</a>
        <a class="btn secondary" href="${ctx}/leaderboard">View Leaderboard</a>
    </div>
</main>
</body>
</html>
