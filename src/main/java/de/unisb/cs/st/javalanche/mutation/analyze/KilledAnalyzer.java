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
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;

public class KilledAnalyzer implements MutationAnalyzer {

	private static final String FORMAT = "%-55s %10d\n";

	public String analyze(Iterable<Mutation> mutations, HtmlReport report) {
		List<Mutation> killedByError = new ArrayList<Mutation>();
		List<Mutation> killedByFailure = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByError = new ArrayList<Mutation>();
		List<Mutation> killedExclusivelyByFailure = new ArrayList<Mutation>();
		for (Mutation mutation : mutations) {
			if (mutation.isKilled()) {
				MutationTestResult mutationResult = mutation
						.getMutationResult();
				if (mutationResult.getNumberOfErrors() > 0) {
					killedByError.add(mutation);
					if (mutationResult.getNumberOfFailures() == 0) {
						killedExclusivelyByError.add(mutation);
					}
				}
				if (mutationResult.getNumberOfFailures() > 0) {
					killedByFailure.add(mutation);
					if (mutationResult.getNumberOfErrors() == 0) {
						killedExclusivelyByFailure.add(mutation);
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(FORMAT,
				"Number of mutations killed by errors:", killedByError.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed exclusively by errors:",
				killedExclusivelyByError.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed by failures:", killedByFailure
						.size()));
		sb.append(String.format(FORMAT,
				"Number of mutations killed exclusively by failures:",
				killedExclusivelyByFailure.size()));
		return sb.toString();
	}

	
}
