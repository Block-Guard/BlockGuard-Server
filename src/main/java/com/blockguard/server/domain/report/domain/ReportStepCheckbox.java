package com.blockguard.server.domain.report.domain;

import com.blockguard.server.domain.report.domain.enums.CheckboxType;
import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "report_step_checkboxes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_progress_type_index",
                columnNames = {"progress_id", "type", "box_index"}
        ))
public class ReportStepCheckbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 단일 Surrogate PK

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private CheckboxType type;

    @Column(name = "box_index", nullable = false)
    private int boxIndex;       // 단계 내에서 몇 번째 체크박스인지

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "progress_id", nullable = false, updatable = false)
    private ReportStepProgress stepProgress;

    public static ReportStepCheckbox createRequiredCheckbox(ReportStepProgress reportStepProgress, int boxIndex) {
        return ReportStepCheckbox.builder()
                .stepProgress(reportStepProgress)
                .type(CheckboxType.REQUIRED)
                .boxIndex(boxIndex)
                .build();
    }

    public static ReportStepCheckbox createRecommendedCheckbox(ReportStepProgress reportStepProgress, int boxIndex) {
        return ReportStepCheckbox.builder()
                .stepProgress(reportStepProgress)
                .type(CheckboxType.RECOMMENDED)
                .boxIndex(boxIndex)
                .build();
    }

    public void updateChecked(boolean checked) {
        this.isChecked = checked;
    }
}
