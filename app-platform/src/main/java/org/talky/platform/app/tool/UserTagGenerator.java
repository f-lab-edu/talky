package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UserTagGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "빠른", "용감한", "조용한", "밝은", "귀여운",
            "멋진", "강한", "부드러운", "날쌘", "똑똑한",
            "재빠른", "씩씩한", "온화한", "활발한", "영리한",
            "대담한", "침착한", "유쾌한", "당당한", "늠름한"
    );

    private static final List<String> NOUNS = List.of(
            "호랑이", "사자", "독수리", "늑대", "토끼",
            "여우", "곰", "판다", "코끼리", "기린",
            "펭귄", "돌고래", "매", "올빼미", "고양이",
            "강아지", "햄스터", "다람쥐", "수달", "카피바라"
    );

    private static final int MAX_ATTEMPTS = 10;

    private final UserReader userReader;


    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String tag = createUserTag();
            if (!userReader.existsUserTag(tag)) {
                return tag;
            }
        }
        throw new CoreException(ErrorCode.DEFAULT_ERROR);
    }

    private String createUserTag() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        int number = random.nextInt(10000);

        return adjective + noun + "#" + String.format("%04d", number);
    }
}
