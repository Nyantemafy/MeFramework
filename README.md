Bonjour : 
*MeFramework est un framework qui utilise le méthode MVC (Model View Controller) 
*Initialisation de l'environnemt : 
    > Copier le fichier,"Meframework.jar" dans votre projet java ,dans le répemrtoire "\lib"
    
*Utilisation : 
    > mette vos controller dans un dossie controller
    > il est initial d'annote vos class java par l'annotation : @AnnotedController()
    > annote les methodes de la classe : @AnnotedMth("votre_url")
    > on a aussi un class ModelView avec un methode addObject(String key, Object value) 
    > configurer votre web.xml avec :
        .servlet-class : mg.itu.prom16.FrontController
        .param-value : controller
        .url-pattern : /

*Fonctionnalite :
    >retourne l'url present
    >retourne les methodes de la class
    >retourne la reponse de la methode specifie dans l'annotation si c'est un String ou ModelView   

