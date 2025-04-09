package com.groupe2cs.generator.tests.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        properties = "spring.config.name=application-test"
)
@Profile("test")
@AutoConfigureMockMvc
public class BaseUnitTests {

    @Autowired
    public MockMvc mockMvc;

}
