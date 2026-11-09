<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>KHALID – MATH WHIZ</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="site">
    <h1>KHALID – MATH WHIZ</h1>
    <p>Create your own maths problem, solve it, and let Java check your answer</p>
</header>

<main>
    <div class="card">
        <h2>Welcome, player!</h2>
        <p class="hint">
            Choose a module, set the question yourself and type your answer.
            Java calculates the real result and awards the marks. Total: 10 marks.
            The difficulty (<span class="diff easy">Easy</span> <span class="diff medium">Medium</span>
            <span class="diff hard">Hard</span>) depends on how hard a question you set, and after every
            answer the game shows you the correct answer.
        </p>

        <c:if test="${not empty error}">
            <div class="msg bad"><c:out value="${error}"/></div>
        </c:if>

        <form method="post" action="${ctx}/game">
            <input type="hidden" name="action" value="start">
            <label class="field" for="playerName">Player Name</label>
            <input type="text" id="playerName" name="playerName" maxlength="100"
                   placeholder="Enter your name" value="<c:out value='${param.playerName}'/>" required autofocus>
            <div class="actions">
                <button type="submit" class="btn">Start Game</button>
                <a class="btn secondary" href="${ctx}/leaderboard">Leaderboard</a>
            </div>
        </form>
    </div>

    <div class="card">
        <h2>How it works</h2>
        <table>
            <tr><th>Module</th><th>What you do</th><th>Marks</th></tr>
            <tr><td>Prime Number</td><td>Enter a number (the game randomly adds or subtracts a random value), then answer 1 if prime or 0 if not</td><td>3</td></tr>
            <tr><td>Modulus</td><td>Enter two integers and type the remainder of a % b</td><td>3</td></tr>
            <tr><td>Trigonometry</td><td>Pick sin / cos / tan, enter a degree and type the value</td><td>4</td></tr>
        </table>
    </div>
</main>
</body>
</html>
