package com.test.apex;

public interface VolleyOnEventListener<T> {
    public void onSuccess(String response);

    public void onFailure(String error);
}
