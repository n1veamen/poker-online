# Poker Online

Простой онлайн-покерный стол для небольших компаний. Проект собирается в один Docker-контейнер и деплоится на Render.

## Архитектура

- В памяти хранится состояние стола (`TableService`, `ConcurrentHashMap`), а не истории игр.
- PostgreSQL/Neon используется только для сохранения истории раздач через Flyway и JPA.
- Реальный обмен идёт через STOMP (`/topic/table`, `/topic/chat`, `/user/queue/private`).

## Требования

- Java 21
- Maven 3.9+
- Docker
- PostgreSQL / Neon account

## Переменные окружения

Создайте `.env` на основе `.env.example`:

```bash
cp .env.example .env
```

Пример:

```bash
export PORT=8080
export DATABASE_URL="jdbc:postgresql://host.neon.tech:5432/neondb?sslmode=require"
export DATABASE_USERNAME="your_user"
export DATABASE_PASSWORD="your_password"
```

## Запуск локально

```bash
mvn clean package -DskipTests
java -jar target/poker-online-1.0.0.jar
```

Открыть: http://localhost:8080

## Docker

```bash
docker build -t poker-online .
docker run --rm -p 8080:8080 --env-file .env poker-online
```

## Деплой на Render

1. Создайте аккаунт на Render.
2. Нажмите New -> Web Service.
3. Подключите GitHub репозиторий проекта.
4. Выберите Docker как тип сборки.
5. Укажите порт `8080`.
6. Добавьте env-переменные:
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`
   - `PORT=8080`
7. Включите автодеплой и запустите сервис.
8. WebSocket работает по `wss://` в продакшене, для клиента используйте `new SockJS('/ws')`, а хост задайте через `window.location.origin`.

## Neon PostgreSQL

1. Создайте БД на Neon.
2. Скопируйте connection string.
3. Добавьте `?sslmode=require` в JDBC URL.
4. Укажите `DATABASE_URL` / `SPRING_DATASOURCE_URL` в Render env.

## Игровая логика

- Раздача карт: `Collections.shuffle` через `SecureRandom`
- Bblind rotation: dealer / small blind / big blind смещаются по кругу
- Стадии: Preflop → Flop → Turn → River → Showdown
- Действия: Fold, Check, Call, Bet, Raise, All-in
- Победитель определяется по стандартной иерархии покерных комбинаций
- Side pots поддерживаются на уровне вычисления ставок и распределения банка
- Таймер хода: 30 сек, затем автофолд по истечении

## Примечания

- Состояние игрового стола хранится в памяти для максимальной скорости и простоты масштабирования на небольшую комнату.
- История раздач сохраняется в PostgreSQL только для статистики.
- Никаких секретов и паролей в репозитории: только env-переменные и `.env.example`.
