package com.ulfric.conf4j.interpreter.locale;

import java.util.Map;

import com.ulfric.conf4j.ConfigurationBean;

public interface Messages extends ConfigurationBean {

	Map<String, String> messages();

}
