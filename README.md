# RU
RESTful-сервис для регистрации, авторизации и управления доступом пользователей на основе JWT-токенов и ролей, реализованный на Spring Boot.

## Настройки по умолчанию   

```properties
jwt.secret=DRPIK3cFfgy2KHu7xv6G3lG3dbPMmz18VetjHQ
jwt.ttl-minutes=1
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
jwt.secret=DRPIK3cFfgy2KHu7xv6G3lG3dbPMmz18VetjHQ
jwt.ttl-minutes=1
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
