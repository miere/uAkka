package com.texoit.uakka.slave;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class PersistedData {

	public PersistedData(AtomicInteger integer) {
		setIntegerData(integer.get());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	Integer integerData;
}
