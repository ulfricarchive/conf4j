package com.ulfric.conf4j.reload;

import com.ulfric.conf4j.Configuration;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PeriodicalReloadingStrategy implements ReloadingStrategy {

	private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(1, runnable -> {
		Thread thread = new Thread(runnable, "conf4j-periodical-reloading");
		thread.setDaemon(true);
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		return thread;
	});

	private final ConcurrentMap<Configuration, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>(1);

	private final long period;
	private final TimeUnit unit;

	public PeriodicalReloadingStrategy(Long period, TimeUnit unit) {
		Objects.requireNonNull(period, "period");
		if (period < 1) {
			throw new IllegalArgumentException("period must be >= 1");
		}
		Objects.requireNonNull(unit, "unit");

		this.period = period;
		this.unit = unit;
	}

	@Override
	public void register(Configuration configuration) {
		Objects.requireNonNull(configuration, "configuration");

		ScheduledFuture<?> scheduled = POOL.scheduleAtFixedRate(configuration::reload, period, period, unit);
		tasks.putIfAbsent(configuration, scheduled);
	}

	@Override
	public void remove(Configuration configuration) {
		Objects.requireNonNull(configuration, "configuration");

		ScheduledFuture<?> task = tasks.remove(configuration);
		if (task != null) {
			task.cancel(false);
		}
	}

}
