package au.gov.dto.dibp.appointments.util;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class FakeTemplateLoader extends TemplateLoader {

    public FakeTemplateLoader() {
        super(null, null);
    }

    @Override
    public Template loadRequestTemplate(String requestTemplatePath){
        return new Template(null, Mustache.compiler()){
            @Override
            public String toString(){
                return requestTemplatePath;
            }
        };
    }
}
