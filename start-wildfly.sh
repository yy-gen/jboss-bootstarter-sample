#!/bin/sh
exec /opt/jboss/wildfly/bin/standalone.sh \
  -Dorg.springframework.boot.logging.LoggingSystem=none \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} \
  -b 0.0.0.0 \
  -bmanagement 0.0.0.0