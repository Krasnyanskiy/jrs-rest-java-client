package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.importservice;

import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.handling.DefaultErrorHandler;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.importexport.StateDto;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest.buildRequest;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link ImportRequestAdapter}
 */
@PrepareForTest({ImportRequestAdapter.class, JerseyRequest.class})
public class ImportRequestAdapterTest extends PowerMockTestCase {

    @Mock
    private StateDto stateMock;

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private JerseyRequest<StateDto> requestStateDtoMock;

    @Mock
    private OperationResult<StateDto> operationResultStateDtoMock;

    @Mock
    private Callback<OperationResult<StateDto>, Object> callbackMock;

    private String taskId = "njkhfs8374";
    private String[] fakeArrayPath = new String[]{"/import", taskId, "/state"};

    @BeforeMethod
    public void after() {
        initMocks(this);
    }

    @Test(testName = "constructor")
    public void should_pass_corresponding_params_to_the_constructor_and_invoke_parent_class_constructor_with_these_params()
            throws IllegalAccessException {
        ImportRequestAdapter adapter = new ImportRequestAdapter(sessionStorageMock, taskId);
        SessionStorage retrievedSessionStorage = adapter.getSessionStorage();

        Field field = field(ImportRequestAdapter.class, "taskId");
        Object retrievedField = field.get(adapter);

        assertSame(retrievedField, taskId);
        assertEquals(retrievedSessionStorage, sessionStorageMock);
    }

    @Test(testName = "state")
    public void should_return_proper_OperationResult_object() {

        // Given
        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(StateDto.class), eq(fakeArrayPath), any(DefaultErrorHandler.class))).thenReturn(requestStateDtoMock);
        when(requestStateDtoMock.get()).thenReturn(operationResultStateDtoMock);

        // When
        ImportRequestAdapter adapter = new ImportRequestAdapter(sessionStorageMock, taskId);
        OperationResult<StateDto> opResult = adapter.state();

        // Then
        assertSame(opResult, operationResultStateDtoMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void asyncState() throws Exception {

        // Given
        final String TASK_ID = "simple_task_7";

        Object resultMock = PowerMockito.mock(Object.class);

        ImportRequestAdapter adapterSpy = PowerMockito.spy(new ImportRequestAdapter(sessionStorageMock, TASK_ID));

        PowerMockito.mockStatic(JerseyRequest.class);
        PowerMockito.when(
                buildRequest(
                        eq(sessionStorageMock),
                        eq(StateDto.class),
                        eq(new String[]{"/import", TASK_ID, "/state"})))
                .thenReturn(requestStateDtoMock);

        PowerMockito.doReturn(operationResultStateDtoMock).when(requestStateDtoMock).get();
        PowerMockito.doReturn(resultMock).when(callbackMock).execute(operationResultStateDtoMock);

        // When
        RequestExecution retrieved = adapterSpy.asyncState(callbackMock);

        // Than
        Assert.assertNotNull(retrieved);
        Assert.assertNotNull(resultMock);

        PowerMockito.verifyStatic(times(1));
        JerseyRequest.buildRequest(
                eq(sessionStorageMock),
                eq(StateDto.class),
                eq(new String[]{"/import", TASK_ID, "/state"}));

        Mockito.verify(callbackMock, times(1)).execute(operationResultStateDtoMock);
        Mockito.verify(requestStateDtoMock, times(1)).get();

        Mockito.verifyNoMoreInteractions(requestStateDtoMock);
        Mockito.verifyNoMoreInteractions(callbackMock);
    }

    @AfterMethod
    public void before() {
        reset(sessionStorageMock, requestStateDtoMock, operationResultStateDtoMock, stateMock);
    }
}