package com.estsoft.api.config;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Config {
    public static final Scheduler APPLICATION_SCHEDULER = Schedulers.elastic();
}
