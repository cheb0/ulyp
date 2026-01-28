package com.ulyp.storage.search;

import com.ulyp.core.EnterMethodCall;
import com.ulyp.core.ExitMethodCall;

public interface SearchResultListener {

    void onStart();

    void onMatch(int recordingId, EnterMethodCall enterMethodCall);

    void onMatch(int recordingId, ExitMethodCall exitMethodCall);

    void onEnd();
}
