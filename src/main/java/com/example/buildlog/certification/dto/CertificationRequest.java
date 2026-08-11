package com.example.buildlog.certification.dto;
import jakarta.validation.constraints.*; import java.time.LocalDate;
public record CertificationRequest(@NotBlank @Size(max=150) String name,@NotBlank @Size(max=150) String issuer,
 @NotNull LocalDate acquiredDate,@Size(max=150) String credentialId,@Size(max=500) String credentialUrl){}
