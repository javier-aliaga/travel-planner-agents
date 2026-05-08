package io.dapr.examples.travel;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * Mock ChatModel that returns predictable responses for integration testing.
 * Takes priority over the OpenAI ChatModel bean via {@code @Alternative @Priority(1)}.
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class MockChatModel implements ChatModel {

    @Override
    public ChatResponse doChat(ChatRequest request) {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from(
                        "Your trip to Paris is all set! "
                                + "Flight: Delta DL200 departing 12:15, arriving 15:45 - $380. "
                                + "Hotel: Hotel Le Marais, 4 star, $180/night near Notre-Dame. "
                                + "Activities: Louvre Museum tour, Seine River cruise, Montmartre food tour. "
                                + "Estimated total budget: $1,660 for 5 nights."))
                .tokenUsage(new TokenUsage(10, 50))
                .finishReason(FinishReason.STOP)
                .build();
    }
}
