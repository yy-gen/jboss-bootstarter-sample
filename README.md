좋습니다. 아래 내용을 그대로 **`README.md`** 로 저장해서 쓰시면 됩니다.

```md
# eGovFrame 5.0 + Spring Boot WAR + WildFly 배포/트러블슈팅 정리

## 1. 개요

이 문서는 **eGovFrame 5.0 기반 Spring Boot 프로젝트를 WAR로 패키징하여 WildFly 컨테이너에 배포**하는 과정과, 실제 배포 중 발생했던 주요 문제 및 해결 과정을 정리한 문서이다.

기본 배포 방식은 `quay.io/wildfly/wildfly:latest-jdk17` 이미지를 사용하고, 빌드된 `ROOT.war` 를 WildFly의 `standalone/deployments/` 디렉터리에 복사하는 방식이다. WildFly 공식 컨테이너 문서도 가장 단순하고 일반적인 배포 방식으로 `deployments/` 디렉터리 배치를 안내한다. [Source](https://docs.wildfly.org/wildfly-container/)

---

## 2. 프로젝트 목표

- eGovFrame 5.0 Spring Boot 프로젝트를 **외부 WAS(WildFly)** 에 WAR 형태로 배포
- 로컬 내장 톰캣 실행 방식이 아닌 **전통적인 WAR 배포 방식** 사용
- 환경별(dev/stg/prod) 설정을 **Spring Profile** 로 분리
- 로깅은 애플리케이션 내부 `log4j2.xml` 대신 **WildFly logging subsystem** 으로 통합 관리

Spring Boot는 전통적인 WAR 배포를 지원하며, 외부 서블릿 컨테이너에 배포하려면 `SpringBootServletInitializer` 기반 구성이 필요하다. [Source](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)

---

## 3. 초기 구성

### Dockerfile 초안

```dockerfile
FROM quay.io/wildfly/wildfly:latest-jdk17

COPY --chown=jboss:root target/ROOT.war $JBOSS_HOME/standalone/deployments/ROOT.war

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
```

### 핵심 배포 개념

- `ROOT.war` 로 배포하면 context root는 `/`
- WildFly는 `standalone/deployments/` 의 WAR를 자동 스캔하여 배포
- 외부 WAS 배포 시 내장 톰캣은 `provided` 처리 필요

WildFly 컨테이너 문서는 `deployments/` 디렉터리에 WAR를 넣는 방식을 기본 배포 방법으로 설명한다. [Source](https://docs.wildfly.org/wildfly-container/)  
Spring Boot 전통적 WAR 배포 가이드는 `SpringBootServletInitializer` 와 외부 서블릿 컨테이너 구성을 설명한다. [Source](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)

---

## 4. pom.xml 핵심 설정

### 필수 조건

- `<packaging>war</packaging>`
- `spring-boot-starter-tomcat` → `<scope>provided</scope>`
- 메인 클래스가 `SpringBootServletInitializer` 상속

### 메인 클래스 예시

```java
package egovframework.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EgovBootApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EgovBootApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EgovBootApplication.class, args);
    }
}
```

이 구성은 Spring Boot 공식 문서에서 설명하는 외부 WAS용 WAR 배포 방식과 일치한다. [Source](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)

---

## 5. 최초 증상

배포는 되는 것처럼 보였지만 실제 접속 시:

- `http://localhost:8080` → 애플리케이션이 아니라 **WildFly welcome page**
- 컨트롤러 경로 접근 불가
- 로컬 톰캣에서는 정상 동작

이 상황은 단순 URL 매핑 문제라기보다, **WAR는 배치되었지만 애플리케이션 컨텍스트가 정상 기동하지 못한 상태**로 판단했다.

---

## 6. 실제 원인 1차: 로깅 충돌

초기 로그 분석 결과 다음과 같은 충돌이 발생했다.

- `JBossLoggerContext cannot be cast to LoggerContext`
- `LoggerFactory is not a Logback LoggerContext but Logback is on the classpath`

즉, WAR 내부에 포함된 **Logback / Log4j2 구현체** 와 WildFly의 **JBoss LogManager 기반 로깅 체계**가 충돌했다.

WildFly는 애플리케이션이 다양한 로깅 facade를 사용할 수 있도록 허용하지만, 실제 로그 관리와 출력은 서버 logging subsystem과 통합하는 방향을 권장한다. [Source](https://www.wildfly.org/guides/application-logging/)  
또한 WildFly의 log4j2 지원은 **`log4j-api` 위임 수준**이며, **`log4j-core` 와 `log4j2.xml` 구성 자체는 지원 범위가 아니다.** [Source](http://docs.wildfly.org/wildfly-proposals/logging/WFCORE-482-log4j2-support.html)

---

## 7. dependency tree 분석 결과

의존성 추적 결과 핵심 원인은 Spring Boot starter가 아니라 **eGovFrame 의존성 체인** 안에 있었다.

```text
egovframe-rte-psl-dataaccess
  └─ egovframe-rte-fdl-logging
      ├─ log4j-core
      └─ log4j-slf4j-impl
```

즉 `egovframe-rte-psl-dataaccess` 가 `egovframe-rte-fdl-logging` 을 통해 `log4j-core`, `log4j-slf4j-impl` 을 WAR 안으로 끌고 들어오고 있었다.

WildFly는 WAR를 하나의 단일 모듈처럼 취급하고 `WEB-INF/lib`, `WEB-INF/classes` 를 같은 클래스 로더로 읽는다. 따라서 컨테이너가 제공하는 로깅 클래스와 WAR 내부 로깅 구현체가 겹치면 충돌이 발생할 수 있다. [Source](https://docs.wildfly.org/36/Developer_Guide.html)

---

## 8. pom.xml 수정 사항

### 8.1 Spring Boot 기본 logging 제거

아래 starter들에서 `spring-boot-starter-logging` 을 제외했다.

- `spring-boot-starter-web`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-validation`

### 8.2 eGovFrame transitively 포함된 Log4j2 제거

```xml
<dependency>
    <groupId>org.egovframe.rte</groupId>
    <artifactId>egovframe-rte-psl-dataaccess</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j-impl</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 8.3 제거한 파일

- `src/main/resources/log4j2.xml`

`log4j2.xml` 은 `log4j-core` 없이 남아 있어도 로깅 초기화 흐름에 혼선을 줄 수 있으므로 제거했다.

---

## 9. 로깅 관련 최종 원칙

### 하지 말아야 할 것

- WAR 안에 `log4j-core`, `log4j-slf4j-impl`, `logback-classic` 포함
- `log4j2.xml` 을 그대로 들고 WildFly에 배포
- WildFly 서버 로깅과 앱 자체 로깅 구현체를 동시에 운영

### 권장 방식

- 코드에서는 `SLF4J` 또는 `log4j-api` 수준까지만 사용
- 실제 포맷/핸들러는 **WildFly logging subsystem** 에서 관리
- Spring Boot LoggingSystem은 비활성화

WildFly의 log4j2 지원은 API 위임만 포함하고, `log4j-core` 및 config 파일은 비지원임이 문서에 명시되어 있다. [Source](http://docs.wildfly.org/wildfly-proposals/logging/WFCORE-482-log4j2-support.html)

---

## 10. Spring Boot LoggingSystem 비활성화

최종적으로 Spring Boot 자체 로깅 시스템을 끄고 WildFly 로깅만 사용하도록 설정했다.

### `start-wildfly.sh`

```bash
#!/bin/sh
exec /opt/jboss/wildfly/bin/standalone.sh \
  -Dorg.springframework.boot.logging.LoggingSystem=none \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev} \
  -b 0.0.0.0 \
  -bmanagement 0.0.0.0
```

이 옵션이 실제로 반영되었는지는 `server.log` 의 `sun.java.command` 또는 `VM Arguments` 에서 확인했다.

Spring Boot는 로깅 시스템을 시스템 프로퍼티로 제어할 수 있다. [Source](https://docs.spring.io/spring-boot/reference/features/logging.html)

---

## 11. WildFly logging subsystem으로 포맷 이관

기존 `log4j2.xml` 대신 WildFly CLI 설정으로 포맷/핸들러를 정의했다.

### `configuration.cli`

```cli
embed-server --std-out=echo

/subsystem=logging/pattern-formatter=APP_PATTERN:add(pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t] %c - %s%e%n")

/subsystem=logging/console-handler=APP_CONSOLE:add(level=INFO,named-formatter=APP_PATTERN)

/subsystem=logging/periodic-rotating-file-handler=APP_FILE:add(append=true,suffix=".yyyy-MM-dd",file={"path"=>"app.log","relative-to"=>"jboss.server.log.dir"},named-formatter=APP_PATTERN,level=INFO)

/subsystem=logging/logger=egovframework:add(level=DEBUG,handlers=[APP_CONSOLE,APP_FILE],use-parent-handlers=false)
/subsystem=logging/logger=org.springframework:add(level=INFO,handlers=[APP_CONSOLE,APP_FILE],use-parent-handlers=false)

stop-embedded-server
```

WildFly 공식 가이드는 CLI를 이용해 `console-handler`, `logger` 등을 수정하고 애플리케이션 로그 레벨/출력을 서버에서 제어하는 방식을 예시로 제공한다. [Source](https://www.wildfly.org/guides/application-logging/)

---

## 12. 최종 Dockerfile

```dockerfile
FROM quay.io/wildfly/wildfly:latest-jdk17

COPY --chown=jboss:root target/ROOT.war $JBOSS_HOME/standalone/deployments/ROOT.war
COPY --chown=jboss:root configuration.cli /opt/jboss/configuration.cli
COPY --chown=jboss:root start-wildfly.sh /opt/jboss/start-wildfly.sh

RUN chmod +x /opt/jboss/start-wildfly.sh && \
    $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/configuration.cli && \
    rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history && \
    rm -rf $JBOSS_HOME/standalone/tmp && \
    rm -rf $JBOSS_HOME/standalone/data

EXPOSE 8080 9990

CMD ["/opt/jboss/start-wildfly.sh"]
```

WildFly 공식 컨테이너 문서는 `deployments/` 배포 방식과 CLI를 이용한 설정 변경 방식을 안내한다. [Source](https://docs.wildfly.org/wildfly-container/)

---

## 13. 환경별 배포 전략

### 핵심 원칙

- **WAR/이미지는 하나만 유지**
- 환경별 차이는 `spring.profiles.active` 와 환경변수로 분리
- 민감정보(DB 비밀번호 등)는 Git에 넣지 않고 외부 주입

### `application.properties`

```properties
spring.application.name=egovframe-project
spring.profiles.active=dev
server.port=8080
spring.main.allow-bean-definition-overriding=true
spring.jpa.open-in-view=false

logging.level.root=INFO
logging.level.egovframework=INFO
logging.level.org.springframework=INFO

server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true
```

### `application-dev.properties`

```properties
app.env=dev

spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.url=${DB_URL:jdbc:hsqldb:mem:testdb}
spring.datasource.username=${DB_USERNAME:sa}
spring.datasource.password=${DB_PASSWORD:}

logging.level.egovframework=DEBUG
logging.level.org.springframework=INFO

spring.thymeleaf.cache=false
```

### `application-stg.properties`

```properties
app.env=stg

spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

logging.level.egovframework=INFO
logging.level.org.springframework=WARN

spring.thymeleaf.cache=true
```

### `application-prod.properties`

```properties
app.env=prod

spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

logging.level.root=INFO
logging.level.egovframework=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN

spring.thymeleaf.cache=true
```

Spring Boot는 WAR 배포 환경에서도 profile과 property 체계를 그대로 사용할 수 있다. [Source](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)

---

## 14. 실행 예시

### dev

```bash
docker run -d \
  --name egov-app-dev \
  -e SPRING_PROFILES_ACTIVE=dev \
  -p 18081:8080 \
  egovframe-project:1.0
```

### stg

```bash
docker run -d \
  --name egov-app-stg \
  -e SPRING_PROFILES_ACTIVE=stg \
  -e DB_URL=jdbc:oracle:thin:@stg-db-host:1521/STGDB \
  -e DB_USERNAME=stg_user \
  -e DB_PASSWORD=stg_password \
  -p 18082:8080 \
  egovframe-project:1.0
```

### prod

```bash
docker run -d \
  --name egov-app-prod \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:oracle:thin:@prod-db-host:1521/PRODDB \
  -e DB_USERNAME=prod_user \
  -e DB_PASSWORD=prod_password \
  -p 18083:8080 \
  egovframe-project:1.0
```

---

## 15. Docker 실행 중 만난 흔한 오류

### 15.1 포트 충돌

```bash
Bind for 0.0.0.0:8080 failed: port is already allocated
```

의미:
- 호스트의 `8080` 포트를 다른 컨테이너/프로세스가 사용 중

해결:
- 기존 점유 프로세스 종료
- 또는 다른 포트 사용 (`18081`, `18082`, `18083` 등)

### 15.2 컨테이너 이름 충돌

```bash
The container name "/egov-app-dev" is already in use
```

의미:
- 이전 실패 실행으로 컨테이너 이름이 이미 생성됨

해결:

```bash
docker rm -f egov-app-dev
```

---

## 16. 디버깅 명령어 모음

### dependency tree 확인

```bash
mvn dependency:tree | grep -E "logback|log4j|slf4j"
```

### 특정 의존성 유입 경로 확인

```bash
mvn dependency:tree -Dincludes=org.apache.logging.log4j:log4j-core
mvn dependency:tree -Dincludes=org.apache.logging.log4j:log4j-slf4j-impl
```

### WAR 내부 로깅 파일 확인

```bash
jar tf target/ROOT.war | grep -E 'logging.properties|log4j2|logback|web.xml'
```

### WebApplicationInitializer 탐색

```bash
grep -R "implements WebApplicationInitializer\|extends SpringBootServletInitializer\|extends AbstractDispatcherServletInitializer\|extends AbstractAnnotationConfigDispatcherServletInitializer\|extends AbstractSecurityWebApplicationInitializer" -n src/main/java
```

### 컨테이너 포트 점유 확인

```bash
docker ps --format "table {{.Names}}\t{{.Ports}}"
```

### 로컬 포트 사용 확인

```bash
lsof -i :8080
lsof -i :8081
```

### WildFly 서버 로그 확인

```bash
docker exec -it egov-app sh -c 'tail -n 300 /opt/jboss/wildfly/standalone/log/server.log'
```

### 오류만 필터링

```bash
docker exec -it egov-app sh -c "grep -nE 'ERROR|Exception|Caused by|Failed|WFLYCTL' /opt/jboss/wildfly/standalone/log/server.log | tail -n 100"
```

---

## 17. 최종 결론

이번 이슈의 본질은 **컨트롤러 매핑 문제**가 아니라,  
**외부 WAS(WildFly) 환경에서 eGovFrame + Spring Boot WAR의 로깅 구현체와 서버 로깅 체계가 충돌한 문제**였다.

최종 해결 포인트는 다음 4가지였다.

1. WAR 배포 기본 구성 맞추기
    - `war` packaging
    - `SpringBootServletInitializer`
    - `spring-boot-starter-tomcat` provided [Source](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)

2. WAR 내부 로깅 구현체 제거
    - `log4j-core`
    - `log4j-slf4j-impl`
    - `logback` 계열

3. `log4j2.xml` 제거 후 WildFly logging subsystem으로 이관 [Source](http://docs.wildfly.org/wildfly-proposals/logging/WFCORE-482-log4j2-support.html)

4. `-Dorg.springframework.boot.logging.LoggingSystem=none` 적용 [Source](https://docs.spring.io/spring-boot/reference/features/logging.html)

---

## 18. 참고 문서

- Spring Boot Traditional Deployment  
  https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html

- Spring Boot Logging  
  https://docs.spring.io/spring-boot/reference/features/logging.html

- WildFly Container Guide  
  https://docs.wildfly.org/wildfly-container/

- WildFly Application Logging Guide  
  https://www.wildfly.org/guides/application-logging/

- WildFly Log4j2 Support Proposal  
  http://docs.wildfly.org/wildfly-proposals/logging/WFCORE-482-log4j2-support.html

- WildFly Developer Guide  
  https://docs.wildfly.org/36/Developer_Guide.html
```

원하시면 제가 이어서 바로  
**이 README를 더 짧게 “사내 배포 표준 문서형”으로 압축한 버전**도 만들어드릴게요.