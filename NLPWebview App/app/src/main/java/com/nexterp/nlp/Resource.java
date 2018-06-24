package com.nexterp.nlp;

import java.util.UUID;

/**
 * Created by jayprakashk on 04-05-2017.
 */

public class Resource {



    private UUID drmSchemeUuid;
    private  String drmLicenseUrl;
    private  String drmKeyRequestProperties;
    private  String extension;
    private  String uri;

    public String getDrmLicenseUrl() {
        return drmLicenseUrl;
    }

    public void setDrmLicenseUrl(String drmLicenseUrl) {
        this.drmLicenseUrl = drmLicenseUrl;
    }

    public String getDrmKeyRequestProperties() {
        return drmKeyRequestProperties;
    }

    public void setDrmKeyRequestProperties(String drmKeyRequestProperties) {
        this.drmKeyRequestProperties = drmKeyRequestProperties;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public UUID getDrmSchemeUuid() {
        return drmSchemeUuid;
    }


    public void setDrmSchemeUuid(UUID drmSchemeUuid) {
        this.drmSchemeUuid = drmSchemeUuid;
    }
}
