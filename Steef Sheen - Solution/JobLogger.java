import java.io.File;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.Statement;

import java.text.DateFormat;

import java.util.Date;

import java.util.Map;

import java.util.Properties;

import java.util.logging.ConsoleHandler;

import java.util.logging.FileHandler;

import java.util.logging.Level;

import java.util.logging.Logger;


public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private boolean initialized;
	private static Map dbParams;
	private static Logger logger;
	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam, boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLog");
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}

	public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception { // We need to remove the static keword cause I think we need to have first the class initialized first 
		
		messageText.trim(); // wrong, if the mssageText variable comes with null value, an exception will appear.
		if (messageText == null || messageText.length() == 0) {
			return;
		}

		if (!logToConsole && !logToFile && !logToDatabase) { 
			throw new Exception("Invalid configuration");
		}

		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) { // We don't need the (!logError && !logMessage && !logWarning) if parameter 'cause we already have asked for it in line 54
			throw new Exception("Error or Warning or Message must be specified");
		}

		
		/**We can move the whole Connection logic to a new class named for example ConnectionManager for example
		 * also there is no need to create a connection if we will not use it*/
		Connection connection = null;
		Properties connectionProps = new Properties(); 
		connectionProps.put("user", dbParams.get("userName")); 
		connectionProps.put("password", dbParams.get("password"));
		connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName") +	":" + dbParams.get("portNumber") + "/", connectionProps);
		
		int t = 0;// bad variable name, the name doesn't give us any clue of what it does.
		if (message && logMessage) {
			t = 1;
		}
		if (error && logError) {
			t = 2;
		}
		if (warning && logWarning) {
			t = 3;
		}
		Statement stmt = connection.createStatement();
		String l = null; // wrong, If we want to initialize an string variable to be used as a buffer, it is better to use an stringbuffer which is faster.
		// bad variable name, the name doesn't give us any clue of what it does.
		
		//We don't need to add the logic for files if we wil not use this feature 
		File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt"); //Paths must contain two / in order to work.
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt"); //Paths must contain two / in order to work.
		
		
		//We don't need to add the logic for console if we wil not use this feature 
		ConsoleHandler ch = new ConsoleHandler();

		
		// we can have a provate method to get the message instead of repeat the code 3 times 
		if (error && logError) {// this if is repeated, we can use the same if of line 72
			l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText; //null + some string = nullsome string, cause we initialize our l variable with null 
		}
		if (warning && logWarning) { // this if is repeated, we can use the same if of line 75
			l = l + "warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText; //null + some string = nullsome string, cause we initialize our l variable with null 
		}
		if (message && logMessage) { // this if is repeated, we can use the same if of line 78
			l = l + "message " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText; //nul + some string = nullsome string, cause we initialize our l variable with null 
		}
		if(logToFile) {
			logger.addHandler(fh);
			logger.log(Level.INFO, messageText); // I think you want to save the l word 
		}
		if(logToConsole) {
			logger.addHandler(ch);
			logger.log(Level.INFO, messageText);
		}
		if(logToDatabase) {
			stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) +")"); //Im not sure here, i think we wan't to add the messageText or the l variable, not the message whirh is a bol
		}
	}

}
