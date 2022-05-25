# java-filmorate
Template repository for Filmorate project.

# Database diagram

![database diagram](images/database diagram.png)

## Примеры запросов в БД с помощью SQL
    
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
    