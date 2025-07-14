package com.blockguard.server.domain.user.domain;

import com.blockguard.server.domain.analysis.domain.FraudAnalysisRecord;
import com.blockguard.server.domain.guardian.domain.Guardian;
import com.blockguard.server.domain.user.domain.enums.Gender;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@SQLRestriction("deleted_at IS NULL")
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "profile_image_key", length = 512)
    private String profileImageKey;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Guardian> guardians = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserReportRecord> userReportRecords = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FraudAnalysisRecord> fraudAnalysisRecords = new ArrayList<>();

    /**
     * Marks the user as logically deleted by setting the deletion timestamp to the current time.
     */
    public void markDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Updates the user's password to the specified value.
     *
     * @param tempPassword the new password to set for the user
     */
    public void changePassword(String tempPassword) {
        this.password = tempPassword;
    }
}
