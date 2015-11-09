/*
 * Copyright 2015 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.gen5.commons.util.Preconditions;

/**
 * @author Sam Brannen
 * @since 5.0
 */
public abstract class AbstractTestDescriptor implements TestDescriptor {

	private final String uniqueId;

	private TestDescriptor parent;

	private final Set<TestDescriptor> children = new LinkedHashSet<>();

	protected AbstractTestDescriptor(String uniqueId) {
		Preconditions.notBlank(uniqueId, "uniqueId must not be null or empty");
		this.uniqueId = uniqueId;
	}

	@Override
	public final String getUniqueId() {
		return this.uniqueId;
	}

	@Override
	public TestDescriptor getParent() {
		return this.parent;
	}

	protected final void setParent(TestDescriptor parent) {
		this.parent = parent;
	}

	protected void removeChild(AbstractTestDescriptor abstractTestDescriptor) {
		children.remove(abstractTestDescriptor);
	}

	@Override
	public void remove() {
		if (parent instanceof AbstractTestDescriptor)
			((AbstractTestDescriptor) parent).removeChild(this);
		setParent(null);
		children.clear(); //to prevent visiting
	}

	@Override
	public final void addChild(TestDescriptor child) {
		Preconditions.notNull(child, "child must not be null");
		if (child instanceof AbstractTestDescriptor)
			((AbstractTestDescriptor) child).setParent(this);
		this.children.add(child);
	}

	@Override
	public final Set<TestDescriptor> getChildren() {
		return this.children;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		new LinkedHashSet<>(getChildren()).forEach(child -> child.accept(visitor));
	}

	@Override
	public Set<TestTag> getTags() {
		return Collections.emptySet();
	}

	@Override
	public final boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (this.getClass() != other.getClass()) {
			return false;
		}
		TestDescriptor otherDescriptor = (TestDescriptor) other;
		return this.getUniqueId().equals(otherDescriptor.getUniqueId());
	}

	@Override
	public final int hashCode() {
		return this.uniqueId.hashCode();
	}

}
