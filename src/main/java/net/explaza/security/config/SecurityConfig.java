package net.explaza.security.config;

import lombok.AllArgsConstructor;
import net.explaza.security.factory.UrlResourcesMapFactoryBean;
import net.explaza.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import net.explaza.security.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private MemberService memberService;
    @Autowired
    private UserDetailsService userDetailsService;
        /* 로그인 실패 핸들러 의존성 주입 */
    private final AuthenticationFailureHandler customFailureHandler;

    @Autowired
    private SecurityResourceService securityResourceService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception
    {
//         static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 )
//        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        logger.info("#1", http.authorizeRequests().toString());

        http
                .authorizeRequests()
                // 페이지 권한 설정
                .antMatchers(HttpMethod.GET,"/login","/denied").permitAll() // 인증절차 없이 허용
                .antMatchers(HttpMethod.GET, "/main").authenticated() //인증된 사용자
//                .antMatchers(HttpMethod.POST, "/user/my/password/edit").authenticated() //인증된 사용자
                .anyRequest().authenticated()
            .and() // 로그인 설정
                .csrf()
//                .ignoringAntMatchers("/api/bulk")
//                .ignoringAntMatchers("/api/one/**")
            .and()
                .formLogin()
                .loginPage("/login")
                .failureHandler(customFailureHandler) // 로그인 실패 핸들러
                .defaultSuccessUrl("/main", true)
//                .successHandler(new CustomSuccessHandler())
                .permitAll()
            .and()  // 403 예외처리 핸들링
                .exceptionHandling()
                .accessDeniedPage("/denied")
            .and() // 로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
            .and()
                .sessionManagement()
                .maximumSessions(1)                 // 최대 허용 가능 세션 수 , -1 : 무제한 로그인 세션 허용
                .maxSessionsPreventsLogin(false) //true: 동시 로그인 차단함,  false : 기존 세션 만료(default)
                .expiredUrl("/login")             // 세션이 만료된 경우 이동 할 페이지
                .sessionRegistry(sessionRegistry())
            .and()
                .sessionFixation().migrateSession()
            .and()
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
                .httpBasic();


//        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }
    // logout 후 login할 때 정상동작을 위함
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//        auth.userDetailsService(userDetailsService);
    }

///////////////////////////////////////////////////////////////////////////////
    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());   // 권한정보 셋팅
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());    // 인증매니저
        return filterSecurityInterceptor;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }


    private List<AccessDecisionVoter<? extends Object>> getAccessDecisionVoters() {
        return Arrays.asList(new RoleVoter());
    }


    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject());
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    // 인증 매니저
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



}