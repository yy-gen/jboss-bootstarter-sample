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