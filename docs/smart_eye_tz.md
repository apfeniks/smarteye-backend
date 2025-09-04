# Техническое задание на разработку комплекса SmartEYE v2 (ESP32)

## 1. Общая информация

**Объект внедрения:** ООО «ЖБК», г. Набережные Челны.\
**Назначение:** Автоматизация контроля качества выпускаемой вибропрессованной продукции (тротуарная плитка, бордюрный камень, газонная решётка и т.п.) с использованием 2D‑лазерного сканера и весовых установок.\
**Разработчик:** команда проекта SmartEYE.

## 2. Цели проекта

- Автоматическая фиксация параметров каждого поддона: геометрия, поверхность, вес.
- Обеспечение идентификации и прослеживаемости поддонов с использованием RFID.
- Централизованное хранение данных в PostgreSQL (Docker) и облаков точек во внешнем файловом хранилище.
- Возможность операторского контроля и принятия решений по браку.
- Интеграция с 1С и экспорт отчётности.

## 3. Состав комплекса

### 3.1. Аппаратная часть

1. **Контроллер**:

   - ESP32 (ESP-WROVER-IE) с поддержкой Wi-Fi и внешнего модуля Ethernet W5500.
   - Подключение периферии через UART, RS-485, SPI, I2C, GPIO.
   - Использование опторазвязки и модулей реле для сопряжения с промышленными сигналами.

2. **Сканирующий комплекс**:

   - Лазерный 2D-сканер RIFTEK RF627Smart-690/1000-535/1000-3-AK-Ind.
   - Подключение по TCP/IP для передачи профилей.
   - Возможность продольного перемещения сканера по направляющим (ШВП + шаговый двигатель).

3. **Пресс и периферия**:

   - Вибропресс Frima-1000 с дискретными сигналами старта/остановки.
   - Концевые выключатели и датчики положения поддона.

4. **Весовые посты**:

   - Две пневматические установки для взвешивания паллетов.
   - Возможность интеграции через аналоговые выходы/модули HX711 или промышленные терминалы по RS-485.

5. **RFID-система**:

   - UHF-считыватели с рабочим диапазоном 860–960 МГц.
   - Подключение к ESP32 через UART или RS-485.
   - RFID-метки на поддонах для уникальной идентификации.

6. **Сетевая инфраструктура**:

   - Ethernet-сеть для связи ESP32, Backend и хранилища.
   - Возможность резервного канала по Wi-Fi.

7. **Электропитание**:

   - Блоки питания 24–36 В для шагового двигателя и периферии.
   - Отдельный источник 5 В для ESP32, RFID и модулей связи.
   - Резервное питание (опционально) для сохранности данных.

### 3.2. Программная часть

1. **Прошивка ESP32 (Edge Firmware):**

   - Управление периферией: RFID, концевики, импульсы запуска сканера.
   - Сбор данных о событиях и передача в Backend.
   - Буферизация при потере связи.
   - Использование JSON-сообщений по HTTP/WS.

2. **Recorder (Python-сервис):**

   - Получение профилей от сканера (до 2800 профилей на поддон).
   - Сборка и обработка облака точек.
   - Сохранение в формате .parquet/.ply.
   - Загрузка файлов в MinIO или SMB.
   - Отправка статуса и метаданных в Backend.

3. **Backend (Java/Spring Boot):**

   - REST и WebSocket API для обмена с Edge и АРМ.
   - Запись в PostgreSQL (Docker, с Liquibase миграциями).
   - Управление статусами измерений.
   - Логика дефектов, классификация, хранение справочников.
   - Авторизация и разграничение доступа (JWT/OAuth2).

4. **АРМ оператора (Python + Open3D 0.19):**

   - Графический интерфейс для просмотра облаков точек.
   - Возможность увеличения, сечения, измерений координат.
   - Подсветка дефектов.
   - Внесение решений оператора (Accept/Reject/Rework).
   - Отправка решений в Backend.

5. **Файловое хранилище:**

   - S3-совместимое (MinIO) или SMB/NFS.
   - Хранение облаков точек с именованием по шаблону `scanner_cloud_<дата>_<время>_<RFID>.parquet`.

6. **Интеграции:**

   - Экспорт отчётов в Excel/XML.
   - Интеграция с 1С через REST или обмен XML.

## 4. Функциональные требования

1. **Идентификация поддона**:

   - Считывание RFID-метки при входе на пост.
   - Привязка UID к конкретному измерению.
   - Ведение истории проходов одного и того же поддона.

2. **Измерение геометрии продукции**:

   - Автоматический запуск сканирования при поступлении поддона.
   - Съём \~2800 профилей с шагом, соответствующим длине поддона.
   - Построение 3D-облака точек с координатами X, Y, Z.
   - Контроль размеров по шаблону (длина, ширина, высота, ровность).
   - Проверка на смещение плиток внутри поддона и коррекция привязки.
   - Подсветка зон, выходящих за допуски.

3. **Контроль веса**:

   - Взвешивание пустого поддона (tare) и запись результата.
   - Взвешивание поддона с продукцией после прессования.
   - Расчёт массы продукции = вес после – вес до.
   - Сопоставление массы с расчётным объёмом для вычисления плотности.

4. **Контроль качества поверхности**:

   - Автоматическое выявление дефектов: выбоины, трещины до 1,5 мм.
   - Анализ «волны» и неровностей поверхности.
   - Классификация дефектов по типу и степени.

5. **Формирование результатов измерения**:

   - Сохранение облака точек в файловое хранилище.
   - Запись метаданных в PostgreSQL (время, RFID, устройство, файл, вес, плотность, статус).
   - Привязка к справочникам номенклатуры и рецептов.

6. **Режимы работы**:

   - Silent Mode (АРМ выключен):
     - Все измерения автоматически сохраняются.
     - Статус PENDING\_REVIEW до появления оператора.
   - Operator Mode (АРМ включен):
     - Отображение карточки измерения.
     - Возможность просмотра 3D-облака и выявленных дефектов.
     - Принятие решения: ACCEPT, REJECT, REWORK.
     - Внесение комментариев и причин брака.

7. **Работа с шаблонами**:

   - Возможность добавления новых шаблонов продукции (размеры, форма, масса).
   - Автоматическая привязка измерений к шаблону.
   - Сравнение результатов с эталонными параметрами.

8. **Отчётность и интеграция**:

   - Генерация отчётов по дням/сменам.
   - Экспорт в Excel, CSV, XML.
   - Интеграция с 1С через REST или обмен XML.
   - Архивирование результатов в долгосрочном хранилище.

9. **Журналирование и трассировка**:

   - Запись всех событий (старты, ошибки, сбои связи, решения операторов).
   - Ведение audit-trail для всех действий.
   - Привязка всех логов к `measurement_id`.

## 5. Нефункциональные требования

### 5.1. Надёжность

- Устойчивость к обрывам связи между ESP32 и Backend.
- Буферизация данных на ESP32 при отсутствии сети, с последующей синхронизацией.
- Автоматический ретрай загрузки файлов облаков в хранилище при сбоях.
- Обработка аварийных ситуаций: отказ сканера, перегрузка весового поста, сбой RFID.

### 5.2. Производительность

- Минимальная скорость: ≥1 поддон за 30–40 секунд (включая сканирование, взвешивание и запись).
- Возможность масштабирования до ≥1000 измерений за смену без деградации.
- Размер облака: не более 3 МБ/поддон после сжатия.
- Хранение ≥6 месяцев активных данных и ≥3 лет в архиве.

### 5.3. Безопасность

- Аутентификация и авторизация пользователей (оператор, технолог, администратор).
- Использование JWT/OAuth2 для АРМ и Backend.
- Шифрование соединений (HTTPS, WSS).
- Ролевая модель доступа к функциям и данным.
- Логирование всех действий пользователей (audit-log).

### 5.4. Масштабируемость и интеграция

- Возможность добавления новых сканеров и весовых постов.
- Расширение БД без изменения базовой логики.
- Поддержка нескольких операторских АРМ одновременно.
- Гибкость интеграции с 1С и другими системами учёта.

### 5.5. Мониторинг и эксплуатация

- Сбор метрик (кол-во измерений, среднее время цикла, количество брака).
- Использование Prometheus/Grafana для визуализации (опционально).
- Поддержка централизованного логирования (ELK или аналог).
- Автоматическое уведомление о критических ошибках.
- Документация для установки, настройки и эксплуатации.

---

## 6. Архитектура системы

### 6.1. Описание производственного процесса

1. Пустой технологический поддон поступает под пресс Frima-1000.
2. После прессования поддон с продукцией направляется под сканирующий пост.
3. Поддон останавливается:
   - Производится взвешивание на пневматической платформе.
   - Запускается процесс сканирования: сканер перемещается продольно и снимает профили.
4. Сформированное облако точек сохраняется в файловое хранилище.
5. Метаданные (вес, время, RFID, статус) записываются в БД.
6. Если АРМ включен — оператор видит результаты, подтверждает или отклоняет их.
7. Поддон далее направляется в камеру выдержки, затем — на штабелер-разборщик.

### 6.2. Общая схема взаимодействия

```
[Frima PLC] --DI/DO--> [ESP32 Edge] <--RFID-- [UHF Reader]
                                  |
                                  | HTTP/WS (JSON)
                                  v
                           [Backend API + PostgreSQL (Docker)]
                                  | \
                                  |  \
                                  |   +--> [MinIO / SMB Storage] (.parquet/.ply)
                                  |       
                                  +--> [АРМ Operator App (Open3D 0.19)]
```

### 6.3. Взаимодействие компонентов

- **ESP32 Edge**:

  - Принимает сигналы от пресса и концевиков.
  - Считывает RFID-метки.
  - Формирует JSON-сообщения о начале цикла.
  - Передаёт данные в Backend через REST/WS.

- **Recorder (Python)**:

  - Подключается напрямую к сканеру RIFTEK по TCP/IP.
  - Получает \~2800 профилей за цикл.
  - Формирует облако точек.
  - Сохраняет файл в файловом хранилище.
  - Уведомляет Backend о завершении.

- **Backend (Spring Boot)**:

  - Регистрирует событие измерения.
  - Сохраняет метаданные в PostgreSQL.
  - Получает ссылки на файлы из Recorder.
  - Генерирует дефекты и статистику.
  - Передаёт данные на АРМ через WebSocket.

- **АРМ оператора (Open3D)**:

  - Загружает облака точек из хранилища.
  - Отображает и позволяет измерять X/Y/Z.
  - Подсвечивает зоны с дефектами.
  - Позволяет оператору принять решение.

### 6.4. Логика обмена данными

1. `ESP32 → Backend`: сигнал о старте цикла (`/measurements/start`).
2. `Recorder → Storage`: сохранение облака точек.
3. `Recorder → Backend`: уведомление о завершении цикла (`/finish`).
4. `ESP32 → Backend`: данные о весе и события.
5. `Backend → АРМ`: поток событий и карточки измерений.
6. `АРМ → Backend`: решения оператора (`/operator/decision`).

### 6.5. Масштабирование и отказоустойчивость

- Возможность подключения нескольких ESP32 Edge к одному Backend.
- Поддержка нескольких Recorder для разных сканеров.
- Репликация PostgreSQL и резервное копирование.
- Кластеризация MinIO при увеличении объёмов.

---

## 7. База данных

### 7.1. Основные сущности и таблицы

1. **devices** — список оборудования (сканеры, ESP32, весы, RFID‑считыватели).

   - `id` (PK, BIGSERIAL)
   - `type` (scanner, esp32, scale, rfid)
   - `serial`
   - `location`
   - `status`
   - `last_seen`

2. **pallets** — информация о поддонах.

   - `id` (PK)
   - `rfid_uid` (уникальный UID метки)
   - `product_code` (код продукции)
   - `color_code` (код цвета)
   - `recipe_id` (ссылка на справочник рецептов)
   - `created_at`

3. **measurements** — измерения.

   - `id` (PK)
   - `pallet_id` (FK → pallets)
   - `device_id` (FK → devices)
   - `started_at`, `finished_at`
   - `mode` (SILENT/OPERATOR)
   - `status` (CREATED, IN\_PROGRESS, PENDING\_REVIEW, ACCEPTED, REJECTED, REWORK)
   - `operator_id`
   - `issue_code`, `issue_comment`
   - `file_id` (FK → files)

4. **weights** — данные взвешивания.

   - `id` (PK)
   - `measurement_id` (FK → measurements)
   - `phase` (BEFORE/AFTER)
   - `value_kg`
   - `sensor_ok`
   - `ts`

5. **defects** — выявленные дефекты.

   - `id` (PK)
   - `measurement_id` (FK → measurements)
   - `type` (FLATNESS, CRACK, WAVE и др.)
   - `severity` (INFO/WARN/ERROR)
   - `metric`
   - `threshold`
   - `ts`

6. **operator\_actions** — действия операторов.

   - `id` (PK)
   - `measurement_id` (FK → measurements)
   - `action` (ACCEPT/REJECT/REWORK)
   - `reason`
   - `comment`
   - `actor_id`
   - `ts`

7. **files** — хранилище облаков точек.

   - `id` (PK)
   - `key` (путь или ключ в MinIO/SMB)
   - `size_bytes`
   - `hash`
   - `format` (.parquet/.ply)
   - `storage`
   - `created_at`

8. **events** — журнал событий.

   - `id` (PK)
   - `source` (ESP32/RECORDER/BACKEND/ARM)
   - `level` (INFO/WARN/ERROR)
   - `message`
   - `context_json`
   - `ts`

9. **recipes** — справочник рецептов.

   - `id` (PK)
   - `name`
   - `description`
   - `target_mass`
   - `target_height`
   - `tolerance`

10. **users** — пользователи системы.

    - `id` (PK)
    - `login`
    - `password_hash`
    - `role` (OPERATOR, TECHNOLOGIST, ADMIN)
    - `fullname`
    - `created_at`

### 7.2. Связи и правила

- Один поддон (`pallets`) может иметь несколько измерений (`measurements`).
- Измерение связано с файлом облака (`files`), весами (`weights`), дефектами (`defects`), действиями операторов (`operator_actions`).
- Все действия логируются в `events`.
- `recipes` используются для проверки соответствия продукции параметрам.
- `users` фиксируют ответственных за действия.

### 7.3. ER‑диаграмма (текстовая форма)

```
[pallets] 1 ───< [measurements] >───1 [devices]
                  │
                  ├────< [weights]
                  ├────< [defects]
                  ├────< [operator_actions]
                  └────1 [files]

[users] 1 ───< [operator_actions]
[recipes] 1 ───< [pallets]
[events] — отдельная таблица событий
```

### 7.4. Индексация

- `rfid_uid` (быстрый поиск поддона).
- `started_at` (хронология).
- `status` (очередь для оператора).
- `file_key` (доступ к облакам).
- `user_id` (история действий).

### 7.5. Пример DDL (фрагмент)

```sql
CREATE TABLE pallets (
  id BIGSERIAL PRIMARY KEY,
  rfid_uid VARCHAR(64) UNIQUE NOT NULL,
  product_code VARCHAR(64),
  color_code VARCHAR(64),
  recipe_id BIGINT REFERENCES recipes(id),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE measurements (
  id BIGSERIAL PRIMARY KEY,
  pallet_id BIGINT REFERENCES pallets(id),
  device_id BIGINT REFERENCES devices(id),
  started_at TIMESTAMPTZ NOT NULL,
  finished_at TIMESTAMPTZ,
  mode VARCHAR(16),
  status VARCHAR(24),
  operator_id BIGINT REFERENCES users(id),
  issue_code VARCHAR(64),
  issue_comment TEXT,
  file_id BIGINT REFERENCES files(id)
);
```

---

## 8. API (полное описание)

### 8.1. Общие положения

- **Версия API:** v1 (`/api/v1`)
- **Формат:** JSON (UTF‑8)
- **Аутентификация:** JWT Bearer (`Authorization: Bearer <token>`) для АРМ и внутренних сервисов. Для ESP32 и Recorder допускается API‑ключ (`X-API-Key`) либо служебный JWT.
- **Идемпотентность:** для операций создания рекомендуется заголовок `Idempotency-Key`.
- **Трассировка:** `X-Request-Id` и `measurement_id` в ответах/логах.
- **Веб‑сокеты:** `/ws/live` (события для АРМ: очередь измерений, статусы, дефекты).

### 8.2. OpenAPI 3.0 (YAML)

```yaml
openapi: 3.0.3
info:
  title: SmartEYE Backend API
  version: 1.0.0
servers:
  - url: http://localhost:8080/api/v1
security:
  - bearerAuth: []
  - apiKeyAuth: []
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
    apiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
  schemas:
    Error:
      type: object
      required: [code, message]
      properties:
        code: { type: string, example: VALIDATION_ERROR }
        message: { type: string, example: "Field 'pallet_rfid' is required" }
        details: { type: object, additionalProperties: true }
    ApiResponse:
      type: object
      properties:
        success: { type: boolean }
        data: { }
        error: { $ref: '#/components/schemas/Error' }
    Device:
      type: object
      properties:
        id: { type: integer }
        type: { type: string, enum: [scanner, esp32, scale, rfid] }
        serial: { type: string }
        location: { type: string }
        status: { type: string }
        last_seen: { type: string, format: date-time }
    Pallet:
      type: object
      properties:
        id: { type: integer }
        rfid_uid: { type: string }
        product_code: { type: string }
        color_code: { type: string }
        recipe_id: { type: integer }
        created_at: { type: string, format: date-time }
    FileRef:
      type: object
      properties:
        id: { type: integer }
        key: { type: string, example: scanner_cloud_20250418_155315_ABC123.parquet }
        format: { type: string, enum: [parquet, ply] }
        storage: { type: string, enum: [minio, smb] }
        size_bytes: { type: integer }
        created_at: { type: string, format: date-time }
    Measurement:
      type: object
      properties:
        id: { type: integer }
        pallet_id: { type: integer }
        device_id: { type: integer }
        started_at: { type: string, format: date-time }
        finished_at: { type: string, format: date-time, nullable: true }
        mode: { type: string, enum: [SILENT, OPERATOR] }
        status: { type: string, enum: [CREATED, IN_PROGRESS, PENDING_REVIEW, ACCEPTED, REJECTED, REWORK] }
        operator_id: { type: integer, nullable: true }
        issue_code: { type: string, nullable: true }
        issue_comment: { type: string, nullable: true }
        file: { $ref: '#/components/schemas/FileRef' }
    MeasurementCreate:
      type: object
      required: [pallet_rfid, device_id]
      properties:
        pallet_rfid: { type: string, example: E20034120123456789012345 }
        recipe: { type: string, example: TILE_200x100x60 }
        device_id: { type: integer, example: 12 }
        mode: { type: string, enum: [SILENT, OPERATOR], default: SILENT }
    MeasurementFinish:
      type: object
      required: [file_key]
      properties:
        file_key: { type: string, example: scanner_cloud_20250418_155315_ABC123.parquet }
        file_format: { type: string, enum: [parquet, ply], default: parquet }
        storage: { type: string, enum: [minio, smb], default: minio }
        summary_metrics:
          type: object
          additionalProperties: { type: number }
          example: { z_flatness_mm: 0.8, z_wave_mm: 1.2 }
    WeightCreate:
      type: object
      required: [measurement_id, phase, value_kg]
      properties:
        measurement_id: { type: integer }
        phase: { type: string, enum: [BEFORE, AFTER] }
        value_kg: { type: number, format: float }
        sensor_ok: { type: boolean, default: true }
        ts: { type: string, format: date-time }
    DefectCreate:
      type: object
      required: [measurement_id, type]
      properties:
        measurement_id: { type: integer }
        type: { type: string, example: FLATNESS }
        severity: { type: string, enum: [INFO, WARN, ERROR], default: WARN }
        metric: { type: number, format: float, nullable: true }
        threshold: { type: number, format: float, nullable: true }
        ts: { type: string, format: date-time }
    OperatorDecision:
      type: object
      required: [measurement_id, action]
      properties:
        measurement_id: { type: integer }
        action: { type: string, enum: [ACCEPT, REJECT, REWORK] }
        reason: { type: string, example: SURFACE_DEFECTS }
        comment: { type: string }
        actor_id: { type: integer }
        ts: { type: string, format: date-time }
paths:
  /health:
    get:
      summary: Health check
      security: []
      responses:
        '200': { description: OK }
  /auth/login:
    post:
      summary: Obtain JWT token
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [login, password]
              properties:
                login: { type: string }
                password: { type: string }
      responses:
        '200':
          description: Token issued
          content:
            application/json:
              schema:
                type: object
                properties:
                  token: { type: string }
        '401': { description: Unauthorized }
  /measurements/start:
    post:
      summary: Start measurement
      operationId: startMeasurement
      parameters:
        - in: header
          name: Idempotency-Key
          schema: { type: string }
          required: false
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/MeasurementCreate' }
      responses:
        '201':
          description: Created
          content:
            application/json:
              schema: { $ref: '#/components/schemas/Measurement' }
        '400': { description: Bad Request, content: { application/json: { schema: { $ref: '#/components/schemas/Error' } } } }
        '401': { description: Unauthorized }
  /measurements/{id}:
    get:
      summary: Get measurement by id
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer }
      responses:
        '200': { description: OK, content: { application/json: { schema: { $ref: '#/components/schemas/Measurement' } } } }
        '404': { description: Not Found }
  /measurements/{id}/finish:
    post:
      summary: Finish measurement
      operationId: finishMeasurement
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer }
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/MeasurementFinish' }
      responses:
        '200': { description: OK }
        '404': { description: Not Found }
  /weights:
    post:
      summary: Create weight record
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/WeightCreate' }
      responses:
        '201': { description: Created }
        '404': { description: Measurement Not Found }
  /defects:
    post:
      summary: Create defect record
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/DefectCreate' }
      responses:
        '201': { description: Created }
        '404': { description: Measurement Not Found }
  /operator/decision:
    post:
      summary: Submit operator decision
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/OperatorDecision' }
      responses:
        '201': { description: Created }
        '404': { description: Measurement Not Found }
  /files/{id}:
    get:
      summary: Get file metadata
      parameters:
        - in: path
          name: id
          required: true
          schema: { type: integer }
      responses:
        '200': { description: OK, content: { application/json: { schema: { $ref: '#/components/schemas/FileRef' } } } }
        '404': { description: Not Found }
```

### 8.3. Формат событий WebSocket `/ws/live`

**Типы событий:**

- `measurement.created` — новое измерение в очереди.
- `measurement.finished` — завершено, доступно облако.
- `defects.detected` — список дефектов.
- `operator.action` — решение применено.

**Пример:**

```json
{
  "type": "measurement.finished",
  "payload": {
    "measurement_id": 102345,
    "status": "PENDING_REVIEW",
    "file_key": "scanner_cloud_20250418_155315_ABC123.parquet"
  }
}
```

### 8.4. Коды ошибок

- `VALIDATION_ERROR` — некорректные входные данные.
- `NOT_FOUND` — сущность не найдена (measurement/pallet/file).
- `UNAUTHORIZED` / `FORBIDDEN` — проблемы с доступом.
- `CONFLICT` — нарушение идемпотентности/дубликат операции.
- `STORAGE_ERROR` — ошибка при сохранении файла в хранилище.
- `INTERNAL_ERROR` — прочие ошибки сервера.

### 8.5. Версионирование и совместимость

- Минорные изменения совместимы назад (добавление полей/эндпоинтов).
- Ломающее изменение — новая мажорная версия (`/api/v2`).

## 9. Roadmap (этапы разработки)

### Этап A. Архитектура и окружение (1–2 недели)

**Задачи:**

- Настройка Docker‑compose окружения (PostgreSQL, MinIO, pgAdmin, Adminer).
- Создание скелета Backend (Spring Boot, Liquibase, JWT авторизация).
- Создание скелета Recorder (Python, подключение к сканеру по SDK).
- Создание прототипа АРМ с Open3D окном.
  **Артефакты:** базовые сервисы запускаются, тестовые API и пример записи файла в MinIO.
  **Критерий готовности:** успешный тест записи и чтения метаданных.

### Этап B. Edge‑уровень (2–3 недели)

**Задачи:**

- Реализация прошивки ESP32 (работа с RFID, DI/DO, Ethernet).
- Интеграция с Backend по REST/WS.
- Буферизация сообщений при обрывах связи.
  **Артефакты:** прошивка ESP32, тестовый обмен с Backend.
  **Критерий готовности:** при обрыве связи данные буферизуются и синхронизируются.

### Этап C. Поток измерений (2–3 недели)

**Задачи:**

- Подключение Recorder к сканеру RIFTEK.
- Съём 2800 профилей и формирование облака точек.
- Сохранение облаков в формате .parquet в MinIO.
- Автоматическая генерация дефектов и запись в PostgreSQL.
  **Артефакты:** рабочее облако точек в хранилище, запись метаданных в БД.
  **Критерий готовности:** успешно обработан ≥10 поддонов подряд.

### Этап D. АРМ и операторский контур (2 недели)

**Задачи:**

- Создание интерфейса очереди PENDING\_REVIEW.
- Реализация визуализации облака с подсветкой дефектов.
- Внесение оператором решений (ACCEPT/REJECT/REWORK).
  **Артефакты:** полноценный GUI для оператора.
  **Критерий готовности:** оператор может открыть облако, увидеть дефекты и зафиксировать решение.

### Этап E. Эксплуатация и надёжность (1–2 недели)

**Задачи:**

- Реализация механизмов ретраев и буферизации.
- Внедрение мониторинга (Prometheus/Grafana).
- Настройка логирования (ELK/централизованное).
- Подготовка документации.
  **Артефакты:** стабильная работа комплекса при нагрузке.
  **Критерий готовности:** комплекс стабильно обрабатывает ≥1000 измерений за смену.

---

## 10. Критерии приёмки

### 10.1. Функциональные

1. **Идентификация (RFID):**
   - 100% поддонов, прошедших пост, получают корректный UID в БД.
   - Ошибки чтения ≤ 1% за смену; повторная попытка чтения — автоматическая.
2. **Сканирование и облако точек:**
   - На каждый поддон записывается ровно одно облако в хранилище, ссылка сохранена в БД.
   - Объём файла ≤ 3 МБ при среднем поддоне; формат — `.parquet` (по умолчанию).
   - Кол-во профилей \~2800 ±10% при длине поддона \~1,4 м (шаг задан в рецепте).
3. **Контроль геометрии/поверхности:**
   - Высота, ровность, «волна», дефекты поверхности рассчитываются и сохраняются в `defects` с метриками.
   - Пороговые значения берутся из рецепта/настроек и применяются корректно.
4. **Вес/плотность:**
   - Две записи в `weights` (BEFORE/AFTER) на измерение, `value_kg` в пределах поверочного диапазона датчиков.
   - Плотность рассчитывается (если в рецепте включено) и сохраняется в метаданные измерения.
5. **Режимы работы:**
   - **Silent Mode:** все новые измерения получают статус `PENDING_REVIEW`, без блокировок процесса.
   - **Operator Mode:** оператор видит карточку, открывает облако, фиксирует `ACCEPT/REJECT/REWORK` с комментарием; действие отражается в `operator_actions`.
6. **АРМ (Open3D 0.19):**
   - Отображение облака, инструменты навигации/сечения/замеров координат.
   - Подсветка выявленных дефектов по данным `defects`.
7. **Интеграции/отчёты:**
   - Экспорт отчётов по смене в Excel/CSV запускается и формируется без ошибок.
   - Выгрузка в 1С (REST или XML) — минимум по итогам смены (этап пилота).

### 10.2. Нефункциональные

1. **Производительность:**
   - Время полного цикла (скан+вес+запись) ≤ 40 с при номинальной загрузке.
   - Обработка ≥ **1000 измерений за смену** без деградации интерфейса и очередей.
2. **Надёжность/отказоустойчивость:**
   - При недоступности Backend/хранилища данные буферизуются и доставляются после восстановления, потерь нет.
   - Автоматические ретраи с экспоненциальной задержкой; максимум X попыток (конфигурируемо).
3. **Безопасность:**
   - Доступ к Backend и АРМ — только по аутентифицированным сессиям (JWT/OAuth2), роли применяются.
   - Передача данных — по HTTPS/WSS.
4. **Логирование/трассировка:**
   - Все ключевые события (старт/финиш/ошибки/решения) пишутся в `events` с `measurement_id`.
   - Логи позволяют восстановить ход любого измерения.
5. **Соответствие API:**
   - Реализованы эндпоинты и контракт из раздела 8 (OpenAPI), проходят валидацию по JSON‑схемам.

### 10.3. Приёмочные тесты (набор)

1. **PT‑01 RFID End‑to‑End:** поддон проходит пост — в БД создан `pallet`, стартует `measurement`, UID совпадает с меткой.
2. **PT‑02 Scan & Store:** выполняется съём \~2800 профилей, файл `.parquet` записан в MinIO/SMB, `files.key` и `measurements.file_id` заполнены.
3. **PT‑03 Weights:** записаны два значения (BEFORE/AFTER), рассчитана масса продукции, значения в отчёте коррелируют с эталоном.
4. **PT‑04 Defects Rules:** искусственно создаются нарушения допусков — в `defects` фиксируются тип/метрика/порог.
5. **PT‑05 Silent Mode:** при выключенном АРМ серия из 50 измерений сохраняется со статусом `PENDING_REVIEW`.
6. **PT‑06 Operator Mode:** оператор открывает 10 карточек, принимает решения, записи появляются в `operator_actions`.
7. **PT‑07 Resilience:** во время записи файла отключается сеть — файл сохраняется локально и догружается после восстановления.
8. **PT‑08 Performance:** нагрузочный прогон 1000 измерений/смена — время цикла и размер файлов в норме, без падений.
9. **PT‑09 Security:** попытки доступа без токена/с ролью ниже требуемой — отклоняются корректными кодами ошибок.
10. **PT‑10 Reports:** формируется отчёт за смену, проверка корректности сумм/количеств и гиперссылок на облака.

### 10.4. Пилот и ввод в эксплуатацию

- **Пилотный период:** не менее 10 смен подряд, суммарно ≥ 10 000 измерений, инциденты классифицированы и закрыты.
- **Документация:** инструкции по установке, эксплуатации, план резервного копирования, регламенты обновлений.
- **Обучение:** проведён инструктаж операторов и технолога, чек‑листы подтверждены подписями.

---




## 11. Приложения

### 11.1. Схемы подключения аппаратной части
```
                +------------------+
                |   Frima PLC      |
                +------------------+
                         |
                        DI/DO
                         |
              +---------------------+
              | Опторазвязка (PC817)|
              +---------------------+
                         |
                       GPIO
                         |
                +------------------+
                |      ESP32       |
                | (ESP-WROVER-IE)  |
                +------------------+
   |      |          |         |         |
   |      |          |         |         +--> SPI --> W5500 Ethernet --> LAN --> Backend
   |      |          |         +--> UART/RS-485 --> RFID Reader(s)
   |      |          +--> RS-485 --> Весы (через MAX485)
   |      +--> GPIO(D5) --> MAX485-1 --> IN1 Scanner (Trigger)
   |                
   +--> GPIO(D6) --> MAX485-2 --> Reset Counter Scanner

Шаговый двигатель (ШВП):
ESP32 GPIO14/12/13 --> TB6600 Driver --> Stepper Motor
Концевики (спереди/сзади) --> ESP32 GPIO32/33/34
Аварийный выключатель --> ESP32 GPIO35

Питание:
36V — Шаговый двигатель через TB6600
24V — периферия, датчики
5V  — ESP32, W5500, RFID

```

### 11.2. Пример Liquibase changelog (фрагмент)
```xml
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">

  <changeSet id="001-create-tables" author="smarteye">
    <createTable tableName="devices">
      <column name="id" type="BIGSERIAL">
        <constraints primaryKey="true" />
      </column>
      <column name="type" type="VARCHAR(32)" />
      <column name="serial" type="VARCHAR(64)" />
      <column name="status" type="VARCHAR(16)" />
      <column name="last_seen" type="TIMESTAMPTZ" />
    </createTable>

    <createTable tableName="pallets">
      <column name="id" type="BIGSERIAL">
        <constraints primaryKey="true" />
      </column>
      <column name="rfid_uid" type="VARCHAR(64)" />
      <column name="created_at" type="TIMESTAMPTZ" defaultValueComputed="CURRENT_TIMESTAMP" />
    </createTable>
  </changeSet>

</databaseChangeLog>
```

### 11.3. Минимальный docker-compose.yml
```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    container_name: smarteye-db
    environment:
      POSTGRES_DB: smarteye
      POSTGRES_USER: smarteye
      POSTGRES_PASSWORD: smarteye
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  minio:
    image: quay.io/minio/minio
    container_name: smarteye-minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minio
      MINIO_ROOT_PASSWORD: minio12345
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio:/data

  backend:
    build: ./backend
    container_name: smarteye-backend
    environment:
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"
    depends_on:
      - db
      - minio

volumes:
  pgdata:
  minio:
```

### 11.4. Структура хранилища облаков точек
```
/storage/
   /2025/
      /04/
         /07/
            scanner_cloud_20250407_083000_A1B2C3.parquet
            scanner_cloud_20250407_083045_D4E5F6.parquet
```
Формат имени: `scanner_cloud_<YYYYMMDD>_<HHMMSS>_<RFID>.parquet`

### 11.5. Дополнительные материалы
- Инструкции по пусконаладке ESP32.
- Настройки MinIO (bucket: smarteye-clouds).
- Справочники продукции, рецептов и операторов в виде SQL-дампа.



## 11. Приложения

### 11.1. Электрические соединения и сигналы (эскиз)

**ESP32 (ESP‑WROVER‑IE) — пины и интерфейсы:**
- **Ethernet (W5500, SPI):** `MOSI=GPIO23`, `MISO=GPIO19`, `SCLK=GPIO18`, `CS=GPIO5`, `RST=GPIO21`  
- **UART0 (логи):** `TX0=GPIO1`, `RX0=GPIO3`  
- **UART1 (RFID‑1):** `TX1=GPIO10`, `RX1=GPIO9` *(вариативно; при конфликте переназначить)*  
- **UART2 (RFID‑2/RS‑485):** `TX2=GPIO17`, `RX2=GPIO16`, `DE/RE=GPIO4`  
- **RS‑485 (весовой терминал, опц.):** линий A/B через MAX485, DE/RE управляется `GPIO4`  
- **DI (входы через опторазвязку):**  
  - `IN_START` (сигнал «цикл/готовность от пресса») → `GPIO32`  
  - `IN_END_FWD` (концевик «сканер спереди») → `GPIO33`  
  - `IN_END_BACK` (концевик «сканер сзади») → `GPIO34`  
  - `IN_ESTOP` (аварийный) → `GPIO35`
- **DO (выходы через реле/транзисторы):**  
  - `OUT_SCAN_TRIGGER` (импульсы на IN1 сканера) → `GPIO25`  
  - `OUT_COUNTER_RESET` (сброс счётчика сканера) → `GPIO26`  
  - `OUT_BUZZER/LAMP` (индикация статуса) → `GPIO27`
- **ШВП/шаговый двигатель (если управляет ESP32):**  
  - `STEP=GPIO14`, `DIR=GPIO12`, `ENA=GPIO13` → драйвер TB6600  
  - Концевики продольного хода уже учтены (см. DI)

> Примечание: все дискретные цепи — через опторазвязку (PC817/модуль), общая «земля» согласована, для индуктивных нагрузок — диоды/RC‑снабберы.

**Сигналы сканера RIFTEK:**
- **IN1** — запуск импульсами (с `OUT_SCAN_TRIGGER`), 
- **RESET** — сброс счётчика (с `OUT_COUNTER_RESET`),
- **TCP/IP** — профили к Recorder.

**Питание:**
- 24–36 V: TB6600/двигатель, клапаны, реле.  
- 12 V: периферия (опц.).  
- 5 V: ESP32, W5500, RFID‑считыватели TTL.  
- Гальваническая развязка между силовой и логикой.

**Сеть и адресация (пример):**
- `ESP32`: 192.168.10.20  
- `RF627Smart`: 192.168.10.30  
- `Backend`: 192.168.10.40:8080  
- `MinIO`: 192.168.10.41:9000/9001  
- `PostgreSQL`: 192.168.10.42:5432

---

### 11.2. Форматы сообщений Edge ⇄ Backend

**POST /api/v1/measurements/start (ESP32 → Backend)**
```json
{
  "pallet_rfid": "E20034120123456789012345",
  "recipe": "TILE_200x100x60",
  "device_id": 12,
  "mode": "SILENT",
  "signals": {"start": true, "end_fwd": false, "end_back": true},
  "fw_version": "esp32-1.2.3",
  "ts": "2025-09-01T10:15:30Z"
}
```

**POST /api/v1/weights (ESP32 → Backend)**
```json
{
  "measurement_id": 102345,
  "phase": "BEFORE",
  "value_kg": 38.420,
  "sensor_ok": true,
  "ts": "2025-09-01T10:16:05Z"
}
```

**POST /api/v1/measurements/{id}/finish (Recorder → Backend)**
```json
{
  "file_key": "scanner_cloud_20250901_101732_E20034120123456789012345.parquet",
  "file_format": "parquet",
  "storage": "minio",
  "summary_metrics": {"z_flatness_mm": 0.72, "z_wave_mm": 1.05}
}
```

---

### 11.3. Структура файла облака точек (.parquet)
Обязательные колонки:
- `x` (float32, мм), `y` (float32, мм), `z` (float32, мм)
- `intensity` (uint16, опц.)
- `profile_idx` (int32) — порядковый № профиля
- `created_at` (timestamp)
Метаданные файла (Parquet key/value): `scanner_model`, `firmware`, `rfid_uid`, `measurement_id`, `recipe`, `sampling_step_mm`.

---

### 11.4. Liquibase changelog (фрагмент)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
  xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.23.xsd">

  <changeSet id="001-init-tables" author="smarteye">
    <createTable tableName="pallets">
      <column name="id" type="BIGSERIAL">
        <constraints primaryKey="true"/>
      </column>
      <column name="rfid_uid" type="VARCHAR(64)">
        <constraints nullable="false" unique="true"/>
      </column>
      <column name="product_code" type="VARCHAR(64)"/>
      <column name="color_code" type="VARCHAR(64)"/>
      <column name="recipe_id" type="BIGINT"/>
      <column name="created_at" type="TIMESTAMPTZ" defaultValueComputed="now()"/>
    </createTable>

    <createTable tableName="devices">
      <column name="id" type="BIGSERIAL"><constraints primaryKey="true"/></column>
      <column name="type" type="VARCHAR(32)"/>
      <column name="serial" type="VARCHAR(64)"/>
      <column name="location" type="VARCHAR(128)"/>
      <column name="status" type="VARCHAR(16)"/>
      <column name="last_seen" type="TIMESTAMPTZ"/>
    </createTable>

    <createTable tableName="files">
      <column name="id" type="BIGSERIAL"><constraints primaryKey="true"/></column>
      <column name="key" type="TEXT"><constraints nullable="false"/></column>
      <column name="size_bytes" type="BIGINT"/>
      <column name="hash" type="VARCHAR(128)"/>
      <column name="format" type="VARCHAR(16)" defaultValue="parquet"/>
      <column name="storage" type="VARCHAR(16)" defaultValue="minio"/>
      <column name="created_at" type="TIMESTAMPTZ" defaultValueComputed="now()"/>
    </createTable>

    <createTable tableName="measurements">
      <column name="id" type="BIGSERIAL"><constraints primaryKey="true"/></column>
      <column name="pallet_id" type="BIGINT"/>
      <column name="device_id" type="BIGINT"/>
      <column name="started_at" type="TIMESTAMPTZ"/>
      <column name="finished_at" type="TIMESTAMPTZ"/>
      <column name="mode" type="VARCHAR(16)"/>
      <column name="status" type="VARCHAR(24)"/>
      <column name="operator_id" type="BIGINT"/>
      <column name="issue_code" type="VARCHAR(64)"/>
      <column name="issue_comment" type="TEXT"/>
      <column name="file_id" type="BIGINT"/>
    </createTable>

    <createIndex tableName="pallets" indexName="ix_pallets_rfid">
      <column name="rfid_uid"/>
    </createIndex>
    <createIndex tableName="measurements" indexName="ix_meas_started">
      <column name="started_at"/>
    </createIndex>
    <createIndex tableName="measurements" indexName="ix_meas_status">
      <column name="status"/>
    </createIndex>
  </changeSet>
</databaseChangeLog>
```
> Примечание: последующие changeSet — для `weights`, `defects`, `operator_actions`, `events`, `recipes`, `users` + внешние ключи и ENUM (через check/lookup).

---

### 11.5. Docker‑compose (минимальный стенд)
```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: smarteye
      POSTGRES_USER: smarteye
      POSTGRES_PASSWORD: smarteye
    ports: ["5432:5432"]
    volumes: ["pgdata:/var/lib/postgresql/data"]

  minio:
    image: quay.io/minio/minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minio
      MINIO_ROOT_PASSWORD: minio12345
    ports: ["9000:9000", "9001:9001"]
    volumes: ["minio:/data"]

  backend:
    image: ghcr.io/smarteye/backend:latest # placeholder
    depends_on: [db, minio]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/smarteye
      SPRING_DATASOURCE_USERNAME: smarteye
      SPRING_DATASOURCE_PASSWORD: smarteye
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio12345
    ports: ["8080:8080"]

volumes:
  pgdata:
  minio:
```

---

### 11.6. Структура хранилища и именование
```
/minio/bucket-smarteye/
  ├─ year=2025/
  │   ├─ month=09/
  │   │   ├─ day=01/
  │   │   │   ├─ scanner_cloud_20250901_101732_E20034120123456789012345.parquet
  │   │   │   └─ scanner_cloud_20250901_104201_E2003...parquet
  └─ _manifests/
      └─ checksums.jsonl
```
**Шаблон имени:** `scanner_cloud_<YYYYMMDD>_<HHMMSS>_<RFID>.parquet`  
**Контроль целостности:** `hash` (SHA‑256) хранится в таблице `files` и в `_manifests/checksums.jsonl`.

---

### 11.7. Резервное копирование и восстановление (guideline)
- **PostgreSQL:** ежедневный `pg_dump` (полный), почасовой WAL‑архив (опц.).  
- **MinIO:** репликация бакета или периодический `mc mirror` на внешний узел.  
- **Тест восстановления:** еженедельно выборочная проверка 1% файлов и связей `files↔measurements`.

---

### 11.8. Требования к журналированию
- Уровни: `INFO`, `DEBUG`, `WARN`, `ERROR`.
- Корреляция по `measurement_id`, `X-Request-Id`.
- Хранение логов не менее 90 дней на пилоте; экспорт в центральный сборщик (ELK) — опц.

---

### 11.9. Требования к безопасности ключей
- `X-API-Key` для ESP32/Recorder хранится в `.env` Backend и в защищённой памяти Edge.
- Ротация ключей — минимум раз в 90 дней; отзыв при компрометации.
- Доступ к MinIO — по отдельным учётным данным с правами только на нужный бакет.

---



### 11.10. ASCII‑схема проводок (эскиз для монтажников)
```
                ┌───────────────────────── ЦЕХ / ЛИНИЯ ─────────────────────────┐
                │                                                               │
        ┌───────┴───────┐        DI/DO (24V, опто)         ┌──────────────────┐ │
        │   Frima PLC   ├───────────────┬──────────────────►│  ESP32 (Edge)   │ │
        └───────┬───────┘               │                   │  WROVER‑IE      │ │
                │                       │                   │  + W5500 (SPI)  │ │
                │                       │                   └─────┬─────┬─────┘ │
                │                       │                         │     │       │
                │                       │ UART/RS‑485             │     │       │
                │                       └──────────────┐          │     │       │
                │                                      │          │     │       │
                │                           ┌──────────▼───┐      │     │       │
                │                           │ RFID Reader  │      │     │       │
                │                           └──────────────┘      │     │       │
                │                                                 │     │       │
                │   RS‑485 (опц.)                                 │     │       │
                └─────────────────────────────────────────────────┘     │       │
                                                                        │       │
                                                 DO→IN1 / DO→RESET      │       │
                                               (через реле/транзистор)  │       │
                                                ┌───────────────┐       │       │
                           TCP/IP  ┌───────────►  RIFTEK RF627  ◄───────┘       │
                           профили  │           └───────────────┘               │
                                     
                                             SPI (LAN)                           │
                                         ┌───────────────┐                       │
                                         │   W5500 ETH   │───────LAN────────────┼───► Backend
                                         └───────────────┘                       │     + PostgreSQL (Docker)
                                                                                 │     + MinIO (S3)
        ШВП / Stepper  ┌─────────────────────────────────────────────────────────┘
        ┌──────────────▼────────────┐     DI (концевики)   
        │   TB6600 (STEP/DIR/ENA)  │◄─────────────────────── ESP32 GPIO
        └───────────────────────────┘

Питание: 36V (шаговый/клапаны), 24V (реле/датчики), 5V (ESP32/W5500/RFID). Все дискретные цепи через опторазвязку.
```

### 11.11. Схема проводок (вектор/SVG)
Сгенерирован файл `smarteye_wiring.svg` с блок‑схемой и подписанными линиями (DI/DO, UART/RS‑485, SPI, LAN, питание). См. ссылки на скачивание в чате.

