# API Versioning Strategy

## Principes généraux

Cette API suit le **Richardson Maturity Model niveau 3** (HATEOAS) et une stratégie de versioning basée sur le **préfixe de chemin URL**.

### Format de version

```
/v{MAJOR}/{ressource}
```

Exemples :
```
GET /v0/accounts
GET /v1/accounts
POST /v0/accounts/{id}/transactions
```

---

## Règles de versioning

### Quand créer une nouvelle version ?

Une nouvelle version majeure est **obligatoire** uniquement pour les **breaking changes** :

| Type de changement | Action |
|---|---|
| Suppression d'un champ dans une réponse | ✅ Nouvelle version |
| Changement de type d'un champ (`String` → `Long`) | ✅ Nouvelle version |
| Changement de sémantique d'un endpoint | ✅ Nouvelle version |
| Renommage d'un endpoint | ✅ Nouvelle version |
| Ajout d'un champ **optionnel** dans une requête | ❌ Pas de nouvelle version |
| Ajout d'un champ dans une réponse | ❌ Pas de nouvelle version |
| Ajout d'un nouvel endpoint | ❌ Pas de nouvelle version |
| Correction de bug | ❌ Pas de nouvelle version |

### Politique de dépréciation

- Une version ancienne est maintenue **au minimum 3 mois** après la sortie de la nouvelle version.
- Un header `Deprecation` est ajouté aux réponses de la version dépréciée.
- La version dépréciée est documentée dans Swagger avec `@Deprecated`.

---

## Comment implémenter v1 en gardant v0

### 1. Créer le package v1

Les nouveaux contrôleurs v1 vont dans un sous-package dédié :

```
api/
├── AccountController.java          ← v0
├── AccountTransactionController.java ← v0
├── v1/
│   ├── AccountV1Controller.java    ← v1
│   └── AccountTransactionV1Controller.java ← v1
```

### 2. Créer le contrôleur v1

Créer un nouveau contrôleur avec le mapping `/v1/...`. Il peut réutiliser les mêmes `Adapter` et `Business` si la logique métier ne change pas, ou injecter de nouveaux services si nécessaire :

```java
@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts v1", description = "...")
public class AccountV1Controller {

    private final AccountAdapter accountAdapter;           // Réutilisé de v0
    private final AccountV1ModelAssembler assembler;       // Nouveau si format différent

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AccountV1Response>> findById(@PathVariable Long id) {
        // Même logique, nouveau format de réponse
    }
}
```

### 3. Créer les nouveaux DTOs si le format change

Si le format de réponse change (breaking change), créer de nouveaux records :

```
api/
├── response/
│   ├── AccountResponse.java      ← v0 (conservé, ne pas modifier)
│   └── v1/
│       └── AccountV1Response.java ← v1 (nouveau format)
```

### 4. Créer un nouvel assembler HATEOAS pour v1

```java
@Component
public class AccountV1ModelAssembler
        implements RepresentationModelAssembler<AccountV1Response, EntityModel<AccountV1Response>> {

    @Override
    public EntityModel<AccountV1Response> toModel(AccountV1Response response) {
        return EntityModel.of(response,
                linkTo(methodOn(AccountV1Controller.class).findById(response.id())).withSelfRel(),
                linkTo(methodOn(AccountV1Controller.class).findAll()).withRel("accounts"));
    }
}
```

### 5. Mettre à jour OpenApiConfig pour documenter les deux versions

```java
@Bean
public GroupedOpenApi v0Api() {
    return GroupedOpenApi.builder()
            .group("v0 (deprecated)")
            .pathsToMatch("/v0/**")
            .build();
}

@Bean
public GroupedOpenApi v1Api() {
    return GroupedOpenApi.builder()
            .group("v1")
            .pathsToMatch("/v1/**")
            .build();
}
```

### 6. Marquer v0 comme dépréciée dans SecurityConfiguration

Ajouter le filtre de dépréciation pour v0 :

```java
// Dans SecurityConfiguration ou un filtre dédié
@Component
public class DeprecationHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/v0/")) {
            response.setHeader("Deprecation", "true");
            response.setHeader("Sunset", "2026-12-31");
            response.setHeader("Link", "</v1/>; rel=\"successor-version\"");
        }
        chain.doFilter(request, response);
    }
}
```

### 7. Écrire les tests Cucumber pour v1

Créer un nouveau fichier feature pour les endpoints v1 :

```gherkin
Feature: Accounts v1

  Scenario: Get account with new v1 format
    Given a user named robinj
    ...
    When robinj get "/v1/accounts/1"
    Then we receive a status OK_200
    And we receive:
    """yml
    # Nouveau format v1
    accountId: 1
    accountName: compte 1
    """
```

---

## Checklist avant de créer une v1

- [ ] Identifier précisément le breaking change (champ supprimé, renommé, type changé)
- [ ] Vérifier qu'un ajout de champ optionnel ne suffit pas
- [ ] Créer le contrôleur `v1/` avec `@RequestMapping("/v1/...")`
- [ ] Créer les nouveaux DTOs dans `response/v1/` si le format change
- [ ] Créer le nouvel assembler HATEOAS v1
- [ ] Mettre à jour `OpenApiConfig` avec les groupes Swagger v0 et v1
- [ ] Ajouter le filtre de dépréciation pour v0 (`Deprecation`, `Sunset`, `Link` headers)
- [ ] Écrire les tests Cucumber pour v1
- [ ] Vérifier que les tests v0 passent toujours
- [ ] Documenter la date de sunset de v0 dans le README

---

## Versions actives

| Version | Statut | Sunset |
|---|---|---|
| **v0** | ✅ Actuelle | - |
