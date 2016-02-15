package com.dogether.dogether.customcamera.utils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.regions.Region;
import com.dogether.dogether.customcamera.ui.activities.MainActivity;

import java.net.URL;

/**
 * Created by dogether on 15/2/16.
 */
public class Util {

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider  = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    Constants.COGNITO_POOL_ID, // Identity Pool ID
                    Regions.AP_NORTHEAST_1 // Region
            );
        }
        return sCredProvider;
    }

    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
            sS3Client.setEndpoint("s3-ap-northeast-1.amazonaws.com");
        }
        return sS3Client;
    }


    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    public static URL getBucketURL(){
        return(sS3Client.getUrl(Constants.BUCKET_NAME, MainActivity.TAG));
    }

    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[] {
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0;; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

}
