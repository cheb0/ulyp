package com.ulyp.storage.search;

import com.ulyp.core.EnterMethodCall;
import com.ulyp.core.ExitMethodCall;
import com.ulyp.core.MethodCall;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StubSearchResultListener implements SearchResultListener {

    private final List<MethodCall> matchedCalls = new ArrayList<>();

    @Override
    public void onStart() {

    }

    @Override
    public void onMatch(int recordingId, EnterMethodCall enterMethodCall) {
        matchedCalls.add(enterMethodCall);
    }

    @Override
    public void onMatch(int recordingId, ExitMethodCall exitMethodCall) {
        matchedCalls.add(exitMethodCall);
    }

    @Override
    public void onEnd() {

    }
}
