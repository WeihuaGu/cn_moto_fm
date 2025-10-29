package com.testuse.motofm;

public interface TransactionSearchListener {
    void onDiscoveryStart();
    void onTransactionFound(int transactionId, int cmd);
    void onDiscoveryEnd();
}
