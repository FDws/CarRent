package org.teamwe.carrent.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.teamwe.carrent.controller.utils.Format;
import org.teamwe.carrent.controller.utils.SessionAttr;
import org.teamwe.carrent.entity.User;
import org.teamwe.carrent.utils.ReturnStatus;
import org.teamwe.carrent.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author FDws
 * Created on 2018/6/20 16:51
 */
@Configuration
public class PermitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            return true;
        }

        if (allowedAll(request.getRequestURI(), request.getMethod())) {
            return true;
        }
        if (allowedUser(request)) {
            return true;
        }
        if (allowEngineer(request)) {
            return true;
        }
        if (allowedTourist(request)) {
            return true;
        }
        if (allowedAdmin(request)) {
            return true;
        }

        Format format = new Format().code(ReturnStatus.FAILURE)
                .message(StringUtil.NO_PERMISSION)
                .addData("uri", request.getRequestURI())
                .addData("method", request.getMethod())
                .addData("type", request.getSession().getAttribute(SessionAttr.USER_TYPE));
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(format));
        return false;
    }

    private boolean allowedAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String method = request.getMethod().toUpperCase();
        String url = request.getRequestURI();

        String type = "" + session.getAttribute(SessionAttr.USER_TYPE);

        if (!type.equals(User.ADMINISTRATOR + "")) {
            return false;
        }

        String[][] patterns = {
                {"/user/[^/]+", HttpMethod.DELETE.name()},
                {"/engineer", HttpMethod.GET.name()},
                {"/engineer", HttpMethod.PUT.name()},
                {"/brand", HttpMethod.POST.name()},
                {"/city", HttpMethod.POST.name()},
                {"/city", HttpMethod.PUT.name()},
        };
        for (String[] pat : patterns) {
            if (pat[0].equals(url) && pat[1].equals(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean allowedTourist(HttpServletRequest request) {
        if (request.getSession().getAttribute(SessionAttr.USER_ID) != null) {
            return false;
        }
        String url = request.getRequestURI();
        String method = request.getMethod().toUpperCase();

        String[][] patterns = {
                {"/user", HttpMethod.POST.name()},
                {"/active/[^/]+", HttpMethod.GET.name()},
                {"/session", HttpMethod.POST.name()},
        };
        for (String[] pat : patterns) {
            if (pat[0].equals(url) && pat[1].equals(method)) {
                return true;
            }
        }

        return false;
    }

    private boolean allowedAll(String url, String method) {
        method = method.toUpperCase();
        String[][] patterns = {
                {"/", HttpMethod.GET.name()},
                {"/validate", HttpMethod.GET.name()},
                {"/session/status", HttpMethod.GET.name()},
                {"/password/[^/]+", HttpMethod.GET.name()},
                {"/password", HttpMethod.PUT.name()},
                {"/type", HttpMethod.GET.name()},
                {"/brand", HttpMethod.GET.name()},
                {"/images/[^/]+", HttpMethod.GET.name()},
                {"/car", HttpMethod.GET.name()},
                {"/car/pages", HttpMethod.GET.name()},
                {"/city", HttpMethod.GET.name()},
                {"/comment/[^/]+", HttpMethod.GET.name()},
                {"/pay", HttpMethod.GET.name()}
        };

        for (String[] pat : patterns) {
            if (url.matches(pat[0]) && pat[1].equals(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean allowedUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String method = request.getMethod().toUpperCase();
        String url = request.getRequestURI();
        String[] partUrl = url.split("/");

        String type = "" + session.getAttribute(SessionAttr.USER_TYPE);
        String email = "" + session.getAttribute(SessionAttr.USER_ID);

        if (!type.equals(User.COMMEN_USER + "")) {
            return false;
        }

        String[][] patterns = {
                {"/session", HttpMethod.DELETE.name()},
                {"/car", HttpMethod.POST.name()},
                {"/order", HttpMethod.POST.name()},
        };
        for (String[] pat : patterns) {
            if (pat[0].equals(url) && pat[1].equals(method)) {
                return true;
            }
        }

        if ("/user/[^/]+".matches(url) &&
                HttpMethod.GET.name().equals(method) &&
                partUrl.length == 3 &&
                email.equals(url.split("/")[2])) {
            return true;
        }

        if ("/user".equals(url) &&
                HttpMethod.PUT.name().equals(method) &&
                email.equals(request.getParameter("email"))) {
            return true;
        }

        if ("/user/[^/]+".equals(url) &&
                HttpMethod.DELETE.name().equals(method) &&
                partUrl.length == 3 &&
                email.equals(url.split("/")[2])) {
            return true;
        }

        return "/user/[^/]+/order".equals(url) &&
                HttpMethod.GET.name().equals(method) &&
                partUrl.length == 4 &&
                email.equals(partUrl[2]);
    }

    private boolean allowEngineer(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String method = request.getMethod().toUpperCase();
        String url = request.getRequestURI();

        String type = "" + session.getAttribute(SessionAttr.USER_TYPE);

        if (!type.equals(User.ENGENEER + "")) {
            return false;
        }

        String[][] patterns = {
                {"/checkcar", HttpMethod.GET.name()},
                {"/checkcar", HttpMethod.PUT.name()},
                {"/user/[^/]+/order", HttpMethod.GET.name()},
                {"/order", HttpMethod.PUT.name()},
                {"/order/[^/]+/time", HttpMethod.DELETE.name()},
                {"/order/[^/]", HttpMethod.GET.name()},
        };
        for (String[] pat : patterns) {
            if (pat[0].equals(url) && pat[1].equals(method)) {
                return true;
            }
        }
        return false;
    }
}