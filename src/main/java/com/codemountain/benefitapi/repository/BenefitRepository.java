package com.codemountain.benefitapi.repository;

import com.codemountain.benefitapi.entities.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
}
