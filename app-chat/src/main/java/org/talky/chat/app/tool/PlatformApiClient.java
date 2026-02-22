package org.talky.chat.app.tool;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.talky.chat.support.error.CoreException;
import org.talky.chat.support.error.ErrorCode;
import org.talky.chat.support.response.ApiResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

@Component
public class PlatformApiClient {

    private final WebClient platformClient;

    public PlatformApiClient(WebClient platformClient) {
        this.platformClient = platformClient;
    }

    /**
     * 채팅 생성 가능 여부를 검증하고, 참여자(생성자 + 초대 대상자) ID 목록을 반환합니다.
     *
     * @param creatorId   채팅 생성 요청자 ID
     * @param inviteeTags 초대 대상자 태그 목록
     * @return 생성자 ID를 첫 번째로 포함한 전체 참여자 ID 목록
     */
    public Mono<List<Long>> validateCreatingChat(Long creatorId, List<String> inviteeTags) {
        return platformClient.post()
                .uri("/api/v1/internal/chats/create-check")
                .bodyValue(new CreateChatCheckRequest(creatorId, inviteeTags))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CreateChatCheckResponse>>() {})
                .onErrorMap(WebClientResponseException.class, e -> new CoreException(ErrorCode.DEFAULT_ERROR))
                .map(response -> {
                    CreateChatCheckResponse data = response.data();
                    return Stream.concat(
                            Stream.of(Long.parseLong(data.creatorId())),
                            data.inviteeIds().stream().map(Long::parseLong)
                    ).toList();
                });
    }

    private record CreateChatCheckRequest(Long creatorId, List<String> inviteeTags) {}

    private record CreateChatCheckResponse(String creatorId, List<String> inviteeIds) {}
}
