package net.jupw.hubertus.app.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var jwtConf: JWTConfiguration

    @Autowired
    private lateinit var authEntryPoint: AuthEntryPoint

    @Autowired
    private lateinit var userDetailsService: AppUserDetailsService

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.userDetailsService(userDetailsService)
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity?) {
        if(http == null) return

        http.cors()
            .and()
            .csrf()
            .disable()

        http.addFilterBefore(JWTAuthenticationFilter(userDetailsService, jwtConf), UsernamePasswordAuthenticationFilter::class.java)

        http.exceptionHandling().authenticationEntryPoint(authEntryPoint)

        http.authorizeRequests()
            .antMatchers("/auth").permitAll()
            .anyRequest().authenticated()

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }


    @Bean
    fun encoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}