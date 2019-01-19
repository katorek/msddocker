package com.wjaronski.studies.pmd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Wojciech Jaronski
 */

@Configuration
@EnableJpaRepositories("com.wjaronski.studies.pmd.repositories")
public class Config {

}
