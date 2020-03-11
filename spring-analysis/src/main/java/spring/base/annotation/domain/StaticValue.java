package spring.base.annotation.domain;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;


public class StaticValue implements EmbeddedValueResolverAware {
	private static String value;

	public String getValue() {
		return value;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		StaticValue.value =resolver.resolveStringValue("${value.one}");
	}


}
