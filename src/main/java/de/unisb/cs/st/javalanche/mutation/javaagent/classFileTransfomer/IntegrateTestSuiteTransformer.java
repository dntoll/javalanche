/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;

public class IntegrateTestSuiteTransformer implements
		ClassFileTransformer {

	private static Logger logger = Logger
			.getLogger(IntegrateTestSuiteTransformer.class);

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		try {
			if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
				classfileBuffer = BytecodeTasks.integrateTestSuite(
						classfileBuffer, classNameWithDots);
			}
		} catch (Throwable t) {
			logger.warn(t.getMessage());
			t.printStackTrace();
			throw new RuntimeException(t);
		}
		return classfileBuffer;
	}

}