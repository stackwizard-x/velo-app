package com.velo.player;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.PluginMethod;

@CapacitorPlugin(
    name = "VeloMedia",
    permissions = {
        @Permission(alias = "audio", strings = { Manifest.permission.READ_MEDIA_AUDIO }),
        @Permission(alias = "storage", strings = { Manifest.permission.READ_EXTERNAL_STORAGE })
    }
)
public class VeloMedia extends Plugin {

    private String alias() {
        return Build.VERSION.SDK_INT >= 33 ? "audio" : "storage";
    }

    private JSObject granted(boolean g) {
        JSObject o = new JSObject();
        o.put("granted", g);
        return o;
    }

    @PluginMethod
    public void ask(PluginCall call) {
        if (getPermissionState(alias()) == PermissionState.GRANTED) {
            call.resolve(granted(true));
        } else {
            requestPermissionForAlias(alias(), call, "askDone");
        }
    }

    @PermissionCallback
    private void askDone(PluginCall call) {
        call.resolve(granted(getPermissionState(alias()) == PermissionState.GRANTED));
    }

    @PluginMethod
    public void scan(PluginCall call) {
        try {
            JSArray arr = new JSArray();
            String[] cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
            };
            String sel = MediaStore.Audio.Media.IS_RINGTONE + "=0 AND "
                + MediaStore.Audio.Media.IS_NOTIFICATION + "=0 AND "
                + MediaStore.Audio.Media.IS_ALARM + "=0";
            Cursor c = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, sel, null,
                MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
            if (c != null) {
                while (c.moveToNext()) {
                    String path = c.getString(3);
                    if (path == null) continue;
                    JSObject o = new JSObject();
                    o.put("id", c.getLong(0));
                    o.put("title", c.getString(1));
                    o.put("dur", c.getLong(2) / 1000);
                    o.put("path", path);
                    arr.put(o);
                }
                c.close();
            }
            JSObject ret = new JSObject();
            ret.put("songs", arr);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }
}
