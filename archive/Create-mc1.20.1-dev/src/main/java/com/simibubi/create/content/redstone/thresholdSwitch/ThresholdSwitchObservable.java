package com.simibubi.create.content.redstone.thresholdSwitch;

import net.minecraft.network.chat.MutableComponent;

public interface ThresholdSwitchObservable {
	
	public int getMaxValue();
	
	public int getMinValue();
	
	public int getCurrentValue();

	public MutableComponent format(int value);
	
}
