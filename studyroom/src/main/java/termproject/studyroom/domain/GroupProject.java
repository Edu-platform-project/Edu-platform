package termproject.studyroom.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "GroupProjects")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GroupProject {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Integer gpId;

    @Column(nullable = false)
    private Boolean groupValid;

    @Column(nullable = false, unique = true, length = 10)
    private String groupName;

    @Column(columnDefinition = "text")
    private String groupDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user_id_id", nullable = false)
    private User createdUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id_id", nullable = false)
    private LectureList lectureId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
