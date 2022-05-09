package com.plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.configuration.fields.DropdownField;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.common.collect.ImmutableMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class HumioHttp implements MessageOutput {

	private boolean shutdown;
	private String url;
	private String token;
	private static final String CK_INGEST_TOKEN = "output_token";
	private static final String CK_HUMIO_SUBDOMAIN = "output_subdomain";
	private static final Logger LOG = LoggerFactory.getLogger(HumioHttp.class);

	@Inject
	public HumioHttp(@Assisted Stream stream, @Assisted Configuration conf) throws HumioHttpException {

		url = conf.getString(CK_HUMIO_SUBDOMAIN);
		token = conf.getString(CK_INGEST_TOKEN);
		shutdown = false;
		LOG.info(" Humio Http Plugin has been configured with the following parameters:");
		LOG.info(token + " : " + url);

		try {
            final URL urlTest = new URL(url);
        } catch (MalformedURLException e) {
        	LOG.info("Error in the given API", e);
            throw new HumioHttpException("Error while constructing the API.", e);
        }
	}

	@Override
	public boolean isRunning() {
		return !shutdown;
	}

	@Override
	public void stop() {
		shutdown = true;

	}

	@Override
	public void write(List<Message> msgs) throws Exception {
		for (Message msg : msgs) {
			writeBuffer(msg.getFields());
		}
	}

	@Override
	public void write(Message msg) throws Exception {
		if (shutdown) {
			return;
		}

		writeBuffer(msg.getFields());
	}

	public void writeBuffer(Map<String, Object> data) throws HumioHttpException {
		OkHttpClient client = new OkHttpClient();
		Gson gson = new Gson();

		try {
			final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(JSON, gson.toJson(data));
			Request request = new Request.Builder().url(url)
			.addHeader("AUTHORIZATION", "BEARER" + " " + token)
			.post(body).build();
			Response response = client.newCall(request).execute();
			response.close();
			if (response.code() != 200) {
				LOG.info("Unexpected HTTP response status " + response.code());
				throw new HumioHttpException("Unexpected HTTP response status " + response.code());
			}
		} catch (IOException e) {
			LOG.info("url: "  + url);
			LOG.info("token: " + token);
			LOG.info("Error while posting the stream data to the given API", e);
            throw new HumioHttpException("Error while posting stream to HTTP.", e);
		}

	}

	public interface Factory extends MessageOutput.Factory<HumioHttp> {
		@Override
		HumioHttp create(Stream stream, Configuration configuration);

		@Override
		Config getConfig();

		@Override
		Descriptor getDescriptor();
	}

	public static class Descriptor extends MessageOutput.Descriptor {
		public Descriptor() {
			super("HumioHumio Http", false, "", "Forwards stream to HTTP.");
		}
	}

	public static class Config extends MessageOutput.Config {
		@Override
		public ConfigurationRequest getRequestedConfiguration() {
			 final Map<String, String> subdomains = ImmutableMap.of(
        "https://cloud.us.humio.com/api/v1/ingest/raw/", "US",
        "https://cloud.humio.com/api/v1/ingest/raw/", "EU",
        "https://cloud.au.humio.com/api/v1/ingest/raw/", "AU");
			final ConfigurationRequest configurationRequest = new ConfigurationRequest();
			configurationRequest.addField(new DropdownField(CK_HUMIO_SUBDOMAIN, "Subdomain", "", subdomains, "Humio Cloud Subdomain", ConfigurationField.Optional.NOT_OPTIONAL));
			configurationRequest.addField(new TextField(CK_INGEST_TOKEN, "Humio Ingest Token", "-",
					"https://library.humio.com/cloud/docs/ingesting-data/ingest-tokens/", ConfigurationField.Optional.NOT_OPTIONAL));

			return configurationRequest;
		}
	}

	public class HumioHttpException extends Exception {

		private static final long serialVersionUID = -5301266791901423492L;

		public HumioHttpException(String msg) {
            super(msg);
        }

        public HumioHttpException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }
}