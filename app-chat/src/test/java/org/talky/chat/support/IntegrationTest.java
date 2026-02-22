package org.talky.chat.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.talky.chat.app.tool.PlatformApiClient;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @MockitoBean
    protected PlatformApiClient platformApiClient;
}
