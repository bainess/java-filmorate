# java-filmorate
<img width="957" height="557" alt="image" src="https://github.com/user-attachments/assets/42b2a326-14c3-4029-9a34-64df574d5765" />


*Main queries*
getUsers
SELECT *
FROM user as u
LEFT JOIN user_friends AS uf ON u.user_id = uf.user_id

**getUser**
SELECT *
FROM user AS u
LEFT JOIN user_friends AS uf ON u.user_id = uf.user_id
WHERE user LIKE 'user_id'

**getUserFriends**
SELECT friend_id
FROM user AS u
LEFT JOIN user_friends AS uf ON u.user_id = uf.user_id
WHERE user LIKE 'user_id'

**getCommonFriends**
SELECT COUNT(friend_id)
FROM (
SELECT friend_id
FROM user AS u
WHERE friend_id IN (SELECT: friend_id
LEFT JOIN user_friends AS uf ON u.user_id = uf.user_id
WHERE user LIKE 'user1_id')


**getFilms**
**getFilm**
**getPopularFilms**
**getFilmsByRating**
**getFilmsByGenre**
