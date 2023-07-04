package oleksii.stepaniak.noteswebapplication.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import oleksii.stepaniak.noteswebapplication.model.UserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JsonWebTokenFilter extends OncePerRequestFilter {
    private static final String AUTHENTICATION_PATH = "/authentication/authenticate";
    private static final String REGISTRATION_PATH = "/users/register";
    private final UserDetailsService userDetailsService;
    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String username;
        final String jwtToken;
        final String path = request.getServletPath();
        if (path.contains(AUTHENTICATION_PATH) || path.contains(REGISTRATION_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is already expired!");
            response.getWriter().flush();
            return;
        }
        jwtToken = authenticationHeader.substring(7);
        try {
            username = jsonWebTokenUtil.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is already expired!");
            response.getWriter().flush();
            return;
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean isTokenValid;
            try {
                UserDetails userDetails = (UserDetails) userDetailsService
                        .loadUserByUsername(username);
                isTokenValid = jsonWebTokenUtil.validateToken(jwtToken, userDetails);
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("The owner of the token doesn't exist!");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
