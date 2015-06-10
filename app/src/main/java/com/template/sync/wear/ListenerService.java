package com.template.sync.wear;

import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.template.sync.wear.common.Constants;

/**
 * Created by anelson on 5/12/15.
 */
public class ListenerService extends WearableListenerService {

    private LocalBroadcastManager broadcaster;
    private GoogleApiClient mApiClient;

    public ListenerService() {
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
        String messageData = new String(messageEvent.getData());
        showToast(messageData);

        String fullMessage = new String(messageEvent.getData());
        String header = fullMessage.substring(0, 1) + fullMessage.substring(1, 2) + fullMessage.substring(2, 3);
        String message = fullMessage.substring(3, fullMessage.length());

        switch(header) {
            case Constants.CMD_GET_OPT_1:
                //user requested information for option 1
                //download, calculate, etc stuff for option 1
                //and send back data
                break;
            case Constants.CMD_GET_OPT_2:
                //user requested information for option 2
                //download, calculate, etc stuff for option 2
                //and send back data
                break;
            case Constants.CMD_GET_OPT_3:
                //user requested information for option 3
                //download, calculate, etc stuff for option 2
                //and send back data
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, "Message received from wear (service)", Toast.LENGTH_LONG).show();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    public void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

}
