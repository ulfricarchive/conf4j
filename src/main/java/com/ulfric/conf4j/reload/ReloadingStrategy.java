package com.ulfric.conf4j.reload;

import com.ulfric.conf4j.Configuration;

public interface ReloadingStrategy {

	void register(Configuration configuration);

	void remove(Configuration configuration);

}
