package com.svc.sml.Model;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.Serializable;

/**
 * Created by himanshu on 1/20/16.
 */
public class TransferProgressModel implements Serializable {

    public TransferProgressModel(){
        this.progress = 0;
        this.transferState = TransferState.UNKNOWN;
    }

    public TransferProgressModel(String filename){
        this.filename = filename;
        this.progress = 0;
        this.transferState = TransferState.WAITING;
    }
    public TransferProgressModel(String filename, int progress,TransferState transferState){
        this.filename = filename;
        this.progress = progress;
        this.transferState = transferState;
    }
    public String filename;
    public int progress;
    public TransferState transferState;
    public String awsUploadKeyPath;
}
