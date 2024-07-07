<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page 1</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
</head>
<body>
    <div class="container">
        <div class="card offset-md-3 col-md-7">
            <div class="card-body">
                <h5 class="card-title">Formulaire de test</h5>
                <form action="submitObject" method="get">
                    <div class="form-group">
                        <label for="name">Name</label>
                        <input id="name" name="emp.name" type="text" class="form-control" placeholder="Entrez votre nom">
                    </div>
                    <div class="form-group">
                        <label for="age">Age</label>
                        <input type="number" id="age" name="emp.age" class="form-control" placeholder="Entrez votre age">
                    </div>
                    <div class="text-center">
                        <input type="submit" class="btn btn-dark" value="Valider">
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
