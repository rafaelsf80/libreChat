package es.rafaelsf80.apps.irccfree;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import es.rafaelsf80.apps.irccfree.Chats.Chats;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService;
import es.rafaelsf80.apps.irccfree.Service.ConnectionService.IConnectionService;
import es.rafaelsf80.apps.irccfree.TabConnect.EditServerDialog;
import es.rafaelsf80.apps.irccfree.TabConnect.TabConnect;
import es.rafaelsf80.apps.irccfree.TabConnect.TabConnect.ITabConnect;
import es.rafaelsf80.apps.irccfree.TabConnect.TabConnectAdapter;
import es.rafaelsf80.apps.irccfree.TabConnect.TabConnectAdapter.ITabConnectAdapter;
import es.rafaelsf80.apps.irccfree.TabList.TabList;


public class Main extends FragmentActivity implements ActionBar.TabListener {

	private final String TAG = getClass().getSimpleName();
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	
	public static Context context;

    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      
        
        setContentView(R.layout.main);
		
       context = this;
       
       Intent in = new Intent(this, ConnectionService.class);
  
        startService(in);
        
        
        Intent intent = getIntent();
        
        if (intent != null) {
        	int key = intent.getIntExtra(IRCHelper.NOTIFICATION_CHANNEL_KEY, -1);
        	boolean is_dcc = intent.getBooleanExtra(IRCHelper.NOTIFICATION_IS_DCC, false);
        	if (key != -1) {
        		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        		mNM.cancel(IRCHelper.NOTIFICATION_ID);
        		
        		// Show fragment
        		if (MasterArray.joinedChannelSize() > 0) {
					Intent i = new Intent(this, Chats.class);
					i.putExtra(IRCHelper.NOTIFICATION_FRAGMENT_KEY, key);
					startActivity(i);
				}

        	}
        }
        
  
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int j = 0; j < mAppSectionsPagerAdapter.getCount(); j++) {
        	actionBar.addTab(
        			actionBar.newTab()
        			.setText(mAppSectionsPagerAdapter.getPageTitle(j))
        			.setTabListener(this));
        }
    }
    
    protected void onResume() {
    
        super.onResume();
        Log.d(TAG, "onResume");
        
        ConnectionService.regListener(new IConnectionService() {
        	
        	
        	
        	Fragment fragConnect = mAppSectionsPagerAdapter.getItem(0); // En TabConnect es donde están los colores
			Fragment fragList = mAppSectionsPagerAdapter.getItem(1); // En TabList hay que actualizar lista es donde están los colores
			
		@Override
			public void updateColors() {
				// TODO Auto-generated method stub
			}

			@Override
			public void updateUI() {
				
				/* Only need to update current selected tab */
				int item = mViewPager.getCurrentItem();
				
				Fragment frag; 
				switch (item) {
	        	case 0:              	
	        		 frag = (TabConnect) mAppSectionsPagerAdapter.getItem(0);
	        		 ((TabConnect) frag).updateList();
	        		 
	        		 break;
	        	case 1:
	        		frag = (TabList) mAppSectionsPagerAdapter.getItem(1);
	        		((TabList) frag).updateList();
	        		break;
	        	case 2:
	        		frag = (TabOptions) mAppSectionsPagerAdapter.getItem(2);
	        		break;
	        	default:
	
				}
		}
		});
        
        
        /* Listener to launch Edit Server dialog */
        TabConnectAdapter.regListener(new ITabConnectAdapter() {
    		@Override
    		public void itemClick(Server server) {
    	
    			Bundle args;
    			FragmentManager fm = getSupportFragmentManager();
    	        EditServerDialog editNameDialog = new EditServerDialog();
    	        args = new Bundle();
    	        args.putParcelable("SERVER", server);
    	        args.putString("TITLE", getResources().getString(R.string.edit_server_dialog_title_edit_server));
                editNameDialog.setArguments(args);
    	        editNameDialog.show(fm, "fragment_edit_name");
    		}
    	});
        
        /* Listener to launch New Server dialog */
        TabConnect.regListener(new ITabConnect() {
    		@Override
    		public void itemClick(Server server) {

    			Bundle args;
    			FragmentManager fm = getSupportFragmentManager();
    	        EditServerDialog editNameDialog = new EditServerDialog();
    	        args = new Bundle();
    	        args.putParcelable("SERVER", server);
    	        args.putString("TITLE", getResources().getString(R.string.edit_server_dialog_title_new_server));
                editNameDialog.setArguments(args);
    	        editNameDialog.show(fm, "fragment_edit_name");
    		}
    	});
    }
    
   

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    	Log.d(TAG, String.valueOf(tab.getPosition()));
    	
         mViewPager.setCurrentItem(tab.getPosition());
    
         /* Only need to update current selected tab */
			int item = mViewPager.getCurrentItem();
			
			Fragment frag; 
			
			
			switch (item) {
     	case 0:              	
     		 frag = (TabConnect) mAppSectionsPagerAdapter.getItem(0);
     		 ((TabConnect) frag).updateList();
     		 break;
     	case 1:
     		frag = (TabList) mAppSectionsPagerAdapter.getItem(1);
     		((TabList) frag).updateList();
     		break;
     	case 2:
     		frag = (TabOptions) mAppSectionsPagerAdapter.getItem(2);
     		break;
     	default:
     		frag = null;
			}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.mainmenu, menu);
      return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.action_refresh:
        Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT)
            .show();
        break;
      case R.id.action_settings:
        Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT)
            .show();
        break;

      default:
        break;
      }

      return true;
    } 

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    
   
    @Override
    protected void onPause() {
    	
    	super.onPause();
    	Log.d(TAG, "onPause");
    	
    	TabConnect.unregisterListener();
    	TabConnectAdapter.unregisterListener();
    	ConnectionService.unregisterListener();
  
    }
    
    
    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    	stopService(new Intent(getApplicationContext(), ConnectionService.class));
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    	
    	Fragment connectTabFragment = null, listTabFragment = null, optionsTabFragment = null;

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	Bundle args;
        	switch (i) {
        	case 0:              	
        		if (connectTabFragment == null) {
        			connectTabFragment = new TabConnect();
        			args = new Bundle();
        			args.putInt(TabConnect.ARG_SECTION_NUMBER, i + 1);
        			connectTabFragment.setArguments(args);
        		}
        		return connectTabFragment;
        	case 1:
        		if (listTabFragment == null) {
        			listTabFragment = new TabList();
        			args = new Bundle();
        			args.putInt(TabList.ARG_SECTION_NUMBER, i + 1);
        			listTabFragment.setArguments(args);
        		}
        		return listTabFragment;
        	case 2:
        		if (optionsTabFragment == null) {
        			optionsTabFragment = new TabOptions();
        			args = new Bundle();
        			args.putInt(TabOptions.ARG_SECTION_NUMBER, i + 1);
        			optionsTabFragment.setArguments(args);
        		}
        		return optionsTabFragment;
        	default:
        		return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position) {
        	case 0:
				//return "/connect";
        		return getString(R.string.tab_title_0);
			case 1:
				//return "/list";
				return getString(R.string.tab_title_1);
			case 2:
				//return "Options";
				return getString(R.string.tab_title_2);
        	}
        	return null;
         }
    }
    
    
    /**
     * Fragment for Options tab
     */
    public static class TabOptions extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tab_options, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText("This is OptionsTab");
            return rootView;
        }
    }
}
