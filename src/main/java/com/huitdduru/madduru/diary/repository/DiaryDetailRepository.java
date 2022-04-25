package com.huitdduru.madduru.diary.repository;

import com.huitdduru.madduru.diary.entity.Diary;
import com.huitdduru.madduru.diary.entity.DiaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryDetailRepository extends JpaRepository<DiaryDetail, Integer> {

    List<DiaryDetail> findByDiaryOrderByCreatedAt(Diary diary);

}
