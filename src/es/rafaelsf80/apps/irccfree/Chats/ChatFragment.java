package es.rafaelsf80.apps.irccfree.Chats;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import es.rafaelsf80.apps.irccfree.IRCHelper;
import es.rafaelsf80.apps.irccfree.MyRunnable;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.MyMessage;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService;


public class ChatFragment extends ListFragment {

	private final String TAG = getClass().getSimpleName();
	public static final String ARG_OBJECT = "object";

    private static ArrayList<MyMessage> mMessages;
    
    private LinearLayout llStatusMessage;
	private TextView tvStatusMessage;
    private Button btCloseStatusMessage;
    
    Button btSend, btPopupMenu, btDCCSend, btWho;
    LinearLayout llPopupMenuParent, llPopupMenu;
    Server server;
    Integer key1;
    BubbleAdapter adapter;
    View rootView;
    
    ViewGroup container2;
    LayoutInflater inflater2;
    
    private AdView adView;
    LinearLayout llAdv;
    
    Activity activity = getActivity();
    
    public interface IChatFragment {
    	void itemClick(Server s);
    }
    
    private static IChatFragment mIService;
    
    public static void regListener(IChatFragment iTabConnect) {
    	mIService = iTabConnect;
    }
    
    public static void unregisterListener () {
		mIService = null;
	}
 
       
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
//    	if (activity instanceof myListener)
//    		listener = (myListener) activity;
//    	
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	final LayoutInflater inflater1 = inflater;
    	inflater2 = inflater;
    	final ViewGroup container1 = container;
    	container2 = container;
        rootView = inflater.inflate(R.layout.chat_fragment, container, false);
        
        llStatusMessage = (LinearLayout) rootView.findViewById(R.id.llStatusMessage);
        tvStatusMessage = (TextView) rootView.findViewById(R.id.tvStatusMessage);
        btCloseStatusMessage = (Button) rootView.findViewById(R.id.btCloseStatusMessage);
        llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));  // Do not use llStatusMessage.setVisibility(View.GONE); 
        

		llAdv = (LinearLayout) rootView.findViewById (R.id.lladv);
		adView = (AdView) rootView.findViewById(R.id.adView);
		llAdv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));  // Do not use llStatusMessage.setVisibility(View.GONE);
		
		Bundle args = getArguments();
        final EditText etUserText = (EditText) rootView.findViewById(R.id.etUserText);
        btSend = (Button) rootView.findViewById(R.id.btSend);
        llPopupMenuParent = (LinearLayout) rootView.findViewById(R.id.llPopupMenuParent);
        
        btDCCSend = (Button) rootView.findViewById(R.id.btDCCSendFile);
        btWho = (Button) rootView.findViewById(R.id.btWho);
        btPopupMenu = (Button) rootView.findViewById(R.id.btPopupMenu);
        server = args.getParcelable(IRCHelper.SERVER); 
        key1 = args.getInt(IRCHelper.KEY);
        server = MasterArray.findServerByKey(key1);
        
    	SparseArray<Channel> ch = server.getChannelArray();
		mMessages = ch.get(key1).getMessageArray();
		
		adapter = new BubbleAdapter(getActivity(), server, key1);
        setListAdapter(adapter);
      		
        updateUI();
        
        btCloseStatusMessage.setOnClickListener(new View.OnClickListener() {

        	@Override
        	public void onClick(View v) {

        		llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0)); //Do not use llStatusMessage.setVisibility(View.GONE);
        		server.setStatusMessage("");
        	}
        });
        
        /* Listener for Send Button */
        btSend.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {

        		String channelName = MasterArray.findChannelNameByKey(key1);
        		String userInput = etUserText.getText().toString();
        		if (userInput.startsWith("/"))
        			ConnectionService.sendIRCCommand(server, userInput); 						
        		else
        			ConnectionService.sendIRCChat(server, channelName, userInput);
        		etUserText.getEditableText().clear(); 

        	}
        });
        
        /* Listener for PopupMenu Button */
        btPopupMenu.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {

        		final View child = inflater1.inflate(R.layout.chat_popupmenu, container1, false);
        		llPopupMenuParent.addView(child);


        		btDCCSend = (Button) child.findViewById(R.id.btDCCSendFile);
        		btWho = (Button) child.findViewById(R.id.btWho);

        		btDCCSend.setOnClickListener(new OnClickListener() {

        			@Override
        			public void onClick(View v) {



        				llPopupMenuParent.removeView(child);

        			}
        		});
        		btWho.setOnClickListener(new OnClickListener() {

        			@Override
        			public void onClick(View v) {
        				llPopupMenuParent.removeView(child);

        			}
        		});
        	
        	
        	//llPopupMenu.setVisibility(View.VISIBLE);
//				if (mIService != null) {
//	        		
//	        		mIService.itemClick(server);
//        		}
				
			}
		});
        
        
        
        
       
        
       
        
        
        /* Listener for EditText (if Enter pressed)  */
        TextView.OnEditorActionListener exampleListener = 
    			new TextView.OnEditorActionListener(){
    			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { 
    				if (actionId == EditorInfo.IME_NULL && 
    						event.getAction() == KeyEvent.ACTION_DOWN) { 
   					
    					//server.sync
    					String channelName = MasterArray.findChannelNameByKey(key1);
    					String userInput = ((EditText) v).getText().toString();
    					if (userInput.startsWith("/"))
    						ConnectionService.sendIRCCommand(server, userInput); 						
    					else
    						ConnectionService.sendIRCChat(server, channelName, userInput);
    					etUserText.getEditableText().clear(); 
    				} 
    				return true; 
    				} 
    			};
    		etUserText.setOnEditorActionListener(exampleListener);
    		 
         return rootView;
    }
    
    public String getPageTitle() {
    	return (server.getChannelArray()).get(key1).getName(); 
    	
    }
    
    public void showAd() {
    	
    	/* Shows Ad only if no status message */
    	if ((llStatusMessage.getMeasuredHeight() == 0)  &&
    			(llAdv.getMeasuredHeight() == 0)){

    		AdRequest request = new AdRequest();
    		adView.loadAd(request);
    		Log.d(TAG, "Loading Ad");
    		llAdv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 10));
    		/* Wait 5 second before making layout invisible */
    		Log.d(TAG, "Removing Ad after 5 seconds");
    		Handler handler = new Handler(); 
    		handler.postDelayed(new MyRunnable(server) { 
    			public void run() {		

    				llAdv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0)); 
    				llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));

    			} 
    		}, IRCHelper.TIMER_ACTIVE_AD);
    	}
    	
    }
    
    
    public void updateUI() {
    	
    	String uiText = "Status: " + String.valueOf(server.getStatus()) + " Key: " + String.valueOf(key1) + " " + server.getChannelArray().get(key1);
    	SparseArray<Channel> ch = server.getChannelArray();
    	ArrayList<MyMessage> messages = ch.get(key1).getMessageArray();
    	    	
        adapter.notifyDataSetChanged();
        
        Log.d(TAG, "SIZE: "+String.valueOf(messages.size()));
    	//getListView().setSelection(messages.size()-1);  // ESTO PETA
    	Log.d(TAG, "Fragment - UI updated");
    	Log.d(TAG, uiText);
    	
    	/* Shows status message (and hide)*/  	
    	if ((server.getStatus() != Server.DISCONNECTED) &&
    			(server.getStatus() != Server.CONNECTING) &&
    			(server.getStatusMessage() != null)) {
    		if (server.getStatusMessage().compareTo("") != 0) { 		
    			llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    			tvStatusMessage.setText( server.getStatusMessage() );
    			
    			/* Wait 5 second before making layout invisible */
    				Log.d(TAG, "Removing after 5 seconds");
    				Handler handler = new Handler(); 
    				handler.postDelayed(new MyRunnable(server) { 
    					public void run() {
    						
    						llStatusMessage.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));
    						server.setStatusMessage("");
    					} 
    				} , IRCHelper.TIMER_STATUS_MESSAGE);
    		}	
    	}
    	
    
			
    	
//    	if (server.isDccOfferReceived()) {
//    		
//    		new AlertDialog.Builder(getActivity())
//    	    .setTitle("DCC Download")
//    	    .setMessage("Are you sure you want to download " + server.getDccFileName() + " ?")
//    	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//    	        public void onClick(DialogInterface dialog, int which) { 
//    	        	DownloadFileTask task = new DownloadFileTask( );
//    				task.setContext( getActivity() );
//    				task.execute( new String[]{server.getDccIp(), String.valueOf(server.getDccPort()), server.getDccFileName(), String.valueOf(server.getDccFileSize())} );
//    				server.setDccOfferReceived(false);
//    	        }
//    	     })
//    	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//    	        public void onClick(DialogInterface dialog, int which) { 
//    	            // do nothing
//    	        }
//    	     })
//    	     .show();
// 		}


    }
   
}