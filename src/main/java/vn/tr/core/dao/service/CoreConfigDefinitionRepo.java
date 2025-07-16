package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreConfigDefinition;

@Repository
public interface CoreConfigDefinitionRepo extends JpaRepository<CoreConfigDefinition, Long>, JpaSpecificationExecutor<CoreConfigDefinition> {

}
