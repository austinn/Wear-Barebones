package com.template.sync.wear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.template.sync.wear.common.Constants;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulruiz on 9/26/14.
 */
public class WearMessageListenerService extends WearableListenerService implements DataApi.DataListener{

    private LocalBroadcastManager broadcaster;
    private GoogleApiClient mApiClient;

    public WearMessageListenerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String fullMessage = new String(messageEvent.getData());
        Toast.makeText(getApplicationContext(), "Message received from wear (service)", Toast.LENGTH_SHORT).show();

        //I use a 3 char header to determine my message
        String header = fullMessage.substring(0, 1) + fullMessage.substring(1, 2) + fullMessage.substring(2, 3);
        String message = fullMessage.substring(3, fullMessage.length());

        //The mobile device sent back data
        //forwarding the info to the activity
        Intent intent = new Intent(Constants.RESULT);
        intent.putExtra("HEADER", header);
        intent.putExtra("MESSAGE", message);

        broadcaster.sendBroadcast(intent);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals(Constants.WEAR_MESSAGE_PATH)) {

            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mApiClient.blockingConnect(2000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mApiClient, asset).await().getInputStream();
        mApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("TAG", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

}
