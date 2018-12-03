# Money transfer service

```POST /account``` creates account, example request's body:
```
{
  "name": "John",
  "surname": "Doe"
}
```

```GET /account/{accountId}``` get account details

```GET /account/{accountId}/balance``` get account balance

```GET /account/{accountId}/{from}/{to}``` get account's money transfers in specified time range

```POST /transfer``` transfer money between accounts, example request's body:
```
{
    "from": "d236e769-4903-47dd-8358-d3d64c00c071",
    "to": "10bbdc0b-30a8-4534-8939-4115efd83247",
    "amount": 100.0,
    "title": "Title"
}
```

```POST /transfer/deposit``` deposit money, example requests's body:

```
{
    "to": "727caead-adf3-468a-9f03-ad2311c152ab",
    "amount": 100.0,
    "title": "Title"
}
```

```GET /transfer/{transferId}``` get transfer's details

