package es.rafaelsf80.apps.irccfree.Chats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DownloadFileTask extends AsyncTask<String, Integer, Integer>{

	ProgressDialog pd = null;
	private final String TAG = getClass().getSimpleName();
	
	private String fileName, file;
	
	private Context mContext;
	
	public void setContext(Context ctx) {
		mContext = ctx;
	}
	
	protected void onPreExecute()
	{
		// Show progressDialog
		pd = new ProgressDialog(mContext);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("ProgressDialog");
		pd.setMessage("Download file ...");
		pd.show();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		
		fileName = params[0];	
		Log.d(TAG, fileName);
		
		try {
			file = downloadFile(params[0], params[1], params[2], params[3],
					Environment.getExternalStorageDirectory().toString());
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			return -1;
		} catch (IOException e) {
			return -1;				
		}
		
		return 1;
	}

	@Override
	protected void onProgressUpdate(final Integer... progress) {
        pd.setProgress(progress[0]);
    }
	
	@Override
    protected void onPostExecute(Integer result)
    {
    	pd.dismiss();
    	
    	if (result == 1)    	
    		Toast.makeText(mContext, fileName + " downloaded correctly in " + file , Toast.LENGTH_LONG).show();    	
    	else
    		Toast.makeText(mContext, "Download error file: " +  fileName  , Toast.LENGTH_LONG).show();
    		
    }
	
	
	/**
     * Download the file in fileSystem
     * 
     * @param destUrl The uri to get/download the file
     * @param directory The base path where save the file
     * @return String The filename path where it has been saved
     */
	private String downloadFile (String ip, String port, String fileName, String fileSize, final String directory) throws IOException
	{				
		Log.d("downloadFile", "downloadFile(), " + directory);
		
		// Shows a typical 0-100% progress bar
		publishProgress(0);

		Socket connection = new Socket(ip, Integer.parseInt(port));
		
		final int fileLength = Integer.parseInt(fileSize);

		// Download the file
		final File destinationFolder = new File(directory);
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}

		final File destination = new File(destinationFolder, fileName);
		if (destination.exists())
		{
			Log.d(TAG,"File already exists! Remove it");
			destination.delete();
		}

		Log.d(TAG, "Starting download on: " + destination.toString());

		final InputStream input = new java.io.BufferedInputStream(connection.getInputStream());
		OutputStream output = null;
		try {
			output = new FileOutputStream(destination);
		} catch (IOException ex) {
			Log.e(TAG, "Error opening destination file: " + ex.getMessage());
			return null;
		}

		final byte data[] = new byte[4096];
		long total = 0;
		int count;
		while ((count = input.read(data)) != -1) {
			total += count;
			// publishing the progress....                      
			publishProgress((int) (total * 100 / fileLength));
			output.write(data, 0, count);
		}

		output.flush();
		output.close();
		input.close();

		Log.d(TAG, "Finished download on: " + destination.toString());

		return destination.toString();
	}
}
