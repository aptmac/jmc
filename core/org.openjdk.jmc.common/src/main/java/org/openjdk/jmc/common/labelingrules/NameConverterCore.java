package org.openjdk.jmc.common.labelingrules;

public final class NameConverterCore extends NameConverterBase {
	private static final NameConverterCore INSTANCE = new NameConverterCore();

	public NameConverterCore() {
		super();
	}

	/**
	 * @return a singleton instance
	 */
	public static NameConverterBase getInstance() {
		return INSTANCE;
	}
}