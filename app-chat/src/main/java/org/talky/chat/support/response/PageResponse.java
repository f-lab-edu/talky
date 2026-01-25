package org.talky.chat.support.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        boolean hasNext
) {
}
