/**
 Copyright 2018 KDDI Technology Corp.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.kddi_tech.sd4.sdlsamplev1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.LockScreenManager;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.interfaces.OnSystemCapabilityListener;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.DisplayCapabilities;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetSystemCapabilityResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.HeadLampStatus;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendHapticDataResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayout;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.Speak;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleData;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TextField;
import com.smartdevicelink.proxy.rpc.TireStatus;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.AmbientLightStatus;
import com.smartdevicelink.proxy.rpc.enums.ComponentVolumeStatus;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.LockScreenStatus;
import com.smartdevicelink.proxy.rpc.enums.RequestType;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SystemAction;
import com.smartdevicelink.proxy.rpc.enums.SystemCapabilityType;
import com.smartdevicelink.proxy.rpc.enums.VehicleDataResultCode;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener;
import com.smartdevicelink.transport.BTTransportConfig;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.MultiplexTransportConfig;
import com.smartdevicelink.transport.TCPTransportConfig;
import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.transport.USBTransportConfig;
import com.smartdevicelink.util.CorrelationIdGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class SdlService extends Service implements IProxyListenerALM, TextToSpeech.OnInitListener {

	private static final String LOG_TAG					= "[Log:[SdlService]]";
	private static final String DEBUG_TAG				= "[Log:[DEBUG]]";
	private static String APP_ID							= "0";          // set your own APP_ID
	private static Boolean USE_MANTICORE					= true;
	private static String APP_NAME						= null;
	private static int MANTICORE_TCP_PORT					= 0;
	private static String MANTICORE_IP_ADDRESS			= null;
	private static String NOTIFICATION_CHANNEL_ID			= null;

	private SdlProxyALM proxy								= null;
	private LockScreenManager lockScreenManager			= new LockScreenManager();

	// Settings(DisplayCapabilities)
	private DisplayCapabilities mDisplayCapabilities	= null;
	private ArrayList<String> mAvailableTemplates		= null;
	private ArrayList<TextField> mTextFields				= null;
	private Boolean mGraphicsSupported					= false;
	private Boolean mDisplayLayoutSupported			= false;
	private int mNumberOfTextFields						= 0;

	// 取得可能な車両情報のMapデータ
    private Map<String, Boolean> usableVehicleData		= new HashMap<String, Boolean>();
	private static final String VD_FUEL_LEVEL			= "FUEL_LEVEL";
	private static final String VD_HEAD_LAMP_STATUS	= "HEAD_LAMP_STATUS";
	private static final String VD_TIRE_PRESSURE		= "TIRE_PRESSURE";
	// 車両情報のsubscribe済フラグ
	private boolean isVehicleDataSubscribed			= false;

	// Templateの変更管理
	private static String currentTemplateName				= "DEFAULT";            // 現在表示しているテンプレート
	private static String reqTemplateName					= "GRAPHIC_WITH_TEXT";    // 変更要求をかける際のテンプレート
	private static int requestemplateID					= 0;                           // 変更要求をかける際のID

	// Command id
	private static final int COMMAND_ID_1				= 1;
	private static final int COMMAND_ID_2				= 2;
	private static final int COMMAND_ID_3				= 3;

	// SoftButton id
	private static final int SOFT_BUTTON_ID_1			= 1;
	private static final int SOFT_BUTTON_ID_2			= 2;
	private static final int SOFT_BUTTON_ID_3			= 3;

    // app icon id
    private static final int ICON_CORRELATION_ID		= CorrelationIdGenerator.generateId();
	// app icon name
	private static final String ICON_LOCK_SCREEN		= "sdl_lock_screen_img.png";
	private static final String ICON_TIRE				= "sdl_tire.png";
	private static final String ICON_HEADLIGHT			= "sdl_headlight.png";
	private static final String ICON_FUEL				= "sdl_fuel.png";
	// 画像ファイル
	// Manticoreでは一定枚数を超えた画像のアップロードは破棄される
	private List<String> remoteFiles;
	private static final String ICON_FILENAME			= "sdl_hu_icon.png";
	private static final String PIC_CHARACTER			= "sdl_chara.png";
	private static final String PIC_SORRY				= "sdl_hu_sorry.png";
	// TTS用変数
	private TextToSpeech tts;
	private boolean isTtsEnabled = false;
	private Map<Integer, String> ttsStandby = new HashMap<Integer,String>();

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		remoteFiles = new ArrayList<>();
		_connectForeground();
	}

	private void _connectForeground() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			APP_ID = getResources().getString(R.string.app_id);
			APP_NAME = getResources().getString(R.string.app_name);
			String manticorePort = BuildConfig.MANTICORE_PORT;
			MANTICORE_IP_ADDRESS = BuildConfig.MANTICORE_IP_ADDR;
			if(manticorePort == null || MANTICORE_IP_ADDRESS == null) {
				USE_MANTICORE = false;
			} else {
				MANTICORE_TCP_PORT = Integer.parseInt(manticorePort);
				USE_MANTICORE = true;
			}
			// パッケージ毎に一意のID値(長い文字列長の場合切り捨てられる場合があるようです)
			NOTIFICATION_CHANNEL_ID = getResources().getString(R.string.notif_channel_id);
			usableVehicleData.put(VD_FUEL_LEVEL,false);
			usableVehicleData.put(VD_HEAD_LAMP_STATUS,false);
			usableVehicleData.put(VD_TIRE_PRESSURE,false);
			startForeground(1, _createNotification());
			tts = new TextToSpeech(this, this);
		}
	}

	/**
	 * Android SDK ver 26(Oreo)以降の端末向け対応
	 * 通知チャネル(NotificationChannel)を登録し、通知用のインスタンスを返却する
	 * @return Notification 作成した通知情報
	 */
	private Notification _createNotification() {
		String name = getResources().getString(R.string.notif_channel_name);
		String description = getResources().getString(R.string.notif_channel_desctiption);
		int importance = NotificationManager.IMPORTANCE_HIGH; // デフォルトの重要度

		NotificationManager manager = getSystemService(NotificationManager.class);
		if (manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
			NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
			channel.setDescription(description);
			channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
			channel.enableVibration(true);
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.setSound(null, null);
			channel.setShowBadge(false);
			manager.createNotificationChannel(channel);
		}
		Notification builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
				.setContentTitle(getResources().getString(R.string.notif_content_title))
				.setContentText(getResources().getString(R.string.notif_content_text))
				.setSmallIcon(R.drawable.ic_sdl)
				.build();

		return builder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG,"onStartCommand called");
		// Serviceを2回目以降に起動する際は、OnCreateが呼ばれません。
		// Serviceの挙動が変わったため、startForegroundをここからも呼べるようにする必要があります。
		if (! intent.getBooleanExtra("isFirstConnect",true)) {
			_connectForeground();
		}

		boolean forceConnect = intent !=null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);
		if (proxy == null) {
			try {
				BaseTransportConfig transport = null;
				if(BuildConfig.TRANSPORT.equals("MBT")){
					int securityLevel;
					if(BuildConfig.SECURITY.equals("HIGH")){
						securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH;
					}else if(BuildConfig.SECURITY.equals("MED")){
						securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED;
					}else if(BuildConfig.SECURITY.equals("LOW")){
						securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW;
					}else{
						securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF;
					}
					transport = new MultiplexTransportConfig(this, APP_ID, securityLevel);
				} else if(BuildConfig.TRANSPORT.equals("LBT")) {
					transport = new BTTransportConfig();
				} else if(BuildConfig.TRANSPORT.equals("TCP")){
					transport = new TCPTransportConfig(MANTICORE_TCP_PORT, MANTICORE_IP_ADDRESS, true);
				} else if(BuildConfig.TRANSPORT.equals("USB")) {
					if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
						if (intent != null && intent.hasExtra(UsbManager.EXTRA_ACCESSORY)) {
							transport = new USBTransportConfig(getBaseContext(), (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY));
						}
					} else {
						Log.e("SdlService", "Unable to start proxy. Android OS version is too low");
					}
				}

				if(transport != null) {
					if (USE_MANTICORE && BuildConfig.TRANSPORT.equals("TCP")) {
						// ここでHUに対して接続を張りにいきます
						proxy = new SdlProxyALM(this, APP_NAME, false, APP_ID, new TCPTransportConfig(MANTICORE_TCP_PORT, MANTICORE_IP_ADDRESS, false));
					} else {
						proxy = new SdlProxyALM(this.getBaseContext(), this, APP_NAME, true, APP_ID);
					}
				}
			}catch(SdlException e) {
				if (proxy == null) {
					stopForeground(Service.STOP_FOREGROUND_REMOVE);
					stopSelf();
				}
				e.printStackTrace();
			}
		} else if(forceConnect){
			proxy.forceOnConnected();
		}
		return START_STICKY;
	}



	@Override
	public void onDestroy(){
		_disposeSyncProxy();
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			if(notificationManager!=null){ //If this is the only notification on your channel
				notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
			}
			stopForeground(Service.STOP_FOREGROUND_REMOVE);
		}
		super.onDestroy();
	}

	private void _disposeSyncProxy() {
		sendBroadcast(new Intent(LockScreenActivity.CLOSE_LOCK_SCREEN_ACTION));
		if (proxy != null) {
			try {
				proxy.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				proxy = null;
			}
		}
		isVehicleDataSubscribed = false;
	}


	private void _getSdlSettings() {
		if (proxy == null) {
			return;
		}
		// DisplayCapabilitiesを参照できるようにする
		_detectDisplayCapabilities();
		if(mDisplayCapabilities != null) {
			Boolean gSupport = mDisplayCapabilities.getGraphicSupported();
			if (gSupport != null && gSupport.booleanValue()) {
				mGraphicsSupported = true;
				_uploadImages();
			}
			if (mDisplayCapabilities.getTextFields() != null) {
				mTextFields = new ArrayList<TextField>(mDisplayCapabilities.getTextFields());
			}
			mAvailableTemplates = new ArrayList<String>(mDisplayCapabilities.getTemplatesAvailable());
			if (mAvailableTemplates != null && mAvailableTemplates.contains(reqTemplateName)) {
				mDisplayLayoutSupported = true;
			}
			if (mAvailableTemplates != null) {
				/*
				// 利用可能なテンプレート情報を表示する
				for (String str : mAvailableTemplates) {
					Log.i(DEBUG_TAG, "dispCapabilities：" + str);
				}
				*/
			}
			if (mDisplayLayoutSupported) {
				_updateTemplate();
			}
		}
	}


	/**
	 * テンプレートを指定したものに変更する
	 *
	 */
	private void _updateTemplate() {
		SetDisplayLayout setDisplayLayoutRequest = new SetDisplayLayout();
		setDisplayLayoutRequest.setDisplayLayout(reqTemplateName);
		// note : テンプレートの変更リクエストを行う際にID値を連携したい場合は、
		// SetDisplayLayout.setCorrelationIDではなくgetCorrelationIDで生成された値を保持しておく
		requestemplateID = setDisplayLayoutRequest.getCorrelationID();
		sendRequest(setDisplayLayoutRequest);
	}

	/**
	 * SdlProxyALM.getDisplayCapabilities(deprecated)の代替機能
	 * 定義済みの変数：
	 * DisplayCapabilities mDisplayCapabilities
	 *
	 * このメソッドを呼び出すと、
	 * mDisplayCapabilities は、SdlProxyALM.getDisplayCapabilities()と同等の振る舞いをします。
	 *
	 * @todo
	 * SdlProxyALM.getXXXCapabilities()系は軒並みdeprecatedになっているので、必要に応じて同等の処理を行ってください。
	 */
	private void _detectDisplayCapabilities () {
		if (mDisplayCapabilities != null) {
			return;
		}
		if (proxy.isCapabilitySupported(SystemCapabilityType.DISPLAY)) {
			proxy.getCapability(SystemCapabilityType.DISPLAY, new OnSystemCapabilityListener(){
				@Override
				public void onCapabilityRetrieved(Object capability){
					mDisplayCapabilities = (DisplayCapabilities) capability;
					Log.i(DEBUG_TAG, "getDisplayCapabilities Success");
				}
				@Override
				public void onError(String info){
					Log.e(DEBUG_TAG, "Capability could not be retrieved: "+ info);
				}
			});
		}
	}


	// SDL method
	@Override
	public void onOnHMIStatus(OnHMIStatus notification) {
		Log.i(DEBUG_TAG, "OnHMIStatus : HmiLevel : "+ notification.getHmiLevel() + ", getFirstRun :"+notification.getFirstRun());

		if (notification.getHmiLevel().equals(HMILevel.HMI_FULL)) {
			// Other HMI (Show, PerformInteraction, etc.) would go here
			if(notification.getFirstRun()) {
				_setCommand();
				_subscribeVehicleData();
				_setUIParts();
			}
		} else if (notification.getHmiLevel().equals(HMILevel.HMI_BACKGROUND)) {
			Log.i(DEBUG_TAG, "HMI Status : HMI_BACKGROUND");
			// Other app setup (SubMenu, CreateChoiceSet, etc.) would go here
		} else if (notification.getHmiLevel().equals(HMILevel.HMI_NONE)) {
			Log.i(DEBUG_TAG, "HMI Status : HMI_NONE");
			if(notification.getFirstRun()) {
				_getSdlSettings();
			}
		}
	}


	private void _setUIParts() {
		_showTextField(getResources().getString(R.string.hello_msg_1), getResources().getString(R.string.hello_msg_2), null, null);
		_showImage(PIC_CHARACTER);
		// ソフトボタンは定義していますが、使用しているテンプレートが
        // ソフトボタンに対応していないので表示されません。
        // ソフトボタンに対応したテンプレートを指定することで、表示されるようになります。
		_showSoftButtons();
	}

	private void _showSoftButtons() {

		SoftButton sb1 = new SoftButton();
		SoftButton sb2 = new SoftButton();
		SoftButton sb3 = new SoftButton();

		sb1.setSoftButtonID(SOFT_BUTTON_ID_1);
		sb1.setText("ボタン１");//getResources().getString(R.string.sb1_prev));
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setIsHighlighted(false);
		sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

		sb2.setSoftButtonID(SOFT_BUTTON_ID_2);
		sb2.setText("ボタン２");
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setIsHighlighted(false);
		sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

		sb3.setSoftButtonID(SOFT_BUTTON_ID_3);
		sb3.setText("ボタン３");
		sb3.setType(SoftButtonType.SBT_TEXT);
		sb3.setSystemAction(SystemAction.DEFAULT_ACTION);

		Show show = new Show();
		Vector<SoftButton> softButtons = new Vector<SoftButton>();
		softButtons.add(sb1);
		softButtons.add(sb2);
		softButtons.add(sb3);
		show.setSoftButtons(softButtons);
		sendRequest(show);
	}

	private void _showImage(String icon_name) {
		Show show = new Show();
		Image image = new Image();

		image.setValue(icon_name);
		image.setImageType(ImageType.DYNAMIC);
		show.setGraphic(image);
		sendRequest(show);
	}

	private void _showTextField(String tf1, String tf2, String tf3, String tf4) {
		Show show = new Show();
		if(tf1 != null) {
			show.setMainField1(tf1);
		}
		if(tf2 != null) {
			show.setMainField2(tf2);
		}
		if(tf3 != null) {
			show.setMainField3(tf3);
		}
		if(tf4 != null) {
			show.setMainField4(tf4);
		}
		int id = CorrelationIdGenerator.generateId();
		show.setCorrelationID(id);
		ttsStandby.put(id, tf1);
		sendRequest(show);
	}


	private void _subscribeVehicleData() {
		Log.i(LOG_TAG, "subscribeVehicleData." + isVehicleDataSubscribed);

		if(isVehicleDataSubscribed) {
			return;
		}
		SubscribeVehicleData subscribeRequest = new SubscribeVehicleData();
		subscribeRequest.setHeadLampStatus(true);
		subscribeRequest.setFuelLevel(true);
		subscribeRequest.setTirePressure(true);
		sendRequest(subscribeRequest);
	}

	private void _uploadImages() {
		ListFiles listFiles = new ListFiles();
		listFiles.setOnRPCResponseListener(new OnRPCResponseListener() {

			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				if(response.getSuccess()){
					remoteFiles = ((ListFilesResponse) response).getFilenames();
				}
				// Check the mutable set for the AppIcon
				// If not present, upload the image
				if(remoteFiles== null || !remoteFiles.contains(SdlService.ICON_FILENAME)){
					sendIcon();
				}else{
					// If the file is already present, send the SetAppIcon request
					for (String str :remoteFiles) {
						Log.i(LOG_TAG, "[image] uploaded file name : "+str);
					}

					try {
						proxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
					} catch (SdlException e) {
						e.printStackTrace();
					}
				}

				// Check the mutable set for the SDL image
				// If not present, upload the image
				if(remoteFiles== null || !remoteFiles.contains(SdlService.ICON_LOCK_SCREEN)){
					uploadImage(R.drawable.ic_lock, ICON_LOCK_SCREEN, CorrelationIdGenerator.generateId(), true);
				}
			}
		});
		sendRequest(listFiles);
	}



	private void sendIcon(){
		Log.i(LOG_TAG, "sendIcon put icon");
		// アプリアイコン
		uploadImage(R.drawable.ic_application_icon, ICON_FILENAME, ICON_CORRELATION_ID, true);
		// その他のアイコン(Manticoreでは5件までしか画像をアップロードできない)
		uploadImage(R.drawable.tire, ICON_TIRE, CorrelationIdGenerator.generateId(), true);
		uploadImage(R.drawable.fuel, ICON_FUEL, CorrelationIdGenerator.generateId(), true);
		uploadImage(R.drawable.headlight, ICON_HEADLIGHT, CorrelationIdGenerator.generateId(), true);
		uploadImage(R.drawable.pic_welcome, PIC_CHARACTER, CorrelationIdGenerator.generateId(), true);
	}

	private void uploadImage(int resource, String imageName, int correlationId, boolean isPersistent){
		PutFile putFile = new PutFile();
		putFile.setFileType(FileType.GRAPHIC_PNG);
		putFile.setSdlFileName(imageName);
		putFile.setCorrelationID(correlationId);
		putFile.setPersistentFile(isPersistent);
		putFile.setSystemFile(false);
		putFile.setBulkData(contentsOfResource(resource));

		sendRequest(putFile);
	}

	private byte[] contentsOfResource(int resource) {
		InputStream is = null;
		try {
			is = getResources().openRawResource(resource);
			ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
			final int bufferSize = 4096;
			final byte[] buffer = new byte[bufferSize];
			int available;
			while ((available = is.read(buffer)) >= 0) {
				os.write(buffer, 0, available);
			}
			return os.toByteArray();
		} catch (IOException e) {
			Log.w(LOG_TAG, "Can't read icon file", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * SDL Coreに対してのリクエストを行う
	 * テンプレート変更や、画像、テキスト、コマンド等の変更時に呼び出す
	 * @param req RPCRequest
	 */
	private void sendRequest(RPCRequest req) {
		try{
			proxy.sendRPCRequest(req);
			// response onShowResponse() called
		}catch (SdlException e){
			e.printStackTrace();
		}
	}

	/**
	 * SDL 標準機能。
	 * Showリクエストのレスポンスを受信する部分になります。
	 * @param response Showリクエストの結果
	 */
	@Override
	public void onShowResponse(ShowResponse response) {
		Log.i(LOG_TAG, "Show response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
		if(response.getSuccess()){
			if(ttsStandby.containsKey(response.getCorrelationID())) {
				_ttsSpeech(ttsStandby.get(response.getCorrelationID()), String.valueOf(response.getCorrelationID()));
			}
		}
	}

	/**
	 * TTS：initialize
	 * @param status init status
	 */
	@Override
	public void onInit(int status) {
		if (TextToSpeech.SUCCESS == status) {
			Locale locale = Locale.JAPAN;
			if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
				isTtsEnabled = true;
				tts.setLanguage(locale);
			} else {
				Log.w(LOG_TAG,"言語設定に日本語を選択できませんでした");
				isTtsEnabled = true;
			}
		} else {
			isTtsEnabled = false;
		}
	}

	/**
	 * TTS：指定した文字列を読み上げさせる
	 * @param str TTSで読み上げさせたい文字列
	 * @param utteranceId リクエスト用の一意のID値(null可)
	 */
	private void _ttsSpeech(String str, @Nullable String utteranceId) {
		if(isTtsEnabled && tts != null) {
			if (tts.isSpeaking()) {
				tts.stop();
			}
			tts.setSpeechRate(1.2f);
			tts.setPitch(1.0f);
			if(utteranceId == null) {
				utteranceId = String.valueOf(CorrelationIdGenerator.generateId());
			}
			tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
			// ヘッドユニット側にもTTSで読み上げさせる
			//sendRequest(new Speak(TTSChunkFactory.createSimpleTTSChunks(str)));
		}
	}



	// SDL method
	@Override
	public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {
		stopSelf();
		if(reason.equals(SdlDisconnectedReason.LANGUAGE_CHANGE) && BuildConfig.TRANSPORT.equals("MBT")){
			Intent intent = new Intent(TransportConstants.START_ROUTER_SERVICE_ACTION);
			//intent.putExtra(SdlReceiver.RECONNECT_LANG_CHANGE, true);
			sendBroadcast(intent);
		}
	}

	/*
	 * SystemRequest
	 */
	// SDL method
	@Override
	public void onOnSystemRequest(OnSystemRequest notification) {
		Log.i(LOG_TAG, "OnSystemRequest notification from SDL: " + notification);
		if(notification.getRequestType().equals(RequestType.LOCK_SCREEN_ICON_URL)){
			if(notification.getUrl() != null && lockScreenManager.getLockScreenIcon() == null){
				lockScreenManager.downloadLockScreenIcon(notification.getUrl(), new LockScreenDownloadedListener());
			}
		}
	}
	@Override
	public void onSystemRequestResponse(SystemRequestResponse response) {
		Log.i(LOG_TAG, "SystemRequest response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	private class LockScreenDownloadedListener implements LockScreenManager.OnLockScreenIconDownloadedListener{
		@Override
		public void onLockScreenIconDownloaded(Bitmap icon) {
			Log.i(LOG_TAG, "Lock screen icon downloaded successfully");
		}
		@Override
		public void onLockScreenIconDownloadError(Exception e) {
			Log.e(LOG_TAG, "Couldn't download lock screen icon, resorting to default.");
		}
	}

	// SDL method
	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus notification) {
		Log.i(LOG_TAG, "OnLockScreenNotification: " + notification);
		if(notification.getHMILevel() == HMILevel.HMI_FULL && notification.getShowLockScreen() == LockScreenStatus.REQUIRED) {
			Intent showLockScreenIntent = new Intent(this, LockScreenActivity.class);
			showLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(lockScreenManager.getLockScreenIcon() != null){
				// HUからロックスクリーン用のアイコンが取得できた場合、デフォルトで設定していた画像は上書きする
				showLockScreenIntent.putExtra(LockScreenActivity.LOCKSCREEN_BITMAP_EXTRA, lockScreenManager.getLockScreenIcon());
			}
			startActivity(showLockScreenIntent);
		}else if(notification.getShowLockScreen() == LockScreenStatus.OFF){
			sendBroadcast(new Intent(LockScreenActivity.CLOSE_LOCK_SCREEN_ACTION));
		}
	}


	// SDL method
	@Override
	public void onGetSystemCapabilityResponse(GetSystemCapabilityResponse response) {
		Log.i(LOG_TAG, "GetSystemCapabilityResponse from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	// SDL method
	@Override
	public void onServiceEnded(OnServiceEnded serviceEnded) {
		Log.i(LOG_TAG, "onServiceEnded response from SDL: " + serviceEnded.getSessionType().getName() + " Info: ");
	}

	// SDL method
	@Override
	public void onError(String info, Exception e) {
	}

	/*
	 * StreamRPC
	 */
	// SDL method
	@Override
	public void onOnStreamRPC(OnStreamRPC notification) {
		Log.i(LOG_TAG, "OnStreamRPC notification from SDL: " + notification);
	}
	@Override
	public void onStreamRPCResponse(StreamRPCResponse response) {
		Log.i(LOG_TAG, "StreamRPC response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	/*
	 * InteractionChoiceSet
	 */
	// SDL method
	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
		Log.i(LOG_TAG, "CreateInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
		Log.i(LOG_TAG, "DeleteInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	private int _getGenerateUniqueID(){
		// autoIncCorrId++;と同じ
		return CorrelationIdGenerator.generateId();
	}

	private void _setCommand() {
		MenuParams params = new MenuParams();
		params.setParentID(0);
		params.setPosition(0);
		params.setMenuName(getResources().getString(R.string.cmd_exit));

		AddCommand command = new AddCommand();
		command.setCmdID(COMMAND_ID_1);
		command.setMenuParams(params);
		command.setVrCommands(Collections.singletonList(getResources().getString(R.string.cmd_exit)));
		sendRequest(command);
	}
	/*
	 * Command
	 */
	// SDL method
	@Override
	public void onOnCommand(OnCommand notification) {
		// コマンド(サブメニュー)を選択した際のレスポンス
		Log.i(LOG_TAG, "onOnCommand");
		Integer id = notification.getCmdID();
		if(id != null) {
			switch (id) {
				case COMMAND_ID_1:
					Intent broadcast = new Intent();
					broadcast.putExtra("isFirstConnect", false);
					broadcast.setAction(getResources().getString(R.string.action_service_close));
					getBaseContext().sendBroadcast(broadcast);
					this.onDestroy();
					break;
				default:
					Log.i(DEBUG_TAG,"Button Pess : default " + id);
					break;
			}
		}
	}

	@Override
	public void onAddCommandResponse(AddCommandResponse response) {
		Log.i(LOG_TAG, "AddCommand response from SDL: " + response.getResultCode().name());
	}
	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse response) {
		Log.i(LOG_TAG, "DeleteCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	/*
	 * SubMenu
	 */
	// SDL method
	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse response) {
		Log.i(LOG_TAG, "AddSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
		Log.i(LOG_TAG, "DeleteSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	/*
	 * GlobalProperties
	 */
	// SDL method
	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {
		Log.i(LOG_TAG, "ResetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
		Log.i(LOG_TAG, "SetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	/*
	 * Button
	 */
	// SDL method
	@Override
	public void onOnButtonEvent(OnButtonEvent notification) {
		// ボタンを押下した際に呼び出されます。
		// note : OnButtonEventは、ボタンを1度押下した際に、BUTTONDOWNとBUTTONUPの2度呼び出されます。
		// 特に問題がなければOnButtonPressを使うようにしましょう。
		Log.i(LOG_TAG, "OnButtonEvent notification from SDL: " + notification);
		/*
		if (notification.getButtonEventMode().equals(ButtonEventMode.BUTTONDOWN)) {
		} else {    // ButtonEventMode.BUTTONUP
		}
		*/
	}
	@Override
	public void onOnButtonPress(OnButtonPress notification) {
		Log.i(LOG_TAG, "OnButtonPress notification from SDL: " + notification);
	}
	@Override
	public void onButtonPressResponse(ButtonPressResponse response) {
		Log.i(LOG_TAG, "ButtonPress response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
		Log.i(LOG_TAG, "SubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
		Log.i(LOG_TAG, "UnsubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	/*
	 * VehicleData
	 */
	// SDL method
	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {
		Log.i(LOG_TAG, "UnsubscribeVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
		Log.i(LOG_TAG, "GetVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
		// 車両データの登録結果が返却されます。
		// note : 複数の車両データを登録した場合、responseにてそれぞれの登録結果が判定可能です。
		Log.i(LOG_TAG, "SubscribeVehicleDataResponse response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

		if (response.getSuccess()) {
			if (response.getFuelLevel() != null) {
				usableVehicleData.put(VD_FUEL_LEVEL, response.getFuelLevel().getResultCode().equals(VehicleDataResultCode.SUCCESS));
			}
			if (response.getHeadLampStatus() != null) {
				usableVehicleData.put(VD_HEAD_LAMP_STATUS, response.getHeadLampStatus().getResultCode().equals(VehicleDataResultCode.SUCCESS));
			}
			if (response.getTirePressure() != null) {
				usableVehicleData.put(VD_TIRE_PRESSURE, response.getTirePressure().getResultCode().equals(VehicleDataResultCode.SUCCESS));
			}
			if (usableVehicleData.get(VD_FUEL_LEVEL) ||
					usableVehicleData.get(VD_HEAD_LAMP_STATUS) ||
					usableVehicleData.get(VD_TIRE_PRESSURE)) {
				isVehicleDataSubscribed = true;
			}
		}

		if (! isVehicleDataSubscribed) {
			_showTextField(getResources().getString(R.string.sorry_msg_1), getResources().getString(R.string.sorry_msg_2),null,null);
			_showImage(PIC_SORRY);
		}
	}
	@Override
	public void onOnVehicleData(OnVehicleData notification) {
		// 車両データに変更があった場合、このメソッドに通知されます。
		// note: notificationには変更のあったデータのみが格納されます。
		// 例：`beltStatus`と`speed`の2つのデータをsubscribeしている場合に`speed`が変更された場合、notificationには`beltStatus`のデータは含まれません。
		Log.i(LOG_TAG, "OnVehicleData from SDL: " + notification);

		if (usableVehicleData.get(VD_HEAD_LAMP_STATUS) && notification.getHeadLampStatus() != null) {
			_changeDisplayByHeadLampStatus(notification.getHeadLampStatus());
		}
		if (usableVehicleData.get(VD_FUEL_LEVEL) && notification.getFuelLevel() != null) {
			_changeDisplayByFuelLevel(notification.getFuelLevel());
		}
		if (usableVehicleData.get(VD_TIRE_PRESSURE) && notification.getTirePressure() != null) {
			_changeDisplayByTirePressure(notification.getTirePressure());
		}
	}


	private void _changeDisplayByTirePressure(TireStatus tire) {
		ComponentVolumeStatus inLeft = tire.getInnerLeftRear().getStatus();
		ComponentVolumeStatus inRight = tire.getInnerRightRear().getStatus();
		ComponentVolumeStatus frontLeft = tire.getLeftFront().getStatus();
		ComponentVolumeStatus frontRight = tire.getRightFront().getStatus();
		ComponentVolumeStatus rearLeft = tire.getLeftRear().getStatus();
		ComponentVolumeStatus rearRight = tire.getRightRear().getStatus();

		String textfield1 = _checkTirePressure(ComponentVolumeStatus.LOW, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);
		String textfield2 = _checkTirePressure(ComponentVolumeStatus.ALERT, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);
		String textfield3 = _checkTirePressure(ComponentVolumeStatus.FAULT, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);

		if (textfield1 != null) {
			textfield1 = textfield1.concat(getResources().getString(R.string.low_of_pressure));
		}

		if (textfield2 != null) {
			if (textfield3 != null) {
				textfield2 = String.join("、", textfield2,textfield3);
			}
			textfield2 = textfield2.concat(getResources().getString(R.string.detect_fault));
		} else if (textfield3 != null) {
			textfield2 = textfield3.concat(getResources().getString(R.string.detect_fault));
		}
		if (textfield1 == null && textfield2 != null) {
			textfield1 = textfield2;
			textfield2 = "";
		}
		if(textfield2 == null) {
			textfield2 = "";
		}

		if (textfield1 != null) {
			_showTextField(textfield1, textfield2, null, null);
			_showImage(ICON_TIRE);
		}
	}
	private String _checkTirePressure(ComponentVolumeStatus checkStatus, ComponentVolumeStatus frontLeft,
									  ComponentVolumeStatus frontRight, ComponentVolumeStatus rearLeft,
									  ComponentVolumeStatus rearRight, ComponentVolumeStatus inLeft,
									  ComponentVolumeStatus inRight) {
		List<String> list = new ArrayList<String>();
		if (checkStatus.equals(frontLeft)) {
			list.add(getResources().getString(R.string.tire_frontLeft));
		}
		if (checkStatus.equals(frontRight)) {
			list.add(getResources().getString(R.string.tire_frontRight));
		}
		if (checkStatus.equals(rearLeft)) {
			list.add(getResources().getString(R.string.tire_rearLeft));
		}
		if (checkStatus.equals(rearRight)) {
			list.add(getResources().getString(R.string.tire_rearRight));
		}
		if (checkStatus.equals(inLeft)) {
			list.add(getResources().getString(R.string.tire_inLeft));
		}
		if (checkStatus.equals(inRight)) {
			list.add(getResources().getString(R.string.tire_inRight));
		}
		if (list != null && list.size() != 0) {
			return String.join("、", list);
		}
		return null;
	}

	/**
	 * 残燃料状態に応じてメッセージを表示する
	 * @param fuelLevel
	 */
	private void _changeDisplayByFuelLevel(Double fuelLevel) {
		int fuel = fuelLevel.intValue();
		// 50%から10%刻みで通知を行う
		if(fuel <= 50 && fuel > 0){
			if (fuel % 10 == 0) {
				String str = (fuel <= 30) ? getResources().getString(R.string.fuel_under30) : "";
				_showTextField(getResources().getString(R.string.fuel_notif1) + fuel + getResources().getString(R.string.fuel_notif2), str, null, null);
				_showImage(ICON_FUEL);
			}
		}
	}

	private static boolean currentHeadLightStateIsOff = false;
	/**
	 * ヘッドランプステータスの状態変更通知があった際の処理
	 * @param lampStatus onOnVehicleData()で取得したnotification.getHeadLampStatus()
	 */
	private void _changeDisplayByHeadLampStatus(HeadLampStatus lampStatus) {
		AmbientLightStatus lightStatus = lampStatus.getAmbientLightStatus();
		if (_checkAmbientStatusIsNight(lightStatus)) {
			if (! _checkAnyHeadLightIsOn(lampStatus)){
				_showHeadLightTurnOnMsg();
			}
		} else if (lightStatus.equals(AmbientLightStatus.DAY)) {
			if(_checkAnyHeadLightIsOn(lampStatus)) {
				_showHeadLightTurnOffMsg();
			}
		}
	}
	/**
	 * 周辺光センサーの値が夜(Twilight_1～4、Night)かどうか判定する
	 * @param lightStatus AmbientLightStatus
	 * @return 周辺光が夜に該当する場合Trueを返却する
	 */
	private boolean _checkAmbientStatusIsNight(AmbientLightStatus lightStatus) {
		if (lightStatus.equals(AmbientLightStatus.TWILIGHT_1) ||
				lightStatus.equals(AmbientLightStatus.TWILIGHT_2) ||
				lightStatus.equals(AmbientLightStatus.TWILIGHT_3) ||
				lightStatus.equals(AmbientLightStatus.TWILIGHT_4) ||
				lightStatus.equals(AmbientLightStatus.NIGHT)) {
			return true;
		}
		return false;
	}
	/**
	 * ハイビームかロービームのいずれかが点灯状態にあるか確認する
	 * @param lampStatus HeadLampStatus
	 * @return いずれかが点灯状態の場合Trueを返却する
	 */
	private boolean _checkAnyHeadLightIsOn(HeadLampStatus lampStatus){
		if(lampStatus.getHighBeamsOn() || lampStatus.getLowBeamsOn()){
			return true;
		}
		return false;
	}
	private void _showHeadLightTurnOffMsg() {
		_showTextField(getResources().getString(R.string.headlight_off_msg_1), getResources().getString(R.string.headlight_off_msg_2),null,null);
		_showImage(ICON_HEADLIGHT);
	}
	private void _showHeadLightTurnOnMsg() {
		_showTextField(getResources().getString(R.string.headlight_on_msg_1), getResources().getString(R.string.headlight_on_msg_2),null,null);
		_showImage(ICON_HEADLIGHT);
	}

	/*
	 * InteriorVehicleData
	 */
	// SDL method
	@Override
	public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse response) {
		Log.i(LOG_TAG, "SetInteriorVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse response) {
		Log.i(LOG_TAG, "GetInteriorVehicleDataResponse response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onOnInteriorVehicleData(OnInteriorVehicleData notification) {
		Log.i(LOG_TAG, "OnInteriorVehicleData from SDL: " + notification);
	}

	/*
	 * FileResponse
	 */
	// SDL method
	@Override
	public void onPutFileResponse(PutFileResponse response) {
		Log.i(DEBUG_TAG, "onPutFileResponse from SDL");

		if(response.getCorrelationID() == ICON_CORRELATION_ID){ //If we have successfully uploaded our icon, we want to set it
			try {
				// アプリアイコンがアップロードされたら、setappiconでHUに表示するように指定する
				proxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
			} catch (SdlException e) {
				e.printStackTrace();
			}
		} else {
			// アプリアイコン以外の画像ファイルをアップロードした後に何らかの処理をしたい場合
			Log.i(LOG_TAG, "upload files" +
					"ID：" + response.getCorrelationID() +
					", result-code : "+response.getResultCode()+
					", getSuccess: " + response.getSuccess()+
					", getSpaceAvailable: " + response.getSpaceAvailable());
		}

	}
	@Override
	public void onDeleteFileResponse(DeleteFileResponse response) {
		Log.i(LOG_TAG, "DeleteFile response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onListFilesResponse(ListFilesResponse response) {
		Log.i(LOG_TAG, "onListFilesResponse from SDL");
	}

	/*
	 * AudioPassThru
	 */
	// SDL method
	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
		Log.i(LOG_TAG, "PerformAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
		Log.i(LOG_TAG, "EndAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onOnAudioPassThru(OnAudioPassThru notification) {
		Log.i(LOG_TAG, "OnAudioPassThru notification from SDL: " + notification );
	}

	/*
	 * WayPoints
	 */
	// SDL method
	@Override
	public void onGetWayPointsResponse(GetWayPointsResponse response) {
		Log.i(LOG_TAG, "GetWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response) {
		Log.i(LOG_TAG, "SubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response) {
		Log.i(LOG_TAG, "UnsubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}
	@Override
	public void onOnWayPointChange(OnWayPointChange notification) {
		Log.i(LOG_TAG, "OnWayPointChange notification from SDL: " + notification);
	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange notification) {
		Log.i(DEBUG_TAG, "Permision changed: " + notification);
/*
		List<PermissionItem> permissions = notification.getPermissionItem();
		for(PermissionItem permission:permissions){
			if(permission.getRpcName().equalsIgnoreCase(FunctionID.SUBSCRIBE_VEHICLE_DATA.toString())){
				if(permission.getHMIPermissions().getAllowed()!=null && permission.getHMIPermissions().getAllowed().size()>0){
					if(!isVehicleDataSubscribed){ //If we haven't already subscribed we will subscribe now
						try {
							proxy.subscribevehicledata(false,false,false,true, true, false,false, false, true, false, false, false, false, false, autoIncCorrId++);
						} catch (SdlException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
		}
*/
	}

	// SDL method
	@Override
	public void onSetAppIconResponse(SetAppIconResponse response) {
		Log.i(LOG_TAG, "SetAppIcon response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
		Log.i(LOG_TAG, "SetDisplayLayout response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo() + " CorrelationID: " + response.getCorrelationID());
		// テンプレート変更時に、登録成否が返却される
		// note: esponseにgetXXXXCapabilities()系のメソッドが用意されているものの、Nullが返却される
		/*
		if (response.getSuccess()) {
			if (response.getCorrelationID().equals(requestemplateID)) {
				// 指定したテンプレートに更新後、何らかの処理を行う場合はここで行う
				// currentTemplateName = reqTemplateName
			}
		}
		*/
	}

	// SDL method
	@Override
	public void onOnTouchEvent(OnTouchEvent notification) {
		Log.i(LOG_TAG, "OnTouchEvent notification from SDL: " + notification);
	}

	// SDL method
	@Override
	public void onOnLanguageChange(OnLanguageChange notification) {
		Log.i(LOG_TAG, "OnLanguageChange notification from SDL: " + notification);
	}

	// SDL method
	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse response) {
		Log.i(LOG_TAG, "AlertManeuver response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onAlertResponse(AlertResponse response) {
		Log.i(LOG_TAG, "Alert response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
		Log.i(LOG_TAG, "ChangeRegistration response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {
		Log.i(LOG_TAG, "DiagnosticMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onDialNumberResponse(DialNumberResponse response) {
		Log.i(LOG_TAG, "DialNumber response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onGenericResponse(GenericResponse response) {
		Log.i(LOG_TAG, "Generic response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onGetDTCsResponse(GetDTCsResponse response) {
		Log.i(LOG_TAG, "GetDTCs response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onOnDriverDistraction(OnDriverDistraction notification) {
		// Some RPCs (depending on region) cannot be sent when driver distraction is active.
		Log.i(LOG_TAG, "OnDriverDistraction from SDL");
	}

	// SDL method
	@Override
	public void onOnHashChange(OnHashChange notification) {
		Log.i(LOG_TAG, "OnHashChange notification from SDL: " + notification);
	}

	// SDL method
	@Override
	public void onOnKeyboardInput(OnKeyboardInput notification) {
		Log.i(LOG_TAG, "OnKeyboardInput notification from SDL: " + notification);
	}

	// SDL method
	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response) {
		Log.i(LOG_TAG, "PerformInteraction response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onReadDIDResponse(ReadDIDResponse response) {
		Log.i(LOG_TAG, "ReadDID response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse response) {
		Log.i(LOG_TAG, "ScrollableMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSendLocationResponse(SendLocationResponse response) {
		Log.i(LOG_TAG, "SendLocation response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSendHapticDataResponse(SendHapticDataResponse response) {
		Log.i(LOG_TAG, "SendHapticDataResponse from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
		Log.i(LOG_TAG, "SetMediaClockTimer response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {
		Log.i(LOG_TAG, "ShowConstantTbt response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSliderResponse(SliderResponse response) {
		Log.i(LOG_TAG, "Slider response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onSpeakResponse(SpeakResponse response) {
		Log.i(LOG_TAG, "SpeakCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onOnTBTClientState(OnTBTClientState notification) {
		Log.i(LOG_TAG, "OnTBTClientState notification from SDL: " + notification);
	}

	// SDL method
	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
		Log.i(LOG_TAG, "UpdateTurnList response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	// SDL method
	@Override
	public void onServiceNACKed(OnServiceNACKed serviceNACKed) {
		// Negative ACKnowledge
	}

	// SDL method
	@Override
	public void onServiceDataACK(int dataSize) {}
}
