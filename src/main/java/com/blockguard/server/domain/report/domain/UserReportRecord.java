package com.blockguard.server.domain.report.domain;

import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "user_report_records")
public class UserReportRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStep step;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserReportRecordCheckbox> checkboxes = new ArrayList<>();
}
