package es.rafaelsf80.apps.irccfree.Chats;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.MyMessage;
import es.rafaelsf80.apps.irccfree.Data.Server;



public class BubbleAdapter extends BaseAdapter{
	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	int key;
	private AdView adView;
	private LinearLayout llHeader = null;
	Server server;

	public BubbleAdapter(Context context, Server server,  int key) {
		super();
		this.mContext = context;
		this.server = server;
		this.key = key;

	}
	
	@Override
	public int getCount() {
		
		server = MasterArray.findServerByKey(key); 
		
		SparseArray<Channel> ch = server.getChannelArray();
    	ArrayList<MyMessage> messages = ch.get(key).getMessageArray();		
		return messages.size();
	}
	@Override
	public Object getItem(int position) {		

		SparseArray<Channel> ch = server.getChannelArray();
    	ArrayList<MyMessage> messages = ch.get(key).getMessageArray();
		return messages.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		MyMessage message = (MyMessage) this.getItem(position);
		ViewHolder holder; 

		if(convertView == null)
		{
			holder = new ViewHolder();

			/* Load "ad" layout or bubble layout */
			if (message.isAdMessage()) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.ad, parent, false);
				llHeader = (LinearLayout) convertView.findViewById(R.id.lladv);

				adView = (AdView) convertView.findViewById(R.id.adView);
				AdRequest request = new AdRequest();
				adView.loadAd(request);
				Log.d(TAG, "Loading Ad");
			}
			else {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.bubble, parent, false);
				holder.message = (TextView) convertView.findViewById(R.id.message_text);
			}
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		if (message.isAdMessage()) {

			adView.setAdListener(new AdListener() { 			
				@Override
				public void onReceiveAd(Ad arg0) {

					llHeader.setVisibility(View.VISIBLE);

					Thread th = new Thread(new Runnable() {

						@Override
						public void run() {				

							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							((Activity) mContext).runOnUiThread( new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									llHeader.setVisibility(View.INVISIBLE);
								}
							});
						}
					});

					th.start();
				}


				@Override
				public void onPresentScreen(Ad arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLeaveApplication(Ad arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDismissScreen(Ad arg0) {
					// TODO Auto-generated method stub

				}
			});
		}

		if (!message.isAdMessage()) {
			if (message.getMessage() == null)
				Log.i(TAG, "HTML es null");
			String pepe = message.getMessage();
			holder.message.setText(Html.fromHtml(pepe));
			//holder.message.setText(message.getMessage());
			LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
			//check if it is a status message then remove background, and change text color.
			if(message.isStatusMessage())
			{
				holder.message.setBackgroundDrawable(null);
				lp.gravity = Gravity.LEFT;
				holder.message.setTextColor( mContext.getResources().getColor(R.color.textFieldColor) );
			}
			else
			{		
				//Check whether message is mine to show green background and align to right
				if(message.isMine())
				{
					holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
					lp.gravity = Gravity.RIGHT;
				}
				//If not mine then it is from sender to show orange background and align to left
				else
				{
					holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
					lp.gravity = Gravity.LEFT;
				}
				holder.message.setLayoutParams(lp);
				holder.message.setTextColor( mContext.getResources().getColor(R.color.textColor));	
			}
		}
		return convertView;
	}
	private  class ViewHolder
	{

		LinearLayout lladv; 
		TextView message;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return 0;
	}

}  


