package co.mscsea.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;

public class PenUtils {
	
	public static boolean processUnsupportedException(final Activity activity, SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED ||
            errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            Toast.makeText(activity, "This device does not support Spen.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } 
        else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            showAlertDialog(activity, 
			                "You need to install additional Spen software"
			                +" to use this application."
			                + "You will be taken to the installation screen."
			                + "Restart this application after the software has been installed."
			                , true);
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            showAlertDialog(activity,
			                "You need to update your Spen software to use this application."
			                + " You will be taken to the installation screen." 
			                + " Restart this application after the software has been updated."
			                , true);
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            showAlertDialog(activity,
			                "We recommend that you update your Spen software"
			                + " before using this application."
			                + " You will be taken to the installation screen."
			                + " Restart this application after the software has been updated."
			                , false);
            return false;
        }
        
        return true;
    }

    private static void showAlertDialog(final Activity activity, String msg, final boolean closeActivity) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setIcon(activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
        dlg.setTitle("Upgrade Notification")
            .setMessage(msg)
            .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                        DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("market://details?id=" + Spen.SPEN_NATIVE_PACKAGE_NAME);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);

                        dialog.dismiss();
                        activity.finish();
                    }
                })
            .setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                        DialogInterface dialog, int which) {
                        if(closeActivity == true) {
                            activity.finish();
                        }
                        dialog.dismiss();
                    }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(closeActivity == true) {
                        activity.finish();
                    }
                }
            })
            .show();
        dlg = null;
    }
}
