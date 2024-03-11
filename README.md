# MusicJan
**MusicJan** - это музыкальный бот для *Discord*, который играет музыку с *YouTube*.

## Возможности
На данный момент бот поддерживает следующие команды:
```
- play - играть трек по ссылке или названию (поиск на YouTube). Также, эта команда добавляет трек в очередь
- skip - пропустить текущий трек
- stop - останавливает воспроизведение
```
Эти команды доступны через слеш (/). Например, чтобы воспроизвести трек, напишите:
```
/play https://www.youtube.com/watch?v=dQw4w9WgXcQ
```

## Использование
### Конфигурация
Для использования бота необходимо указать значения ключей в файле .env.

Вот описание ключей:
```
- TOKEN* - токен бота Discord
- YOUTUBE_EMAIL - e-mail Youtube аккаунта.
- YOUTUBE_PASSWORD - пароль Youtube аккаунта.

* - обязательные параметры
```
Инструкция по созданию бота доступна [здесь](https://discord.com/developers/docs/getting-started).

`YOUTUBE_EMAIL` и `YOUTUBE_PASSWORD` нужны для возможности воспроизведения треков с YouTube с возрастными ограничениями.

### Запуск
Для запуска приложения в первый раз выполните команду:
```bash
docker-compose up -d
```
Для последующих запусков достаточно ввести:
```bash
docker-compose start 
```
Чтобы остановить бота, используйте команду:
```bash
docker-compose stop 
```