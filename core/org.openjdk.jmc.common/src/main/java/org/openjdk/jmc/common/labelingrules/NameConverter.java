package org.openjdk.jmc.common.labelingrules;

public final class NameConverter extends NameConverterBase {
	private static final NameConverter INSTANCE = new NameConverter();

	public NameConverter() {
		super();
	}

	/**
	 * @return a singleton instance
	 */
	public static NameConverterBase getInstance() {
		return INSTANCE;
	}
}
