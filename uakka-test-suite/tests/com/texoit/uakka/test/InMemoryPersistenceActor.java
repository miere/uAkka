package com.texoit.uakka.test;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

import com.texoit.uakka.api.HandledActor;
import com.texoit.uakka.api.Receiver;
import com.texoit.uakka.api.Service;

@Service
@Log
public class InMemoryPersistenceActor extends HandledActor {

	List<String> persistence = new ArrayList<String>();

	@Receiver
	Saved persist( Persist dataToPersist ) {
		log.info("Persisting data: " + dataToPersist.data());
		synchronized( persistence ) {
			persistence.add(dataToPersist.data());
		}
		return Saved.successfully();
	}

	@Receiver
	FoundRegisters retrieve( Retrieve retrieve ){
		log.info("Retrieving data...");
		synchronized( persistence ) {
			if ( persistence.size() == 0 ){
				log.info("nothing.");
				return FoundRegisters.nothing();
			} else {
				log.info(persistence.size() + " registers.");
				return FoundRegisters.data(persistence);
			}
		}
	}

	@Value
	@Accessors(fluent=true)
	@RequiredArgsConstructor(staticName="data")
	public static class Persist {
		final String data;
	}

	@Accessors(fluent=true)
	@NoArgsConstructor(staticName="everything")
	public static class Retrieve {}

	@Accessors(fluent=true)
	@NoArgsConstructor(staticName="successfully")
	public static class Saved {}

	@Accessors(fluent=true)
	@Getter
	@NoArgsConstructor( staticName="nothing" )
	@RequiredArgsConstructor(staticName="data")
	public static class FoundRegisters {
		@NonNull List<String> data;
	}

}
