package com.barobot.wizard;

import android.os.Bundle;
import android.view.View;

import com.barobot.R;
import com.barobot.hardware.Arduino;
import com.barobot.hardware.devices.BarobotConnector;
import com.barobot.other.Android;
import com.barobot.other.UpdateManager;

public class FirmwareActivity extends BlankWizardActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_firmware);
		//enableTimer( 1000, 2000 );
	}

	public void onOptionsButtonClicked(View view)
	{
		switch(view.getId()){
			case R.id.wizard_firmware_upload:
				upload( false );
				break;
			case R.id.wizard_firmware_upload_manual:
				upload( true  );
				break;
			case R.id.wizard_firmware_check:
				check_version();
				break;	
			default:
				super.onOptionsButtonClicked(view);
				break;
		}
	}
	private void check_version() {
		Android.checkNewSoftwareVersion( true, this );
	}
	private void upload(boolean isManual) {
		final BarobotConnector barobot = Arduino.getInstance().barobot;
		UpdateManager.downloadAndBurnFirmware( this, barobot.use_beta, isManual );
	}
	public void onTick(){
		
	}
}
