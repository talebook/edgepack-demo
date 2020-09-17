package com.meituan.android.walle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ChannelReader {
    public static final String CHANNEL_KEY = "channel";
    public static final String CDN_MAGIC_KEY = "APK Sig Block 42";

    private ChannelReader() {
        super();
    }

    /**
     * easy api for get channel & extra info.<br/>
     *
     * @param apkFile apk file
     * @return null if not found
     */
    public static ChannelInfo get(final File apkFile) {
        final Map<String, String> result = getMap(apkFile);
        if (result == null) {
            return null;
        }
        final String channel = result.get(CHANNEL_KEY);
        result.remove(CHANNEL_KEY);
        return new ChannelInfo(channel, result);
    }

    public static String getString(final File apkFile)  {
        final String rawString = getRaw(apkFile);
        if (rawString == null) {
            return null;
        }
        if ( ! rawString.startsWith(CDN_MAGIC_KEY) ) {
            return null;
        }
        final String dataString = rawString.substring(CDN_MAGIC_KEY.length()).trim();
        try {
            return URLDecoder.decode(dataString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return dataString;
        }
    }

    /**
     * get channel & extra info by map, use {@link ChannelReader#CHANNEL_KEY PayloadReader.CHANNEL_KEY} get channel
     *
     * @param apkFile apk file
     * @return null if not found
     */
    public static Map<String, String> getMap(final File apkFile) {
        try {
            final String rawString = getRaw(apkFile);
            if (rawString == null) {
                return null;
            }
            if ( ! rawString.startsWith(CDN_MAGIC_KEY) ) {
                return null;
            }
            final String jsonString = URLDecoder.decode(rawString.substring(CDN_MAGIC_KEY.length()).trim(), "UTF-8");
            final JSONObject jsonObject = new JSONObject(jsonString);
            final Iterator keys = jsonObject.keys();
            final Map<String, String> result = new HashMap<String, String>();
            while (keys.hasNext()) {
                final String key = keys.next().toString();
                result.put(key, jsonObject.getString(key));
            }
            return result;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get raw string from channel id
     *
     * @param apkFile apk file
     * @return null if not found
     */
    public static String getRaw(final File apkFile) {
        return  PayloadReader.getString(apkFile, ApkUtil.APK_CHANNEL_BLOCK_ID);
    }
}
