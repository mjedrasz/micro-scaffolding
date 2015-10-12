package com.scaffold.common.rest.config;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.mangofactory.swagger.configuration.JacksonSwaggerSupport;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SwaggerApiListingJsonSerializer;
import com.mangofactory.swagger.core.SwaggerCache;
import com.mangofactory.swagger.models.alternates.AlternateTypeRule;
import com.mangofactory.swagger.models.alternates.Alternates;
import com.mangofactory.swagger.models.alternates.WildcardType;
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerPluginAdapter;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;
import com.wordnik.swagger.model.Parameter;

import scala.Option;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.mutable.LinkedHashMap;

@Configuration 
@EnableSwagger
public class DocsConfig {

    private final static Logger logger = LoggerFactory.getLogger(DocsConfig.class);

    @Bean
    Module scalaModule() {
        DefaultScalaModule defaultScalaModule = new DefaultScalaModule();
        return defaultScalaModule;
    }

    @Bean
    public Module apiListingModule(RelProvider relProvider) {
        SimpleModule simpleModule = new SimpleModule(UUID.randomUUID().toString());
        simpleModule.addSerializer(ApiListing.class, new SwaggerApiListingJsonSerializer());
        return simpleModule;
    }

    public static AlternateTypeRule resource() {
        TypeResolver resolver = new TypeResolver();

        ResolvedType httpEntity = resolver.resolve(HttpEntity.class, WildcardType.class);
        ResolvedType pagedHttpEntity = resolver.resolve(httpEntity, PagedResources.class);

        ResolvedType wrapper = resolver.resolve(PageableAlternate.class, WildcardType.class);
        return Alternates.newRule(pagedHttpEntity, wrapper);

    }

    public abstract class PageableAlternate<T> extends ResourceSupport {

        @JsonProperty
        private PageMetadata page;

        @JsonProperty
        private Embedable<T> _embedded;
    }

    public class Embedable<T> {

        @JsonProperty
        private List<T> _list;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation(SpringSwaggerConfig springSwaggerConfig) {

        SwaggerSpringMvcPlugin apiInfo = new SwaggerSpringMvcPlugin(springSwaggerConfig).apiInfo(apiInfo());
        apiInfo.directModelSubstitute(DateTime.class, Long.class);
        apiInfo.directModelSubstitute(LocalDate.class, String.class);
        apiInfo.directModelSubstitute(LocalTime.class, String.class);
        apiInfo.alternateTypeRules(resource());
        List<RequestMappingReader> asList = new ArrayList<>();
        asList.add(new PageableParam());
        apiInfo.genericModelSubstitutes(HttpEntity.class, ResponseEntity.class);
        apiInfo.customAnnotationReaders(asList);
        apiInfo.ignoredParameterTypes(Pageable.class, PagedResourcesAssembler.class, UriComponentsBuilder.class,
                ModelAndView.class);
        apiInfo.includePatterns(".*/cms/.*", ".*/dms/.*", ".*/rms/.*", ".*/ums/.*");

        return apiInfo;
    }

    public class PageableParam implements RequestMappingReader {

        @Override
        public void execute(RequestMappingContext context) {
            HandlerMethod handlerMethod = context.getHandlerMethod();
            List<Parameter> parameters = (List<Parameter>) context.get("parameters");
            if (parameters == null) {
                parameters = newArrayList();
            }
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            if (methodParameters != null) {
                for (MethodParameter mp : methodParameters) {
                    if (mp.getParameterType().isAssignableFrom(Pageable.class)) {
                        Parameter pageParameter = new Parameter("page", toOption("page number(starting from 0)"),
                                toOption("0"), false, false, "integer", null, "query", toOption(null));
                        Parameter sizeParameter = new Parameter("size", toOption("page size"), toOption("100"), false,
                                false, "integer", null, "query", toOption(null));
                        Parameter sortParameter = new Parameter("sort", toOption("sorted fields"), toOption(""),
                                false, true, "integer", null, "query", toOption(null));
                        parameters.addAll(Arrays.asList(pageParameter, sizeParameter, sortParameter));
                    }
                }
            }
            context.put("parameters", parameters);
        }

    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("My Apps API Title", "My Apps API Description", "My Apps API terms of service",
                "My Apps API Contact Email", "My Apps API Licence Type", "My Apps API License URL");
        return apiInfo;
    }

    @Bean
    public SwaggerPluginAdapter swaggerPluginAdapter(SpringSwaggerConfig springSwaggerConfig, RelProvider relProvider) {
        return new CustomeSwaggerPluginAdapter(springSwaggerConfig, relProvider);
    }

    @Bean
    public JacksonSwaggerSupport jacksonSwaggerSupport(ApplicationContext applicationContext,
            MappingJackson2HttpMessageConverter converter) {
        return new CustomJacksonSwaggerSupport(applicationContext, converter);
    }

    private class CustomeSwaggerPluginAdapter extends SwaggerPluginAdapter {

        private final SpringSwaggerConfig springSwaggerConfig;
        private final RelProvider relProvider;

        public CustomeSwaggerPluginAdapter(SpringSwaggerConfig springSwaggerConfig, RelProvider relProvider) {
            super(springSwaggerConfig);
            this.springSwaggerConfig = springSwaggerConfig;
            this.relProvider = relProvider;
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            super.onApplicationEvent(contextRefreshedEvent);
            SwaggerCache swaggerCache = springSwaggerConfig.swaggerCache();
            Map<String, Map<String, ApiListing>> swaggerApiListingMap = swaggerCache.getSwaggerApiListingMap();
            Map<String, ApiListing> def = swaggerApiListingMap.get("default");
            Set<Entry<String, ApiListing>> entrySet = def.entrySet();
            for (Entry<String, ApiListing> entry : entrySet) {
                hack(entry.getValue());
            }
        }

        // TODO it's a big hack - anyway - refactoring is needed
        public void hack(ApiListing value) {
//            TypeResolver resolver = new TypeResolver();
//            scala.collection.immutable.List<scala.collection.immutable.Map<String, Model>> list = value.models().toList();
//            scala.collection.immutable.Map<String, Model> map = list.head();
//            Iterator<Tuple2<String, Model>> modelIterator = map.iterator();
//            while (modelIterator.hasNext()) {
//                Tuple2<String, Model> next = modelIterator.next();
//                Model m = next._2;
//                String qualifiedType = m.qualifiedType();
//                try {
//                    Class<?> forName = Class.forName(qualifiedType);
//                    ResolvedType resolve = resolver.resolve(forName);
//                    if (resolve.getParentClass() != null
//                            && resolve.getParentClass().getErasedType().equals(Resource.class)) {
//                        ResolvedType boundType = resolve.getParentClass().getTypeBindings().getBoundType(0);
//                        String name = boundType.getErasedType().getSimpleName();
//                        LinkedHashMap<String, ModelProperty> dataProperties = map.get(name).get().properties();
//                        LinkedHashMap<String, ModelProperty> properties = m.properties();
//                        properties.remove("content");
//                        Iterator<Tuple2<String, ModelProperty>> iterator = dataProperties.iterator();
//                        while (iterator.hasNext()) {
//                            Tuple2<String, ModelProperty> next2 = iterator.next();
//                            properties.put(next2._1, next2._2);
//                        }
//                        Option<Model> pageableAlternate = map.get(Embedable.class.getSimpleName() + "«" + forName.getSimpleName()
//                                + "»");
//                        if (!pageableAlternate.isEmpty()) {
//                            Model model = pageableAlternate.get();
//                            LinkedHashMap<String, ModelProperty> embededProperties = model.properties();
//                            Option<ModelProperty> option = embededProperties.get("_list");
//                            if (!option.isEmpty()) {
//                                embededProperties.remove("_list");
//                                embededProperties.put(relProvider.getCollectionResourceRelFor(boundType.getErasedType()),
//                                        option.get());
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private class CustomJacksonSwaggerSupport extends JacksonSwaggerSupport {

        private ObjectMapper springsMessageConverterObjectMapper;
        private ApplicationContext applicationContext;
        private MappingJackson2HttpMessageConverter converter;

        public CustomJacksonSwaggerSupport(ApplicationContext applicationContext,
                MappingJackson2HttpMessageConverter converter) {
            this.setApplicationContext(applicationContext);
            this.converter = converter;
        }

        public ObjectMapper getSpringsMessageConverterObjectMapper() {
            return springsMessageConverterObjectMapper;
        }

        @Autowired
        public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter[] requestMappingHandlerAdapters) {
        }

        public void setup() {
            this.springsMessageConverterObjectMapper = converter.getObjectMapper();
            this.springsMessageConverterObjectMapper.registerModule(new JodaModule());

            Map<String, DefaultModelPropertiesProvider> beans = applicationContext
                    .getBeansOfType(DefaultModelPropertiesProvider.class);

            for (DefaultModelPropertiesProvider defaultModelPropertiesProvider : beans.values()) {
                defaultModelPropertiesProvider.setObjectMapper(this.springsMessageConverterObjectMapper);
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }
}
