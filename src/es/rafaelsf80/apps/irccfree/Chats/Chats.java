package es.rafaelsf80.apps.irccfree.Chats;

import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import es.rafaelsf80.apps.irccfree.IRCHelper;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Chats.ChatFragment.IChatFragment;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService.IConnectionService;

public class Chats extends FragmentActivity  {
	
	private final String TAG = getClass().getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    ChatPagerAdapter mChatPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;
    
    int numChats;
	 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.chats);
    	
    	/* Show advertisements every 20 seconds */
    	final Handler handler = new Handler();
    	Runnable r = new Runnable()
    	{
    	    public void run() 
    	    {
    	        handler.postDelayed(this, IRCHelper.TIME_BETWEEN_ADS);
    	        
    	        int index = mViewPager.getCurrentItem();
    	        Fragment fragment = mChatPagerAdapter.getFragment(index);
    	        if (fragment != null) {
    				
    				((ChatFragment) fragment).showAd();
    			}
    	    }
    	};
    	handler.postDelayed(r, IRCHelper.TIME_BETWEEN_ADS);
    	

    	ConnectionService.regListener(new IConnectionService() {

    		@Override
    		public void updateUI() {
    			// TODO Auto-generated method stub

    			int index = mViewPager.getCurrentItem();
    			Fragment fragment = mChatPagerAdapter.getFragment(index);
    			Log.d(TAG, "UI update on Fragment #" + index);

    			if (fragment != null) {
    				((ChatFragment) fragment).adapter.notifyDataSetChanged();
    				((ChatFragment) fragment).updateUI();
    			}
    		}
    		
    		

    		@Override
    		public void updateColors() {
    			// TODO Auto-generated method stub

    		}
    	});
		
    	ChatFragment.regListener(new IChatFragment() {
    		@Override
    		public void itemClick(Server server) {

    			Bundle args;
    			FragmentManager fm = getSupportFragmentManager();
    	        ChatPopupMenu editNameDialog = new ChatPopupMenu();
    	        args = new Bundle();
    	        args.putParcelable("SERVER", server);
                editNameDialog.setArguments(args);
    	        editNameDialog.show(fm, "fragment_edit_name");
    		}
    	});
    	
    	
    	
    	int key = -1;
    	Intent intent = getIntent();
        
        if (intent != null) 
        	 key = intent.getIntExtra(IRCHelper.NOTIFICATION_FRAGMENT_KEY, -1);

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        // 
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        //mChatPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager(), mActiveChannelListArray);
        mChatPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mChatPagerAdapter);
        
        /* No need to notify DCC since it should be enabled on server.isDccReceived */
        if (key != -1)      
        	mChatPagerAdapter.getItem( MasterArray.KeyToPosition(key) );
        	
        //Toast.makeText(this, "Parcelable " + server.getName() + " " + server.getIp() + " Buffer: " + server.getBuffer(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, Chats.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class ChatPagerAdapter extends FragmentStatePagerAdapter {
    	private Map<Integer, Fragment> mFragmentMap;
    	
    	
    	public ChatPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentMap = new HashMap<Integer, Fragment>();

        }

        @Override
        public Fragment getItem(int i) {
        	
        	
            Fragment fragment = new ChatFragment();
            
            Bundle args = new Bundle();
            
            Log.d("Chats", "getItem() #" + i + " " + MasterArray.findJoinedChannelNameByPosition(i));
            args.putParcelable(IRCHelper.SERVER, MasterArray.findServerByKey(i));
            args.putInt(IRCHelper.KEY, MasterArray.positionToKey(i));
            fragment.setArguments(args);
            mFragmentMap.put(i, fragment);
        
            return fragment;
        }

        @Override
        public int getCount() {
        	Log.d("Chats", "getCount()="+String.valueOf(MasterArray.joinedChannelSize()));
            return MasterArray.joinedChannelSize();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return "CHANNEL #" + (position + 1);
        	Log.d("Chats", "GetPageTitle #" + position + " " + MasterArray.findJoinedChannelNameByPosition(position));
        	return MasterArray.findJoinedChannelNameByPosition(position);
        	//return "CHANNEL #" + (position + 1);
        }
        
        @Override
        public void destroyItem(View container, int position, Object object) {
        	// TODO Auto-generated method stub
        	super.destroyItem(container, position, object);
        	mFragmentMap.remove(position);
        }
        
        /* Used by ConnectionService, to update UI */
        public Fragment getFragment(int key) {
        	
            return mFragmentMap.get(key);
        }
        
    }
}
    
