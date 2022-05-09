# HumioHttp Plugin for Graylog

**Required Graylog version:** 2.0 and later

This Plugin has a very specific purpose:  Write the messages to the Humio Raw Ingest Endpoint, indicated in the Plugin configuration parameters.

This plugin is based extremely heavily on the [Graylog Http plugin](https://github.com/sagarinpursue/graylog-http-plugin)

Getting started
---------------

This project is using Maven 3 and requires Java 8 or higher.

Installation
------------
[Download the plugin](https://github.com/zalary`/graylog-http-plugin)

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

The plugin directory is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

Usage
-----

Once you have installed the plugin, you can configure an Output of type  com.plugin.HumioHttp by selecting which Cloud you are on, and adding an Ingest Token.   

Plugin Release
--------------

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```
