package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs.calendar.CalendarType;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs.calendar.SingleCalendarOperationsAdapter;
import com.jaspersoft.jasperserver.jaxrs.client.core.Callback;
import com.jaspersoft.jasperserver.jaxrs.client.core.JRSVersion;
import com.jaspersoft.jasperserver.jaxrs.client.core.JerseyRequest;
import com.jaspersoft.jasperserver.jaxrs.client.core.MimeType;
import com.jaspersoft.jasperserver.jaxrs.client.core.RequestExecution;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.Job;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.jaxb.wrappers.CalendarNameListWrapper;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * Unit tests for {@link com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs.JobsService}
 */
@PrepareForTest({JerseyRequest.class, JobsService.class})
public class JobsServiceTest extends PowerMockTestCase {

    @Mock
    private SessionStorage sessionStorageMock;

    @Mock
    private BatchJobsOperationsAdapter expectedAdapterMock;

    @Mock
    private SingleJobOperationsAdapter expectedJobOperationsAdapter;

    @Mock
    private JerseyRequest<Job> jobRequestMock;

    @Mock
    private JerseyRequest<CalendarNameListWrapper> wrapperRequestMock;

    @Mock
    private OperationResult<Job> expectedJobOperationResultMock;

    @Mock
    private OperationResult<CalendarNameListWrapper> expectedWrapperOperationResultMock;

    @Mock
    private Job reportMock;

    @Mock
    private RestClientConfiguration configurationMock;

    @Mock
    private Callback<OperationResult<CalendarNameListWrapper>, Object> callbackMock;

    @Mock
    private RequestExecution executionMock;

    @Mock
    private SingleCalendarOperationsAdapter expectedCalendarOperationsAdapterMock;

    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test(testName = "jobs")
    public void should_return_proper_adapter() throws Exception {

        // Given
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        whenNew(BatchJobsOperationsAdapter.class)
                .withParameterTypes(SessionStorage.class)
                .withArguments(sessionStorageMock)
                .thenReturn(expectedAdapterMock);

        // When
        BatchJobsOperationsAdapter retrieved = serviceSpy.jobs();

        // Than
        assertSame(retrieved, expectedAdapterMock);
    }

    @Test(testName = "job")
    public void should_return_proper_SingleJobOperationsAdapter() throws Exception {

        // Given
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        whenNew(SingleJobOperationsAdapter.class)
                .withParameterTypes(SessionStorage.class, String.class)
                .withArguments(sessionStorageMock, "9056")
                .thenReturn(expectedJobOperationsAdapter);

        // When
        SingleJobOperationsAdapter retrieved = serviceSpy.job(9056);

        // Than
        verify(serviceSpy, times(1)).job(9056);
        assertSame(retrieved, expectedJobOperationsAdapter);
    }

    @Test(testName = "scheduleReport_for_v5_6_1")
    public void should_return_proper_OperationResult_when_JRS_version_is_v5_6_1() {

        // Given
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));

        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(Job.class), eq(new String[]{"/jobs"}), any(JobValidationErrorHandler.class))).thenReturn(jobRequestMock);
        when(jobRequestMock.put(reportMock)).thenReturn(expectedJobOperationResultMock);
        when(sessionStorageMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getJrsVersion()).thenReturn(JRSVersion.v5_6_1);
        when(configurationMock.getContentMimeType()).thenReturn(MimeType.XML);

        // When
        serviceSpy.scheduleReport(reportMock);

        // Than
        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(Job.class), eq(new String[]{"/jobs"}), any(JobValidationErrorHandler.class));
        verify(jobRequestMock, times(1)).put(reportMock);
        verify(jobRequestMock, times(1)).setContentType("application/job+XML");
        verify(jobRequestMock, times(1)).setAccept("application/job+XML");
    }

    @Test(testName = "scheduleReport_for_v4_7_0")
    public void should_return_proper_OperationResult_when_JRS_version_is_v4_7_0() {

        // Given
        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(Job.class), eq(new String[]{"/jobs"}), any(JobValidationErrorHandler.class))).thenReturn(jobRequestMock);
        when(jobRequestMock.put(reportMock)).thenReturn(expectedJobOperationResultMock);
        when(sessionStorageMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getJrsVersion()).thenReturn(JRSVersion.v4_7_0);
        when(configurationMock.getContentMimeType()).thenReturn(MimeType.JSON);
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));

        // When
        OperationResult<Job> retrieved = serviceSpy.scheduleReport(reportMock);

        // Than
        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(Job.class), eq(new String[]{"/jobs"}), any(JobValidationErrorHandler.class));
        verify(jobRequestMock, times(1)).put(reportMock);
        verify(jobRequestMock, times(1)).setContentType("application/job+json");
        verify(jobRequestMock, times(1)).setAccept("application/job+json");

        assertNotNull(retrieved);
        assertSame(retrieved, expectedJobOperationResultMock);
    }

    @Test(testName = "calendars")
    public void should_return_proper_op_result_object() {

        // Given
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        doReturn(expectedWrapperOperationResultMock).when(serviceSpy).calendars(null);

        // When
        OperationResult<CalendarNameListWrapper> retrieved = serviceSpy.calendars();

        // Than
        verify(serviceSpy, times(1)).calendars();
        verify(serviceSpy, times(1)).calendars(null);
        verify(serviceSpy, never()).calendars(CalendarType.holiday);
        verifyNoMoreInteractions(serviceSpy);

        assertSame(retrieved, expectedWrapperOperationResultMock);
    }

    @Test(testName = "asyncCalendars")
    public void should_return_RequestExecution_with_CalendarNameListWrapper_instance() {

        // Given
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        doReturn(executionMock).when(serviceSpy).asyncCalendars(null, callbackMock);

        // When
        RequestExecution retrieved = serviceSpy.asyncCalendars(callbackMock);

        // Than
        verify(serviceSpy, times(1)).asyncCalendars(null, callbackMock);
        verify(serviceSpy, never()).asyncCalendars(CalendarType.holiday, callbackMock);

        assertSame(retrieved, executionMock);
    }

    @Test(testName = "calendars_with_param")
    public void should_return_proper_op_result_object_when_param_is_not_null() {

        // Given
        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(CalendarNameListWrapper.class), eq(new String[]{"/jobs", "/calendars"}))).thenReturn(wrapperRequestMock);
        when(wrapperRequestMock.get()).thenReturn(expectedWrapperOperationResultMock);
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));

        // When
        OperationResult<CalendarNameListWrapper> retrieved = serviceSpy.calendars(CalendarType.daily);

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, expectedWrapperOperationResultMock);

        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(CalendarNameListWrapper.class), eq(new String[]{"/jobs", "/calendars"}));

        verify(wrapperRequestMock, times(1)).get();
        verify(wrapperRequestMock, times(1)).addParam("calendarType", CalendarType.daily.toString().toLowerCase());
    }


    @Test(testName = "calendars_with_param")
    public void should_return_proper_op_result_object_when_param_is_null() {

        // Given
        mockStatic(JerseyRequest.class);
        when(JerseyRequest.buildRequest(eq(sessionStorageMock), eq(CalendarNameListWrapper.class), eq(new String[]{"/jobs", "/calendars"}))).thenReturn(wrapperRequestMock);
        when(wrapperRequestMock.get()).thenReturn(expectedWrapperOperationResultMock);
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));

        // When
        OperationResult<CalendarNameListWrapper> retrieved = serviceSpy.calendars(null);

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, expectedWrapperOperationResultMock);

        verifyStatic(times(1));
        JerseyRequest.buildRequest(eq(sessionStorageMock), eq(CalendarNameListWrapper.class), eq(new String[]{"/jobs", "/calendars"}));

        verify(wrapperRequestMock, times(1)).get();
        verify(wrapperRequestMock, never()).addParam("calendarType", CalendarType.daily.toString().toLowerCase());
    }


    @Test(testName = "calendar")
    public void should_return_an_calendar_adapter() throws Exception {

        // Given
        final String calendarName = "testCalendar";
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        whenNew(SingleCalendarOperationsAdapter.class)
                .withParameterTypes(SessionStorage.class, String.class)
                .withArguments(sessionStorageMock, calendarName)
                .thenReturn(expectedCalendarOperationsAdapterMock);

        // When
        SingleCalendarOperationsAdapter retrieved = serviceSpy.calendar(calendarName);

        // Than
        assertNotNull(retrieved);
        assertSame(retrieved, expectedCalendarOperationsAdapterMock);
    }

    @Test(testName = "calendar", expectedExceptions = IllegalArgumentException.class)
    public void should_throw_an_exception_when_invalid_calendar_name() throws Exception {
        JobsService serviceSpy = spy(new JobsService(sessionStorageMock));
        serviceSpy.calendar("");
    }

    @AfterMethod
    public void after() {
        reset(sessionStorageMock, expectedAdapterMock, expectedJobOperationsAdapter,
                jobRequestMock, expectedJobOperationResultMock, reportMock, configurationMock);
    }
}