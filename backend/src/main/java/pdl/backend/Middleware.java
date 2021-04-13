package pdl.backend;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

@Component("middleware")
public class Middleware extends OncePerRequestFilter {
    public static final Locale defaultLocale = Locale.ENGLISH;
    public static Locale requestLocale = Locale.ENGLISH;

    @Override
    @RequestMapping()
    @ResponseBody
    protected void doFilterInternal(HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain) throws ServletException, IOException {
        requestLocale = Locale.ENGLISH;
        if (httpServletRequest.getLocale() != requestLocale) {
            detectLocaleFromRequest(httpServletRequest);
        }
        setLocaleToResponse(httpServletResponse);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void detectLocaleFromRequest(HttpServletRequest request) {
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            Locale l = locales.nextElement();
            // Check if l is in supportedLanguages
            if (Internationalization.getInstance().getAllJsonLang().containsKey(l.getLanguage())) {
                requestLocale = l;
                break;
            }
        }
    }

    private void setLocaleToResponse(HttpServletResponse response) {
        response.setLocale(requestLocale);
    }

    public static Locale getLocale() {
        return requestLocale;
    }

}
