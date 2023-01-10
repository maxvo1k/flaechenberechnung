/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2022 SciJava developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.io.event;


import org.scijava.io.location.FileLocation;
import org.scijava.io.location.Location;

/**
 * An event indicating that data has been saved to a destination.
 * 
 * @author Curtis Rueden
 */
public class DataSavedEvent extends IOEvent {

	public DataSavedEvent(final Location destination, final Object data) {
		super(destination, data);
	}

	/**
	 * @deprecated use {@link #DataSavedEvent(Location, Object)} instead
	 */
	@Deprecated
	public DataSavedEvent(final String destination, final Object data) {
		this(new FileLocation(destination), data);
	}

	/**
	 * @deprecated use {@link #getLocation} instead
	 */
	@Deprecated
	public String getDestination() {
		try {
			FileLocation fileLocation = (FileLocation) getLocation();
			return fileLocation.getFile().getAbsolutePath();
		} catch(ClassCastException e) {
			return getLocation().getURI().toString();
		}
	}
}
