<%@ page import="model.Employe" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
   <%
    Employe emp = (Employe) request.getAttribute("emp");
    if (emp != null) {
        out.println(emp.getName());
        out.println(emp.getAge());
    }
    else{
        out.println("tss");
    }
    %>
</body>
</html>
