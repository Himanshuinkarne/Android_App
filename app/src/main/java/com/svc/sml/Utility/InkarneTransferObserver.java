package com.svc.sml.Utility;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

/**
 * Created by himanshu on 12/24/15.
 */
public class InkarneTransferObserver {

    private TransferObserver observer;
    private int observerId;
    private  String deviceFilePath;
    private  String awsFileKeyPath;
    public InkarneTransferObserver(){

    }
    public InkarneTransferObserver(TransferObserver observer, String awsKey, String deviceFilePath){
        this.observer = observer;
        this.deviceFilePath = deviceFilePath;
        this.awsFileKeyPath = awsKey;
    }

    public InkarneTransferObserver(TransferObserver observer, String awsKey){
        this.observer = observer;
        this.awsFileKeyPath = awsKey;
    }

    public TransferObserver getObserver() {
        return observer;
    }

    public void setObserver(TransferObserver observer) {
        this.observer = observer;
    }

    public int getObserverId() {
        return observerId;
    }

    public void setObserverId(int observerId) {
        this.observerId = observerId;
    }

    public String getDeviceFilePath() {
        return deviceFilePath;
    }

    public void setDeviceFilePath(String deviceFilePath) {
        this.deviceFilePath = deviceFilePath;
    }

    public String getAwsFileKeyPath() {
        return awsFileKeyPath;
    }

    public void setAwsFileKeyPath(String awsFileKeyPath) {
        this.awsFileKeyPath = awsFileKeyPath;
    }


}
