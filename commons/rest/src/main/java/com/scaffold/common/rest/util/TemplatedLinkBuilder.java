package com.scaffold.common.rest.util;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.lang.reflect.Method;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scaffold.common.rest.config.RootController;

public class TemplatedLinkBuilder {

    private static final MappingDiscoverer discoverer = new AnnotationMappingDiscoverer(RequestMapping.class);

    public static Link templatedLinkTo(Object methodOn, String rel) {
        DummyInvocationUtils.LastInvocationAware invocations = (DummyInvocationUtils.LastInvocationAware) methodOn;
        Method method = invocations.getLastInvocation().getMethod();
        String mapping = discoverer.getMapping(method);
        return new Link(linkTo(RootController.class).toUriComponentsBuilder().path(mapping).build().toUriString(), rel);
    }

    public static Link appendPathVariable(Link link, String name) {
        return appendUriTemplate(link, name, VariableType.SEGMENT);
    }

    public static Link appendRequestParameter(Link link, String name) {
        return appendUriTemplate(link, name, VariableType.REQUEST_PARAM);
    }

    private static Link appendUriTemplate(Link link, String name, VariableType variableType) {
        Assert.notNull(link, "Link must not be null!");
        String uri = link.getHref();
        UriTemplate uriTemplate = new UriTemplate(uri);
        return new Link(uriTemplate.with(new TemplateVariables(new TemplateVariable(name, variableType, name))),
                link.getRel());
    }

}
