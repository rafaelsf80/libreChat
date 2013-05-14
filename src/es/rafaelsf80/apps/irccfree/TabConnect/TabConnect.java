package es.rafaelsf80.apps.irccfree.TabConnect;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import es.rafaelsf80.apps.irccfree.IRCHelper;
import es.rafaelsf80.apps.irccfree.MyRunnable;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;



/**
 * Fragment for /connect tab
 */
public class TabConnect extends Fragment {

    private final String TAG = getClass().getSimpleName();
	public static final String ARG_SECTION_NUMBER = "section_number";

    
    public TabConnectAdapter mAdapter;
   
    private Button btAddNewServer;
	private ExpandableListView listView;
	private LinearLayout llStatusMessage;
	private TextView tvStatusMessage;
    private Button btCloseStatusMessage;
    
    private boolean can_show = true;
    
    Handler mHandler;
    
    public interface ITabConnect {
    	void itemClick(Server s);
    }
    
    private static ITabConnect mIService;
    
    public static void regListener(ITabConnect iTabConnect) {
    	mIService = iTabConnect;
    }
    
    public static void unregisterListener () {
		mIService = null;
	}
    
    
    
    public void updateList() {
    	Log.d(TAG, "Updating adapter: " + mAdapter + " " + String.valueOf(MasterArray.getData().size()));
    	if (mAdapter != null) 
    		mAdapter.notifyDataSetChanged();
    	
    	Server server;
    	
    	/* Show/hide status message */
    	for (int i = 0; i < MasterArray.getData().size(); i++) 
			for (int j = 0; j < MasterArray.getData().get(i).size(); j++) {
				server = MasterArray.getData().get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
					(server.getStatus() != Server.CONNECTING) &&
					(server.getStatusMessage() != null)) {
					if ((server.getStatusMessage().compareTo("") != 0) &&
						(can_show)){ 
						
						llStatusMessage.setVisibility(View.VISIBLE);
						tvStatusMessage.setText( server.getStatusMessage() );
						
						/* Wait 5 second before making layout invisible */
						if (llStatusMessage.getVisibility() == View.VISIBLE) {
							Handler handler = new Handler(); 
							handler.postDelayed(new MyRunnable(server) { 
								public void run() {					
									llStatusMessage.setVisibility(View.GONE);
									server.setStatusMessage("");
								} 
							} ,5000);
						}
	
						}
				}
			}
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_connect, container, false);
       
        mAdapter = new TabConnectAdapter(getActivity());
        Log.d(TAG, "Starting adapter: " + mAdapter);
        
        llStatusMessage = (LinearLayout) rootView.findViewById(R.id.llStatusMessage);
        tvStatusMessage = (TextView) rootView.findViewById(R.id.tvStatusMessage);
        btCloseStatusMessage = (Button) rootView.findViewById(R.id.btCloseStatusMessage);
        
        listView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        
        btAddNewServer = (Button) rootView.findViewById(R.id.btAddNewServer);
        
        llStatusMessage.setVisibility(View.GONE);
        
        btCloseStatusMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 llStatusMessage.setVisibility(View.GONE);
				 can_show = false;
				 /* Wait 1 min before showing anything again */
					Handler handler = new Handler(); 
					handler.postDelayed(new Runnable() { 
						public void run() {
							can_show = true;						
						} 
					}, IRCHelper.TIME_BETWEEN_STATUS_MESSAGE);
			}
		});

		btAddNewServer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if (mIService != null) {
					Server server = new Server();
					mIService.itemClick(server);
				}
			}			
		});   

        
        listView.setOnChildClickListener(new OnChildClickListener()
        {
           
            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2, int arg3, long arg4)
            {
                Toast.makeText(getActivity() , "Child clicked. TODO:Launch editDialog", Toast.LENGTH_LONG).show();
                
               // arg2 = groupPosition: arg3 = childPosition; arg4 = row id of the child that was clicked 
                Server server = (Server) mAdapter.getChild(arg2, arg3);
                
                int index = arg3;
//                EditServerDialog details = (EditServerDialog)
//                        getFragmentManager().findFragmentById(R.id.linearLayout0);
//                if (details == null || details.getShownIndex() != index) {
//                    // Make new fragment to show this selection.
//                    details = EditServerDialog.newInstance(index);
//
//                    // Execute a transaction, replacing any existing fragment
//                    // with this one inside the frame.
//                    
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    if (index == 0) {
//                        ft.replace(R.id.linearLayout0, details);
//                    } else {
//                        ft.replace(R.id.linearLayout0, details);
//                    }
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                    ft.commit();
//                }
             // arg2 = groupPosition: arg3 = childPosition; arg4 = row id of the child that was clicked 
                
                Log.d(TAG, server.getIp());
                Log.d(TAG, String.valueOf(server.getStatus()));
                
               
                

              
                
            return false;
            }
        });
       
        listView.setOnGroupClickListener(new OnGroupClickListener()
        {
           
            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3)
            {
                Log.d(TAG, "Group clicked");
                return false;
            }
        });
        
       
        
        listView.setAdapter(mAdapter);

//        rootView.findViewById(R.id.btConnect)
//        .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), Chats.class);
//                startActivity(intent);
//            }
//        });
        
        
        //((TextView) rootView.findViewById(android.R.id.text1)).setText("This is ConnectTab");
        return rootView;
    }
}
