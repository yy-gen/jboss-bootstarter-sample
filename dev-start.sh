docker rm -f egov-app-dev 2>/dev/null || true
docker run -d \
  --name egov-app-dev \
  -e SPRING_PROFILES_ACTIVE=dev \
  -p 18081:8080 \
  egovframe-project:1.0
