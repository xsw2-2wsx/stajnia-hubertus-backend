package net.jupw.hubertus.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver

@Configuration
class ThymeleafConfiguration {

    @Bean
    @Scope("singleton")
    fun getTemplateResolver(): ITemplateResolver = ClassLoaderTemplateResolver().apply {
        templateMode = TemplateMode.HTML
        prefix = "/templates/"
        suffix = ".html"
    }

    @Bean
    @Scope("singleton")
    fun getTemplateEngine(templateResolver: ITemplateResolver): ITemplateEngine = TemplateEngine().apply {
        setTemplateResolver(templateResolver)
    }

}