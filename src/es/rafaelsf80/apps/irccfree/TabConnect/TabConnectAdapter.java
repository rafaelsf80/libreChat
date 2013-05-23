package es.rafaelsf80.apps.irccfree.TabConnect;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import es.rafaelsf80.apps.irccfree.MyRunnable;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService;

public class TabConnectAdapter extends BaseExpandableListAdapter {

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    private Context mContext;
    TextView tvStatus, tv;
    Server server;
    Button bt;
    Switch sw;
    
    public interface ITabConnectAdapter {
    	void itemClick(Server s);
    }
    
    private static ITabConnectAdapter mIService;
    
    public static void regListener(ITabConnectAdapter iTabConnect) {
    	mIService = iTabConnect;
    }
    
    public static void unregisterListener () {
		mIService = null;
	}
    
  
    

    public TabConnectAdapter(Context context) {
        this.mContext = context;
        
    }

    /**
     * A general add method, that allows you to add a Vehicle to this list
     *
     * Depending on if the category opf the vehicle is present or not,
     * the corresponding item will either be added to an existing group if it
     * exists, else the group will be created and then the item will be added
     * @param vehicle
     */
    /*public void addItem(Server server) {
        if (!groups.contains(vehicle.getGroup())) {
            groups.add(vehicle.getGroup());
        }
        int index = groups.indexOf(vehicle.getGroup());
        if (children.size() < index + 1) {
            children.add(new ArrayList<Vehicle>());
        }
        children.get(index).add(vehicle);
    }*/

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return (MasterArray.getData()).get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
   
    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition,  int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
    	final int childPos = childPosition;
    	final int groupPos = groupPosition;
    	
        final Server server = ((Server) getChild(groupPosition, childPosition));
        Log.d("TabConnectAdapter", server + " " + (Server) getChild(groupPosition, childPosition));
        Log.d("TabConnectAdapter", "SERVER("+groupPosition+","+childPosition+"): " + server.getIp());
        //server.sync();
        Log.d("TabConnectAdapter", "SERVER2: " + server.getIp() + " " + server.getStatus());
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_server, null);
        }
        
        //LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.childServerLayout);
        tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        
        tv = (TextView) convertView.findViewById(R.id.tvChild);
        tv.setText("   " + server.getName() + " " + server.getStatus());
        
        sw = (Switch) convertView.findViewById(R.id.switch1); 
        if (server.getStatus() == Server.DISCONNECTED) {
        	tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.serverStatusDisconnected));      	
        	sw.setChecked(false);   
        } else if (server.getStatus() == Server.JOINED) {
        	tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.serverStatusConnected));   	   
        	sw.setChecked(true);
        } else {
        	tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.serverStatusConnecting));
        	sw.setChecked(true);
        }
        
        sw.setOnClickListener(new OnClickListener() {
			
        	@Override
        	public void onClick(View v) {
        		sw = (Switch) v;
        		if (sw.isChecked()) {

        			Log.d("TabConnectAdapter", "CLICK ON SWITCH: " + server.getIp());

        			sw.setChecked(true);

        			/* Send /CONNECT */
        			ConnectionService.sendIRCCommand(server, "/CONNECT");
        			
        			/* Wait 1 second and then send /AUTH */
        			Handler handler = new Handler(); 
        			handler.postDelayed(new MyRunnable(server) { 
        				public void run() {
        					Log.d("TabConnectAdapter", " " + server + " " + server.getIp());
        					server.sync();
        					if (server.getStatus() == Server.CONNECTED) {
        						ConnectionService.sendIRCCommand(server, "/AUTH "+server.getNickname());
        					}
        				} 
        			}, 1000);



        			/* Wait 3 seconds and then send /LIST */			
//        			handler = new Handler(); 
//        			handler.postDelayed(new MyRunnable(server) { 
//        				public void run() { 
//        					Log.d("TabConnectAdapter", "Hola1");
//        					server.sync();
//        				
//        					if (server.getStatus() == Server.AUTHENTICATED) {
//        						ConnectionService.sendIRCCommand(server, "/LIST");
//        					}
//        				} 
//        			}, 3000);

        			/* Wait 2 seconds before updating ListView */
//        			handler = new Handler(); 
//        			handler.postDelayed(new MyRunnable(server) { 
//        				public void run() {
//        					server.sync();
//        					if (server.getStatus() == Server.AUTHENTICATED) {
//        						//notifyDataSetChanged();
//        					}
//        					else {
//        						sw.setChecked(false);
//        					}
//
//        				} 
//        			}, 2000);

        		} else {
        			if (server.getStatus() == Server.CONNECTING) {
        				tvStatus.setBackgroundColor(mContext.getResources().getColor(R.color.serverStatusDisconnected));      	
        	        	sw.setChecked(false);
        				/* kill thread . Thread.stop() is deprecated */
        				Log.d("TabConnectAdapter", "Cancelling: " + server + " " + server.getIp() + " "+server.getThread());
        				if (server.getThread() != null)
        					server.getThread().interrupt();
        				server.setThread(null);
        				server.setStatus(Server.DISCONNECTED);
        				
        			}

        			if ((server.getStatus() != Server.DISCONNECTED) &&
        					(server.getStatus() != Server.CONNECTING)) {
        				sw.setChecked(false);
        				ConnectionService.sendIRCCommand(server, "/QUIT");
        			}
                }
				
			}
		});
        
        
        
        /* Click on Edit Server button, which opens an edit dialog */
        bt = (Button) convertView.findViewById(R.id.btEditServer);
        bt.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		if (mIService != null) {
	        		Server server1 = (Server) getChild(groupPos, childPos);
	        		mIService.itemClick(server1);
        		}
        	}
        });
        
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (MasterArray.getData()).get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return (MasterArray.getData()).get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return (MasterArray.getData()).size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
    	
    	
    	// All servers within the same group share same "groupname"     	
    	Server server = (Server) getChild(groupPosition, 0);
    	String group = server.getGroup();
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_server, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
        tv.setText(group);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}