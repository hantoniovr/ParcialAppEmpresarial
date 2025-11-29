package uts.corte3;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppContext implements ApplicationContextAware {
    private static ApplicationContext ctx;
    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { ctx = applicationContext; }
    public static PasswordEncoder getEncoder() { return ctx.getBean(PasswordEncoder.class); }
}
