package es.rafaelsf80.apps.irccfree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

	// used to know if the back button was pressed in the splash screen activity and avoid opening the next activity
	private boolean mIsBackButtonPressed;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				// make sure we close the splash screen so the user won't come back when it presses back key

				finish();

				if (!mIsBackButtonPressed) {
					// start the home screen if the back button wasn't pressed already
					Intent intent = new Intent(SplashScreen.this, Main.class);
					startActivity(intent);
				}

			}

		}, IRCHelper.SPLASH_DURATION);

	}

	@Override
	public void onBackPressed() {

		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}
}