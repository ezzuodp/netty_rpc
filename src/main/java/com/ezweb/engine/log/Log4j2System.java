package com.ezweb.engine.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author : zuodp
 * @version : 1.10
 */
public class Log4j2System {
	private String name = null;

	public Log4j2System(String name) {
		this.name = name;
	}

	public void init(Properties other) {
		if (other != null) System.setProperties(other);
		System.setProperty("applicationName", this.name);

		loadConfiguration(this.name + "-log4j2.xml");
	}

	protected void loadConfiguration(String location) {
		try {
			LoggerContext ctx = getLoggerContext();
			URL url = ClassLoader.getSystemClassLoader().getResource(location);
			ConfigurationSource source = getConfigurationSource(url);
			ctx.start(ConfigurationFactory.getInstance().getConfiguration(ctx, source));
		} catch (Exception ex) {
			throw new IllegalStateException(
					"Could not initialize Log4J2 logging from " + location, ex);
		}
	}

	private ConfigurationSource getConfigurationSource(URL url) throws IOException {
		InputStream stream = url.openStream();
		/*if ("file".equals(url.getProtocol())) {
			return new ConfigurationSource(stream, ResourceUtils.getFile(url));
		}*/
		return new ConfigurationSource(stream, url);
	}

	private LoggerContext getLoggerContext() {
		return (LoggerContext) LogManager.getContext(false);
	}
}
