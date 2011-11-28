/**
 * Sarah - A Vi(s)u(a)lizer fo(r) Dat(a)base Sc(h)edulers Copyright (c) 2009 The
 * Sarah Team All Rights Reserved. This file is part of the project
 * "MyScheduler" of Sarah. Sarah is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version. This package is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program; see the file
 * LICENSE. If not you can find the GPL at http://www.gnu.org/copyleft/gpl.html
 */
package myscheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

import sarah.Scheduler;
import sarah.operations.Abort;
import sarah.operations.Commit;
import sarah.operations.Operation;
import sarah.operations.Read;
import sarah.operations.Write;

/**
 * The class {@code SGTScheduler} is an implementation of a serialization graph
 * scheduler.
 */
public class SGTScheduler extends Scheduler {

	private LinkedHashSet<Operation> history = new LinkedHashSet<Operation>();
	private SerializationGraph graph = new SerializationGraph();

	/**
	 * Creates a new {@link SGTScheduler}.
	 */
	public SGTScheduler() {

	}

	/*
	 * (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Read)
	 */
	@Override
	public void visit(Read read) {
		handleOp(read);
	}

	/*
	 * (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Write)
	 */
	@Override
	public void visit(Write write) {
		handleOp(write);
	}

	/*
	 * (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Abort)
	 */
	@Override
	public void visit(Abort abort) {

		/* Cleanup graph */
		graph.removeNode(abort.getTransaction());
		output(abort);
		gCollection();
	}

	/*
	 * (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Commit)
	 */
	@Override
	public void visit(Commit commit) {
		output(commit);
		gCollection();
	}

	private void handleOp(Operation op) {
		long transaction = op.getTransaction();

		/* Add node on first operation */
		if (!graph.hasNode(transaction))
			graph.addNode(transaction);

		/* Add edges according to conflicts */
		for (Operation o : history)
			if (conflict(o, op))
				graph.addEdge(o.getTransaction(), transaction);

		/* Test if graph is still acyclic */
		if (graph.isAcyclic()) {
			history.add(op);
			output(op);
		} else
			visit(new Abort(transaction));
	}

	private boolean conflict(Operation op1, Operation op2) {

		return graph.hasNode(op1.getTransaction())
				&& op1.getTransaction() != op2.getTransaction()
				&& op1.getParameters().equals(op2.getParameters())
				&& (op1 instanceof Write || op2 instanceof Write);
	}

	private void gCollection() {

		/*
		 * Remove transactions that are already committed and feature no
		 * incoming edges
		 */
		for (Long t : graph.getNodes())
			if (isCommitted(t) && !graph.hasIncoming(t))
				graph.removeNode(t);
	}

	private class SerializationGraph {

		/* Attention: Alot of (un)boxing longs is going on */
		private HashMap<Long, HashSet<Long>> adjacency = new HashMap<Long, HashSet<Long>>();

		public SerializationGraph addNode(long transaction) {
			adjacency.put(transaction, new HashSet<Long>());
			return this;
		}

		public Collection<Long> getNodes() {
			return new HashSet<Long>(adjacency.keySet());
		}

		public boolean hasNode(long transaction) {
			return adjacency.get(transaction) != null;
		}

		public SerializationGraph removeNode(long transaction) {
			adjacency.remove(transaction);

			for (HashSet<Long> set : adjacency.values())
				set.remove(transaction);

			return this;
		}

		public SerializationGraph addEdge(long source, long target) {

			if (adjacency.get(source) != null)
				adjacency.get(source).add(target);
			return this;
		}

		public boolean hasIncoming(long transaction) {
			boolean incoming = false;

			for (HashSet<Long> set : adjacency.values())
				incoming |= set.contains(transaction);

			return incoming;
		}

		public boolean isAcyclic() {
			Queue<Long> bfsQueue = new LinkedList<Long>();
			HashSet<Long> visited = new HashSet<Long>();

			/* Choose root for bfs traverse */
			if (!adjacency.isEmpty()) {
				long root = adjacency.keySet().iterator().next();
				bfsQueue.offer(root);
				visited.add(root);
			}

			while (!bfsQueue.isEmpty()) {
				long source = bfsQueue.poll();

				for (Long target : adjacency.get(source)) {
					if (visited.contains(target))
						return false;
					bfsQueue.add(target);
					visited.add(target);
				}
			}

			return true;
		}

		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(adjacency);
			return b.toString();
		}
	}
}
