docker pull postgres:latest
docker run --name mappin-standalone-pedido-db -p 5434:5432 -e POSTGRES_USER=mappin -e POSTGRES_PASSWORD=mappinPedido -e POSTGRES_DB=mappin-pedido-db -d postgres