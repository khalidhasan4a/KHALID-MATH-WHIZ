<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${empty state}"><c:redirect url="/index.jsp"/></c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>KHALID – MATH WHIZ | Game</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="site">
    <h1>KHALID – MATH WHIZ</h1>
    <p>Set your own problem. Java checks your answer.</p>
</header>

<main>
    <div class="card status">
        <div>Player: <strong><c:out value="${state.playerName}"/></strong></div>
        <div class="score">Score: ${state.score} / ${state.maxScore}</div>
    </div>

    <c:if test="${not empty message}">
        <div class="msg ${messageOk ? 'ok' : 'bad'}"><c:out value="${message}"/></div>
    </c:if>

    <%-- ===== Correct answer card: shown after every answer so the player can learn ===== --%>
    <c:if test="${not empty lastAttempt}">
        <div class="card feedback ${lastAttempt.correct ? 'ok' : 'bad'}">
            <h2>${lastAttempt.correct ? '&#10004; Correct!' : '&#10008; Not correct'}
                <span class="diff ${lastAttempt.difficultyCss}">${lastAttempt.difficultyLabel}</span></h2>
            <table class="plain">
                <tr><th>Module</th><td><c:out value="${lastAttempt.moduleLabel}"/></td></tr>
                <tr><th>Question</th><td><c:out value="${lastAttempt.question}"/></td></tr>
                <tr><th>Your answer</th><td><c:out value="${lastAttempt.yourAnswer}"/></td></tr>
                <tr><th>Correct answer</th><td class="right-answer"><c:out value="${lastAttempt.correctAnswer}"/></td></tr>
                <tr><th>Marks</th><td>${lastAttempt.marks} / ${lastAttempt.maxMarks}</td></tr>
            </table>
            <p class="why"><b>Why?</b> <c:out value="${lastAttempt.explanation}"/></p>
        </div>
    </c:if>

    <c:choose>

        <%-- ============ PRIME: step 1 - enter a number ============ --%>
        <c:when test="${screen eq 'primeInput'}">
            <div class="card">
                <h2>Prime Number</h2>
                <p class="hint">
                    Type any whole number. The game picks a <b>random value (1–20)</b> and either
                    <b>adds it to</b> or <b>subtracts it from</b> your number (chosen randomly, one at a time).
                    Then you decide whether the new number is prime:
                    answer <b>1</b> if it is prime, <b>0</b> if it is not.
                </p>
                <p class="hint guide"><b>Difficulty</b> is set by the new number:
                    <span class="diff easy">Easy</span> up to 50 &nbsp;
                    <span class="diff medium">Medium</span> 51–500 &nbsp;
                    <span class="diff hard">Hard</span> above 500</p>
                <form method="post" action="${ctx}/game">
                    <input type="hidden" name="action" value="primeGenerate">
                    <label class="field" for="number">Your number</label>
                    <input type="number" id="number" name="number" value="<c:out value='${param.number}'/>" required autofocus>
                    <div class="actions">
                        <button type="submit" class="btn">Generate Question</button>
                        <a class="btn secondary" href="${ctx}/game">Back</a>
                    </div>
                </form>
            </div>
        </c:when>

        <%-- ============ PRIME: step 2 - answer 1 or 0 ============ --%>
        <c:when test="${screen eq 'primeAnswer'}">
            <div class="card">
                <h2>Prime Number</h2>
                <p class="hint">
                    You entered <b>${state.primeBase}</b> and the game <b>${state.primeVerb}</b> a random
                    <b>${state.primeExtra}</b>:
                </p>
                <p style="text-align:center" class="hint">
                    ${state.primeBase} ${state.primeOperator} ${state.primeExtra} =</p>
                <div class="big-number">${state.primeNumber}</div>
                <p style="text-align:center"><b>Is it a prime number?</b></p>
                <form method="post" action="${ctx}/game">
                    <input type="hidden" name="action" value="primeAnswer">
                    <div class="choices">
                        <label class="choice"><input type="radio" name="choice" value="1" required>
                            <span class="big">1</span>Prime</label>
                        <label class="choice"><input type="radio" name="choice" value="0" required>
                            <span class="big">0</span>Not Prime</label>
                    </div>
                    <div class="actions">
                        <button type="submit" class="btn">Submit Answer (${primeMarks} marks)</button>
                        <a class="btn secondary" href="${ctx}/game">Back</a>
                    </div>
                </form>
            </div>
        </c:when>

        <%-- ============ MODULUS ============ --%>
        <c:when test="${screen eq 'mod'}">
            <div class="card">
                <h2>Modulus</h2>
                <p class="hint">
                    Enter two <b>integers</b> (a and b). Then work out the remainder of
                    <b>a % b</b> yourself and type it in the answer box. The divisor (b) must not be zero.
                </p>
                <p class="hint guide"><b>Difficulty</b> is set by your numbers:
                    <span class="diff easy">Easy</span> up to 50 &nbsp;
                    <span class="diff medium">Medium</span> 51–500 &nbsp;
                    <span class="diff hard">Hard</span> above 500
                    (a negative number makes it one level harder)</p>
                <form method="post" action="${ctx}/game">
                    <input type="hidden" name="action" value="modAnswer">
                    <label class="field" for="a">First number (a)</label>
                    <input type="number" id="a" name="a" step="1" value="<c:out value='${param.a}'/>" required autofocus>
                    <label class="field" for="b">Second number (b)</label>
                    <input type="number" id="b" name="b" step="1" value="<c:out value='${param.b}'/>" required>
                    <label class="field" for="answer">Your answer (a % b)</label>
                    <input type="number" id="answer" name="answer" step="1" value="<c:out value='${param.answer}'/>" required>
                    <div class="actions">
                        <button type="submit" class="btn">Submit Answer (${modMarks} marks)</button>
                        <a class="btn secondary" href="${ctx}/game">Back</a>
                    </div>
                </form>
            </div>
        </c:when>

        <%-- ============ TRIGONOMETRY ============ --%>
        <c:when test="${screen eq 'trig'}">
            <div class="card">
                <h2>Trigonometry</h2>
                <p class="hint">
                    Choose a ratio, enter an angle in <b>degrees</b>, then type the value you think it has.
                    An approximate answer is fine: it is correct if it is close to the real value
                    (within ±0.05, or within 3% for big values like tan 89°). A big difference is wrong.
                </p>
                <p class="hint guide"><b>Difficulty</b> is set by your angle:
                    <span class="diff easy">Easy</span> 0, 30, 45, 60, 90 &nbsp;
                    <span class="diff medium">Medium</span> those angles in other quadrants (120, 210, −30…) &nbsp;
                    <span class="diff hard">Hard</span> any other angle (37, 75, 12.5…)</p>
                <form method="post" action="${ctx}/game">
                    <input type="hidden" name="action" value="trigAnswer">
                    <label class="field" for="ratio">Ratio</label>
                    <select id="ratio" name="ratio" required>
                        <option value="sin" ${param.ratio eq 'sin' ? 'selected' : ''}>Sin</option>
                        <option value="cos" ${param.ratio eq 'cos' ? 'selected' : ''}>Cos</option>
                        <option value="tan" ${param.ratio eq 'tan' ? 'selected' : ''}>Tan</option>
                    </select>
                    <label class="field" for="degree">Angle (degrees)</label>
                    <input type="number" id="degree" name="degree" step="any" value="<c:out value='${param.degree}'/>" required>
                    <label class="field" for="answer">Your answer</label>
                    <input type="number" id="answer" name="answer" step="any" value="<c:out value='${param.answer}'/>" required>
                    <div class="actions">
                        <button type="submit" class="btn">Submit Answer (${trigMarks} marks)</button>
                        <a class="btn secondary" href="${ctx}/game">Back</a>
                    </div>
                </form>
            </div>
        </c:when>

        <%-- ============ MENU: choose a module ============ --%>
        <c:otherwise>
            <div class="card">
                <h2>Choose a module to play</h2>
                <form method="get" action="${ctx}/game">

                    <label class="module ${state.primeDone ? 'done' : ''}">
                        <input type="radio" name="module" value="prime" required ${state.primeDone ? 'disabled' : ''}>
                        <span class="m-title">Prime Number</span>
                        <c:choose>
                            <c:when test="${state.primeDone}"><span class="badge done">Done: ${state.primeAttempt.marks}/${state.primeAttempt.maxMarks}</span></c:when>
                            <c:otherwise><span class="badge">${primeMarks} marks</span></c:otherwise>
                        </c:choose>
                        <span class="m-desc">Enter a number, the game adds a random value; answer 1 (prime) or 0 (not prime).</span>
                    </label>

                    <label class="module ${state.modDone ? 'done' : ''}">
                        <input type="radio" name="module" value="mod" ${state.modDone ? 'disabled' : ''}>
                        <span class="m-title">Modulus</span>
                        <c:choose>
                            <c:when test="${state.modDone}"><span class="badge done">Done: ${state.modAttempt.marks}/${state.modAttempt.maxMarks}</span></c:when>
                            <c:otherwise><span class="badge">${modMarks} marks</span></c:otherwise>
                        </c:choose>
                        <span class="m-desc">Enter two integers and type the remainder of a % b.</span>
                    </label>

                    <label class="module ${state.trigDone ? 'done' : ''}">
                        <input type="radio" name="module" value="trig" ${state.trigDone ? 'disabled' : ''}>
                        <span class="m-title">Trigonometry</span>
                        <c:choose>
                            <c:when test="${state.trigDone}"><span class="badge done">Done: ${state.trigAttempt.marks}/${state.trigAttempt.maxMarks}</span></c:when>
                            <c:otherwise><span class="badge">${trigMarks} marks</span></c:otherwise>
                        </c:choose>
                        <span class="m-desc">Pick sin / cos / tan, enter a degree and type the value.</span>
                    </label>

                    <div class="actions">
                        <button type="submit" class="btn">Play Selected Module</button>
                        <a class="btn secondary" href="${ctx}/leaderboard">Leaderboard</a>
                    </div>
                </form>

                <c:if test="${state.attemptCount > 0}">
                    <form method="post" action="${ctx}/game" style="margin-top:12px">
                        <input type="hidden" name="action" value="finish">
                        <button type="submit" class="btn green">Finish &amp; Save Score</button>
                    </form>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
