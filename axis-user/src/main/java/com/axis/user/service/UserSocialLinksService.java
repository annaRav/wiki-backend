package com.axis.user.service;

import com.axis.user.model.dto.UserSocialLinksRequest;
import com.axis.user.model.dto.UserSocialLinksResponse;

public interface UserSocialLinksService {

    UserSocialLinksResponse get();

    UserSocialLinksResponse upsert(UserSocialLinksRequest request);

    void delete();
}