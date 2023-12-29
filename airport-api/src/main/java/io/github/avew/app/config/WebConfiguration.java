package io.github.avew.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.*;
import io.github.avew.app.config.properties.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.zalando.problem.jackson.ProblemModule;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableWebMvc
public class WebConfiguration extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {


    private final ApplicationProperties applicationProperties;


    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter(provideObjectMapper()));
        converters.add(new ByteArrayHttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }


    @Bean
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule().withStackTraces(false))
                .registerModule(new JavaTimeModule())
                .registerModule(new Hibernate6Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang == null || headerLang.isEmpty()
                ? Locale.getDefault()
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), Arrays.asList(
                new Locale("en"),
                new Locale("id")));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("REGISTERING CORS FILTER");
        registry.addMapping("/**")
                .allowedOrigins(applicationProperties.getCors().getAllowedOrigins().stream().toArray(String[]::new))
                .allowedMethods(applicationProperties.getCors().getAllowedMethods().stream().toArray(String[]::new))
                .allowedHeaders(applicationProperties.getCors().getAllowedHeaders().stream().toArray(String[]::new))
                .maxAge(applicationProperties.getCors().getMaxAge())
                .exposedHeaders(applicationProperties.getCors().getExposedHeaders().stream().toArray(String[]::new))
                .allowCredentials(applicationProperties.getCors().getAllowCredentials());
    }


    @Bean
    public CallableProcessingInterceptor callableProcessingInterceptor() {
        return new TimeoutCallableProcessingInterceptor() {
            @Override
            public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
                log.error("timeout!");
                return super.handleTimeout(request, task);
            }
        };
    }


    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (value, type, context) -> {
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                                    .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                    .withZone(ZoneOffset.UTC);
                            String str = dateTimeFormatter.format(value);
                            return new JsonPrimitive(str);
                        }
                )
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (jsonElement, type, context) -> {
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                    .withZone(ZoneOffset.UTC);
                            return Instant.from(dateTimeFormatter.parse(jsonElement.getAsJsonPrimitive().getAsString()));
                        }
                ).create();
    }


}


