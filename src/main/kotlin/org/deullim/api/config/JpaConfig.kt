package org.deullim.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * JPA Auditing 활성화 전용 설정.
 *
 * `@EnableJpaAuditing`을 메인 애플리케이션 클래스가 아닌 별도 `@Configuration`에 두어
 * 슬라이스 테스트(`@WebMvcTest` 등)에 감사 인프라가 끌려오지 않도록 격리하고,
 * 추후 `AuditorAware`(createdBy/modifiedBy) 빈 등록 지점을 한곳에 모은다.
 */
@Configuration
@EnableJpaAuditing
class JpaConfig
