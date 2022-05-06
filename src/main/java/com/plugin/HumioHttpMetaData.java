package com.plugin;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * Implement the PluginMetaData interface here.
 */
public class HumioHttpMetaData implements PluginMetaData {
    private static final String PLUGIN_PROPERTIES = "com.plugin.graylog-plugin-humio-http/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "com.plugin.HumioHttpPlugin";
    }

    @Override
    public String getName() {
        return "Humio Raw Ingest HTTP Plugin";
    }

    @Override
    public String getAuthor() {
        return "Zalary Young <zalary@gmail.com>";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/zalary/graylog-humio-http-plugin");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public String getDescription() {
        return "Graylog plugin to post Stream data to Humio via HTTP.";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(0, 0, 0, "unknown"));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
