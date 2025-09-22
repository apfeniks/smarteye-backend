smart-eye-backend/
├─ pom.xml
├─ .env.example
├─ .gitignore
├─ Dockerfile
├─ docker-compose.yml
├─ README.md
└─ src/
├─ main/
│  ├─ java/
│  │  └─ org/smarteye/backend/
│  │     ├─ SmartEyeApplication.java
│  │     ├─ config/
│  │     │  ├─ OpenApiConfig.java
│  │     │  ├─ JacksonConfig.java
│  │     │  ├─ WebConfig.java
│  │     │  ├─ SecurityConfig.java
│  │     │  ├─ MethodSecurityConfig.java
│  │     │  ├─ MinioConfig.java
│  │     │  ├─ ConveyorProperties.java        # @ConfigurationProperties(prefix="conveyor")
│  │     │  └─ CorsConfig.java
│  │     ├─ security/
│  │     │  ├─ jwt/
│  │     │  │  ├─ JwtAuthFilter.java
│  │     │  │  ├─ JwtService.java
│  │     │  │  └─ JwtAuthenticationEntryPoint.java
│  │     │  ├─ api/
│  │     │  │  ├─ AuthController.java         # /api/v1/auth/login, refresh
│  │     │  │  └─ dto/
│  │     │  │     └─ AuthDtos.java
│  │     │  └─ model/
│  │     │     ├─ Role.java
│  │     │     └─ UserPrincipal.java
│  │     ├─ common/
│  │     │  ├─ exception/
│  │     │  │  ├─ ApiException.java
│  │     │  │  ├─ NotFoundException.java
│  │     │  │  ├─ ValidationException.java
│  │     │  │  └─ GlobalExceptionHandler.java
│  │     │  ├─ audit/
│  │     │  │  ├─ AuditLogger.java
│  │     │  │  └─ RequestIdFilter.java
│  │     │  └─ util/
│  │     │     ├─ IdempotencyUtil.java
│  │     │     └─ TimeUtil.java
│  │     ├─ domain/                # JPA сущности (строго по ТЗ + tech_pallets)
│  │     │  ├─ TechPallet.java     # вместо Pallet
│  │     │  ├─ Device.java
│  │     │  ├─ Recipe.java
│  │     │  ├─ FileRef.java
│  │     │  ├─ Measurement.java
│  │     │  ├─ Weight.java         # BEFORE/AFTER
│  │     │  ├─ Defect.java
│  │     │  ├─ OperatorAction.java
│  │     │  ├─ User.java
│  │     │  ├─ Event.java          # аудит/события
│  │     │  └─ enums/
│  │     │     ├─ MeasurementStatus.java
│  │     │     ├─ MeasurementMode.java
│  │     │     ├─ WeightPhase.java # BEFORE, AFTER
│  │     │     ├─ TechPalletStatus.java # ACTIVE, DECOMMISSIONED
│  │     │     └─ StorageType.java # minio, smb
│  │     ├─ repository/
│  │     │  ├─ TechPalletRepository.java
│  │     │  ├─ DeviceRepository.java
│  │     │  ├─ RecipeRepository.java
│  │     │  ├─ FileRefRepository.java
│  │     │  ├─ MeasurementRepository.java
│  │     │  ├─ WeightRepository.java
│  │     │  ├─ DefectRepository.java
│  │     │  ├─ OperatorActionRepository.java
│  │     │  ├─ UserRepository.java
│  │     │  └─ EventRepository.java
│  │     ├─ mapper/                # MapStruct
│  │     │  ├─ TechPalletMapper.java
│  │     │  ├─ MeasurementMapper.java
│  │     │  ├─ WeightMapper.java
│  │     │  ├─ FileRefMapper.java
│  │     │  ├─ DeviceMapper.java
│  │     │  ├─ DefectMapper.java
│  │     │  └─ UserMapper.java
│  │     ├─ service/
│  │     │  ├─ TechPalletService.java
│  │     │  ├─ DeviceService.java
│  │     │  ├─ RecipeService.java
│  │     │  ├─ FileService.java           # работа с MinIO (загрузка метаданных/генерация URL)
│  │     │  ├─ MeasurementService.java    # start/finish + статус/операции
│  │     │  ├─ WeightsService.java        # запись весов и вызов пайплайна
│  │     │  ├─ ConveyorPipelineService.java  # логика «смещение 5» + fallback
│  │     │  ├─ DefectService.java         # автогенерация дефектов на основе метрик
│  │     │  ├─ OperatorActionService.java
│  │     │  ├─ UserService.java
│  │     │  └─ ReportingService.java
│  │     ├─ integration/
│  │     │  ├─ RecorderCallbackService.java # приём finish-события от Recorder
│  │     │  └─ EspEdgeService.java          # приём старт/событий с ESP32
│  │     ├─ storage/                  # низкоуровневый слой для MinIO/S3
│  │     │  ├─ S3ClientFactory.java
│  │     │  └─ MinioStorageClient.java
│  │     ├─ web/                      # REST API v1 (по ТЗ)
│  │     │  ├─ dto/
│  │     │  │  ├─ TechPalletDtos.java
│  │     │  │  ├─ DeviceDtos.java
│  │     │  │  ├─ RecipeDtos.java
│  │     │  │  ├─ FileDtos.java
│  │     │  │  ├─ MeasurementDtos.java
│  │     │  │  ├─ WeightDtos.java
│  │     │  │  ├─ DefectDtos.java
│  │     │  │  ├─ OperatorActionDtos.java
│  │     │  │  └─ UserDtos.java
│  │     │  ├─ TechPalletController.java     # /api/v1/tech-pallets/...
│  │     │  ├─ DeviceController.java         # /api/v1/devices/...
│  │     │  ├─ RecipeController.java         # /api/v1/recipes/...
│  │     │  ├─ FileController.java           # /api/v1/files/...
│  │     │  ├─ MeasurementController.java    # /api/v1/measurements/...
│  │     │  ├─ WeightController.java         # /api/v1/weights
│  │     │  ├─ DefectController.java         # /api/v1/defects
│  │     │  ├─ OperatorController.java       # /api/v1/operator/...
│  │     │  └─ UserController.java           # /api/v1/users/...
│  │     ├─ ws/
│  │     │  ├─ WebSocketConfig.java          # /ws/live
│  │     │  └─ LiveEventsGateway.java        # отправка событий в сокет
│  │     └─ reporting/
│  │        └─ ReportController.java         # /api/v1/reports/...
│  ├─ resources/
│  │  ├─ application.yml               # базовые настройки
│  │  ├─ application-local.yml
│  │  ├─ application-docker.yml
│  │  ├─ application-prod.yml
│  │  ├─ conveyor.yml                  # offsetBetweenScales = 5 (по умолчанию)
│  │  ├─ banner.txt
│  │  ├─ db/changelog/
│  │  │  ├─ changelog-master.yml
│  │  │  ├─ 0001-init-schema.yml      # users/roles/tech_pallets/devices/recipes/files
│  │  │  ├─ 0002-measurements.yml
│  │  │  ├─ 0003-weights.yml
│  │  │  ├─ 0004-defects.yml
│  │  │  ├─ 0005-operator-actions.yml
│  │  │  ├─ 0006-events.yml
│  │  │  └─ 0007-indexes.yml          # индексация из ТЗ
│  │  ├─ openapi/smarteye-api.yaml    # полное описание API v1 (из ТЗ)
│  │  └─ messages.properties
│  └─ webapp/
│     └─ static/                       # при необходимости, заглушки
└─ test/
├─ java/
│  └─ org/smarteye/backend/
│     ├─ integration/
│     │  ├─ PostgresMinioTestBase.java  # Testcontainers
│     │  ├─ MeasurementFlowIT.java
│     │  └─ WeightsCorrelationIT.java   # проверка «смещения 5»
│     └─ unit/
│        ├─ ConveyorPipelineServiceTest.java
│        ├─ MeasurementServiceTest.java
│        └─ TechPalletServiceTest.java
└─ resources/
└─ application-test.yml


