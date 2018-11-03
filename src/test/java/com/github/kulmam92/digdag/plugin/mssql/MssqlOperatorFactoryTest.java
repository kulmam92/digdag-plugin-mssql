package com.github.kulmam92.digdag.plugin.mssql;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.digdag.spi.Operator;
import io.digdag.spi.TaskRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MssqlOperatorFactoryTest
{

    private JdbcOpTestHelper testHelper = new JdbcOpTestHelper();
    private MssqlOperatorFactory operatorFactory;

    @Before
    public void setUp()
    {
        operatorFactory = testHelper.injector().getInstance(MssqlOperatorFactory.class);
        System.out.println("GetKEY");
        System.out.println(operatorFactory.getType());
    }

    @Test
    public void getKey()
    {
        assertThat(operatorFactory.getType(), is("mssql"));
    }

    @Test
    public void newOperator()
            throws IOException
    {
        Map<String, Object> configInput = ImmutableMap.of(
                "host", "foobar0.org",
                "user", "user0",
                "database", "database0"
        );
        TaskRequest taskRequest = testHelper.createTaskRequest(configInput, Optional.absent());
        Operator operator = operatorFactory.newOperator(OperatorTestingUtils.newContext(testHelper.projectPath(), taskRequest));
        assertThat(operator, is(instanceOf(MssqlOperatorFactory.MssqlOperator.class)));
    }

}
