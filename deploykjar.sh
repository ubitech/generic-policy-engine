export MAVEN_OPTS=-Djava.security.egd=file:/dev/./urandom
chmod -R 777 $1
cd $1
mvn deploy
