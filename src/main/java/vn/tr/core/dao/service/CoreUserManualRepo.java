package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreUserManual;

@Repository
public interface CoreUserManualRepo extends JpaRepository<CoreUserManual, Long>, JpaSpecificationExecutor<CoreUserManual> {

}
