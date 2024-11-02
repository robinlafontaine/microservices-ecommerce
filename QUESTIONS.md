# Architecture Microservices 

## Gestion de la Charge et de l'État des Services

**Question :** En cas de grande charge sur notre application, nous souhaitons ajouter plusieurs instances d'un micro-service. Comment garder la trace des URL des différentes instances disponibles, ainsi que leurs états ?

**Réponse :** 
- Traefik gère automatiquement la découverte des services lors de la réplication de nos services avec Docker Swarm.
- Leur état initial est surveillé avec des healthchecks, mais un service de gestion (comme Portainer) aurait permis la surveillance en continu des services.

## Communication Inter-Services

**Question :** Comment permettre à chaque micro-service de faire appel à un autre directement, sans être dépendant d'un Load Balancer central ?

**Réponse :**
Puisque les services sont sur le même réseau `backend`, ils peuvent s'appeler directement par leur nom de service sans pour autant passer par Traefik (le Load Balancer dans notre cas).

## API Gateway

Traefik, notre Gateway dans ce cas, est configuré pour router les requêtes vers les bons services via des règles.

Les requêtes (à l'exception du login, de la création de compte et des requêtes dites 'pre-flight') sont interceptées et envoyées vers le service d'authentification pour vérifier les autorisations de l'utilisateur à l'aide du JWT attaché à sa requête. Selon la réponse du service d'auth, la requête continue son chemin vers sa destination ou est refusée et renvoie une erreur 401.

## Monitoring et Traçage

**Question :** Comment identifier le micro-service posant des problèmes ?

**Réponse :** 
Tous les services sont connectés au même réseau que Zipkin et peuvent donc envoyer leurs traces.
