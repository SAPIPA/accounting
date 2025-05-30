package org.vrk.accounting.security;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tess4JConfig {
    @Value("${tess4j.datapath}")
    private String datapath;
    @Value("${tess4j.language}")
    private String language;

    @Bean
    public ITesseract tesseract() {
        Tesseract tess = new Tesseract();
        tess.setDatapath(datapath);
        tess.setLanguage(language);
        return tess;
    }
}
