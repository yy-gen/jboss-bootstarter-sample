docker run -d \
  --name egov-app-prod \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:oracle:thin:@prod-db-host:1521/PRODDB \
  -e DB_USERNAME=prod_user \
  -e DB_PASSWORD=prod_password \
  -p 18083:8080 \
  egovframe-project:1.0
