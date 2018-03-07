<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head profile="http://selenium-ide.openqa.org/profiles/test-case">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="selenium.base" href="http://localhost/graphunit/web/dbWeb/index.php" />
<title>RegisterTest</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">RegisterTest</td></tr>
</thead><tbody>
<tr>
	<td>assertVisible</td>
	<td>link=Register</td>
	<td></td>
</tr>
<tr>
	<td>click</td>
	<td>link=Register</td>
	<td></td>
</tr>
<tr>
	<td>type</td>
	<td>id=email</td>
	<td>test@gmail.com</td>
</tr>
<tr>
	<td>type</td>
	<td>id=password</td>
	<td>test1</td>
</tr>
<tr>
	<td>click</td>
	<td>id=submitButton</td>
	<td></td>
</tr>
<tr>
	<td>assertAlert</td>
	<td>User successfully created</td>
	<td></td>
</tr>
<tr>
	<td>assertVisible</td>
	<td>link=Login</td>
	<td></td>
</tr>

</tbody></table>
</body>
</html>
