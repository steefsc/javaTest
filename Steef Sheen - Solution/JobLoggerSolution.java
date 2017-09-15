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


public class JobLoggerSolution {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private static String logFileFolder;
	private static Logger logger;
	public JobLoggerSolution(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam, boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, String logFileFolder) {
		this.logger = Logger.getLogger("MyLog");
		this.logError = logErrorParam;
		this.logMessage = logMessageParam;
		this.logWarning = logWarningParam;
		this.logToDatabase = logToDatabaseParam;
		this.logToFile = logToFileParam;
		this.logToConsole = logToConsoleParam;
		this.logFileFolder = logFileFolder;
	}

	public void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
		
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		messageText.trim();

		if (!logToConsole && !logToFile && !logToDatabase) { 
			throw new Exception("Invalid configuration");
		}

		if ((!message && !warning && !error)) { 
			throw new Exception("Error or Warning or Message must be specified");
		}

		int type = 0;
		String logginMessage = null;
		if (message && logMessage) {
			type = 1;
			logginMessage = getMessage("message",messageText);
		}
		if (error && logError) {
			type = 2;
			logginMessage = getMessage("error",messageText);
		}
		if (warning && logWarning) {
			type = 3;
			logginMessage = getMessage("error",messageText);
		}

		if(logToFile) {
			if (logFileFolder != null) {
				File logFile = new File(logFileFolder + "//logFile.txt"); //Paths must contain two / in order to work.
				if (!logFile.exists()) {
					logFile.createNewFile();
				}
				FileHandler fh = new FileHandler(logFileFolder + "//logFile.txt");
				logger.addHandler(fh);
				logger.log(Level.INFO, logginMessage);
			} else {
				throw new Exception("File folder not defined");
			}
		}
		if(logToConsole) {
			ConsoleHandler ch = new ConsoleHandler();
			logger.addHandler(ch);
			logger.log(Level.INFO, logginMessage);
		}
		if(logToDatabase) {
			Connection con = ConnectionManager.getConnection();
			Statement stmt = con.createStatement();
			stmt.executeQuery("insert into Log_Values('" + logginMessage + "', " + String.valueOf(type) +")");
			con.close();
		}
	}


	private static String getMessage(String string, String messageText) {
		// TODO Auto-generated method stub
		StringBuffer stb = new StringBuffer();
		stb.append(string).append(" ").append(DateFormat.getDateInstance(DateFormat.LONG).format(new Date())).append(" ").append(messageText);
		return stb.toString();
	}

}
