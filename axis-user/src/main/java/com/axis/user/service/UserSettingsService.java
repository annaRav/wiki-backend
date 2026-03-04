package com.axis.user.service;

import com.axis.user.model.dto.UserSettingsRequest;
import com.axis.user.model.dto.UserSettingsResponse;

public interface UserSettingsService {

    UserSettingsResponse get();

    UserSettingsResponse upsert(UserSettingsRequest request);

    void delete();
}