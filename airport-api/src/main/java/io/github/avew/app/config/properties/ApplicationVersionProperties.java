package io.github.avew.app.config.properties;

public class ApplicationVersionProperties {
    private String name = "Airport";
    private String version = "0.0.1-SNAPSHOT";
    private String timestamp = "";
    private String url = "";

    public ApplicationVersionProperties() {
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getUrl() {
        return this.url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
