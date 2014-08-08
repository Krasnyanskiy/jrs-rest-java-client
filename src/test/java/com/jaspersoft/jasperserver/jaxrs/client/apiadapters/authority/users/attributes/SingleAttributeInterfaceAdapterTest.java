package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.users.attributes;

import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import org.mockito.Mock;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link SingleAttributeInterfaceAdapter}
 */
public class SingleAttributeInterfaceAdapterTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;


    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_1() {
        // ...
    }

    @Test
    public void should_2() {

    }

    @Test
    public void should_3() {

    }

    @Test
    public void should_4() {

    }

    @Test
    public void should_5() {

    }

    @Test
    public void should_6() {

    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock);
    }
}