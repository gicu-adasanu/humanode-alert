package io.humanode.humanode.utils;

public class HumanUtils {
    public static final String SUCCESSFULLY_REGISTERED_MESSAGE = "You have successfully registered, you will receive alerts every day (if have enabled notifications) with time left to authenticate and a reminder alert once a minute, 5 minutes before expiry. To see commands for enable/disable notification type /help";
    public static final String UNKNOWN_COMMAND = "Unknown command, to register for receive alert about your node send command '/register', to set time zone id send command '/timezone time_zone_id'";
    public static final String MISSING_CHAT_ID = "Chat id not found, need to register, to register type '/register' in chat with bot";
    public static final String SUCCESSFULLY_REGISTERED_TIME_ZONE_ID = "You have successfully set time zone id";
    public static final String ENABLE_NOTIF = "Notifications have been enabled";
    public static final String DISABLED_NOTIF = "Notifications have been disabled";

    public static final String HELP = """
                        /register -> register your node
/timezone -> set timezone, example '/timezone Europe/Chisinau'
/enable_notification -> enable notification about node every day at noon
/disable_notification -> disable notification about node every day at noon
/get_bioauth_link -> get new auth link
/help -> list of commands
            """;
}
