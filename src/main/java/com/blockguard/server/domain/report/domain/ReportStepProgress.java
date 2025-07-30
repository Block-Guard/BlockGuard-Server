package com.blockguard.server.domain.report.domain;

import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.blockguard.server.domain.report.domain.enums.ReportStepCheckboxConfig;
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
@Table(
        name = "report_step_progress",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_record_step",
                columnNames = {"record_id", "step"}
        )
)
public class ReportStepProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStep step;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false, updatable = false)
    private UserReportRecord record;

    @OneToMany(mappedBy = "stepProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReportStepCheckbox> checkboxes = new ArrayList<>();

    public static ReportStepProgress createWithDefaultCheckboxes(UserReportRecord userReportRecord, ReportStep step) {
        ReportStepProgress progress = ReportStepProgress.builder()
                .record(userReportRecord)
                .step(step)
                .build();

        ReportStepCheckboxConfig cfg = ReportStepCheckboxConfig.of(step);

        // 필수 체크박스
        for (int i = 0; i < cfg.getRequiredCount(); i++) {
            progress.addCheckbox(
                    ReportStepCheckbox.createRequiredCheckbox(progress, i)
            );
        }

        // 권장 체크박스
        for (int i = 0; i < cfg.getRecommendedCount(); i++) {
            progress.addCheckbox(
                    ReportStepCheckbox.createRecommendedCheckbox(progress, i)
            );
        }
        return progress;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public void addCheckbox(ReportStepCheckbox checkbox) {
        this.checkboxes.add(checkbox);
    }
}
