package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_journal_detail")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreJournalDetail extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "journal_id")
	private Long journalId;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@Column(name = "property", length = 50)
	private String property;
	
	@Column(name = "prop_key", length = 250)
	private String propKey;
	
	@Column(name = "old_value", columnDefinition = "TEXT")
	private String oldValue;
	
	@Column(name = "new_value", columnDefinition = "TEXT")
	private String newValue;
	
}
