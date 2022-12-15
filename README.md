# Repo pour le microservice S3

## structure URL pour spring

    - objets: retourner une liste des objets sur le bucket
    - objet/{nom}: retourne si l'objet est présent ou non (200 ou 404)

## Dockerfile

Créer l'image avec la commande

    sudo docker build -t awss3:latest . 

Il a été choisi de passer le fichier .env en paramètre du docker run. Copier le .env dans le Dockerfile est
problèmatique si nous devons pousser l'image sur une registry. L'utilisation des secrets est compliqué pour peu de plus values.
Le .env doit dans tous les cas etre présent à la racine du projet Java alors autant l'utiliser telquel.

La commande suivante permet de lancer l'application Spring
    
    sudo docker run --env-file .env -p 8080:8080 awss3:latest