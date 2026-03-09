package org.sparta.delivery.store.infrastructure.ai;

import org.sparta.delivery.store.domain.service.AiGenerateProductName;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class AiGenerateProductNameImpl implements AiGenerateProductName {

    private final ChatClient chatClient;

    public AiGenerateProductNameImpl(ChatClient.Builder builder, JdbcChatMemoryRepository chatMemoryRepository) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();

        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1)
                )
                .build();
    }

    @Override
    public String generate(String context, String productCode) {
        String systemMessage = """
        당신은 창의적인 요리 네이밍 전문가입니다. 아래 [특징]을 읽고, 독창적이고 고급스러운 이름을 딱 하나만 지어주세요.
        
        [제약 조건]        
        1. '메뉴명:', '추천하는 이름은' 같은 수식어나 부연 설명을 절대 하지 마세요.
        2. 따옴표나 마침표 없이 오직 메뉴 이름만 출력하세요.
        3. 음식의 본질을 유지하되 50자 이내의 창의적인 단어를 사용하세요.
        
        [특징]  
        """;
        
        String result = chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, productCode))
                .system(systemMessage)
                .user(context)
                .call()
                .content();
        return result == null ? null : result.replace("\"", "").replace("'", "").trim();
    }
}
