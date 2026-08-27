# Auth Service

Handles authentication and authorization for CommerceCore. Built on top of Spring's OAuth2 Authorization Server, so it issues real signed JWTs rather than rolling a custom token system.

## Stack

- Java 17, Spring Boot 3.5
- Spring Security, OAuth2 Authorization Server
- MySQL + Spring Data JPA
- Swagger / OpenAPI

## What it does

- Signup / login / logout
- Roles (a user can have more than one — USER by default, ADMIN can be added)
- Issues RSA-signed JWTs with roles baked into the token claims
- Session tracking, so logging out actually invalidates the token immediately instead of waiting for it to expire on its own
- Full OAuth2 Authorization Code flow too (client credentials, consent screen, the works) — useful if you want to test through Postman's OAuth tab instead of the plain login endpoint

## Running it locally

Needs MySQL running.

```
./mvnw spring-boot:run
```

On first run it seeds an admin account (check `AdminSeeder.java` for the default credentials — change the password before deploying anywhere real).

## API docs

```
http://localhost:8080/swagger-ui/index.html
```

## Endpoints

| Method | Path | Access |
|---|---|---|
| POST | `/auth/signup` | Public |
| POST | `/auth/login` | Public, returns a plain JWT string |
| POST | `/auth/logout` | Needs a token |
| POST | `/auth/validate` | Needs a token |
| GET | `/users/{id}` | Needs a token |
| POST | `/users/{id}/roles` | Admin |
| POST | `/roles` | Admin |

## Why logout actually works here

Most JWT setups can't really "log out" a token early — it's valid until it expires, whatever you do server-side. This service checks an active session table as part of the token validation step itself, so once you log out, that token stops working immediately, not in an hour. Other services (like Product Service) call `/auth/validate` to check the same thing before trusting a token.

## Notes

The bootstrapping problem — you need an admin to create other admins, but nobody starts as admin — is solved by seeding one admin account on startup instead of a manual workaround.

Part of [CommerceCore](https://github.com/farhan-knight/CommerceCore).
