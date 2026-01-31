# 🏦 MyBank Projection API

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/K3s-Raspberry_Pi-blue?style=for-the-badge&logo=kubernetes)](https://k3s.io/)
[![PostgreSQL](https://img.shields.io/badge/Database-Aiven_Cloud-blue?style=for-the-badge&logo=postgresql)](https://aiven.io/)

## 🎯 Le Concept
Une API de gestion bancaire personnelle qui ne se contente pas de lister le passé, mais **projette votre avenir financier**. En intégrant nativement les transactions récurrentes et prévisionnelles, elle permet de visualiser l'état de votre solde à 6, 12 ou 24 mois.

## 🛠 Architecture Technique (Hybride & Cloud-Native)

Le projet repose sur une infrastructure hybride visant la haute disponibilité et le coût zéro :

* **Production :** Déployée sur un cluster **k3s (Raspberry Pi)** avec base de données **PostgreSQL managée sur Aiven Cloud**.
* **Messaging :** Système orienté événements utilisant **Kafka (Aiven)** en production et **Redpanda (C++)** en préproduction pour optimiser les ressources du Pi.
* **GitOps :** Déploiement automatisé via **GitHub Actions** (Multi-arch ARM64 images) et synchronisation continue.



## 🏗 Stack Technique
* **Backend:** Java 21 (Records, Virtual Threads), Spring Boot 3.4.
* **Data:** Spring Data JPA, Hibernate, PostgreSQL.
* **Streaming:** Spring Kafka / Redpanda.
* **Sécurité:** Spring Security, JWT (HttpOnly Cookies), SSL Encryption.
* **DevOps:** Podman (Rootless), k3s, GitHub Actions, GHCR.io.

## 🚀 Stratégie d'Environnement

| Feature | Développement | Préproduction | Production |
| :--- | :--- | :--- | :--- |
| **Host** | Local Machine | Raspberry Pi (k3s) | Raspberry Pi (k3s) |
| **Database** | Podman (Postgres) | Podman (Postgres) | **Aiven Cloud** (SSL) |
| **Broker** | Redpanda (Local) | **Redpanda** (C++) | **Aiven Kafka** |

## 💡 Logique de Projection
Contrairement aux outils classiques qui calculent le solde à la volée, MyBank utilise une **génération asynchrone** :
1. Une règle de récurrence est créée.
2. Un événement est envoyé dans **Kafka/Redpanda**.
3. Un service génère les transactions futures (`is_projection = true`) en base.
4. Le solde cumulé est récupéré via une Window Function SQL ultra-performante :
   `SUM(amount) OVER (ORDER BY date)`

## 🔒 Sécurité
* **Zéro Secret :** Aucun mot de passe n'est présent dans le code. Utilisation des variables d'environnement.
* **SSL :** Connexion obligatoire chiffrée vers Aiven Cloud.
* **Isolation :** Containers de préprod tournant en mode "Rootless" sur le Raspberry Pi.

## 🛠 Installation & Run (Local)

1. Lancer l'infrastructure locale :
   ```bash
   podman-compose up -d