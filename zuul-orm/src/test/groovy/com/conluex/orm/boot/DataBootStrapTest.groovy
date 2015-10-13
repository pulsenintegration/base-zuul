package com.confluex.orm.boot

import org.dbunit.IDatabaseTester
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import static org.mockito.Mockito.*

class DataBootStrapTest {

    @Test
    void afterPropertiesSetShouldInvokeTesterIfEnabled() {
        def bootStrap = new DataBootStrap(
                enabled: true,
                tester: mock(IDatabaseTester),
                csvImportsPath: new ClassPathResource("/data")
        )
        bootStrap.afterPropertiesSet()
        verify(bootStrap.tester).onSetup()
    }

    @Test
    void afterPropertiesSetShouldNotInvokeTesterIfDdlDoesNotMatchTrigger() {
        def bootStrap = new DataBootStrap(
                enabled: false,
                tester: mock(IDatabaseTester),
                csvImportsPath: new ClassPathResource("/data")
        )
        bootStrap.afterPropertiesSet()
        verify(bootStrap.tester, never()).onSetup()
    }
}
