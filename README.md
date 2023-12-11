# TrialTask

## Docker
`docker pull davidchanzz/trial_task`

## Security

- UserName/Password dor Register and LogIn
- JWToken for API EndPoints exept Register and LogIn
  **Header format** - _Authorization Bearer 'token'_

## EndPoints

### UserController

- **GET GetUser**
`http://localhost:8080/api/user`

- **PUT UpdateUser**
`http://localhost:8080/api/user/update`

**Body**
```JSON
{
    "username": "egor!",
    "password": "passworddd",
    "matchingPassword": "passworddd",
    "email": "erogius@email.com"
}
```
**Validation**
- **username**: Size(min = 5, max =25), must not be null
- **password**: Size(min = 5, max =25), must not be null
- **matchingPassword**: must match with password, must not be null
- **email**: must be valid email, must not be null

### AuthController

- **POST Registration**
`http://localhost:8080/api/auth/registration`

**Body**
```JSON
{
    "username": "testUser",
    "password": "testPassword",
    "matchingPassword": "testPassword",
    "email": "testUser@email.com"
}
```

**Validation**
- **username**: Size(min = 5, max =25), must not be null
- **password**: Size(min = 5, max =25), must not be null
- **matchingPassword**: must match with password, must not be null
- **email**: must be valid email, must not be null

- **POST Login**
`http://localhost:8080/api/auth/login`

**Body**
```JSON
{
    "username": "testUser",
    "password": "testPassword"
}
```

**Validation**
- **username**: must not be null
- **password**: must not be null

### QuoteController

- **GET GetQuote**
`http://localhost:8080/api/quote/{id}`

**Path**
`{id} - QuoteId`

- **GET GetRandomQuote**
`http://localhost:8080/api/quote/add`

- **POST AddNewQuote**
`http://localhost:8080/api/quote/rnd`

**Body**
```JSON
{
    "text": "quote text"
}
```

**Validation**
- **text**: Size(min = 10, max = 128), must not be null

- **PUT UpateQuote**
`http://localhost:8080/api/quote/update/{id}`

**Path**
`{id} - QuoteId`

**Body**
```JSON
{
    "text": "quote text"
}
```

**Validation**
- **text**: Size(min = 10, max = 128), must not be null

- **DELETE DeleteQuote**
`http://localhost:8080/api/quote/delete/{id}`

**Path**
`{id} - QuoteId`

- **GET GetTop10Quotes**
`http://localhost:8080/api/quote/top10`

- **GET GetWorse10Quotes**
`http://localhost:8080/api/quote/worse10`

- **GET GetQuoteRatingGraph**
`http://localhost:8080/api/quote/graph/{id}`

**Path**
`{id} - QuoteId`

### QuoteStateController

- **POST UpVote**
`http://localhost:8080/api/vote/up/{id}`

**Path**
`{id} - QuoteId`

- **POST DownVote**
`http://localhost:8080/api/vote/down/{id}`

**Path**
`{id} - QuoteId`




