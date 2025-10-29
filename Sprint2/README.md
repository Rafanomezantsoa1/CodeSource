# Framework Java Simple avec Jakarta EE

## Description

Ce framework Java simple utilise Jakarta EE et intercepte toutes les URLs non-ressources pour afficher une page d'informations au lieu d'une erreur 404.

## Principe de fonctionnement

1. **FrontServlet** : Se configure automatiquement via `@WebServlet(urlPatterns = {"/"})` 
2. **Ressources statiques** : Les fichiers `.html`, `.css`, `.js`, `.jsp`, etc. sont servis normalement par le serveur
3. **URLs inexistantes** : Interceptées automatiquement par le FrontServlet qui affiche une page listant les ressources disponibles
4. **Configuration automatique** : Plus besoin de configurer le mapping dans `web.xml` - le framework s'installe tout seul !

## Avantages

- ✅ **Plug & Play** : Ajoutez simplement le JAR du framework dans `WEB-INF/lib/`
- ✅ **Zero configuration** : Le framework se configure automatiquement 
- ✅ **Réutilisable** : Fonctionne dans n'importe quel projet web Jakarta EE
- ✅ **Non intrusif** : N'interfère pas avec les ressources existantes

## Structure du projet

```
frameworkJAVA/                 # Le framework
├── src/main/java/mg/framework/
│   └── FrontServlet.java       # Le seul fichier du framework
├── pom.xml                     # Configuration Maven
└── README.md

teste/                          # Application de test
├── src/main/webapp/
│   ├── index.html              # Page d'accueil
│   ├── about.html              # Page à propos
│   ├── contact.jsp             # Page JSP
│   ├── css/style.css           # Styles CSS
│   ├── js/script.js            # Scripts JavaScript
│   └── WEB-INF/
│       ├── web.xml             # Configuration Tomcat
│       └── lib/                # Dossier pour le JAR du framework
└── pom.xml                     # Configuration Maven
```

## Compilation et déploiement

### 1. Compiler le framework

```bash
cd frameworkJAVA
mvn clean package
```

Cela génère le fichier `framework-java-1.0.0.jar` dans le dossier `target/`.

### 2. Utiliser le framework dans un projet

Pour utiliser le framework dans un nouveau projet :

1. **Copiez le JAR** `framework-java-1.0.0.jar` dans le dossier `WEB-INF/lib/` de votre projet
2. **C'est tout !** Le framework se configure automatiquement

Aucune configuration dans `web.xml` n'est nécessaire. Le framework s'active automatiquement grâce à l'annotation `@WebServlet`.

### 3. Compiler l'application de test

```bash
cd teste
mvn clean package
```

Cela génère le fichier `teste-framework-1.0.0.war` dans le dossier `target/`.

### 4. Déployer sur Tomcat

1. Copier le fichier `.war` dans le dossier `webapps/` de Tomcat
2. Démarrer Tomcat
3. Accéder à l'application via `http://localhost:8080/teste-framework-1.0.0/`

## Test du framework

Une fois déployé, testez les URLs suivantes :

- `http://localhost:8080/teste-framework-1.0.0/` → Page d'accueil (index.html)
- `http://localhost:8080/teste-framework-1.0.0/about.html` → Page à propos (HTML)
- `http://localhost:8080/teste-framework-1.0.0/contact.jsp` → Page contact (JSP)
- `http://localhost:8080/teste-framework-1.0.0/css/style.css` → Fichier CSS
- `http://localhost:8080/teste-framework-1.0.0/js/script.js` → Fichier JavaScript
- `http://localhost:8080/teste-framework-1.0.0/inexistant` → **Page du framework** (au lieu de 404)
- `http://localhost:8080/teste-framework-1.0.0/autre/test` → **Page du framework** (au lieu de 404)

## Fonctionnalités

- ✅ Interception des URLs inexistantes
- ✅ Affichage des ressources disponibles
- ✅ Interface utilisateur claire et responsive
- ✅ Support des fichiers HTML, JSP, CSS, JS
- ✅ Compatible Jakarta EE
- ✅ Aucune erreur 404 visible

## Configuration web.xml

Le framework ne nécessite **AUCUNE configuration** dans le `web.xml` !

Il se configure automatiquement grâce à l'annotation `@WebServlet(urlPatterns = {"/"})` dans le FrontServlet.

Votre `web.xml` peut rester minimal :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="5.0">
    <display-name>Mon Application</display-name>
    
    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
</web-app>
```

Le framework s'occupe du reste automatiquement !