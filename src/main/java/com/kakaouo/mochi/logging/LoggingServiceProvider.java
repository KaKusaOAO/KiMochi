package com.kakaouo.mochi.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class LoggingServiceProvider implements SLF4JServiceProvider {
    @Override
    public ILoggerFactory getLoggerFactory() {
        return new LoggerFactory();
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return MarkerFactory.getIMarkerFactory();
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return new BasicMDCAdapter();
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.1";
    }

    @Override
    public void initialize() {

    }
}
