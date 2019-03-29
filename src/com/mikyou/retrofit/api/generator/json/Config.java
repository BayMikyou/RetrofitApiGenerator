package com.mikyou.retrofit.api.generator.json;


public class Config {

    private boolean fieldPrivateMode = true;

    private boolean useSerializedName = true;

    private String suffixStr = "";

    private boolean resuseEntity = false;

    private Config() {

    }

    private static Config config;

    public static Config getInstant() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }


    public boolean isFieldPrivateMode() {
        return fieldPrivateMode;
    }


    public boolean isUseSerializedName() {
        return useSerializedName;
    }

    public String getSuffixStr() {
        return suffixStr;
    }

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        Config.config = config;
    }



}
