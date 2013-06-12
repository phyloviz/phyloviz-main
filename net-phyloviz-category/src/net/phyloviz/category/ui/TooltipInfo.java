package net.phyloviz.category.ui;


import java.text.DecimalFormat;

public class TooltipInfo {

	private Float percentage;
	private String name;
	private DecimalFormat twoForm = new DecimalFormat("#.##");

	public TooltipInfo(Float p, String n) {
		this.percentage = p;
		this.name = n;

	}

	public String getPercentage() {
                float f=percentage * 100;
                return twoForm.format(f);
	}

	public String getName() {
		return name;
	}
}
