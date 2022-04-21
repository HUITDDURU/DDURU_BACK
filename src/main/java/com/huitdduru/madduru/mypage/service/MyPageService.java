package com.huitdduru.madduru.mypage.service;

import com.huitdduru.madduru.mypage.payload.request.IntroRequest;
import com.huitdduru.madduru.diary.payload.response.DiaryResponse;
import com.huitdduru.madduru.mypage.payload.response.MyInfoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MyPageService {

    void updateIntroduction(IntroRequest introRequest);

    void updateProfileImage(MultipartFile image) throws IOException;

    void unregister();

    List<DiaryResponse> queryDiaryList();

    MyInfoResponse queryMyInfo();

}
