package capsrock.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    private final MemberService memberService;
//    private final JwtInterceptor jwtInterceptor;
//    private final AdminInterceptor adminInterceptor;
//    private final JwtAuth jwtAuth;
//
//    @Autowired
//    WebMvcConfig(MemberService memberService, JwtInterceptor jwtInterceptor,
//            AdminInterceptor adminInterceptor, JwtAuth jwtAuth) {
//        this.memberService = memberService;
//        this.jwtInterceptor = jwtInterceptor;
//        this.adminInterceptor = adminInterceptor;
//        this.jwtAuth = jwtAuth;
//    }
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/api/**", "/admin/**")
//                .excludePathPatterns("/api/oauth/**", "/resources/**");
//
//        //토큰 받는 경로 지정
//        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/page/**");
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(new LoginMemberArgumentResolver(memberService, jwtAuth));
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("http://localhost:*", "https://localhost:*",
//                        "https://hometry.vercel.app")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .exposedHeaders(HttpHeaders.LOCATION)
//                .allowCredentials(true);
//    }

}
