import org.junit.Test;

public class LoggerTest {
	@Test
    public void testLogger() throws Exception {
		JobLoggerSolution myTest = new JobLoggerSolution(true,true,true,true,true,true,"//Users//Guest//Public");

		myTest.LogMessage("this is a test", true, false, true);

    }
}
