package org.talky.platform.app.api;

import org.talky.platform.app.vo.UserAgentInfo;

public interface UserAgentParser {

    UserAgentInfo parse(String userAgentString);
}
