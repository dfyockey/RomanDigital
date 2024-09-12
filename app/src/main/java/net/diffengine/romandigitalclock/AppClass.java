package net.diffengine.romandigitalclock;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.mail.BuildConfig;

public class AppClass extends Application {
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
