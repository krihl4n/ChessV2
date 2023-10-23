docker build -t chess .
docker run -d --restart unless-stopped -p 8080:8080 --env-file ./env.list chess