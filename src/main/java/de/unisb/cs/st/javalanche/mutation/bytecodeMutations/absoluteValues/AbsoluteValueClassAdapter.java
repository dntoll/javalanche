/*
 * Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.absoluteValues;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.results.persistence.MutationManager;

/**
 * 
 * @author David Schuler
 * 
 */
public class AbsoluteValueClassAdapter extends ClassAdapter {

	/**
	 * The name of the class.
	 */
	private String className;
	private Map<Integer, Integer> possibilities = new HashMap<Integer, Integer>();
	private final MutationManager mm;

	/**
	 * Constructs a new {@link AbsoluteValueClassAdapter}
	 * 
	 * @param cv
	 *            The {@link ClassVisitor} to which this adapter delegates
	 *            calls.
	 */
	public AbsoluteValueClassAdapter(ClassVisitor cv) {
		super(cv);
		mm = new MutationManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		return new AbsoluteValueMethodAdapter(super.visitMethod(access, name,
				desc, signature, exceptions), className, name, possibilities,
				mm, desc);
	}

}
