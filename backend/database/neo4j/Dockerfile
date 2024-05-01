FROM neo4j:5.19.0

WORKDIR ${NEO4J_HOME}

COPY import/ ./import/
COPY conf/ ./conf/

ENTRYPOINT ["tini", "-g", "--", "/startup/docker-entrypoint.sh"]
CMD ["neo4j"]
