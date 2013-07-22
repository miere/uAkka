package io.skullabs.uakka.inject;

import lombok.Data;

@Data
public class SampleHelloClass {

	InjectableInterface injectableFromInterface;

	@InjectableAnnotation
	String injectableFromAnnotation;

}
