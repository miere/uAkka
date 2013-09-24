package com.texoit.uakka.cluster;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName="create")
public class CircularQueue<T> {

	final List<T> data = new ArrayList<T>();
	int current = 0;

	public T next() {
		updateCursor();
		T item = data.get(current);
		if ( item != null )
			current++;
		return item;
	}

	void updateCursor(){
		if ( data.size() < current )
			current = 0;
	}

	public void pull( T item ) {
		data.add( item );
	}
	
	public void remove( T item ) {
		data.remove(current);
	}
}
