package com.moonkeyeu.etl.api.repository.repos;

import com.moonkeyeu.etl.api.model.launch.NetPrecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetPrecisionRepository extends JpaRepository<NetPrecisionEntity, Long> {}
