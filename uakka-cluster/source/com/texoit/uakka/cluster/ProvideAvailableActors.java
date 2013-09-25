package com.texoit.uakka.cluster;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent=true)
@RequiredArgsConstructor(staticName="create")
public class ProvideAvailableActors implements Serializable {

	private static final long serialVersionUID = 5111068916455410861L;

}
