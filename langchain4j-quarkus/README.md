# Travel Planner - LangChain4j + Quarkus + Dapr

Multi-agent travel planner that demonstrates orchestration patterns using
[LangChain4j](https://docs.langchain4j.dev/), [Quarkus](https://quarkus.io/),
and [Dapr Workflows](https://docs.dapr.io/developing-applications/building-blocks/workflow/).

Every LLM call and tool call is a **durable Dapr Workflow activity** — if the
process crashes mid-execution, the workflow resumes from the last successful step.

## Architecture

```
TravelPlanner (@SequenceAgent)
 1. TravelResearch (@ParallelAgent)       ← runs all three in parallel
    ├── FlightFinder   (uses FlightTools)
    ├── HotelFinder    (uses HotelTools)
    └── ActivityPlanner (uses ActivityTools)
 2. ItineraryFormatter                    ← combines results into itinerary
```

### Agents

| Agent | Type | Tools | Description |
|-------|------|-------|-------------|
| FlightFinder | `@Agent` | FlightTools | Searches and recommends flights |
| HotelFinder | `@Agent` | HotelTools | Searches and recommends hotels |
| ActivityPlanner | `@Agent` | ActivityTools | Plans activities based on interests |
| ItineraryFormatter | `@Agent` | none | Combines results into a formatted itinerary |
| TravelResearch | `@ParallelAgent` | — | Runs flight, hotel, and activity search concurrently |
| TravelPlanner | `@SequenceAgent` | — | Orchestrates research then formatting |

### Tools

All tools return mock data and can be swapped for real API integrations:

- **FlightTools** — `searchFlights(origin, destination, date)`
- **HotelTools** — `searchHotels(city, checkIn, nights)`
- **ActivityTools** — `searchActivities(city, interests)`

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker (for Dapr sidecar)
- [Ollama](https://ollama.ai/) running locally (or swap to OpenAI)

```bash
# Pull a model for Ollama
ollama pull llama3.2
```

## Running

### Option 1: With Dapr Dev Services (self-contained)

Set `quarkus.dapr.devservices.enabled=true` in `application.properties`, then:

```bash
mvn quarkus:dev
```

This starts Dapr, placement, scheduler, and a PostgreSQL state store automatically
via Testcontainers. No external setup needed, but workflows won't be visible in
the Diagrid Dashboard.

### Option 2: With standalone Dapr (dashboard visible)

Keep `quarkus.dapr.devservices.enabled=false` (default), then:

```bash
dapr run --app-id travel-planner-app --app-port 8080 -- mvn quarkus:dev
```

Workflows will be visible in the Diagrid Dashboard.

### Calling the endpoint

```bash
curl "http://localhost:8080/travel/plan?origin=NYC&destination=Paris&date=2025-07-01&nights=5&interests=history,food"
```

Parameters (all optional, have defaults):

| Parameter | Default | Description |
|-----------|---------|-------------|
| `origin` | New York | Departure city |
| `destination` | Paris | Destination city |
| `date` | 2025-07-01 | Travel date |
| `nights` | 5 | Number of nights |
| `interests` | history, food, culture | Comma-separated interests |

## Using OpenAI instead of Ollama

In `pom.xml`, swap the dependency:

```xml
<!-- Replace quarkus-langchain4j-ollama with: -->
<dependency>
    <groupId>io.quarkiverse.langchain4j</groupId>
    <artifactId>quarkus-langchain4j-openai</artifactId>
    <version>${quarkus-langchain4j.version}</version>
</dependency>
```

In `application.properties`, replace the Ollama config with:

```properties
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4o-mini
```

## Testing

Tests use a `MockChatModel` that overrides the real LLM provider, so no Ollama or
API key is needed:

```bash
mvn test
```

Tests require Docker for Dapr dev services (automatically enabled in test scope).

## Project Structure

```
src/main/java/io/dapr/examples/travel/
├── TravelResource.java              GET /travel/plan endpoint
├── agents/
│   ├── FlightFinder.java            @Agent + FlightTools
│   ├── HotelFinder.java             @Agent + HotelTools
│   ├── ActivityPlanner.java         @Agent + ActivityTools
│   └── ItineraryFormatter.java      @Agent (LLM-only, no tools)
├── orchestration/
│   ├── TravelPlanner.java           @SequenceAgent (top-level)
│   ├── TravelResearch.java          @ParallelAgent (research phase)
│   └── TravelResearchResult.java    Result record
└── tools/
    ├── FlightTools.java             Mock flight search
    ├── HotelTools.java              Mock hotel search
    └── ActivityTools.java           Mock activity search
```

## How it works

1. A `GET /travel/plan` request arrives at `TravelResource`
2. `TravelPlanner` starts a **Dapr Workflow** with sequential orchestration
3. Step 1: `TravelResearch` starts a **parallel sub-workflow** that concurrently runs:
   - `FlightFinder` calls `FlightTools.searchFlights()` (durable activity)
   - `HotelFinder` calls `HotelTools.searchHotels()` (durable activity)
   - `ActivityPlanner` calls `ActivityTools.searchActivities()` (durable activity)
4. Step 2: `ItineraryFormatter` receives the combined results and produces the final itinerary
5. The response is returned to the caller

All tool calls are recorded in the Dapr Workflow history. If the process crashes,
the workflow resumes from the last completed activity.

## Agent Registry

Agents are automatically registered in the Dapr state store at startup using the
`dapr-agents` registry protocol. This makes them discoverable by other agents
(Python dapr-agents, other Java services) and visible in Diagrid Catalyst.

Registry keys:
- Per-agent: `agents:{team}:{agent-name}`
- Team index: `agents:{team}:_index`
