package com.example.buildlog.certification.domain;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDate;
@Entity @Table(name="certifications") @Getter @NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Certification { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=150) private String name; @Column(nullable=false,length=150) private String issuer;
 @Column(nullable=false) private LocalDate acquiredDate; @Column(length=150) private String credentialId; @Column(length=500) private String credentialUrl;
 public Certification(String n,String i,LocalDate d,String id,String url){update(n,i,d,id,url);}
 public void update(String n,String i,LocalDate d,String id,String url){name=n;issuer=i;acquiredDate=d;credentialId=id;credentialUrl=url;}
}
