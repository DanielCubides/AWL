package dev.anzus.awlumina.util;

/**
 * Created by alej0 on 19/06/2015.
 */
public class ConnectionException extends Exception {

    public static final int WIFI_NOT_CONNECTED = 802;
    public static final int CANT_GET_ADDRESS = 404;

    private int errorCode = 0;

    public ConnectionException(String message, int ErrorCode) {
        super(message);
        errorCode = ErrorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}

