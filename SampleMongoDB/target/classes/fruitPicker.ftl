<html>
<head>
<title>Fruit Picker</title>
</head>
<body>
<form action="/fruit/favorite" method="POST">
<p>What is your favorite fruit?</p>
<#list fruits as fruit>
<p>
<input type="radio" name="fruit" value="${fruit}" >${fruit} </input>
</p>
</#list>

<input type="submit" value="Submit">

</body>
</html>