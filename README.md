# RU
RESTful-сервис для регистрации, авторизации и управления доступом пользователей на основе JWT-токенов и ролей, реализованный на Spring Boot.

## Настройки по умолчанию   

```properties
spring.application.name=AuthorizationService
jwt.ttl-minutes=1
jwt.signing.secret=7M1x0gJw0u9FfG2m0gq8Y9wJm1UjXh1m8zJYwW1Wq4A=
jwt.encryption.secret=JXf1m5C2rX8n0K3v9Q2p6S4d8T0u2Y6a3b9c1d4e6f8=
```
Пароли хэшируются с использованием `BCryptPasswordEncoder`

## Эндпоинты

#### Регистрация пользователя
`POST /auth/register`

**Тело запроса:**
```json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "Secret123",
  "roles": ["ADMIN"]
}
```

**Ответ:**

```json

{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

### Аутентификация пользователя
`POST /auth/login`

**Тело запроса:**

```json
{
  "username": "alice",
  "password": "Secret123"
}
```

**Ответ:**

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

### Обновление токена
`POST /auth/refresh`

**Заголовок:**

```
Authorization: Bearer <refreshToken>
```

**Ответ:**

```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-jwt-refresh-token"
}
```

### Отзыв токена
`POST /auth/logout`

**Заголовок:**

```
Authorization: Bearer <accessToken>
```

**Ответ:**

```
204 No content
```

### Получение ролей
`GET /auth/roles`

**Заголовок:**

```
Authorization: Bearer <accessToken>
```

**Ответ:**

```
[
    "ADMIN"
]
```

# EN
RESTful Service for User Registration, Authentication, and Access Control Based on JWT Tokens and Roles, Implemented with Spring Boot

## Default Settings

```properties
spring.application.name=AuthorizationService
jwt.ttl-minutes=1
jwt.signing.secret=7M1x0gJw0u9FfG2m0gq8Y9wJm1UjXh1m8zJYwW1Wq4A=
jwt.encryption.secret=JXf1m5C2rX8n0K3v9Q2p6S4d8T0u2Y6a3b9c1d4e6f8=
```

Passwords are hashed using `BCryptPasswordEncoder`.

## Endpoints

### User Registration

`POST /auth/register`

**Request Body:**

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "Secret123",
  "roles": ["ADMIN"]
}
```

**Response:**

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

### User Authentication

`POST /auth/login`

**Request Body:**

```json
{
  "username": "alice",
  "password": "Secret123"
}
```

**Response:**

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token"
}
```

### Token Refresh

`POST /auth/refresh`

**Header:**

```
Authorization: Bearer <refreshToken>
```

**Response:**

```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-jwt-refresh-token"
}
```

### Token Revocation

`POST /auth/logout`

**Header:**

```
Authorization: Bearer <accessToken>
```

**Response:**

```
204 No Content
```

### Retrieve Roles

`GET /auth/roles`

**Header:**

```
Authorization: Bearer <accessToken>
```

**Response:**

```json
[
  "ADMIN"
]
```
