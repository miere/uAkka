package com.texoit.uakka.inject;

import lombok.Data;

@Data
public class SampleHelloClass {

	InjectableInterface injectableFromInterface;

	@InjectableAnnotation
	String injectableFromAnnotation;
}
