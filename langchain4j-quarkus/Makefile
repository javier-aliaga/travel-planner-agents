APP_ID       := langchain4j-agent
DAPR_GRPC    := 40001
DAPR_HTTP    := 3500

.PHONY: redis dapr app run stop clean

## Start Redis (required for state store)
redis:
	docker run -d --name dapr-redis -p 6379:6379 redis:7-alpine
	@echo "Redis started on localhost:6379"

## Stop Redis
redis-stop:
	docker rm -f dapr-redis 2>/dev/null || true

## Start Dapr sidecar (no --app-port to avoid circular wait)
dapr:
	dapr run \
		--app-id $(APP_ID) \
		--dapr-grpc-port $(DAPR_GRPC) \
		--dapr-http-port $(DAPR_HTTP) \
		--resources-path ./components \
		--log-level info

## Start Quarkus app (run in a separate terminal)
app:
	DAPR_GRPC_ENDPOINT=http://localhost:$(DAPR_GRPC) \
	DAPR_HTTP_ENDPOINT=http://localhost:$(DAPR_HTTP) \
	mvn quarkus:dev -Ddebug=false

## Quick test endpoints
test-weather:
	curl -s "http://localhost:8080/weather?city=Paris"

test-guide:
	curl -s "http://localhost:8080/guide?city=Paris&cuisine=french&days=3"

test-research:
	curl -s "http://localhost:8080/research?city=Paris&cuisine=french&days=3"

test-trip:
	curl -s "http://localhost:8080/trip?city=Paris&cuisine=french&days=3"

test-travel:
	curl -s "http://localhost:8080/travel/plan?origin=NYC&destination=Paris&date=2025-07-01&nights=5&interests=history,food"

## Build the quarkus extension (run from SDK repo first)
build-sdk:
	cd ../../salaboy-java-sdk/quarkus && \
		mvn install -Dspotbugs.skip=true -DskipTests -q

## Flush Redis (clear all workflow state)
redis-flush:
	docker exec dapr_redis redis-cli FLUSHDB

## Clean
clean:
	mvn clean -q
