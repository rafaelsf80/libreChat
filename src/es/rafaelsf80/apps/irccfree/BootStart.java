package es.rafaelsf80.apps.irccfree;

import es.rafaelsf80.apps.irccfree.Service.ConnectionService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		Intent intent = new Intent(ctx, ConnectionService.class);
		ctx.startService(intent);
		Log.i("Main", "BOOT_COMPLETED intent filter received");		
	}
}
