package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;
import vn.tr.common.jpa.entity.SoftDeletable;

@Entity
@Table(name = "core_tag_assignment")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE core_tag_assignment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreTagAssignment extends BaseEntity implements Identifiable<Long>, SoftDeletable {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "tag_code")
	private String tagCode;
	
	@Column(name = "taggable_value", length = 100)
	private String taggableValue;
	
	@Column(name = "taggable_type", length = 50)
	private String taggableType;
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	@Builder.Default
	private Integer sortOrder = 0;
	
}
