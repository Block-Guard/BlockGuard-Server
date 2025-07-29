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
@Table(
        name = "user_report_record_checkboxes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_record_type_index",
                columnNames = {"record_id", "type", "box_index"}
        )
)
public class UserReportRecordCheckbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 단일 Surrogate PK

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private CheckboxType type;

    @Column(name = "box_index", nullable = false)
    private int boxIndex;       // 단계 내에서 몇 번째 체크박스인지

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private UserReportRecord record;
}
