export MAVEN_OPTS=-Djava.security.egd=file:/dev/./urandom
cd $1
mvn deploy
