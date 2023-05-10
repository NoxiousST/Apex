package com.test.apex;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface VolleyOnEventListener<T> {
    public void onSuccess(String response) throws JsonProcessingException;

    public void onFailure(String error);
}
