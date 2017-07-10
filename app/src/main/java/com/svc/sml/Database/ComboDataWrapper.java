package com.svc.sml.Database;

import java.io.Serializable;
import java.util.List;

/**
 * Created by himanshu on 1/12/16.
 */
public class ComboDataWrapper implements Serializable {

    public class Reconcile implements Serializable {
        public static final String TAG = "ReconcileComboDataResult";
        private List<ComboDataReconcile> ReconcileComboDataResult;

        public List<ComboDataReconcile> getComboDataReconcile() {
            return ReconcileComboDataResult;
        }
    }

    public class ComboDetail implements Serializable {

        public static final String TAG = "GetComboSKUInfoResult";
        private List<ComboData> GetComboSKUInfoResult;

        public List<ComboData> getComboDataReconcile() {
            return GetComboSKUInfoResult;
        }
    }
}
