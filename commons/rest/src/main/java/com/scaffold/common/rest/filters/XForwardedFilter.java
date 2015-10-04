package com.scaffold.common.rest.filters;

import java.io.IOException;
 
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

/**
 * For proxied requests Hateoas link builder implementation
 * {@link org.springframework.hateoas.mvc.ControllerLinkBuilder#getBuilder} expects <i>X-Forwarded-Ssl</i>. But some
 * proxies, e.g. ELB, send <i>X-Forwarded-Proto</i> instead. <i>XForwardedFilter</i> deals with this Hateoas bug and, if
 * applicable, checks <i>X-Forwarded-Proto</i> when <i>X-Forwarded-Ssl</i> is requested.
 *
 */
public class XForwardedFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (StringUtils.isNotBlank(req.getHeader("X-Forwarded-Proto"))
                && StringUtils.isBlank(req.getHeader("X-Forwarded-Ssl"))) {
            chain.doFilter(new HateoasXForwardedRequestWrapper(req), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    public class HateoasXForwardedRequestWrapper extends HttpServletRequestWrapper {

        public HateoasXForwardedRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if ("X-Forwarded-Ssl".equalsIgnoreCase(name)) {
                return "https".equalsIgnoreCase(super.getHeader("X-Forwarded-Proto")) ? "on" : null;
            }
            return super.getHeader(name);
        }
    }
}
