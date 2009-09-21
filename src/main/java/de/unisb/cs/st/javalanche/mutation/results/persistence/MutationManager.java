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
package de.unisb.cs.st.javalanche.mutation.results.persistence;

import org.apache.log4j.Logger;

import de.unisb.cs.st.javalanche.mutation.javaagent.MutationForRun;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;

/**
 * Decides if a mutation should be applied in bytecode when the class is loaded.
 *
 * @author David Schuler
 *
 */
public class MutationManager {

	/**
	 * If set to true all mutations from the database are applied, otherwise
	 * only the mutations given by the {@link MutationForRun}
	 */
	private static boolean applyAllMutation = false;

	private static Logger logger = Logger.getLogger(MutationManager.class);

	public static boolean shouldApplyMutation(Mutation mutation) {
		if(mutation == null){
			logger.warn( "Null Mutation");
			return false;
		}
		else if (MutationForRun.getInstance()
				.containsMutation(mutation)) {
			Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDb == null) {
				logger.warn( "Mutation not in db: " + mutation);
				return false;
			}
			logger.debug("Applying mutation: " + mutationFromDb);
			MutationForRun.mutationApplied(mutationFromDb);
			return true;
		}
		return applyAllMutation;
	}

	/**
	 * If set to true all mutations from the database are applied, otherwise
	 * only the mutations given by the {@link MutationForRun}
	 *
	 * @param applyAllMutation
	 *            the applyAllMutation to set
	 */
	public static void setApplyAllMutation(boolean applyAllMutation) {
		MutationManager.applyAllMutation = applyAllMutation;
	}

}
