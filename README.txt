Проект реализует функционал загрузки/скачивания файлов на сайте с использованием хранилища YandexStorage S3.

Используемые технологии:

1. Yandex Object Storage в рамках Java Spring приложения.
2. Thymeleaf.
3. Реализация пагинации.
4. Security Spring c простой авторизацией и хэшированием паролей.
5. Input/Output в контексте обмена файлами с AWS.

Докер:
docker compose down -v
docker compose up --build -d
docker compose logs -f app

Добавляем данные через консоль после успешного создания образа:
pg_dump -h localhost -p 5432 -U postgres -d firstWebProject --data-only > backup.sql
cat backup.sql | docker compose exec -T postgres psql -U postgres -d firstWebProject

Обращение к БД через консоль:
docker compose exec postgres psql -U postgres -d first