package com.ginkgocap.parasol.sensitive.web.jetty.autoconfig;

import com.ginkgocap.parasol.sensitive.web.jetty.web.error.SensitiveErrorControl;

//@Configuration
public class ErrorControlAutoConfig {
	
	//@Bean
	public SensitiveErrorControl createMetadataErrorControl() {
		return new SensitiveErrorControl();
	}
}
