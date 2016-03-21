package com.cloudwise.ajax.communicate;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class AjaxCommConfiguration extends Configuration{
	@Valid
	@NotNull
	@JsonProperty
	private Map<String, String> suroClusterConfig;

	public Map<String, String> getSuroClusterConfig() {
		return suroClusterConfig;
	}
}
