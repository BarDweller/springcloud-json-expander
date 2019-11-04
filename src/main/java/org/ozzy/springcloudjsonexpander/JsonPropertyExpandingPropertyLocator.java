package org.ozzy.springcloudjsonexpander;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
@ConditionalOnProperty(value = "org.ozzy.springcloudjsonexpander.enabled", matchIfMissing = true)
public class JsonPropertyExpandingPropertyLocator implements PropertySourceLocator {

    private static final Log LOG = LogFactory
			.getLog(JsonPropertyExpandingPropertyLocator.class);

    private final PropertySourceLocator[] otherpslocators;
    public JsonPropertyExpandingPropertyLocator(PropertySourceLocator[] otherpslocators){
        this.otherpslocators = otherpslocators;
    }

    private PropertySource<?> expandObject(String propertyName, String json){
        ObjectMapper mapper = new ObjectMapper();
        try{
            TypeReference<Map<String,Object>> typeRef = new TypeReference<Map<String,Object>>() {};
            Map<String,Object> map = mapper.readValue(json, typeRef);
            Map<String,Object> prefixed = new HashMap<String,Object>();
            for(Map.Entry<String,Object> entry : map.entrySet()){
                //should check if entry.getValue() is a json object to parse again
                prefixed.put(propertyName+"."+entry.getKey(), entry.getValue());
            }
            LOG.debug("*** Built new ps "+prefixed);
            return new MapPropertySource(propertyName+".prefixes", prefixed);
        }catch(Exception e){
            e.printStackTrace();;
        }
        return null;
    }

    private boolean isJson(String testValue){
        //crude, but we only want to expand json objects.
        return testValue.startsWith("{") && testValue.endsWith("}");
    }

    private void evalPropertySource(PropertySource<?> src, CompositePropertySource cps){
        LOG.debug("*** JPEPL: evaluating "+src.getName()+" "+src.getClass().getName());
        if( src instanceof EnumerablePropertySource ){
            EnumerablePropertySource<?> esrc = (EnumerablePropertySource<?>)src;
            for( String propName : esrc.getPropertyNames() ){
                String value = esrc.getProperty(propName).toString();
                if( isJson(value.trim())){
                    LOG.debug("*** JPEPL decided : '"+propName+"' is Json : "+value);
                    PropertySource<?> p = expandObject(propName, value);
                    cps.addPropertySource(p);
                }
            }
        }else{
            LOG.debug("*** JPEPL ps was not enumerable ");
        }
    }

    @Override
	public PropertySource<?> locate(Environment environment) {
        LOG.debug("*** JPEPL locate:");
        CompositePropertySource cps = new CompositePropertySource("JsonExpansion");
        if( environment instanceof ConfigurableEnvironment ){
            ConfigurableEnvironment cenv = (ConfigurableEnvironment)environment;
            for( PropertySource<?> src: cenv.getPropertySources() ) {
                evalPropertySource(src, cps);
            }
        }else{
            LOG.debug("*** JPEPL env was not configurable env "+environment.getClass().getName());
        }
        if(otherpslocators.length>0){
            LOG.debug("*** JPEPL otherps:"); 
            for(PropertySourceLocator psl : otherpslocators){
                PropertySource<?> src = psl.locate(environment);
                if(src!=null){
                    evalPropertySource(src, cps);
                }
            }
        }
        if(cps.getPropertySources().size() > 0 ){
            return cps;
        }
        return null;
    }   
}
