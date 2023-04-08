package com.kakaouo.mochi.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class LoggerFactory implements ILoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new LoggerWrapper(name);
    }
}