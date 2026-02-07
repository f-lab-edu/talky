package org.talky.platform.app.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.talky.platform.support.error.CoreException;
import org.talky.platform.support.error.ErrorCode;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor

//TODO: 그냥 랜덤 64비트 정수로 바꾸자 .. 어차피 TSID 주력으로 쓸거니까
public class UserIdGenerator {

    // JS Number.MAX_SAFE_INTEGER (2^53-1)
    // - JS 정밀도 손실 방지
    // - BIGINT라 JOIN 성능 유지 (UUID 대비 빠름)
    // - 랜덤이라 유저 정보(가입일, 규모) 미노출
    private static final long MAX_ID = (1L << 53) - 1;
    private static final int MAX_ATTEMPTS = 10;

    private final UserReader userReader;

    public Long generate() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            Long id = ThreadLocalRandom.current().nextLong(1, MAX_ID + 1);
            if (!userReader.existsId(id)) {
                return id;
            }
        }
        throw new CoreException(ErrorCode.DEFAULT_ERROR);
    }
}
