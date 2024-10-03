Bonjour : 
*MeFramework est un framework qui utilise le méthode MVC (Model View Controller) 
*Initialisation de l'environnemt : 
    > Copier le fichier,"Meframework.jar" dans votre projet java ,dans le répemrtoire "\lib"
    
*Utilisation : 
    > mette vos controller dans un dossie controller
    > mettre aussi les models dans un dossier model 
    > il est initial d'annote vos class java par l'annotation : @AnnotedController()
    > Annote les paramatres par @Param
    > Pouvoir utilise CurrentSession pour l'utilisation des sessions
    > annote les methodes de la classe : @AnnotedMth("votre_url")
    > on a aussi un class ModelView avec un methode addObject(String key, Object value) 
    > aussi les fonctions de CurrentSession sont : get(String key), add(String key, Object objet), delete(String key)
    > Vous avez aussi a votre disposition une classe Mapping 
    > configurer votre web.xml avec :
        .servlet-class : mg.itu.prom16.FrontController
        .param-value : controller
        .url-pattern : /
    > mettre l'Annotation RestApi pour avoir en json les resultats
    > Annote les methodes par GET ou POST si non par defaut c'est un GET

*Fonctionnalite :
    >retourne l'url present
    >retourne les methodes de la class
    >acces a la liste des controller
    >retourne la reponse de la methode specifie dans l'annotation si c'est un String ou ModelView 
    >Gere aussi les ModelView avec en parametre un Object
    >Gere les exception sur le jsp et le controllerr Tomcat
    >pouvoir gere les session  
    >Gere les json
    >Gere la methode post

