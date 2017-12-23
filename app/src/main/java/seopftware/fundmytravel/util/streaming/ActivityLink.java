package seopftware.fundmytravel.util.streaming;

import android.content.Intent;

/**
 * Created by MSI on 2017-12-22.
 */

public class ActivityLink {
    private final int minSdk;
    private final String label;
    private final Intent intent;

    public ActivityLink(Intent intent, String label, int minSdk) {
        this.intent = intent;
        this.label = label;
        this.minSdk = minSdk;
    }

    public String getLabel() {
        return label;
    }

    public Intent getIntent() {
        return intent;
    }

    public int getMinSdk() {
        return minSdk;
    }
}