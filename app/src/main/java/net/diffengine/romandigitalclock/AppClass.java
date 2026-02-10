/*
 * AppClass.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024 David Yockey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.diffengine.romandigitalclock;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.mail.BuildConfig;

import java.util.Map;

public class AppClass extends Application {
    // Storage for backup of original preference values to recover after settings activity rotation.
    public static Map<String, ?> origprefs = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this, new CoreConfigurationBuilder()
                //core configuration:
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.KEY_VALUE_LIST)
                .withPluginConfigurations(
                        new MailSenderConfigurationBuilder()
                                .withMailTo("appissues@diffengine.net")
                                .withReportAsFile(true)
                                .withReportFileName("RomanDigital_Issue.txt")
                                .withSubject(this.getPackageName() + " bug report")
                                .build(),
                        new DialogConfigurationBuilder()
                                .withText(getString(R.string.dialog_text))
                                .withTitle(getString(R.string.app_name) + " Crashed")
                                .build()
                )
        );
    }
}
