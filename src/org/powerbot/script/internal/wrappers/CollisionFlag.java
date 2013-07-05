package org.powerbot.script.internal.wrappers;

public final class CollisionFlag {
	public static final CollisionFlag NORTHWEST = new CollisionFlag(0x1, false);
	public static final CollisionFlag NORTH = new CollisionFlag(0x2, false);
	public static final CollisionFlag NORTHEAST = new CollisionFlag(0x4, false);
	public static final CollisionFlag EAST = new CollisionFlag(0x8, false);
	public static final CollisionFlag SOUTHEAST = new CollisionFlag(0x10, false);
	public static final CollisionFlag SOUTH = new CollisionFlag(0x20, false);
	public static final CollisionFlag SOUTHWEST = new CollisionFlag(0x40, false);
	public static final CollisionFlag WEST = new CollisionFlag(0x80, false);
	public static final CollisionFlag OBJECT_BLOCK = new CollisionFlag(0x100, false);
	public static final CollisionFlag DECORATION_BLOCK = new CollisionFlag(0x40000, false);
	public static final CollisionFlag DEAD_BLOCK = new CollisionFlag(0x200000, false);
	public static final CollisionFlag ALLOW_RANGE_NORTHWEST = new CollisionFlag(0x400000, false);
	public static final CollisionFlag ALLOW_RANGE_NORTH = new CollisionFlag(0x800000, false);
	public static final CollisionFlag ALLOW_RANGE_NORTHEAST = new CollisionFlag(0x1000000, false);
	public static final CollisionFlag ALLOW_RANGE_EAST = new CollisionFlag(0x2000000, false);
	public static final CollisionFlag ALLOW_RANGE_SOUTHEAST = new CollisionFlag(0x4000000, false);
	public static final CollisionFlag ALLOW_RANGE_SOUTH = new CollisionFlag(0x8000000, false);
	public static final CollisionFlag ALLOW_RANGE_SOUTHWEST = new CollisionFlag(0x10000000, false);
	public static final CollisionFlag ALLOW_RANGE_WEST = new CollisionFlag(0x20000000, false);
	public static final CollisionFlag OBJECT_ALLOW_RANGE = new CollisionFlag(0x40000000, false);
	public static final CollisionFlag PADDING = new CollisionFlag(0xffffffff, false);

	public static CollisionFlag createNewMarkable() {
		return new CollisionFlag(0, true);
	}

	private CollisionFlag(final int type, final boolean markable) {
		this.type = type;
		this.markable = markable;
	}

	private int type;
	private boolean markable;

	public boolean marked(CollisionFlag collisionFlag) {
		return (type & collisionFlag.type) != 0;
	}

	public CollisionFlag mark(final CollisionFlag collisionFlag) {
		if (markable) {
			type |= collisionFlag.type;
			return this;
		} else {
			return new CollisionFlag(type | collisionFlag.type, true);
		}
	}

	public CollisionFlag erase(final CollisionFlag collisionFlag) {
		if (markable) {
			type &= ~collisionFlag.type;
			return this;
		} else {
			return new CollisionFlag(type & ~collisionFlag.type, true);
		}
	}

	public CollisionFlag mark(final int flag) {
		if (markable) {
			type |= flag;
			return this;
		} else {
			return new CollisionFlag(type | flag, true);
		}
	}

	public CollisionFlag erase(final int flag) {
		if (markable) {
			type &= ~flag;
			return this;
		} else {
			return new CollisionFlag(type & ~flag, true);
		}
	}

	public int getType() {
		return type;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CollisionFlag)) {
			return false;
		}
		CollisionFlag that = (CollisionFlag) o;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return type;
	}
}
