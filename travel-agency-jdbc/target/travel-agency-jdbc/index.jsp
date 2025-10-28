<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Travel Agency — SQL Executor</title>
</head>
<body>
  <h2>Travel Agency — Raw SQL Executor (DEMO ONLY)</h2>
  <p><strong>Увага:</strong> ця сторінка виконує довільний SQL. Використовувати лише на локальній тестовій БД.</p>

  <form action="execute" method="post">
    <label for="sql">SQL (введіть повний запит, наприклад <code>SELECT * FROM offer;</code>):</label><br/>
    <textarea id="sql" name="sql" rows="6" cols="100">${param.sql}</textarea><br/>
    <input type="submit" value="Execute"/>
  </form>

  <hr/>
  <h3>Результат</h3>
  <c:if test="${not empty message}">
    <pre style="color:maroon">${message}</pre>
  </c:if>
  <c:if test="${not empty resultHtml}">
    <div>${resultHtml}</div>
  </c:if>
</body>
</html>
