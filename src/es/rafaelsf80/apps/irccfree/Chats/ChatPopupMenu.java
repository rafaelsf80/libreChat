package es.rafaelsf80.apps.irccfree.Chats;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.Server;
import es.rafaelsf80.apps.irccfree.TabConnect.EditServerDialog;

public class ChatPopupMenu extends DialogFragment  {

    
    private Button btDCCSend, btWho, btList, btVoice;
    
    
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


    public ChatPopupMenu() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    
     View view = inflater.inflate(R.layout.chat_popupmenu, container);
     
        
       
        
        Bundle args = getArguments();
        Server server = args.getParcelable("SERVER"); 
        getDialog().setTitle("Menu");
        btDCCSend = (Button) view.findViewById(R.id.btDCCSendFile);
        btWho = (Button) view.findViewById(R.id.btWho);
        btList = (Button) view.findViewById(R.id.btList);
        btVoice = (Button) view.findViewById(R.id.btVoice);
        
        // 
        btDCCSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				
				
			}
		});
        
        btWho.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getDialog().dismiss();	
			}
		});
        

        return view;
    }
    
    
    

}

