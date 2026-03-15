package com.moonkeyeu.core.api.configuration.auditing;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Source - <a href="https://stackoverflow.com/a/60608499">Spring Boot JPA metamodel must not be empty</a>
 Posted by Josef Cech, modified by community. See post 'Timeline' for change history
 Retrieved 2026-03-13, License - CC BY-SA 4.0 **/
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {}
