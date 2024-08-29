package com.detisa.omicrom.bussiness;

/**
 *
 * @author rolando
 */
public interface CommandObserver {
    public void handleNotification(Command command);
}
