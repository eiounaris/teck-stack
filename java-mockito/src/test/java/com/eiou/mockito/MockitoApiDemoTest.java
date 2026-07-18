package com.eiou.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockitoApiDemoTest {
    @Mock
    private Dependency dependency;

    @Captor
    private ArgumentCaptor<String> captor;

    @InjectMocks
    private ApiUnderTest api;

    @Test
    @DisplayName("when + thenReturn + verify")
    void stubbingAndVerify() {
        when(dependency.load("key")).thenReturn("value");

        String result = api.run("key");

        assertEquals("VALUE", result);
        verify(dependency).save("value");
    }

    @Test
    @DisplayName("thenThrow + never")
    void exceptionStubbing() {
        when(dependency.load("error")).thenThrow(new IllegalStateException("demo"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> api.run("error")
        );

        assertEquals("demo", exception.getMessage());
        verify(dependency, never()).save("error");
    }

    @Test
    @DisplayName("ArgumentCaptor")
    void argumentCaptor() {
        when(dependency.load("key")).thenReturn("value");

        api.run("key");

        verify(dependency).save(captor.capture());
        assertEquals("value", captor.getValue());
    }

    @Test
    @DisplayName("spy")
    void spyApi() {
        List<String> list = spy(new ArrayList<>());

        list.add("value");

        verify(list).add("value");
        assertEquals(List.of("value"), list);
    }

    private interface Dependency {
        String load(String key);

        void save(String value);
    }

    private static final class ApiUnderTest {
        private final Dependency dependency;

        private ApiUnderTest(Dependency dependency) {
            this.dependency = dependency;
        }

        private String run(String key) {
            String value = dependency.load(key);
            dependency.save(value);
            return value.toUpperCase();
        }
    }
}
