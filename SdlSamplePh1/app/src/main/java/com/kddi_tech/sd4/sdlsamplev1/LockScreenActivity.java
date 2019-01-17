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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class LockScreenActivity extends Activity {
    private static final String LOG_TAG           = "[Log:[LockScreenActivity]]";
    public static final String LOCKSCREEN_BITMAP_EXTRA = "LOCKSCREEN_BITMAP_EXTRA";
    public static final String CLOSE_LOCK_SCREEN_ACTION = "CLOSE_LOCK_SCREEN";

    private final BroadcastReceiver closeLockScreenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        registerReceiver(closeLockScreenBroadcastReceiver, new IntentFilter(CLOSE_LOCK_SCREEN_ACTION));
        setContentView(R.layout.activity_lock_screen);

        Intent intent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.lockscreen);

        if(intent.hasExtra(LOCKSCREEN_BITMAP_EXTRA)){
            Bitmap lockscreen = (Bitmap) intent.getParcelableExtra(LOCKSCREEN_BITMAP_EXTRA);
            if(lockscreen != null){
                imageView.setImageBitmap(lockscreen);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        unregisterReceiver(closeLockScreenBroadcastReceiver);
        super.onDestroy();
    }
}
