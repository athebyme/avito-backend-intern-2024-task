# Проект: Сервис тендеров и предложений Avito 2024 backend intern

Мое решение тесового задания от Авито на стажера бэкендера 

---

## Структура проекта

В данном проекте вы найдёте пример сборки приложения с помощью Docker из Dockerfile, расположенного в корне проекта. Для сборки используется Gradle, но вы можете изменить проект по своему усмотрению. Основное требование — наличие Dockerfile в корне и работа приложения на порту 8080.

---

## Задание

В папке `задание` размещена задача, которую необходимо выполнить.

---

## Запуск приложения

### Шаги для запуска:

1. Остановите работающий pod в Kubernetes:
   ```bash
   kubectl rollout restart deployment cnrprod1725773447-team-79619-backend --namespace=cnrprod1725773447-team-79619
   ```
   ⚠️ Примечание: В базе данных может быть лимит подключений. Если запущен под, приложение не сможет создать подключение через Hikari драйвер.

2. Запустите приложение в Docker-контейнере:
   ```bash
   docker-compose up --build
   ```
   Контейнер будет работать на localhost:8080. Вы можете изменить параметры запуска при необходимости.
   создастся контейнер с приложением, работающий на localhost:8080 (параметры можно поменять при желании)

## Либо:
### ⚠️ Приложение уже доступно по адресу: https://cnrprod1725773447-team-79619-33208.avito2024.codenrock.com


### 1. Проверка доступности сервера
- **Эндпоинт:** GET /ping
- **Цель:** Убедиться, что сервер готов обрабатывать запросы.

```yaml
GET /api/ping

Response:

  200 OK

  Body: ok
```

### 2. Тестирование функциональности тендеров
#### Получение списка тендеров
- **Эндпоинт:** GET /tenders
- **Описание:** Возвращает список тендеров с возможностью фильтрации по типу услуг.

```yaml
GET /api/tenders

Response:

  200 OK

  Body: [ {...}, {...}, ... ]
```

#### Создание нового тендера
- **Эндпоинт:** POST /tenders/new
- **Описание:** Создает новый тендер с заданными параметрами.

```yaml
POST /api/tenders/new

Request Body:

  {

    "name": "Тендер 1",

    "description": "Описание тендера",

    "serviceType": "Construction",

    "status": "Open",

    "organizationId": 1,

    "creatorUsername": "user1"

  }

Response:

  200 OK

  Body: 
  
  { 
    "id": 1, 
    "name": "Тендер 1", 
    "description": "Описание тендера",
    ...
  }
```

#### Получение тендеров пользователя
- **Эндпоинт:** GET /tenders/my
- **Описание:** Возвращает список тендеров текущего пользователя.

```yaml
GET /api/tenders/my?username=user1

Response:

  200 OK

  Body: [ {...}, {...}, ... ]  
```

#### Редактирование тендера
- **Эндпоинт:** PATCH /tenders/{tenderId}/edit
- **Описание:** Изменение параметров существующего тендера.
```yaml
PATCH /api/tenders/1/edit

Request Body:

  {

    "name": "Обновленный Тендер 1",

    "description": "Обновленное описание"

  }

Response:

  200 OK

  Body: 
  { 
    "id": 1, 
    "name": "Обновленный Тендер 1", 
    "description": "Обновленное описание",
    ...
  }  
```

#### Откат версии тендера
- **Эндпоинт:** PUT /tenders/{tenderId}/rollback/{version}
- **Описание:** Откатить параметры тендера к указанной версии.

```yaml
PUT /api/tenders/1/rollback/2

Response:

  200 OK

  Body: 
  { 
    "id": 1, 
    "name": "Тендер 1 версия 2", 
    ... 
  }
```

### 3. Тестирование функциональности предложений
#### Создание нового предложения
- **Эндпоинт:** POST /bids/new
- **Описание:** Создает новое предложение для существующего тендера.

```yaml
POST /api/bids/new

Request Body:

  {

    "name": "Предложение 1",

    "description": "Описание предложения",

    "status": "Submitted",

    "tenderId": 1,

    "organizationId": 1,

    "creatorUsername": "user1"

  }

Response:

  200 OK

  Body: 
  { 
    "id": 1, 
    "name": "Предложение 1", 
    "description": "Описание предложения",
    ...
  }
```

#### Получение списка предложений пользователя
- **Эндпоинт:** GET /bids/my
- **Описание:** Возвращает список предложений текущего пользователя.

```yaml
GET /api/bids/my?username=user1

Response:

  200 OK

  Body: [ {...}, {...}, ... ]
  ```
  
#### Получение списка предложений для тендера
- **Эндпоинт:** GET /bids/{tenderId}/list
- **Описание:** Возвращает предложения, связанные с указанным тендером.
- **Ожидаемый результат:** Статус код 200 и список предложений для тендера.

```yaml
GET /api/bids/1/list

Response:

  200 OK

  Body: [ {...}, {...}, ... ]
  ```
  
#### Редактирование предложения
- **Эндпоинт:** PATCH /bids/{bidId}/edit
- **Описание:** Редактирование существующего предложения.
- **Ожидаемый результат:** Статус код 200 и обновленные данные предложения.

```yaml
PATCH /api/bids/1/edit

Request Body:

  {

    "name": "Обновленное Предложение 1",

    "description": "Обновленное описание"

  }

Response:

  200 OK

  Body: 
  { 
    "id": 1, 
    "name": "Обновленное Предложение 1", 
    "description": "Обновленное описание",
    ...,
  }
```

#### Откат версии предложения
- **Эндпоинт:** PUT /bids/{bidId}/rollback/{version}
- **Описание:** Откатить параметры предложения к указанной версии.

```yaml
PUT /api/bids/1/rollback/2

Response:

  200 OK

  Body: 
  { 
    "id": 1, 
    "name": "Предложение 1 версия 2", 
    ...
  }
```

### 4. Тестирование функциональности отзывов
#### Просмотр отзывов на прошлые предложения
- **Эндпоинт:** GET /bids/{tenderId}/reviews
- **Описание:** Ответственный за организацию может посмотреть прошлые отзывы на предложения автора, который создал предложение для его тендера.
- **Например:** User3 создал тендер. User2 решил отправить предложение на этот тендер. User3 отправляет отзыв на это предложение. || Теперь тендер создает User1. User2 также создал предложение на этот тендер. User1 может этим запросом посмотреть отзыв User3 на предложение User2 по тому тендеру.

```yaml
GET /api/bids/1/reviews?authorUsername=user2&organizationId=1

Response:

  200 OK

  Body: [ {...}, {...}, ... ]
```
