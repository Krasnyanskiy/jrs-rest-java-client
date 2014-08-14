package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.reportparameters;

import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link ReportParametersConverter}
 */
@PrepareForTest({ReportParametersConverter.class})
public class ReportParametersConverterTest extends PowerMockTestCase {

    @Test
    public void should_convert_InputControlStates_into_map() {

        /* Given */
        List<InputControlState> inputControlsIds = new ArrayList<InputControlState>() {{
            add(new InputControlState().setId("id1"));
            add(new InputControlState().setId("id2"));
        }};

        /* When */
        Map<String, Object> retrieved = ReportParametersConverter.getValueMapFromInputControlStates(inputControlsIds);

        /* Than */
        Assert.assertNotNull(retrieved);
        assertTrue(retrieved.size() == 2);
    }

    @Test
    public void should_convert_InputControlStates_into_map_when_InputControlStates_has_values() {

        /* Given */
        List<InputControlState> inputControlsIds = new ArrayList<InputControlState>() {{
            add(new InputControlState().setId("id1").setValue("value1"));
            add(new InputControlState().setId("id2").setValue("value2"));
        }};

        /* When */
        Map<String, Object> retrieved = ReportParametersConverter.getValueMapFromInputControlStates(inputControlsIds);

        /* Than */
        Assert.assertNotNull(retrieved);
        assertTrue(retrieved.size() == 2);
    }

    @Test
    public void should_return_proper_ReportParameters() {

        /* Given */
        List<InputControlState> inputControlsIds = new ArrayList<InputControlState>() {{
            add(new InputControlState().setId("id1").setValue("value1"));
            add(new InputControlState().setId("id2").setValue("value2"));
        }};

        /* When */
        ReportParameters retrieved = ReportParametersConverter.toReportParameters(inputControlsIds);

        /* Than */
        assertNotNull(retrieved);
        assertTrue(retrieved.getReportParameters().size() == 2);
    }
}