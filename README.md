# **MeFramework - Documentation**

## **Introduction**
MeFramework est un framework Java qui applique le modèle de conception **MVC** (Model View Controller). Il permet de gérer facilement les contrôleurs, les modèles, et la gestion des sessions pour les applications web.

## **Initialisation de l'Environnement**

1. **Ajouter MeFramework à votre projet :**
   - Copiez le fichier `Meframework.jar` dans le répertoire `\lib` de votre projet Java.

## **Utilisation**

1. **Organisation des fichiers :**
   - Placez vos **contrôleurs** dans un dossier `controller`.
   - Placez vos **modèles** dans un dossier `model`.

2. **Annotations nécessaires :**
   - **Contrôleur :** Annotez vos classes Java avec `@AnnotedController()`.
   - **Paramètres :** Utilisez `@Param` pour annoter les paramètres de vos méthodes.
   - **Session :** Utilisez la classe `CurrentSession` pour la gestion des sessions (avec les méthodes : `get(String key)`, `add(String key, Object objet)`, `delete(String key)`).

3. **Configuration des méthodes et des URLs :**
   - **Méthodes annotées :** Annotez les méthodes de vos contrôleurs avec `@AnnotedMth("votre_url")`.
   - **API Rest :** Pour retourner des résultats en **JSON**, utilisez l'annotation `@RestApi`.
   - **Méthodes HTTP :** Annotez les méthodes avec `@GET` ou `@POST` (par défaut, c'est un **GET**).
   - **Upload de fichier :** Utilisez l'annotation `@Part` sur le paramètre qui reçoit le fichier à télécharger.
   - **Autorisation :** Protégez l'accès avec l'annotation `@Autorisation(role = "admin")` pour restreindre l'accès aux administrateurs.

4. **Classe `ModelView` :**
   - Utilisez la classe `ModelView` pour ajouter des objets avec la méthode `addObject(String key, Object value)`.

5. **Configuration de `web.xml` :**
   - Ajoutez les éléments suivants dans votre fichier `web.xml` pour configurer le **FrontController** :
     ```xml
     <servlet>
         <servlet-name>FrontController</servlet-name>
         <servlet-class>mg.itu.prom16.FrontController</servlet-class>
         <init-param>
             <param-name>controller</param-name>
             <param-value>votreController</param-value>
         </init-param>
         <load-on-startup>1</load-on-startup>
     </servlet>
     <servlet-mapping>
         <servlet-name>FrontController</servlet-name>
         <url-pattern>/</url-pattern>
     </servlet-mapping>
     ```

## **Fonctionnalités**

1. **Gestion des URLs et des méthodes :**
   - Retourne l'URL actuelle.
   - Récupère les méthodes d'une classe spécifique.

2. **Gestion des contrôleurs :**
   - Accédez à la liste des contrôleurs enregistrés.

3. **Réponse des méthodes annotées :**
   - Retourne la réponse d'une méthode si elle est annotée avec `@AnnotedMth`. Cela peut être un `String` ou un `ModelView`.

4. **Gestion des exceptions :**
   - Gère les exceptions au niveau du contrôleur et du serveur Tomcat.
   - Gère les pages d'erreur (404, etc.).

5. **Gestion des sessions :**
   - Gestion de la session utilisateur via la classe `CurrentSession`.

6. **Support JSON :**
   - Retourne les résultats en format JSON pour les API REST.

7. **Gestion des méthodes HTTP :**
   - Support des méthodes **POST** et **GET** pour les requêtes HTTP.

8. **Upload de fichiers :**
   - Permet l'upload de fichiers avec les annotations appropriées.

9. **Contrôle d'accès :**
   - Gestion des autorisations via l'annotation `@Autorisation`, avec des rôles comme "admin".
