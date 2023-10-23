1. exec bootJar task in chess-app module
2. scp ./chess-app/build/libs/chess-app-1.0.0-SNAPSHOT.jar krihl4n@192.168.0.200:/home/krihl4n/Desktop/chess/chess-v2.jar
3. docker build -t chess .
4. docker run -d --restart unless-stopped -p 8080:8080 --env-file ./env.list chess

