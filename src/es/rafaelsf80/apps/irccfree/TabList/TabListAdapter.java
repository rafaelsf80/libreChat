package es.rafaelsf80.apps.irccfree.TabList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import es.rafaelsf80.apps.irccfree.MyRunnable;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService;

public class TabListAdapter extends BaseAdapter
{

	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	
	public TabListAdapter(Context context) {
		Log.d(TAG, "Constructor()");
		mContext = context;
	}

	@Override
	public int getCount() {
		//TODO Auto-generated method stub
		Log.d(TAG, "getCount() " + String.valueOf(MasterArray.channelSize()));
		return MasterArray.channelSize();
	}

	@Override
	public Object getItem(int position) {
		//TODO Auto-generated method stub
		return (MasterArray.getData()).get(1).get(1);
	}

	@Override
	public long getItemId(int position) {
		//TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;
		final int key = MasterArray.listToKey(position);
		Log.d(TAG, "getView("+position+") Key: "+String.valueOf(key));

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.child_channel, null);
		}
		else
			view = convertView;
		
		boolean status = false;
		String name = null;
		String topic = null;
		Server server;
		
		for (int i = 0; i < (MasterArray.getData()).size(); i++) 
		for (int j = 0; j < (MasterArray.getData()).get(i).size(); j++) {
			server = (MasterArray.getData()).get(i).get(j);
			if ((server.getStatus() != Server.DISCONNECTED) &&
				(server.getStatus() != Server.CONNECTING)) {
				if ((server.getChannelArray().get(key)) != null) {
					
					status = server.getChannelArray().get(key).isJoined();
					name = server.getChannelArray().get(key).getName();
					topic = server.getChannelArray().get(key).getTopic();
					break;
				}
			}
		}
		
		/* Channel name and topic */
		TextView tvChannelName = (TextView) view.findViewById(R.id.tvChannelName);
		tvChannelName.setText(name);
		TextView tvChannelTopic = (TextView) view.findViewById(R.id.tvChannelTopic);
		tvChannelTopic.setText(topic);
		
		/* Status of channel: true or false and show it in the CheckBox */
		final CheckBox cbChannel = (CheckBox) view.findViewById(R.id.cbChannel);
		cbChannel.setChecked(status);
		
		/* Do not use onCheckedChanged() since it is called when you say setChecked in code */
		cbChannel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/* Find status of channel: "0" or "1" and update CheckBox accordingly */
				Server server = null;
				
				for (int i = 0; i < (MasterArray.getData()).size(); i++) 
					for (int j = 0; j < (MasterArray.getData()).get(i).size(); j++) {
						server = (MasterArray.getData()).get(i).get(j);
						if  ((server.getStatus() != Server.DISCONNECTED) &&
							 (server.getStatus() != Server.CONNECTING) &&
							 (server.getStatus() != Server.LIST_SENT))  {
							
							String channelName = server.getChannelArray().get(key).getName();
							boolean status = server.getChannelArray().get(key).isJoined();
							if (channelName != null) {
								if (!status) {
									ConnectionService.sendIRCCommand(server, "/JOIN "+channelName);
									/* Wait 1 second before updating sw and array */
									Handler handler = new Handler(); 
									handler.postDelayed(new MyRunnable(server) { 
										public void run() {
											server.sync();
											Channel ch = server.getChannelArray().get(key);
											if (server.getStatus() == Server.JOINED) { 
												ch.setJoined(true);
												cbChannel.setChecked(true);
												
												//server.setChannelArray(ch);
												MasterArray.sync(server);
												Log.d(TAG, "onClickd() " + server.getChannelArray().get(key).getName()  + " to true");
											} else  {
												Log.d(TAG, "Not possible to join in server: " + String.valueOf(server.getStatus()) + " " + server);
												Toast.makeText(mContext, 
														String.format( ((Activity) mContext).getResources().getString(R.string.tab_list_error_joining, server.getChannelArray().get(key).getName())), 
														Toast.LENGTH_SHORT).show();
											}
															
										} 
									} ,1000);	
								} else  {
									ConnectionService.sendIRCCommand(server, "/LEAVE "+channelName);
									/* Wait 1 second before updating sw and array */
									Handler handler = new Handler(); 
									handler.postDelayed(new MyRunnable(server) { 
										public void run() {
											server.sync();
											//SparseBooleanArray sb = server.getChannelStatus();
											Channel ch = server.getChannelArray().get(key);
											if (server.getStatus() != Server.LEAVE_SENT) { 
												ch.setJoined(false);
												cbChannel.setChecked(false);
												Log.d(TAG, "onClickd() " + server.getChannelArray().get(key).getName()  + " to false");
												
												//server.setChannelStatus(sb);
												MasterArray.sync(server);
											}
											else  {
												Log.d(TAG, "Not possible to leave");
												Toast.makeText(mContext, 
														String.format( ((Activity) mContext).getResources().getString(R.string.tab_list_error_leaving, server.getChannelArray().get(key).getName())), 
														Toast.LENGTH_SHORT).show();
											}						
										} 
									} ,1000);
								}
								break;
							}
						}
					}
			}
		});	
		return view;
	}
}
