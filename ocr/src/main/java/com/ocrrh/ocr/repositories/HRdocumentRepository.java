package com.ocrrh.ocr.repositories;


import com.ocrrh.ocr.entites.HRdocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HRdocumentRepository extends JpaRepository<HRdocument, Long> {
}
