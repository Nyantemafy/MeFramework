Bonjour : 
*MeFramework est un framework qui utilise le méthode MVC (Model View Controller) 
*Initialisation de l'environnemt : 
    > Copier le fichier,"Meframework.jar" dans votre projet java ,dans le répertoire "\lib"
    
*Utilisation : 
    > mette vos controller dans un dossie controller
    > il est initial d'annote vos class java par l'annotation : @AnnotedController()
    > annote les methodes de la classe : @AnnotedMth("votre_url")
    > configurer votre web.xml avec :
        .servlet-class : mg.itu.prom16.FrontController
        .param-value : controller
        .url-pattern : /


*Fonctionnalite :
    >retourne l'url present
    >retourne les methodes de la class
    >retourne la reponse de la methode specifie dans l'annotation si la methode retorne un String 

