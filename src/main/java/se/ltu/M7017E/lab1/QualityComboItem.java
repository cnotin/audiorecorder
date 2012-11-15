package se.ltu.M7017E.lab1;

/**
 * Stolen from http://stackoverflow.com/a/5661669 because I wanted "Is it
 * possible to set a value and a label to a JComboBox so I can show a label but
 * get a value that is different?"
 */
public class QualityComboItem {
	private float value;
	private String label;

	public QualityComboItem(float value, String label) {
		this.value = value;
		this.label = label;
	}

	public float getValue() {
		return this.value;
	}

	public String getLabel() {
		return this.label;
	}

	@Override
	public String toString() {
		return label;
	}
}
