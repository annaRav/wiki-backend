package com.axis.user.service;

import com.axis.user.model.dto.UserProfileRequest;
import com.axis.user.model.dto.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse get();

    UserProfileResponse upsert(UserProfileRequest request);

    void delete();
}