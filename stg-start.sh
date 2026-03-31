docker run -d \
  --name egov-app-stg \
  -e SPRING_PROFILES_ACTIVE=stg \
  -e DB_URL=jdbc:oracle:thin:@stg-db-host:1521/STGDB \
  -e DB_USERNAME=stg_user \
  -e DB_PASSWORD=stg_password \
  -p 18082:8080 \
  egovframe-project:1.0
