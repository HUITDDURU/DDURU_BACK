package com.huitdduru.madduru.user.service;

import com.huitdduru.madduru.diary.entity.Diary;
import com.huitdduru.madduru.diary.repository.DiaryRepository;
import com.huitdduru.madduru.email.entity.RandomCode;
import com.huitdduru.madduru.exception.exceptions.*;
import com.huitdduru.madduru.email.repository.RandomCodeRepository;
import com.huitdduru.madduru.s3.FileUploader;
import com.huitdduru.madduru.security.TokenProvider;
import com.huitdduru.madduru.user.entity.User;
import com.huitdduru.madduru.user.payload.response.AuthResponse;
import com.huitdduru.madduru.user.payload.response.ImageResponse;
import com.huitdduru.madduru.user.repository.UserRepository;
import com.huitdduru.madduru.user.payload.request.AuthRequest;
import com.huitdduru.madduru.user.payload.request.RegisterRequest;
import com.huitdduru.madduru.user.payload.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final RandomCodeRepository randomCodeRepository;

    private final FileUploader fileUploader;

    private final static Random RANDOM = new Random();
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.jwt.exp.refresh}")
    private Long ttl;

    @Override
    public void register(RegisterRequest registerRequest) {
        randomCodeRepository.findByEmail(registerRequest.getEmail())
                .filter(RandomCode::isVerified)
                .orElseThrow(UserNotAccessExcepion::new);

        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyException();
                });

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isExist(true)
                .intro(registerRequest.getIntro())
                .code(generateRandomCode())
                .imagePath(registerRequest.getImageUrl())
                .build();

        userRepository.save(user);
        myDiary(user);
    }

    @Override
    public AuthResponse auth(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
                .filter(u -> passwordEncoder.matches(authRequest.getPassword(), u.getPassword()))
                .orElseThrow(UserNotFoundException::new);

        Diary diary = diaryRepository.findByUser1AndUser2(user, user);

        return AuthResponse.builder()
                .diaryId(diary.getId())
                .accessToken(tokenProvider.generateAccessToken(authRequest.getEmail()))
                .refreshToken(tokenProvider.generateRefreshToken(user.getEmail()))
                .build();
    }

    @Override
    public TokenResponse refreshToken(String token) {
        return TokenResponse.builder()
                .accessToken(tokenProvider.generateAccessToken(tokenProvider.getEmail(token)))
                .refreshToken(token)
                .build();
    }

    @Override
    public ImageResponse uploadImage(MultipartFile file) throws IOException {
        return ImageResponse.builder()
                .imageUrl(fileUploader.uploadFile(file))
                .build();
    }

    private String generateRandomCode() {
        RANDOM.setSeed(System.currentTimeMillis());
        return Integer.toString(RANDOM.nextInt(1000000));
    }

    private void myDiary(User user) {
        Diary diary = Diary.builder()
                .user1(user)
                .user2(user)
                .createdAt(LocalDateTime.now())
                .relationContinues(true)
                .build();
        diaryRepository.save(diary);
    }

}
