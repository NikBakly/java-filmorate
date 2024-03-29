## Дополнение
При командной доработки данного проекта реализовал класс EventFeedAspect, воспользовавшись парадигмой АОП, чтобы отслеживать ленту событий. Ссылка на мой pull request при командной разработке: https://github.com/sniskorodnova/java-filmorate/pull/21.

# Описание проекта
В данном проекте реализовано приложение Filmorate на Spring Boot.

В приложении есть возможность создавать, редактировать пользователей, а также получать список всех пользователей.
Также есть возможность создавать, редактировать фильмы, а также получать список всех фильмов. Пользователь может
поставить лайк фильму, а также удалить лайк у фильма. Можно добавить пользователя в друзья (данная операция
автоматически взаимная), удалить из друзей, а также получить список общих друзей двух пользователей и топ фильмов
по количеству лайков.

Взаимодействие с приложением происходит по API.  
Методы для работы с пользователями:  
POST /users - создание пользователя  
PUT /users - редактирование пользователя  
GET /users - получение списка всех пользователей  
GET /users/{userId} - получение информации о пользователе по его id  
PUT /users/{userId}/friends/{friendId} - добавление пользователя в друзья другому пользователю  
DELETE /users/{userId}/friends/{friendId} - удаление пользователя из друзей другого пользователя  
GET /users/{userId}/friends - получение списка друзей пользователя  
GET /users/{userId}/friends/common/{otherUserId} - получение списка общих друзей двух пользователей  


Методы для работы с фильмами:  
POST /films - создание фильма  
PUT /films - редактирование фильма  
GET /films - получение списка всех фильмов  
GET /films/{filmId} - получение информации о фильме по его id  
PUT /films/{filmId}/like/{userId} - проставление лайку фильму пользователем  
DELETE /films/{filmId}/like/{userId} - удаление лайка у фильма пользователем  
GET /films/popular - получение топа самых популярных фильмов по количеству лайков (если у двух фильмов одинаковое
количество лайков, то они сортируются по имени)


Для создания и редактирования пользователя добавлены валидационные правила:
- электронная почта не может быть пустой и должна содержать символ @
- логин не может быть пустым и содержать пробелы
- имя для отображения может быть пустым (в таком случае будет использован логин)
- дата рождения не может быть в будущем

Для создания и редактирования фильма добавлены валидационные правила:
- название не может быть пустым
- максимальная длина описание - 200 символов
- дата релиза - не раньше 29 декабря 1895 года
- продолжительность фильма должна быть положительной
- рейтинг фильма не может быть пустым

В приложении добавлено логирование запросов, а также логирование исключений при некорректных входящих данных.

# Database diagram

![](images/database_diagram.png)

## Примеры SQL-запросов в БД
    
Примерный запрос для получения всех фильмов
    
    SELECT f.film_id
            f.name AS name_of_film,
            g.name AS name_of_genre,
            r.name AS name_of_rating,
            COUNT(l.user_id) AS number_of_likes
    FROM film AS f,
            genre AS g
    LEFT JOIN film_genre AS fg ON g.genre_id = fg.genre_id
    LEFT JOIN fg ON f.film_id = fg.film_id
    LEFT JOIN rating_MPA AS r ON f.film_id = r.film_id
    LEFT JOIN like AS l ON f.film_id = l.film_id
    GROUP BY f.film_id;

Примерный запрос для получения всех пользователей

    SELECT u.user_id
            u.name,
            u.login
            COUNT(fg.friend_id) AS number_of_friends
    FROM user AS u
    LEFT JOIN friendship AS f ON u.user_id = f.user_id
    GROUP BY u.user_id
    HAVING f.status = True; -- подсчёт количества только для подтвержденных друзей
    
