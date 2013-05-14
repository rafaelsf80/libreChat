package es.rafaelsf80.apps.irccfree.TabList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Chats.Chats;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;



/**
 * Fragment for /list tab
 */
public class TabList extends Fragment {

    private final String TAG = getClass().getSimpleName();
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	private TabListAdapter mAdapter;
	private ListView listView;
	private Button btGoToChats;

	Handler mHandler;

	public void updateList() {
		Log.d(TAG, "Updating adapter: " + mAdapter);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab_list, container, false);

		listView = (ListView) rootView.findViewById(R.id.lvChannel);
		mAdapter = new TabListAdapter(getActivity());
		Log.d(TAG, "Starting adapter: " + mAdapter);
		listView.setAdapter(mAdapter);
		btGoToChats = (Button) rootView.findViewById(R.id.btGoToChats);

		btGoToChats.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (MasterArray.joinedChannelSize() > 0) {
					Intent intent = new Intent(getActivity(), Chats.class);
					startActivity(intent);
				}
				else {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());
					alertDialogBuilder.setTitle("ERROR");
					alertDialogBuilder
					.setMessage("Please, join a channel first")
					.setCancelable(false)
					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						}
					});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}


			}
		});   
		return rootView;
	}
}