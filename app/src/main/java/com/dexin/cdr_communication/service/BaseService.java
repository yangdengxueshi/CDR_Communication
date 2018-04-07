package com.dexin.cdr_communication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import java.text.MessageFormat;

/**
 * BaseService
 */
public class BaseService extends Service {
    protected final String TAG = MessageFormat.format("TAG_{0}", getClass().getSimpleName());

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
