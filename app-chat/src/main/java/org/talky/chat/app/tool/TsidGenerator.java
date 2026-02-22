package org.talky.chat.app.tool;

import io.hypersistence.tsid.TSID;
import org.springframework.stereotype.Component;

@Component
public class TsidGenerator {

    private static final TSID.Factory FACTORY = TSID.Factory.builder().build();

    public Long generate() {
        return FACTORY.generate().toLong();
    }
}
