# Travel Planner - LangChain4j + Quarkus + Dapr

Multi-agent travel planner that demonstrates orchestration patterns using
[LangChain4j](https://docs.langchain4j.dev/), [Quarkus](https://quarkus.io/),
and [Dapr Workflows](https://docs.dapr.io/developing-applications/building-blocks/workflow/).

Every LLM call and tool call is a **durable Dapr Workflow activity** — if the
process crashes mid-execution, completed agents are skipped on restart and the
in-progress agent is automatically re-run from its original prompt and tools.

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
| WeatherAssistant | `@Agent` | WeatherTools | Checks weather for a city |
| CityGuide | `@Agent` | CityGuideTools (3) | Attractions, restaurants, and transport info |
| FlightFinder | `@Agent` | FlightTools | Searches and recommends flights |
| HotelFinder | `@Agent` | HotelTools | Searches and recommends hotels |
| ActivityPlanner | `@Agent` | ActivityTools | Plans activities based on interests |
| ItineraryFormatter | `@Agent` | none | Combines results into a formatted itinerary |
| TravelAdvisor | `@Agent` | SlowApiTools | Travel advisories via a slow (30s) tool — used for the crash recovery demo |
| TripPrep | `@SequenceAgent` | — | Weather → CityGuide in sequence |
| QuickResearch | `@ParallelAgent` | — | Weather + CityGuide in parallel |
| ItineraryRefiner | `@LoopAgent` | — | Weather + CityGuide looped twice |
| TravelRouter | `@ConditionalAgent` | — | Routes by trip duration (weather vs full guide) |
| TravelResearch | `@ParallelAgent` | — | Flight + hotel + activity search concurrently |
| TravelPlanner | `@SequenceAgent` | — | Orchestrates research then formatting |

### Tools

All tools return mock data and can be swapped for real API integrations:

- **FlightTools** — `searchFlights(origin, destination, date)`
- **HotelTools** — `searchHotels(city, checkIn, nights)`
- **ActivityTools** — `searchActivities(city, interests)`
- **SlowApiTools** — `fetchTravelAdvisory(country)` sleeps 30s so you can kill the
  process mid-call and watch crash recovery

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker (for Dapr sidecar)
- [Ollama](https://ollama.ai/) running locally (or swap to OpenAI)

```bash
# Pull a model for Ollama — use llama3.1:8b or larger.
# Smaller models (e.g. llama3.2 3B) often malform tool call arguments.
ollama pull llama3.1:8b
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

### Endpoints

| Endpoint | Agent | Type | Status |
|----------|-------|------|--------|
| `make test-weather` | WeatherAssistant | @Agent (1 tool) | Stable |
| `make test-guide` | CityGuide | @Agent (3 tools) | Stable |
| `make test-trip` | TripPrep | @SequenceAgent | Stable |
| `make test-research` | QuickResearch | @ParallelAgent | Stable |
| `make test-refine` | ItineraryRefiner | @LoopAgent (2 iterations) | Stable |
| `make test-route-quick` | TravelRouter | @ConditionalAgent (days<=1 → weather only) | Stable |
| `make test-route-long` | TravelRouter | @ConditionalAgent (days>1 → weather + guide) | Stable |
| `make test-travel` | TravelPlanner | @SequenceAgent + nested @ParallelAgent | Stable (verified with gpt-4o-mini; quality is model-dependent) |
| `make test-crash` | TravelAdvisor | @Agent + slow tool (crash recovery demo) | Stable |

### Calling the travel endpoint

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

## LLM Provider Configuration

This project supports two LLM provider modes:

### Option A: Dapr Conversation API (provider-agnostic, default)

Routes LLM calls through the Dapr Conversation building block. Swap LLM providers
(OpenAI, Anthropic, Ollama, etc.) by changing the Dapr component config — no Java
code changes needed. This is the active default in `application.properties`.

```properties
quarkus.langchain4j.chat-model.provider=dapr-conversation
quarkus.langchain4j.dapr.component-name=llm
quarkus.langchain4j.dapr.temperature=0.7
```

Then define a Dapr conversation component in `components/conversation.yaml`. The
committed default targets OpenAI `gpt-4o-mini`, with the API key resolved from the
`OPENAI_API_KEY` environment variable via a local env secret store
(`components/secretstore-env.yaml`) — so no secret is ever stored in the file:

```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: llm
spec:
  type: conversation.openai
  version: v1
  metadata:
  - name: model
    value: "gpt-4o-mini"
  - name: key
    secretKeyRef:
      name: OPENAI_API_KEY
      key: OPENAI_API_KEY
auth:
  secretStore: envvar-secrets
```

Export `OPENAI_API_KEY` in the shell that starts the Dapr sidecar (`make dapr`) —
`daprd` reads its environment at launch. To target Ollama instead, add
`- name: endpoint` with `value: "http://localhost:11434/v1"`, set `model` to
`llama3.1:8b`, and replace the `key` block with `value: "ollama"`. To use
Anthropic, change `type` to `conversation.anthropic`.

### Option B: Ollama (direct)

Calls Ollama directly from the app. No Dapr sidecar needed for LLM calls.

```properties
quarkus.langchain4j.chat-model.provider=ollama
quarkus.langchain4j.ollama.chat-model.model-id=llama3.1:8b
quarkus.langchain4j.ollama.timeout=120s
```

### Option C: OpenAI (direct)

In `pom.xml`, replace `quarkus-langchain4j-ollama` with:

```xml
<dependency>
    <groupId>io.quarkiverse.langchain4j</groupId>
    <artifactId>quarkus-langchain4j-openai</artifactId>
    <version>${quarkus-langchain4j.version}</version>
</dependency>
```

```properties
quarkus.langchain4j.chat-model.provider=openai
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4o-mini
```

## Crash Recovery Demo

`TravelAdvisor` calls `SlowApiTools.fetchTravelAdvisory()`, which sleeps for 30
seconds — long enough to kill the process mid-call:

```bash
# Terminal 1: trigger the slow agent
make test-crash          # GET /crash-test?country=France

# Terminal 2: while the tool is sleeping, kill the app
make kill-app

# Restart the app — the workflow resumes automatically
make app
```

On restart, Dapr replays the workflow: completed agents return cached results,
and the in-progress agent (TravelAdvisor) is re-run from scratch — its original
prompt and tools are re-executed by the extension's `RecoveryAgentActivity`.
Recovery is **agent-level**: LLM/tool calls inside the recovered agent run again;
completed agents are not re-run.

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
├── WeatherResource.java             GET /weather
├── CityGuideResource.java           GET /guide
├── TripPrepResource.java            GET /trip
├── QuickResearchResource.java       GET /research
├── ItineraryRefinerResource.java    GET /refine
├── TravelRouterResource.java        GET /route
├── TravelResource.java              GET /travel/plan
├── CrashRecoveryResource.java       GET /crash-test
├── agents/
│   ├── WeatherAssistant.java        @Agent + WeatherTools
│   ├── CityGuide.java               @Agent + CityGuideTools
│   ├── FlightFinder.java            @Agent + FlightTools
│   ├── HotelFinder.java             @Agent + HotelTools
│   ├── ActivityPlanner.java         @Agent + ActivityTools
│   ├── TravelAdvisor.java           @Agent + SlowApiTools (crash demo)
│   └── ItineraryFormatter.java      @Agent (LLM-only, no tools)
├── orchestration/
│   ├── TripPrep.java                @SequenceAgent (weather → guide)
│   ├── QuickResearch.java           @ParallelAgent (weather + guide)
│   ├── ItineraryRefiner.java        @LoopAgent (weather + guide x2)
│   ├── TravelRouter.java            @ConditionalAgent (by trip duration)
│   ├── TravelPlanner.java           @SequenceAgent (top-level)
│   ├── TravelResearch.java          @ParallelAgent (research phase)
│   └── TravelResearchResult.java    Result record
└── tools/
    ├── WeatherTools.java            Mock weather API
    ├── CityGuideTools.java          Mock city guide APIs (3 tools)
    ├── FlightTools.java             Mock flight search
    ├── HotelTools.java              Mock hotel search
    ├── ActivityTools.java           Mock activity search
    └── SlowApiTools.java            30s-slow advisory API (crash demo)
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

All LLM and tool calls are recorded in the Dapr Workflow history. If the process
crashes, completed agents are skipped on replay and the in-progress agent is
re-run from scratch (agent-level recovery — see the Crash Recovery Demo above).

## Agent Registry

Agents are automatically registered in the Dapr state store at startup using the
`dapr-agents` registry protocol. This makes them discoverable by other agents
(Python dapr-agents, other Java services) and visible in Diagrid Catalyst.

Configured in `application.properties`:

```properties
dapr.agents.statestore=agent-registry
dapr.agents.team=travel-planner
dapr.appid=langchain4j-agent
```

Registry keys:
- Per-agent: `agents:travel-planner:{agent-name}`
- Team index: `agents:travel-planner:_index`
