FROM neo4j:5.19.0

WORKDIR ${NEO4J_HOME}

COPY import/ ./import/
COPY conf/ ./conf/

# todo build own lightweight image out of https://github.com/neo4j/docker-neo4j-publish/blob/5e0122d70f6407707b3ae70e6ef91a1fb3fd2f61/5.6.0/community/Dockerfile

ENTRYPOINT ["tini", "-g", "--", "/startup/docker-entrypoint.sh"]
CMD ["neo4j"]
