# Проект: Сервис тендеров и предложений Avito 2024 backend intern

Мое решение тестового задания от Авито на стажера бэкендера 

---

## Структура проекта

В папке src представлен исходный код приложения

Spring Hibernate - автомиграция. Базы Employee, Organization, Organization_responsible должны иметь какие-либо данные (по условию задания данные уже есть)

есть docker-compose.yml, dockerfile файлы для создания контейнера

---

## Задание

В папке `задание` размещена задача, которая решалась

---

## Запуск приложения на локальном устройстве

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
   Контейнер будет работать на localhost:8080. Можно изменить параметры запуска при необходимости.

## Доступ через хостинг:
### ✅ Приложение уже доступно по адресу: https://cnrprod1725773447-team-79619-33208.avito2024.codenrock.com


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
- **Описание:** Метод возвращает список доступных тендеров с возможностью фильтрации по типу услуг. Также доступны параметры для настройки количества возвращаемых записей и указания смещения для постраничного вывода.
#### Параметры запроса:
- `limit` (опционально, по умолчанию: 5): Ограничивает количество возвращаемых тендеров.
- `offset` (опционально, по умолчанию: 0): Указывает смещение для получения последующих записей.
- `service_type` (опционально): Список типов услуг для фильтрации тендеров.

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
  "description": "Доставка вкусных пончиков",
  "serviceType": "Delivery",
  "organizationId": "550e8400-e29b-41d4-a716-446655440020",
  "creatorUsername": "user1"
}

Response:

  200 OK

  Body: 
  
{
    "id": "8ff28183-3d7e-41fd-ba97-f23cef173c9a",
    "version": 1,
    "description": "Доставка вкусных пончиков",
    "name": "Тендер 1",
    "creatorUsername": "user1",
    "organization_id": "550e8400-e29b-41d4-a716-446655440020",
    "tender_status": "Created",
    "created_at": "2024-09-16T11:42:02.809+0000"
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
- **Примечание:** Если юзер создает предложение от своего имени - ему не нужны права доступа к организации.

```yaml
POST /api/bids/new

Request Body:

{
   "name": "Велосипед",
   "description": "Мы сможем доставлять ваши вкусные пончики куда угодно всего за 5 минут",
   "tenderId": "8ff28183-3d7e-41fd-ba97-f23cef173c9a",
   "authorType": "Organization",
   "authorId": "550e8400-e29b-41d4-a716-446655440002"
}
Response:

  200 OK

  Body: 
{
    "id": "31bc883c-eba2-4d7a-bee5-32e70571ef2d",
    "name": "Велосипед",
    "status": "Created",
    "authorType": "Organization",
    "authorID": "550e8400-e29b-41d4-a716-446655440002",
    "version": 1,
    "created_at": "2024-09-16T11:51:24.806+0000"
}
```

#### Получение списка предложений пользователя
- **Эндпоинт:** GET /bids/my
- **Описание:** Возвращает список предложений текущего пользователя.

```yaml
GET /api/bids/my?username=user5

Response:

  200 OK

  Body:
   [
       {
           "id": "5b451525-e7f3-4664-8163-0fe26aeb7c62",
           "name": "Велосипед",
           "status": "Created",
           "authorType": "User",
           "authorID": "550e8400-e29b-41d4-a716-446655440005",
           "version": 1,
           "created_at": "2024-09-16T12:01:58.750+0000"
       }
   ]
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

- **Например:**
- User3 создал тендер. User2 решил отправить предложение на этот тендер. User3 отправляет отзыв на предложение, которое создал User2.
- Теперь тендер создает User1 (он отвественный за свою организацию). User2 также создал предложение на этот тендер. User1 может этим запросом посмотреть отзыв User3 на предложение User2 по тому тендеру.

```yaml
GET /api/bids/1/reviews?authorUsername=user2&organizationId=1

Response:

  200 OK

  Body: [ {...}, {...}, ... ]
```
