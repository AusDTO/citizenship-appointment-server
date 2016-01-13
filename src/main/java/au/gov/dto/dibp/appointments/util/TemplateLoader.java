package au.gov.dto.dibp.appointments.util;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class TemplateLoader {

    private final ResourceLoader resourceLoader;
    private final Mustache.Compiler mustacheCompiler;

    @Autowired
    public TemplateLoader(Mustache.Compiler mustacheCompiler, ResourceLoader resourceLoader) {
        this.mustacheCompiler = mustacheCompiler;
        this.resourceLoader = resourceLoader;
    }

    public Template loadTemplateByPath(String requestTemplatePath){
        Resource resource = resourceLoader.getResource("classpath:request_templates/" + requestTemplatePath);
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading request template: " + requestTemplatePath, e);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return mustacheCompiler.compile(inputStreamReader);
    }
}
