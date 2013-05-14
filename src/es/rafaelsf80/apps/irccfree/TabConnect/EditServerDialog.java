package es.rafaelsf80.apps.irccfree.TabConnect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.Server;

public class EditServerDialog extends DialogFragment implements OnEditorActionListener {

	private EditText etServerDescription, etServerGroup, etServerIp, etServerPort, etNickname, etPassword;
    private CheckBox cbConnectOnLaunch;
    private Button btSave, btCancel;
    Spinner spEncoding;
    String encoding = Server.DEFAULT_ENCODING;;
    Server server;
    boolean isNewServer = true;
    int i = -1, j = -1;
    
    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }
    
    public static EditServerDialog newInstance(int index) {
    	EditServerDialog f = new EditServerDialog();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }


    public EditServerDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    
     View view = inflater.inflate(R.layout.dialog_editserver, container);
     
        Bundle args = getArguments();
        server = args.getParcelable("SERVER");
        
        String title = args.getString("TITLE");
        
        /* Check if new server or edit existing server */
        if (server.getIp() != "") {
        	isNewServer = false;
        	title = title + " - " + server.getIp();
        	i = MasterArray.getGroupIndex(server);
        	j = MasterArray.getServerIndex(server);
        }
        getDialog().setTitle(title);
        
        etServerDescription = (EditText) view.findViewById(R.id.etServerDescription);
        etServerGroup = (EditText) view.findViewById(R.id.etServerGroup);
        etServerIp = (EditText) view.findViewById(R.id.etServerIp);
        etServerPort = (EditText) view.findViewById(R.id.etServerPort);
        etNickname = (EditText) view.findViewById(R.id.etNickname);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        cbConnectOnLaunch = (CheckBox) view.findViewById(R.id.cbConnectOnLaunch);
        btSave = (Button) view.findViewById(R.id.btSave);
        btCancel = (Button) view.findViewById(R.id.btCancel);
        
        // Spinner (encoding options)
        spEncoding = (Spinner) view.findViewById(R.id.spEncoding);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
        		R.array.encoding, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);   
        spEncoding.setAdapter(adapter);
        spEncoding.setSelection(adapter.getPosition( server.getEncoding() ));
        encoding = server.getEncoding();
        
        // Show soft keyboard automatically
        etServerIp.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etServerIp.setOnEditorActionListener(this);
        
        etServerDescription.setText(server.getName());
        etServerGroup.setText(server.getGroup());
        etServerIp.setText(server.getIp());
        etServerPort.setText(String.valueOf(server.getPort()));
        etNickname.setText(server.getNickname());
        etPassword.setText(server.getPassword());
        cbConnectOnLaunch.setChecked(server.isConnectOnLaunch());
        
        
        if ((server.getStatus() != Server.DISCONNECTED) &&
            (server.getStatus() != Server.CONNECTING)) {
            	btSave.setEnabled(false);
            }
        else btSave.setEnabled(true);
        
        // Spinner listener
        spEncoding.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				encoding = arg0.getItemAtPosition(arg2).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}     	
		});
        
        // Save button listener
        btSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Server newServer = new Server(etServerDescription.getText().toString(),
						etServerGroup.getText().toString(),
						etServerIp.getText().toString(),
						Integer.parseInt( etServerPort.getText().toString() ),
						etNickname.getText().toString(),
						encoding,
						etPassword.getText().toString(),
						cbConnectOnLaunch.isChecked(), 
						null);
				if (!isNewServer) {
					MasterArray.setIp(i, j, etServerIp.getText().toString());
					MasterArray.sync(newServer);
				} else {
					if (server.getIp() != "")
						MasterArray.addNewServer(newServer);
				}
				getDialog().dismiss();	
			}
		});
        
       
        // Cancel button listener. Ask before cancel.
        btCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
	    	    .setTitle("Cancel")
	    	    .setMessage("Save before cancel ?")
	    	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	        	Server newServer = new Server(etServerDescription.getText().toString(),
	    						etServerGroup.getText().toString(),
	    						etServerIp.getText().toString(),
	    						Integer.parseInt( etServerPort.getText().toString() ),
	    						etNickname.getText().toString(),
	    						encoding,
	    						etPassword.getText().toString(),
	    						cbConnectOnLaunch.isChecked(),
	    						null);
	    	        	Log.d("EditServer", encoding);
	    	        	if (!isNewServer) {
	    					MasterArray.sync(newServer);
	    					MasterArray.setIp(i, j, etServerIp.getText().toString());
	    				} else {
	    					if (server.getIp() != "")
	    						MasterArray.addNewServer(newServer);
	    				}
	    	        	getDialog().dismiss();	
	    	        }
	    	     })
	    	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	        	getDialog().dismiss();
	    	        }
	    	     })
	    	     .show();	
			}
		});
 
        return view;
    }
    
    // EditServerDialog must implement the inherited abstract method TextView.OnEditorActionListener.onEditorAction(TextView, int, KeyEvent)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
     if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditNameDialogListener activity = (EditNameDialogListener) getActivity();
            activity.onFinishEditDialog(etServerDescription.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
}
