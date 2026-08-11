package com.example.buildlog.certification.dto;
import com.example.buildlog.certification.domain.Certification; import java.time.LocalDate;
public record CertificationResponse(Long id,String name,String issuer,LocalDate acquiredDate,String credentialId,String credentialUrl){
 public static CertificationResponse from(Certification c){return new CertificationResponse(c.getId(),c.getName(),c.getIssuer(),c.getAcquiredDate(),c.getCredentialId(),c.getCredentialUrl());}}
