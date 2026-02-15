package org.talky.platform.app.vo;

public record Profile(
        Long id,
        Long userId,
        String profileMessage
) {
}
